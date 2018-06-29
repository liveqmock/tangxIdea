package com.topaiebiz.trade.refund.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.util.SecurityContextUtils;
import com.topaiebiz.system.util.SystemUserType;
import com.topaiebiz.trade.dto.order.OrderAddressDTO;
import com.topaiebiz.trade.order.util.OrdersQueryUtil;
import com.topaiebiz.trade.refund.core.context.RefundParamsContext;
import com.topaiebiz.trade.refund.core.executer.AuditRefundExecuter;
import com.topaiebiz.trade.refund.dao.RefundOrderDao;
import com.topaiebiz.trade.refund.dto.RefundAuditdDTO;
import com.topaiebiz.trade.refund.dto.common.ExecuteUserDTO;
import com.topaiebiz.trade.refund.dto.common.RefundGoodDTO;
import com.topaiebiz.trade.refund.dto.detail.PlatformRefundDetailDTO;
import com.topaiebiz.trade.refund.dto.page.RefundOrderPageDTO;
import com.topaiebiz.trade.refund.dto.page.RefundPageParamsDTO;
import com.topaiebiz.trade.refund.entity.RefundOrderDetailEntity;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.trade.refund.enumdata.RefundOrderStateEnum;
import com.topaiebiz.trade.refund.exception.RefundOrderExceptionEnum;
import com.topaiebiz.trade.refund.helper.RefundQueryUtil;
import com.topaiebiz.trade.refund.service.PlatformRefundService;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Description 平台售后处理
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/20 19:06
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Service
public class PlatformRefundServiceImpl implements PlatformRefundService {

    @Autowired
    private RefundOrderDao refundOrderDao;

    @Autowired
    private RefundQueryUtil refundQueryUtil;

    @Autowired
    private OrdersQueryUtil ordersQueryUtil;

    @Autowired
    private AuditRefundExecuter auditRefundExecuter;

    @Override
    public PageInfo<RefundOrderPageDTO> getPlatformRefundOrderPage(RefundPageParamsDTO pageParams) {
        PagePO pagePO = pageParams.getPagePO();
        Page<RefundOrderPageDTO> pageDtoPage = PageDataUtil.buildPageParam(pagePO);
        PageInfo<RefundOrderPageDTO> pageDTOPageInfo;
        // 查询参数
        pageParams.setPfInvolved(Constants.Refund.PLATFORM_HAS_BEEN_INVOLVED);
        List<RefundOrderPageDTO> pageDtoList = refundOrderDao.getPlatformRefundOrderPage(pageDtoPage, pageParams);
        pageDtoPage.setRecords(pageDtoList);
        pageDTOPageInfo = PageDataUtil.copyPageInfo(pageDtoPage, RefundOrderPageDTO.class);
        return pageDTOPageInfo;
    }

    @Override
    public PlatformRefundDetailDTO getPlatformRefundOrderDetail(Long refundOrderId) {
        RefundOrderEntity refundOrderEntity = refundQueryUtil.queryRefundOrder(refundOrderId);
        PlatformRefundDetailDTO detailDto = new PlatformRefundDetailDTO();
        BeanCopyUtil.copy(refundOrderEntity, detailDto);

        String[] refundImgs = new String[3];
        refundImgs[0] = refundOrderEntity.getRefundImg1();
        refundImgs[1] = refundOrderEntity.getRefundImg2();
        refundImgs[2] = refundOrderEntity.getRefundImg3();
        detailDto.setRefundImgs(refundImgs);

        // 订单支付金额
        OrderEntity orderEntity = ordersQueryUtil.queryOrder(refundOrderEntity.getOrderId());
        detailDto.setOrderPayPrice(orderEntity.getPayPrice());
        detailDto.setOrderTotalPrice(orderEntity.getOrderTotal());
        detailDto.setOrderPayType(orderEntity.getPayType());
        detailDto.setOrderMemo(orderEntity.getMemo());

        // 查询支付订单的收货信息
        OrderAddressDTO orderAddressDTO = ordersQueryUtil.queryOrderAddress(refundOrderEntity.getOrderId());
        detailDto.setOrderAddressDTO(orderAddressDTO);

        // 查询售后明细
        List<RefundOrderDetailEntity> refundOrderDetailEntities = refundQueryUtil.queryDetails(refundOrderId, null);
        List<RefundGoodDTO> refundGoodDTOS = new ArrayList<>(refundOrderDetailEntities.size());
        refundOrderDetailEntities.forEach(refundOrderDetailEntity -> {
            RefundGoodDTO refundGoodDTO = new RefundGoodDTO();
            BeanCopyUtil.copy(refundOrderDetailEntity, refundGoodDTO);
            refundGoodDTOS.add(refundGoodDTO);
        });
        detailDto.setRefundGoodDtos(refundGoodDTOS);

        return detailDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditApplyForRefund(RefundAuditdDTO refundAuditdDTO) {
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();

        ExecuteUserDTO executeUserDTO = new ExecuteUserDTO();
        executeUserDTO.setMemberId(currentUserDto.getId());
        executeUserDTO.setFromPlatform(currentUserDto.getType().equals(SystemUserType.PLATFORM));

        RefundParamsContext refundParamsContext = new RefundParamsContext();
        refundParamsContext.setExecuteUserDTO(executeUserDTO);
        refundParamsContext.setAuditSuccess(refundAuditdDTO.getResult().equals(Constants.Refund.AUDIT_SUCCESS));
        if (!refundParamsContext.isAuditSuccess()){
            refundParamsContext.setRefuseDescription(refundAuditdDTO.getRefuseDescription());
        }

        Long refundOrderId = refundAuditdDTO.getRefundOrderId();
        RefundOrderEntity refundOrderEntity = refundQueryUtil.queryRefundOrder(refundOrderId);

        // 当前处理售后订单，是否未平台介入订单
        if (!refundOrderEntity.getPfInvolved().equals(Constants.Refund.PLATFORM_HAS_BEEN_INVOLVED)){
            log.error("----------The refund order：{} has not been intervened by the platform", refundOrderId);
            throw new GlobalException(RefundOrderExceptionEnum.PLATFORMS_DO_NOT_INTERVENE_ORDER);
        }else{
            refundParamsContext.setPlatformInvolved(true);
        }

        // 仅售后订单状态为退款已拒绝/退货已拒绝 时 可操作
        Integer refundState = refundOrderEntity.getRefundState();
        if (!refundState.equals(RefundOrderStateEnum.REJECTED_REFUND.getCode()) && !refundState.equals(RefundOrderStateEnum.REJECTED_RETURNS.getCode())){
            log.error("----------The refund order：{}'s current status is not allowed to be modified on the platform", refundOrderId);
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_NOT_ALLOW_TO_INTERVENED);
        }

        refundParamsContext.setRefundOrderEntity(refundOrderEntity);
        refundParamsContext.setOrderEntity(ordersQueryUtil.queryOrder(refundOrderEntity.getOrderId()));

        return auditRefundExecuter.execute(refundParamsContext);
    }
}
