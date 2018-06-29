package com.topaiebiz.trade.refund.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.dto.pay.GoodPayDTO;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.trade.order.util.OrdersQueryUtil;
import com.topaiebiz.trade.refund.core.context.RefundParamsContext;
import com.topaiebiz.trade.refund.core.executer.CancelRefundExecuter;
import com.topaiebiz.trade.refund.core.executer.SubmitLogisticsExecuter;
import com.topaiebiz.trade.refund.core.executer.SubmitRefundExecuter;
import com.topaiebiz.trade.refund.dao.RefundOrderDao;
import com.topaiebiz.trade.refund.dto.RefundApplyParamDTO;
import com.topaiebiz.trade.refund.dto.RefundLogisticsDTO;
import com.topaiebiz.trade.refund.dto.RefundSubmitDTO;
import com.topaiebiz.trade.refund.dto.common.ExecuteUserDTO;
import com.topaiebiz.trade.refund.dto.common.RefundGoodDTO;
import com.topaiebiz.trade.refund.dto.detail.CustomerRefundDetailDTO;
import com.topaiebiz.trade.refund.dto.page.CustomerRefundPageDTO;
import com.topaiebiz.trade.refund.entity.RefundOrderDetailEntity;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.trade.refund.enumdata.RefundOrderStateEnum;
import com.topaiebiz.trade.refund.enumdata.RefundProcessEnum;
import com.topaiebiz.trade.refund.exception.RefundOrderExceptionEnum;
import com.topaiebiz.trade.refund.facade.MerchantReturnServiceFacade;
import com.topaiebiz.trade.refund.helper.RefundAuditCheckUtil;
import com.topaiebiz.trade.refund.helper.RefundOrderHelper;
import com.topaiebiz.trade.refund.helper.RefundQueryUtil;
import com.topaiebiz.trade.refund.service.CustomerRefundService;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Description 用户售后服务实现层
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/8 12:29
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Service
public class CustomerRefundServiceImpl implements CustomerRefundService {

    @Autowired
    private RefundOrderDao refundOrderDao;

    @Autowired
    private RefundOrderHelper refundOrderHelper;

    @Autowired
    private OrdersQueryUtil ordersQueryUtil;

    @Autowired
    private DistLockSservice distLockSservice;

    @Autowired
    private SubmitRefundExecuter submitRefundExecuter;

    @Autowired
    private SubmitLogisticsExecuter submitLogisticsExecuter;

    @Autowired
    private CancelRefundExecuter cancelRefundExecuter;

    @Autowired
    private RefundAuditCheckUtil refundAuditCheckUtil;

    @Autowired
    private RefundQueryUtil refundQueryUtil;

    @Autowired
    private MerchantReturnServiceFacade merchantReturnServiceFacade;

    @Override
    public PageInfo<CustomerRefundPageDTO> getCustomerRefundOrderPage(PagePO pagePO) {
        Page<RefundOrderEntity> refundOrderEntityPage = PageDataUtil.buildPageParam(pagePO);
        // 1：查询售后
        List<RefundOrderEntity> refundOrderEntities = refundQueryUtil.queryPageByCustomer(pagePO, MemberContext.getCurrentMemberToken().getMemberId());
        if (CollectionUtils.isEmpty(refundOrderEntities)) {
            return null;
        }
        refundOrderEntityPage.setRecords(refundOrderEntities);
        PageInfo<CustomerRefundPageDTO> refundPageDTOPageInfo = PageDataUtil.copyPageInfo(refundOrderEntityPage, CustomerRefundPageDTO.class);

        List<Long> refundOrderIds = new ArrayList<>(refundOrderEntities.size());
        refundOrderEntities.forEach(refundOrderEntity -> refundOrderIds.add(refundOrderEntity.getId()));

        // 2：查询用户售后详情
        Map<Long, List<RefundOrderDetailEntity>> map = refundQueryUtil.queryRefundDetailsMap(refundOrderIds);
        refundPageDTOPageInfo.getRecords().forEach(customerRefundOrderPageDTO -> {
            List<RefundOrderDetailEntity> refundOrderDetailEntities = map.get(customerRefundOrderPageDTO.getId());
            List<RefundGoodDTO> refundGoodDTOS = new ArrayList<>(refundOrderDetailEntities.size());
            refundOrderDetailEntities.forEach(refundOrderDetailEntity -> {
                RefundGoodDTO refundGoodDto = new RefundGoodDTO();
                BeanCopyUtil.copy(refundOrderDetailEntity, refundGoodDto);
                refundGoodDto.setItemId(refundOrderDetailEntity.getGoodItemId());
                refundGoodDTOS.add(refundGoodDto);
            });
            customerRefundOrderPageDTO.setRefundGoodDtos(refundGoodDTOS);
        });
        return refundPageDTOPageInfo;
    }


