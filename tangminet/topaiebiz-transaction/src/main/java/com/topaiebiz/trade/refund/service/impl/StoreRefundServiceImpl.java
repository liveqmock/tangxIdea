package com.topaiebiz.trade.refund.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
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
import com.topaiebiz.trade.refund.dto.detail.StoreRefundDetailDTO;
import com.topaiebiz.trade.refund.dto.page.RefundOrderPageDTO;
import com.topaiebiz.trade.refund.dto.page.RefundPageParamsDTO;
import com.topaiebiz.trade.refund.entity.RefundOrderDetailEntity;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.trade.refund.exception.RefundOrderExceptionEnum;
import com.topaiebiz.trade.refund.helper.RefundQueryUtil;
import com.topaiebiz.trade.refund.service.StoreRefundService;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Description 商家售后服务实现层
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/20 19:17
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Service
public class StoreRefundServiceImpl implements StoreRefundService {

    @Autowired
    private RefundOrderDao refundOrderDao;

    @Autowired
    private RefundQueryUtil refundQueryUtil;

    @Autowired
    private AuditRefundExecuter auditRefundExecuter;

    @Autowired
    private OrdersQueryUtil ordersQueryUtil;

    @Autowired
    private DistLockSservice distLockSservice;

    @Override
    public PageInfo<RefundOrderPageDTO> getRefundOrderPage(RefundPageParamsDTO pageParams) {
        PagePO pagePO = pageParams.getPagePO();
        Page<RefundOrderPageDTO> pageDtoPage = PageDataUtil.buildPageParam(pagePO);
        PageInfo<RefundOrderPageDTO> pageDTOPageInfo;
        // 查询参数
        pageParams.setStoreId(SecurityContextUtils.getCurrentUserDto().getStoreId());

        List<RefundOrderPageDTO> pageDtoList = refundOrderDao.getStoreRefundOrderPage(pageDtoPage, pageParams);
        pageDtoPage.setRecords(pageDtoList);
        pageDTOPageInfo = PageDataUtil.copyPageInfo(pageDtoPage);
        return pageDTOPageInfo;
    }

    @Override
    public StoreRefundDetailDTO getRefundOrderDetail(Long refundOrderId) {
        RefundOrderEntity refundOrderEntity = refundQueryUtil.queryRefundOrder(refundOrderId);
        StoreRefundDetailDTO detailDto = new StoreRefundDetailDTO();
        BeanCopyUtil.copy(refundOrderEntity, detailDto);

        String[] refundImgs = new String[3];
        refundImgs[0] = refundOrderEntity.getRefundImg1();
        refundImgs[1] = refundOrderEntity.getRefundImg2();
        refundImgs[2] = refundOrderEntity.getRefundImg3();
        detailDto.setRefundImgs(refundImgs);

        // 订单支付金额
        OrderEntity orderEntity = ordersQueryUtil.queryOrder(refundOrderEntity.getOrderId());
        detailDto.setOrderPayPrice(orderEntity.getPayPrice());
        if (OrderStatusEnum.PENDING_DELIVERY.getCode().equals(orderEntity.getOrderState())) {
            detailDto.setCantRefuse(Constants.Refund.REFUND_CAN_REFUSE_NO);
        }

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
    public Boolean auditApplyForRefund(RefundAuditdDTO refundAuditdDTO) {
        RefundParamsContext refundParamsContext = new RefundParamsContext();
        Long refundOrderId = refundAuditdDTO.getRefundOrderId();
        RefundOrderEntity refundOrderEntity = refundQueryUtil.queryStoreRefundOrder(refundOrderId, SecurityContextUtils.getCurrentUserDto().getStoreId());
        refundParamsContext.setRefundOrderEntity(refundOrderEntity);

        LockResult subRefundLock = null;
        try {
            subRefundLock = distLockSservice.tryLock(Constants.LockOperatons.REFUND_ORDER_LOCK, refundOrderId);
            if (!subRefundLock.isSuccess()) {
                throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_OPERATE_DUPLICATE);
            }
            CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();

            ExecuteUserDTO executeUserDTO = new ExecuteUserDTO();
            executeUserDTO.setMemberId(currentUserDto.getId());
            executeUserDTO.setStoreId(currentUserDto.getStoreId());
            executeUserDTO.setFromPlatform(currentUserDto.getType().equals(SystemUserType.PLATFORM));

            refundParamsContext.setExecuteUserDTO(executeUserDTO);
            refundParamsContext.setAuditSuccess(refundAuditdDTO.getResult().equals(Constants.Refund.AUDIT_SUCCESS));
            if (!refundParamsContext.isAuditSuccess()) {
                refundParamsContext.setRefuseDescription(refundAuditdDTO.getRefuseDescription());
            }
            Long orderId = refundOrderEntity.getOrderId();
            refundParamsContext.setOrderEntity(ordersQueryUtil.queryOrder(orderId));

            return auditRefundExecuter.execute(refundParamsContext);
        } finally {
            distLockSservice.unlock(subRefundLock);
        }
    }


}
