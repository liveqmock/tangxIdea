package com.topaiebiz.trade.refund.helper;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.order.dao.OrderDao;
import com.topaiebiz.trade.order.dao.OrderDetailDao;
import com.topaiebiz.trade.order.util.OrdersQueryUtil;
import com.topaiebiz.trade.refund.entity.RefundOrderDetailEntity;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description 订单售后状态 修改类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/2 14:05
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class OrderRefundStateUtil {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private OrdersQueryUtil ordersQueryUtil;

    @Autowired
    private RefundQueryUtil refundQueryUtil;

    @Autowired
    private MessageSender messageSender;


    /**
     * Description: 创建售后订单时， 修改订单的售后状态
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/28
     *
     * @param:
     **/
    public boolean updateRefundStateByRefundCreate(Long orderId, Set<Long> orderDetailIds, Long memberId) {
        log.info(">>>>>>>>>>create refund order, update order refundState, params:[ orderId:{}, orderDetailIds:{}, memberId:{}]", orderId, JSON.toJSONString(orderDetailIds), memberId);
        Date currentDate = new Date();

        EntityWrapper<OrderDetailEntity> orderDetailWrapper = new EntityWrapper<>();
        orderDetailWrapper.lt("refundState", OrderConstants.OrderRefundStatus.REFUNDING);
        orderDetailWrapper.eq("orderId", orderId);
        if (CollectionUtils.isNotEmpty(orderDetailIds)) {
            orderDetailWrapper.in("id", orderDetailIds);
        }
        OrderDetailEntity orderDetailUpdate = new OrderDetailEntity();
        orderDetailUpdate.cleanInit();
        orderDetailUpdate.setRefundState(OrderConstants.OrderRefundStatus.REFUNDING);
        orderDetailUpdate.setLastModifierId(memberId);
        orderDetailUpdate.setLastModifiedTime(currentDate);
        int orderDetailRows = orderDetailDao.update(orderDetailUpdate, orderDetailWrapper);
        if (orderDetailRows > 0) {
            log.info(">>>>>>>>>>create refund order, update {} order:{} details's refundState to refunding!", orderDetailRows, orderId);
        } else {
            return false;
        }

        EntityWrapper<OrderEntity> orderWrapper = new EntityWrapper<>();
        orderWrapper.eq("id", orderId);
        orderWrapper.lt("refundState", OrderConstants.OrderRefundStatus.REFUNDING);

        OrderEntity orderUpdate = new OrderEntity();
        orderUpdate.cleanInit();
        orderUpdate.setRefundState(OrderConstants.OrderRefundStatus.REFUNDING);
        orderUpdate.setLastModifierId(memberId);
        orderUpdate.setLastModifiedTime(currentDate);
        if (orderDao.update(orderUpdate, orderWrapper) > 0) {
            log.info(">>>>>>>>>>create refund order, update order:{} refundState to refunding!", orderId);
        } else {
            return false;
        }
        return true;
    }


    /**
     * Description: 取消售后/售后超时关闭/平台拒绝 的情况下， 修改订单的 refundState
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/24
     *
     * @param: platformRefuse : 是否平台拒绝
     **/
    public void updateRefundStateByRefundClose(RefundOrderEntity refundOrderEntity, Long memberId, boolean platformRefuse) {
        if (null == refundOrderEntity) {
            return;
        }
        log.info(">>>>>>>>>>refund cancel, update order's refundState, params:{}", JSON.toJSONString(refundOrderEntity));
        boolean isAllRefund = OrderConstants.OrderRefundStatus.ALL_REFUND_YES.equals(refundOrderEntity.getRefundRange());
        Long orderId = refundOrderEntity.getOrderId();
        Date currentDate = new Date();

        EntityWrapper<OrderDetailEntity> orderDetailWrapper = new EntityWrapper<>();
        OrderDetailEntity orderDetailUpdate = new OrderDetailEntity();
        orderDetailUpdate.cleanInit();
        orderDetailUpdate.setLastModifierId(memberId);
        orderDetailUpdate.setLastModifiedTime(currentDate);
        orderDetailUpdate.setRefundState(platformRefuse ? OrderConstants.OrderRefundStatus.PLATFORM_REFUSED : OrderConstants.OrderRefundStatus.NO_REFUND);

        try {
            if (isAllRefund) {
                orderDetailWrapper.eq("orderId", orderId);
            } else {
                // 售后中申请的订单商品明细ID集合
                List<Long> refundOrderDetailIds = refundQueryUtil.queryDetailsByRefundId(refundOrderEntity.getId()).stream().map(RefundOrderDetailEntity::getOrderDetailId).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(refundOrderDetailIds)) {
                    return;
                }
                orderDetailWrapper.in("id", refundOrderDetailIds);
            }
            int orderDetailRows = orderDetailDao.update(orderDetailUpdate, orderDetailWrapper);
            if (orderDetailRows <= 0) {
                log.error(">>>>>>>>>>no orderDetail's refundState has been update for refund:{} be closed!", refundOrderEntity.getId());
                return;
            }
            log.info(">>>>>>>>>>update {} orderDetail's refundState success, sql params:{}", orderDetailRows, JSON.toJSONString(orderDetailWrapper));

            // 非整单退的售后关闭， 检查订单中是否还有在售后中的商品，有则不修改订单的refundState
            if (!isAllRefund && ordersQueryUtil.hasOrderDetailInRefunding(orderId)) {
                return;
            }

            OrderEntity orderUpdate = new OrderEntity();
            orderUpdate.cleanInit();
            orderUpdate.setId(orderId);
            orderUpdate.setLockState(OrderConstants.OrderLockStatus.NO_LOCK);
            orderUpdate.setRefundState(OrderConstants.OrderRefundStatus.NO_REFUND);
            orderUpdate.setLastModifierId(memberId);
            orderUpdate.setLastModifiedTime(currentDate);
            if (orderDao.updateById(orderUpdate) > 0) {
                log.info(">>>>>>>>>>update {} orderDetail's refundState success, sql params:{}", orderDetailRows, JSON.toJSONString(orderDetailWrapper));
            }
        } catch (Exception e) {
            log.warn(">>>>>>>>>>updateRefundStateByRefundClose executer fail, refundOrderEntity:{}, err:{}", JSON.toJSONString(refundOrderEntity), e);
            throw e;
        }
    }


    /**
     * Description: 退款成功的时候，修改订单明细的售后状态
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/28
     *
     * @param:
     **/
    public void updateRefundStateByRefundSuccess(Long orderId, Set<Long> orderDetailIds, Long memberId) {
        if (null == orderId) {
            return;
        }
        log.info(">>>>>>>>>>refund success, update order refundState to refunded, params:[ orderId:{}, orderDetailIds:{}, memberId:{}]", orderId, JSON.toJSONString(orderDetailIds), memberId);
        Date currentDate = new Date();
        boolean isAllRefund = true;


        EntityWrapper<OrderDetailEntity> orderDetailWrapper = new EntityWrapper<>();
        orderDetailWrapper.lt("refundState", OrderConstants.OrderRefundStatus.REFUND);
        orderDetailWrapper.eq("orderId", orderId);
        if (CollectionUtils.isNotEmpty(orderDetailIds)) {
            isAllRefund = false;
            orderDetailWrapper.in("id", orderDetailIds);
        }
        OrderDetailEntity orderDetailUpdate = new OrderDetailEntity();
        orderDetailUpdate.cleanInit();
        orderDetailUpdate.setRefundState(OrderConstants.OrderRefundStatus.REFUND);
        orderDetailUpdate.setLastModifierId(memberId);
        orderDetailUpdate.setLastModifiedTime(currentDate);
        int orderDetailRows = orderDetailDao.update(orderDetailUpdate, orderDetailWrapper);
        if (orderDetailRows > 0) {
            log.info(">>>>>>>>>>refund success, update {} order:{} details's refundState to refunding!", orderDetailRows, orderId);
        }

        // 修改订单的售后状态
        OrderEntity orderUpdate = new OrderEntity();
        orderUpdate.cleanInit();
        orderUpdate.setId(orderId);
        orderUpdate.setLastModifierId(memberId);
        orderUpdate.setLastModifiedTime(currentDate);

        // 整单退 订单直接修改为已关闭状态
        if (isAllRefund) {
            orderUpdate.setOrderState(OrderStatusEnum.ORDER_CLOSE.getCode());
            orderUpdate.setRefundState(OrderConstants.OrderRefundStatus.REFUND);
            // 发起通知
            this.notifications(MessageTypeEnum.ORDER_CLOSE, orderId, memberId);
        } else {
            // 非整单退则表示发货后的退款， 循环判断是否都退款，退款则修改订单状态为已完成，还可以判断是否有其他商品在售后中
            isAllRefund = true;

            List<OrderDetailEntity> orderDetailEntities = ordersQueryUtil.queryDetailsByOrderId(orderId);
            for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
                Integer refundState = orderDetailEntity.getRefundState();
                if (OrderConstants.OrderRefundStatus.REFUNDING.equals(refundState)) {
                    // 仍然有订单明细在售后中, 直接结束, 不修改订单的状态
                    return;
                }
                if (!OrderConstants.OrderRefundStatus.REFUND.equals(refundState)) {
                    // 仍有订单明细 没有被退款， 则订单状态不会变成已完成
                    isAllRefund = false;
                }
            }
            if (isAllRefund) {
                orderUpdate.setOrderState(OrderStatusEnum.ORDER_COMPLETION.getCode());
                orderUpdate.setCompleteTime(currentDate);
                orderUpdate.setRefundState(OrderConstants.OrderRefundStatus.REFUND);
                // 发起通知
                this.notifications(MessageTypeEnum.ORDER_COMPLETE, orderId, memberId);
            } else {
                // 仍有订单明细 没有被退款 且 没有商品在售后中
                orderUpdate.setRefundState(OrderConstants.OrderRefundStatus.NO_REFUND);
            }
        }
        if (orderDao.updateById(orderUpdate) > 0) {
            log.info(">>>>>>>>>>refund success, update order info success by ID, params:{}!", JSON.toJSONString(orderUpdate));
        }
    }

    /**
     * Description:订单 已完成/已关闭 发起通知
     *
     * @Author: hxpeng
     * createTime: 2018/5/31
     * @param:
     **/
    private void notifications(MessageTypeEnum messageTypeEnum, Long orderId, Long memberId) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMemberId(memberId);
        messageDTO.setType(messageTypeEnum);
        messageDTO.getParams().put("orderId", orderId);
        messageSender.publicMessage(messageDTO);
    }
}