    @Override
    public RefundSubmitDTO applyForRefund(RefundApplyParamDTO refundApplyParamDTO) {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        Long orderId = refundApplyParamDTO.getOrderId();
        log.info("----------member:{} apply for a refund：{}", memberId, JSON.toJSONString(refundApplyParamDTO));
        OrderEntity orderEntity;
        RefundSubmitDTO refundSubmitDTO = new RefundSubmitDTO();
        refundSubmitDTO.setOrderId(orderId);
        refundSubmitDTO.setOrderDetailIds(refundApplyParamDTO.getOrderDetailIds());

        Long refundId = refundApplyParamDTO.getRefundId();
        if (null != refundId) {
            // 售后修改
            RefundOrderEntity refundOrderEntity = refundQueryUtil.queryCustomerRefundOrder(refundId, memberId);
            orderEntity = ordersQueryUtil.queryCustomerOrder(refundOrderEntity.getOrderId(), memberId);
            refundSubmitDTO.setOrderId(orderEntity.getId());

            // 是否能够被修改
            if (!RefundOrderStateEnum.whetherRefundCanUpdate(refundOrderEntity.getRefundState())) {
                throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_CANT_BE_UPDATE);
            }
            refundSubmitDTO.setRefundId(refundId);
            BeanCopyUtil.copy(refundOrderEntity, refundSubmitDTO);

            // 未发货 整单退
            if (orderEntity.getOrderState().equals(OrderStatusEnum.PENDING_DELIVERY.getCode())) {
                refundSubmitDTO.setOrderDetailIds(Collections.emptySet());
            } else {
                refundSubmitDTO.setOrderDetailIds(refundQueryUtil.queryDetailIdsByRefundId(refundId));
            }

        } else {
            //1:校验能否退款
            orderEntity = ordersQueryUtil.queryCustomerOrder(orderId, memberId);
            if (!refundOrderHelper.checkOrderCanRefund(orderEntity, refundSubmitDTO, true)) {
                throw new GlobalException(RefundOrderExceptionEnum.ORDER_CANT_APPLY_REFUND_AGAIN);
            }
        }

        if (orderEntity.getOrderState().compareTo(OrderStatusEnum.PENDING_DELIVERY.getCode()) > 0) {
            refundSubmitDTO.setSending(true);
        }

