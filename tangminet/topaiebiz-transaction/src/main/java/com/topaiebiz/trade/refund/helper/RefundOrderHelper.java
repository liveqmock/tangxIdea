package com.topaiebiz.trade.refund.helper;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.order.util.OrdersQueryUtil;
import com.topaiebiz.trade.refund.core.context.RefundParamsContext;
import com.topaiebiz.trade.refund.core.executer.RefundMoneyExecuter;
import com.topaiebiz.trade.refund.dao.RefundOrderDao;
import com.topaiebiz.trade.refund.dao.RefundOrderLogDao;
import com.topaiebiz.trade.refund.dto.RefundSubmitDTO;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.trade.refund.entity.RefundOrderLogEntity;
import com.topaiebiz.trade.refund.enumdata.RefundOrderStateEnum;
import com.topaiebiz.trade.refund.enumdata.RefundProcessEnum;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Description 售后帮助类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/9 18:01
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class RefundOrderHelper {

    @Autowired
    private RefundOrderDao refundOrderDao;

    @Autowired
    private OrdersQueryUtil ordersQueryUtil;

    @Autowired
    private RefundMoneyExecuter refundMoneyExecuter;

    @Autowired
    private RefundQueryUtil refundQueryUtil;

    @Autowired
    private OrderRefundStateUtil orderRefundStateUtil;

    @Autowired
    private RefundOrderLogDao refundOrderLogDao;


    /**
     * Description: 此方法用户 第三方发货时， 关闭申请仅退款的售后
     *
     * @Author: hxpeng
     * createTime: 2018/5/24
     * @param:
     **/
    public void closeRefundWhenDelivery(Long orderId, Long memberId) {
        log.info(">>>>>>>>>>openaip req delivery, close refund and update orderState, orderId:{}", orderId);
        Date currentDate = new Date();

        EntityWrapper<RefundOrderEntity> wrapper = new EntityWrapper<>();
        // 查询商品订单关联的申请仅退款的售后订单
        wrapper.lt("refundState", RefundOrderStateEnum.WAITING_FOR_RETURN.getCode());
        wrapper.eq("orderId", orderId);
        wrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

        List<RefundOrderEntity> refundOrderEntities = refundOrderDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(refundOrderEntities)) {
            return;
        }
        RefundOrderEntity refundOrderEntity = refundOrderEntities.get(0);

        RefundOrderEntity updateEntity = new RefundOrderEntity();
        updateEntity.cleanInit();
        updateEntity.setId(refundOrderEntity.getId());
        updateEntity.setRefundState(RefundOrderStateEnum.CLOSE.getCode());
        updateEntity.setProcessState(RefundProcessEnum.CANCEL.getCode());
        updateEntity.setRefuseDescription("退款被拒绝, 订单已发货！");
        updateEntity.setCancelTime(currentDate);
        updateEntity.setLastModifierId(memberId);
        updateEntity.setLastModifiedTime(currentDate);

        if (refundOrderDao.updateById(updateEntity) > 0) {
            EntityWrapper<RefundOrderLogEntity> logWrapper = new EntityWrapper<>();
            logWrapper.eq("refundOrderId", refundOrderEntity.getId());
            logWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

            RefundOrderLogEntity updateLogEntity = new RefundOrderLogEntity();
            updateLogEntity.cleanInit();
            updateLogEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
            refundOrderLogDao.update(updateLogEntity, logWrapper);
        }
    }


    /**
     * Description: 检查是否能够申请售后
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/13
     *
     * @param: isNewApply 是否为新申请的售后
     **/
    public Boolean checkOrderCanRefund(OrderEntity orderEntity, RefundSubmitDTO refundSubmitDTO, boolean isNewApply) {
        Integer orderState = orderEntity.getOrderState();
        Long orderId = orderEntity.getId();
        //1：状态校验
        if (!RefundOrderStateEnum.whetherApplyRefund(orderState)) {
            log.warn("----------The current status of the order:{} can not apply for sale!", orderId);
            return false;
        }

        //2：未发货 则整单退
        if (orderState.equals(OrderStatusEnum.PENDING_DELIVERY.getCode())) {
            // 未发货，且已在售后中，不允许再提交售后
            if (OrderConstants.OrderRefundStatus.REFUNDING.equals(orderEntity.getRefundState())) {
                log.warn("----------order:{} has not shiped and in refunding!", orderId);
                return false;
            }
            refundSubmitDTO.setOrderDetailIds(Collections.emptySet());
        }

        // 重新申请/修改申请  不判断订单中的商品能不能售后
        if (isNewApply) {
            //3：校验订单明细是否可以售后
            List<OrderDetailEntity> orderDetailEntities;
            if (CollectionUtils.isNotEmpty(refundSubmitDTO.getOrderDetailIds())) {
                orderDetailEntities = ordersQueryUtil.queryDetailsByDetailsIds(refundSubmitDTO.getOrderDetailIds());
            } else {
                orderDetailEntities = ordersQueryUtil.queryDetailsByOrderId(orderId);
            }

            for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
                if (!RefundOrderStateEnum.whetherRefundCanBeSubmit(orderDetailEntity.getRefundState())) {
                    log.warn("----------The current refund status of the orderdetail:{} can not apply for sale!", orderId);
                    return false;
                }
            }
        }

        //4：判断收货时间是否大于七天
        if (orderState.equals(OrderStatusEnum.HAVE_BEEN_RECEIVED.getCode())) {
            return Instant.now().getEpochSecond() - orderEntity.getReceiveTime().toInstant().getEpochSecond() <= Constants.Refund.ORDER_ALLOW_REFUND_MAX_SECONDS;
        }
        return true;
    }


    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Integer refundOrder(RefundOrderEntity refundOrderEntity, OrderEntity orderEntity, Date currentDate) {
        RefundParamsContext refundParamsContext = new RefundParamsContext();
        // 退款
        refundParamsContext.setRefundOrderEntity(refundOrderEntity);
        if (null == orderEntity) {
            return 0;
        }
        refundParamsContext.setOrderEntity(orderEntity);
        if (!refundMoneyExecuter.execute(refundParamsContext)) {
            return 0;
        }
        // 更新字段
        RefundOrderEntity refundUpdate = new RefundOrderEntity();
        refundUpdate.cleanInit();
        refundUpdate.setId(refundOrderEntity.getId());
        refundUpdate.setCallBackNo(refundParamsContext.getRefundOrderEntity().getCallBackNo());
        refundUpdate.setExpenditureTime(currentDate);
        refundUpdate.setProcessState(RefundProcessEnum.ALREADY.getCode());
        refundUpdate.setRefundState(RefundOrderStateEnum.REFUNDED.getCode());
        refundUpdate.setAuditTime(currentDate);
        refundUpdate.setCompleteTime(currentDate);
        refundUpdate.setLastModifierId(Constants.Order.TIME_TASK_USER_ID);
        refundUpdate.setLastModifiedTime(currentDate);
        if (refundOrderDao.updateById(refundUpdate) > 0) {
            Set<Long> orderDetailIds = Collections.emptySet();
            // 非整单退
            if (OrderConstants.OrderRefundStatus.ALL_REFUND_NO.equals(refundOrderEntity.getRefundRange())) {
                orderDetailIds = refundQueryUtil.queryDetailIdsByRefundId(refundOrderEntity.getId());
            }
            orderRefundStateUtil.updateRefundStateByRefundSuccess(orderEntity.getId(), orderDetailIds, Constants.Order.TIME_TASK_USER_ID);
            return 1;
        }
        return 0;
    }


}