        //1: 计算售后结果
        this.refundParameterAssembly(refundSubmitDTO, orderEntity);
        return refundSubmitDTO;
    }

    @Override
    public RefundSubmitDTO reapply(Long refundId) {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        RefundOrderEntity refundOrderEntity = refundQueryUtil.queryCustomerRefundOrder(refundId, memberId);
        Integer refundState = refundOrderEntity.getRefundState();
        // 只有被商家拒绝的售后, 才可以重新申请售后
        if (!RefundOrderStateEnum.whetherRefundCanReapply(refundState)) {
            log.warn(">>>>>>>>>>refund:{} reapply fail, refund state is not allow!", refundId);
            throw new GlobalException(RefundOrderExceptionEnum.ILLEGAL_OPERATION);
        }
        RefundSubmitDTO refundSubmitDTO = new RefundSubmitDTO();
        refundSubmitDTO.setRefundId(refundOrderEntity.getId());

        Set<Long> orderDetailIds = refundQueryUtil.queryDetailIdsByRefundId(refundId);
        if (CollectionUtils.isEmpty(orderDetailIds)) {
            log.warn("----------从旧的售后订单详情中 查询不到订单详情的ID集合！");
            throw new GlobalException(RefundOrderExceptionEnum.OPERATION_CAN_NOT_BE_EXECUTED);
        }
        refundSubmitDTO.setOrderDetailIds(orderDetailIds);

        // 校验能否退款
        OrderEntity orderEntity = ordersQueryUtil.queryCustomerOrder(refundOrderEntity.getOrderId(), memberId);
        if (!refundOrderHelper.checkOrderCanRefund(orderEntity, refundSubmitDTO, false)) {
            throw new GlobalException(RefundOrderExceptionEnum.ORDER_CANT_APPLY_REFUND_AGAIN);
        }
        // 是否已发货
        if (orderEntity.getOrderState().compareTo(OrderStatusEnum.PENDING_DELIVERY.getCode()) > 0) {
            refundSubmitDTO.setSending(true);
        }

        //1: 计算售后结果
        this.refundParameterAssembly(refundSubmitDTO, orderEntity);

        // 重新申请
        refundSubmitDTO.setIfReapply(true);
        return refundSubmitDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean refundSubmit(RefundSubmitDTO refundSubmitDTO) {
        RefundParamsContext refundParamsContext = new RefundParamsContext();

        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        ExecuteUserDTO executeUserDTO = new ExecuteUserDTO();
        executeUserDTO.setMemberId(memberId);
        refundParamsContext.setExecuteUserDTO(executeUserDTO);

        Long orderId = refundSubmitDTO.getOrderId();
        Long refundId = refundSubmitDTO.getRefundId();
        Long lockId = orderId == null ? refundId : orderId;
        LockResult subRefundLock = null;
        try {
            subRefundLock = distLockSservice.tryLock(Constants.LockOperatons.REFUND_ORDER_LOCK, lockId);
            if (!subRefundLock.isSuccess()) {
                throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_OPERATE_DUPLICATE);
            }
            RefundOrderEntity refundOrderEntity;
            OrderEntity orderEntity;
            if (null != refundId) {
                refundOrderEntity = refundQueryUtil.queryCustomerRefundOrder(refundId, memberId);
                orderId = refundOrderEntity.getOrderId();
                orderEntity = ordersQueryUtil.queryCustomerOrder(orderId, memberId);
                refundParamsContext.setUpdate(true);
                refundSubmitDTO.setOrderId(orderId);

                // 未发货整单退
                if (!orderEntity.getOrderState().equals(OrderStatusEnum.PENDING_DELIVERY.getCode())) {
                    refundSubmitDTO.setOrderDetailIds(refundQueryUtil.queryDetailIdsByRefundId(refundId));
                }

                if (!RefundOrderStateEnum.whetherRefundCanUpdate(refundOrderEntity.getRefundState()) || Constants.Refund.PLATFORM_HAS_BEEN_INVOLVED.equals(refundOrderEntity.getPfInvolved())) {
                    throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_CANT_BE_UPDATE);
                }
            } else {
                // 创建售后，则支付订单ID不能为空！
                if (orderId == null) {
                    throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_ID_CANT_BE_NULL);
                }
                // 创建售后
                orderEntity = ordersQueryUtil.queryCustomerOrder(orderId, memberId);

                //1:校验能否退款
                if (!refundOrderHelper.checkOrderCanRefund(orderEntity, refundSubmitDTO, true)) {
                    throw new GlobalException(RefundOrderExceptionEnum.ORDER_CANT_APPLY_REFUND_AGAIN);
                }
            }
            // 未发货整单退
            if (orderEntity.getOrderState().equals(OrderStatusEnum.PENDING_DELIVERY.getCode())) {
                refundParamsContext.setAllRefund(true);
                refundSubmitDTO.setOrderDetailIds(Collections.emptySet());
            }

            // 判断申请类型 与 是否符合订单发货状态
            if (OrderStatusEnum.PENDING_DELIVERY.getCode().compareTo(orderEntity.getOrderState()) >= 0 && refundSubmitDTO.getRefundType().equals(Constants.Refund.RETURNS)) {
                log.warn("----------订单未发货，不能申请退货退款！");
                throw new GlobalException(RefundOrderExceptionEnum.OPERATION_CAN_NOT_BE_EXECUTED);
            }

            //2:拼装售后参数
            this.refundParameterAssembly(refundSubmitDTO, orderEntity);
            log.info("----------member:{} submitted a refund request：{}", memberId, JSON.toJSONString(refundSubmitDTO));
            //3:提交
            refundParamsContext.setOrderEntity(orderEntity);
            refundParamsContext.setRefundSubmitDTO(refundSubmitDTO);
            return submitRefundExecuter.execute(refundParamsContext);
        } finally {
            distLockSservice.unlock(subRefundLock);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitReapply(RefundSubmitDTO refundSubmitDTO) {
        RefundParamsContext refundParamsContext = new RefundParamsContext();

        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        refundParamsContext.setExecuteUserDTO(new ExecuteUserDTO(memberId));

        Long refundId = refundSubmitDTO.getRefundId();
        // 创建售后，则支付订单ID不能为空！
        if (refundId == null) {
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_ID_CANT_BE_NULL);
        }
        LockResult subRefundLock = null;
        try {
            subRefundLock = distLockSservice.tryLock(Constants.LockOperatons.REFUND_ORDER_LOCK, refundId);
            if (!subRefundLock.isSuccess()) {
                throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_OPERATE_DUPLICATE);
            }
            RefundOrderEntity refundOrderEntity = refundQueryUtil.queryCustomerRefundOrder(refundId, memberId);
            Integer refundState = refundOrderEntity.getRefundState();
            // 只有被商家拒绝的售后, 才可以重新申请售后
            if (!RefundOrderStateEnum.whetherRefundCanReapply(refundState)) {
                log.warn(">>>>>>>>>>refund:{} reapply fail, refund state is not allow!", refundId);
                throw new GlobalException(RefundOrderExceptionEnum.ILLEGAL_OPERATION);
            }
            if (Constants.Refund.PLATFORM_HAS_BEEN_INVOLVED.equals(refundOrderEntity.getPfInvolved())) {
                log.warn(">>>>>>>>>>售后订单申诉中，不支持重新申请! refundId:{}", refundId);
                throw new GlobalException(RefundOrderExceptionEnum.ILLEGAL_OPERATION);
            }
            RefundOrderEntity refundUpdate = new RefundOrderEntity();
            refundUpdate.cleanInit();
            refundUpdate.setId(refundId);
            refundUpdate.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
            refundUpdate.setLastModifiedTime(new Date());
            refundUpdate.setLastModifierId(memberId);
            refundUpdate.setSpareField_1("删除原因：用户重新申请新的售后订单--删除此旧售后订单");
            refundOrderDao.updateById(refundUpdate);

            Long orderId = refundOrderEntity.getOrderId();

            // 创建售后
            OrderEntity orderEntity = ordersQueryUtil.queryCustomerOrder(orderId, memberId);
            refundSubmitDTO.setOrderId(orderId);

            //1:校验能否退款
            if (!refundOrderHelper.checkOrderCanRefund(orderEntity, refundSubmitDTO, false)) {
                throw new GlobalException(RefundOrderExceptionEnum.ORDER_CANT_APPLY_REFUND_AGAIN);
            }

            //2:判断申请类型 与 是否符合订单发货状态
            if (OrderStatusEnum.PENDING_DELIVERY.getCode().compareTo(orderEntity.getOrderState()) >= 0 && Constants.Refund.RETURNS.equals(refundSubmitDTO.getRefundType())) {
                log.warn("----------订单未发货，不能申请退货退款！");
                throw new GlobalException(RefundOrderExceptionEnum.OPERATION_CAN_NOT_BE_EXECUTED);
            }
            //3:拼装售后参数
            Set<Long> orderDetailIds = refundQueryUtil.queryDetailIdsByRefundId(refundId);
            if (CollectionUtils.isEmpty(orderDetailIds)) {
                log.warn("----------从旧的售后订单详情中 查询不到订单详情的ID集合！");
                throw new GlobalException(RefundOrderExceptionEnum.OPERATION_CAN_NOT_BE_EXECUTED);
            }
            refundSubmitDTO.setOrderDetailIds(orderDetailIds);
            this.refundParameterAssembly(refundSubmitDTO, orderEntity);
            log.info("----------member:{} submitted a refund request：{}", memberId, JSON.toJSONString(refundSubmitDTO));
            //4:提交
            refundParamsContext.setOrderEntity(orderEntity);
            refundParamsContext.setRefundSubmitDTO(refundSubmitDTO);
            return submitRefundExecuter.execute(refundParamsContext);
        } finally {
            distLockSservice.unlock(subRefundLock);
        }
    }

    @Override
    public CustomerRefundDetailDTO getRefundDetailInfo(Long refundOrderId) {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        RefundOrderEntity refundOrderEntity = refundQueryUtil.queryCustomerRefundOrder(refundOrderId, memberId);
        refundAuditCheckUtil.check(refundOrderEntity);

        CustomerRefundDetailDTO refundOrderDetailDto = new CustomerRefundDetailDTO();
        BeanCopyUtil.copy(refundOrderEntity, refundOrderDetailDto);

        List<RefundOrderDetailEntity> refundOrderDetailEntities = refundQueryUtil.queryDetails(refundOrderId, null);
        List<RefundGoodDTO> refundGoodDtos = new ArrayList<>(refundOrderDetailEntities.size());
        for (RefundOrderDetailEntity refundOrderDetailEntity : refundOrderDetailEntities) {
            RefundGoodDTO refundGoodDto = new RefundGoodDTO();
            BeanCopyUtil.copy(refundOrderDetailEntity, refundGoodDto);
            refundGoodDto.setItemId(refundOrderDetailEntity.getGoodItemId());
            refundGoodDtos.add(refundGoodDto);
        }
        refundOrderDetailDto.setRefundGoodDtos(refundGoodDtos);

        if (refundOrderEntity.getRefundState().equals(RefundOrderStateEnum.WAITING_FOR_RETURN.getCode())) {
            refundOrderDetailDto.setMerchantReturnDTO(merchantReturnServiceFacade.getStoreReturnAddress(refundOrderEntity.getStoreId()));
        }

        // 售后申请次数检查, 判断显示申诉的按钮
        int refuseCount = refundQueryUtil.queryRefuseRefundApplyCount(refundOrderEntity.getOrderId());
        if (refuseCount >= OrderConstants.OrderRefundStatus.REFUND_SHOW_APPEAL_COUNT) {
            refundOrderDetailDto.setShowAppealBtn(OrderConstants.OrderRefundStatus.REFUND_SHOW_APPEAL_YES);
        }

        // 售后未关闭, 且平台介入中， 页面显示平台介入中状态
        if (Constants.Refund.PLATFORM_HAS_BEEN_INVOLVED.equals(refundOrderEntity.getPfInvolved()) && !RefundOrderStateEnum.CLOSE.getCode().equals(refundOrderEntity.getRefundState())) {
            refundOrderDetailDto.setRefundIntervening(Constants.Refund.PLATFORM_HAS_BEEN_INVOLVED);
        }
        return refundOrderDetailDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submitLogisticsInfo(RefundLogisticsDTO refundLogisticsDTO) {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        Long refundOrderId = refundLogisticsDTO.getRefundOrderId();
        RefundOrderEntity refundOrderEntity = refundQueryUtil.queryCustomerRefundOrder(refundOrderId, memberId);
        if (!refundOrderEntity.getRefundState().equals(RefundOrderStateEnum.WAITING_FOR_RETURN.getCode())) {
            log.warn("----------operation is not allowed，refund' current state is not allow to submit refund logisticsInfo; memerId:{}, refundid:{}", memberId, refundOrderId);
            throw new GlobalException(RefundOrderExceptionEnum.OPERATION_CAN_NOT_BE_EXECUTED);
        }

        RefundParamsContext refundParamsContext = new RefundParamsContext();
        refundParamsContext.setRefundOrderEntity(refundOrderEntity);
        refundParamsContext.setExecuteUserDTO(new ExecuteUserDTO(memberId));
        refundParamsContext.setRefundLogisticsDTO(refundLogisticsDTO);
        return submitLogisticsExecuter.execute(refundParamsContext);
    }

    @Override
    public Boolean cancelRefundOrder(Long refundOrderId) {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        RefundOrderEntity refundOrderEntity = refundQueryUtil.queryCustomerRefundOrder(refundOrderId, memberId);
        if (!RefundOrderStateEnum.whetherRefundCanBeCancel(refundOrderEntity.getRefundState())) {
            return false;
        }
        RefundParamsContext refundParamsContext = new RefundParamsContext();
        refundParamsContext.setRefundOrderEntity(refundOrderEntity);
        refundParamsContext.setExecuteUserDTO(new ExecuteUserDTO(memberId));
        return cancelRefundExecuter.execute(refundParamsContext);
    }

    @Override
    public Boolean platformInvolve(Long refundOrderId) {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        RefundOrderEntity refundOrderEntity = refundQueryUtil.queryCustomerRefundOrder(refundOrderId, memberId);

        Integer refundState = refundOrderEntity.getRefundState();
        if (!refundState.equals(RefundOrderStateEnum.REJECTED_REFUND.getCode()) && !refundState.equals(RefundOrderStateEnum.REJECTED_RETURNS.getCode())) {
            log.warn("----------refundOrder:{} temporarily unable to apply for platform intervention", refundOrderId);
            return false;
        }
        RefundOrderEntity updateEntity = new RefundOrderEntity();
        updateEntity.cleanInit();
        updateEntity.setId(refundOrderId);
        updateEntity.setProcessState(RefundProcessEnum.WAIT.getCode());
        // 平台介入
        updateEntity.setPfInvolved(Constants.Refund.PLATFORM_HAS_BEEN_INVOLVED);
        return refundOrderDao.updateById(updateEntity) > 0;
    }

    /**
     * Description: 售后参数拼装，查询售后的商品信息集合，以及计算价格
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/9
     *
     * @param:
     **/
    private void refundParameterAssembly(RefundSubmitDTO refundSubmitDTO, OrderEntity orderEntity) {
        // 总支付
        BigDecimal totalPrice = BigDecimal.ZERO;
        Integer goodsNum = 0;
        BigDecimal cardPrice = BigDecimal.ZERO;
        BigDecimal scorePrice = BigDecimal.ZERO;
        BigDecimal balance = BigDecimal.ZERO;

        boolean allRefundFlag = false;
        List<OrderDetailEntity> orderDetailEntities;

        // 无具体订单明细 则视为整单退
        if (CollectionUtils.isEmpty(refundSubmitDTO.getOrderDetailIds())) {
            allRefundFlag = true;
            orderDetailEntities = ordersQueryUtil.queryDetailsByOrderId(refundSubmitDTO.getOrderId());
        } else {
            orderDetailEntities = ordersQueryUtil.queryDetailsByDetailsIds(refundSubmitDTO.getOrderDetailIds());
        }

        List<RefundGoodDTO> refundGoodDtos = new ArrayList<>(orderDetailEntities.size());
        for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
            RefundGoodDTO refundGoodDto = new RefundGoodDTO();
            refundGoodDto.setItemId(orderDetailEntity.getItemId());
            refundGoodDto.setGoodSkuId(orderDetailEntity.getSkuId());
            refundGoodDto.setGoodName(orderDetailEntity.getName());
            refundGoodDto.setGoodFileValue(orderDetailEntity.getFieldValue());
            refundGoodDto.setGoodImgUrl(orderDetailEntity.getGoodsImage());
            refundGoodDto.setGoodNum(orderDetailEntity.getGoodsNum().intValue());
            refundGoodDto.setGoodTotalPrice(orderDetailEntity.getTotalPrice());
            refundGoodDto.setPayPrice(orderDetailEntity.getPayPrice());
            refundGoodDto.setOrderDetailId(orderDetailEntity.getId());
            refundGoodDtos.add(refundGoodDto);
            totalPrice = totalPrice.add(orderDetailEntity.getPayPrice());
            goodsNum = goodsNum + refundGoodDto.getGoodNum();

            GoodPayDTO goodPayDTO = JSON.parseObject(orderDetailEntity.getPayDetail(), GoodPayDTO.class);
            scorePrice = scorePrice.add(goodPayDTO.getScorePrice());
            cardPrice = cardPrice.add(goodPayDTO.getCardPrice());
            balance = balance.add(goodPayDTO.getBalance());
        }

        // 整单退， 从支付详情取值
        if (allRefundFlag) {
            cardPrice = orderEntity.getCardPrice() == null ? BigDecimal.ZERO : orderEntity.getCardPrice();
            scorePrice = orderEntity.getScore() == null ? BigDecimal.ZERO : orderEntity.getScore();
            balance = orderEntity.getBalance() == null ? BigDecimal.ZERO : orderEntity.getBalance();
            totalPrice = orderEntity.getPayPrice() == null ? BigDecimal.ZERO : orderEntity.getPayPrice();

            BigDecimal actualFreight = orderEntity.getActualFreight();
            if (orderEntity.getOrderState().equals(OrderStatusEnum.PENDING_DELIVERY.getCode())) {
                refundSubmitDTO.setRefundFreight(actualFreight);
            } else {
                // 已发货 则不退运费
                totalPrice = totalPrice.subtract(actualFreight);
            }
        }
        refundSubmitDTO.setRefundGoodDTOS(refundGoodDtos);
        refundSubmitDTO.setMostRefundPrice(totalPrice);
        refundSubmitDTO.setRefundGoodsNum(goodsNum);

        // 判断用户是否手动输入了金额
        BigDecimal enterRefundAmount = refundSubmitDTO.getRefundPrice();
        if (null == enterRefundAmount || OrderStatusEnum.PENDING_DELIVERY.getCode().equals(orderEntity.getOrderState())) {
            enterRefundAmount = totalPrice;
            refundSubmitDTO.setRefundPrice(enterRefundAmount);
        } else {
            if (MathUtil.greator(enterRefundAmount, totalPrice) || MathUtil.less(enterRefundAmount, BigDecimal.ZERO)) {
                throw new GlobalException(RefundOrderExceptionEnum.REFUND_PRICE_IS_NOT_ALLOWABLE);
            }
        }

        /*
        退款顺序： 美礼卡-积分-余额-三方支付
         */
        BigDecimal refundScore;
        BigDecimal refundBalance;
        BigDecimal refundCard;
        // 美礼卡
        refundCard = MathUtil.greator(cardPrice, enterRefundAmount) ? enterRefundAmount : cardPrice;
        refundSubmitDTO.setRefundCardPrice(refundCard);
        enterRefundAmount = enterRefundAmount.subtract(refundCard);

        // 积分
        refundScore = MathUtil.greator(scorePrice, enterRefundAmount) ? enterRefundAmount : scorePrice;
        refundSubmitDTO.setRefundIntegralPrice(refundScore);
        enterRefundAmount = enterRefundAmount.subtract(refundScore);

        // 余额
        refundBalance = MathUtil.greator(balance, enterRefundAmount) ? enterRefundAmount : balance;
        refundSubmitDTO.setRefundBalance(refundBalance);
        enterRefundAmount = enterRefundAmount.subtract(refundBalance);

        // 三方支付
        refundSubmitDTO.setRefundThirdAmount(MathUtil.greator(enterRefundAmount, BigDecimal.ZERO) ? enterRefundAmount : BigDecimal.ZERO);
    }
}