package com.topaiebiz.trade.order.api.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.api.OrderTaskServiceApi;
import com.topaiebiz.trade.order.core.cancel.OrderCancelChain;
import com.topaiebiz.trade.order.dao.OrderDao;
import com.topaiebiz.trade.order.dao.OrderDetailDao;
import com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.util.OrdersQueryUtil;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/***
 * @author yfeng
 * @date 2018-01-09 14:59
 */
@Slf4j
@Service
public class OrderTaskServiceApiImpl implements OrderTaskServiceApi {

    @Autowired
    private OrdersQueryUtil ordersQueryUtil;

    @Autowired
    private OrderCancelChain orderCancelChain;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private DistLockSservice distLockSservice;

    @Autowired
    private MessageSender messageSender;

    @Override
    public void cancelUnPayOrder() {
        log.info(">>>>>>>>>>Timing tasks: auto cancel unpay order start!!!!");
        List<OrderEntity> orderEntities;
        do {
            orderEntities = ordersQueryUtil.queryUnPayOrder();
            if (CollectionUtils.isEmpty(orderEntities)) {
                break;
            }
            this.doCancelUnPayOrder(orderEntities);
        } while (true);
        log.info(">>>>>>>>>>Timing tasks: auto cancel unpay order finish!!!!");
    }

    private void doCancelUnPayOrder(List<OrderEntity> orderEntities) {
        if (CollectionUtils.isEmpty(orderEntities)) {
            return;
        }
        int size = orderEntities.size();
        int result = 0;
        LockResult orderLock = null;
        for (OrderEntity orderEntity : orderEntities) {
            try {
                orderLock = distLockSservice.tryLock(Constants.LockOperatons.TRADE_ORDER_PAY_, orderEntity.getId());
                if (!orderLock.isSuccess()) {
                    throw new GlobalException(OrderSubmitExceptionEnum.ORDER_CANCEL_ERROR);
                }
                BuyerBO buyerBO = new BuyerBO();
                buyerBO.setMemberId(orderEntity.getMemberId());
                buyerBO.setMemberName(orderEntity.getMemberName());
                result += orderCancelChain.cancel(buyerBO, orderEntity.getPayId()) ? 1 : 0;
            } catch (Exception e) {
                log.error(">>>>>>>>>>cancel order:{} fail, err info:{}", orderEntity.getId(), e);
            } finally {
                distLockSservice.unlock(orderLock);
            }
        }
        log.info("----------query data rows:{}, operation successed rows:{}, fail rows:{} ！", size, result, size - result);
    }

    @Override
    public void receivingOrder() {
        log.info(">>>>>>>>>>Timing tasks: auto receive order start!!!!");
        List<OrderEntity> orderEntities;
        do {
            orderEntities = ordersQueryUtil.queryReceiveOrder();
            if (CollectionUtils.isEmpty(orderEntities)) {
                break;
            }
            this.doReceivingOrder(orderEntities);
        } while (true);
        log.info(">>>>>>>>>>Timing tasks: auto receive order finish!!!!");
    }

    private void doReceivingOrder(List<OrderEntity> orderEntities) {
        if (CollectionUtils.isEmpty(orderEntities)) {
            return;
        }
        Date currentDate = new Date();
        int size = orderEntities.size();
        List<Long> orderIds = orderEntities.stream().map(OrderEntity::getId).collect(Collectors.toList());
        log.info(">>>>>>>>>>query data rows:{} ！", size);

        //1：更新订单状态
        EntityWrapper<OrderEntity> orderWrapper = new EntityWrapper<>();
        orderWrapper.in("id", orderIds);
        orderWrapper.eq("orderState", OrderStatusEnum.PENDING_RECEIVED.getCode());
        orderWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

        OrderEntity updateEntity = new OrderEntity();
        updateEntity.cleanInit();
        updateEntity.setOrderState(OrderStatusEnum.HAVE_BEEN_RECEIVED.getCode());
        updateEntity.setReceiveTime(currentDate);
        updateEntity.setLastModifierId(Constants.Order.TIME_TASK_USER_ID);
        updateEntity.setLastModifiedTime(currentDate);

        int orderRows = orderDao.update(updateEntity, orderWrapper);
        if (orderRows > 0) {
            //2：更新订单明细状态
            EntityWrapper<OrderDetailEntity> orderDetailWrapper = new EntityWrapper<>();
            orderDetailWrapper.in("orderId", orderIds);
            orderDetailWrapper.eq("orderState", OrderStatusEnum.PENDING_RECEIVED.getCode());
            orderDetailWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

            OrderDetailEntity orderDetailUpdate = new OrderDetailEntity();
            orderDetailUpdate.cleanInit();
            orderDetailUpdate.setOrderState(OrderStatusEnum.HAVE_BEEN_RECEIVED.getCode());
            orderDetailUpdate.setReceiveTime(currentDate);
            orderDetailUpdate.setLastModifierId(Constants.Order.TIME_TASK_USER_ID);
            orderDetailUpdate.setLastModifiedTime(currentDate);
            int orderDetailRows = orderDetailDao.update(orderDetailUpdate, orderDetailWrapper);
            if (orderDetailRows > 0) {
                log.info(">>>>>>>>>>operation success, order has {} rows updated, order detail has {} rows updated", orderRows, orderDetailRows);
                return;
            } else {
                log.error(">>>>>>>>>>operation fail, order detail no rows has been update!");
            }
        }
        log.error(">>>>>>>>>>print operation params: {}!", JSON.toJSONString(orderIds));
    }


    @Override
    public void completeOrders() {
        log.info(">>>>>>>>>>Timing tasks: auto complete order start!!!!");
        List<OrderEntity> orderEntities;
        do {
            orderEntities = ordersQueryUtil.queryCompleteOrder();
            if (CollectionUtils.isEmpty(orderEntities)) {
                break;
            }
            this.doCompleteOrders(orderEntities);
        } while (true);
        log.info(">>>>>>>>>>Timing tasks: auto complete order finish!!!!");
    }

    private void doCompleteOrders(List<OrderEntity> orderEntities) {
        if (CollectionUtils.isEmpty(orderEntities)) {
            return;
        }
        int size = orderEntities.size();
        List<Long> orderIds = orderEntities.stream().map(OrderEntity::getId).collect(Collectors.toList());
        log.info(">>>>>>>>>>query data rows:{} ！", size);

        //1：更新订单状态
        EntityWrapper<OrderEntity> orderWrapper = new EntityWrapper<>();
        orderWrapper.in("id", orderIds);
        orderWrapper.eq("lockState", Constants.OrderLockFlag.LOCK_NO);
        orderWrapper.eq("orderState", OrderStatusEnum.HAVE_BEEN_RECEIVED.getCode());
        orderWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

        Date currentDate = new Date();
        OrderEntity updateEntity = new OrderEntity();
        updateEntity.cleanInit();
        updateEntity.setOrderState(OrderStatusEnum.ORDER_COMPLETION.getCode());
        updateEntity.setCompleteTime(currentDate);
        updateEntity.setLastModifiedTime(currentDate);
        updateEntity.setLastModifierId(Constants.Order.TIME_TASK_USER_ID);

        int orderRows = orderDao.update(updateEntity, orderWrapper);
        if (orderRows > 0) {
            //2：更新订单明细状态
            EntityWrapper<OrderDetailEntity> orderDetailWrapper = new EntityWrapper<>();
            orderDetailWrapper.in("orderId", orderIds);
            orderDetailWrapper.eq("orderState", OrderStatusEnum.HAVE_BEEN_RECEIVED.getCode());
            orderDetailWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

            OrderDetailEntity orderDetailUpdate = new OrderDetailEntity();
            orderDetailUpdate.cleanInit();
            orderDetailUpdate.setOrderState(OrderStatusEnum.ORDER_COMPLETION.getCode());
            orderDetailUpdate.setLastModifiedTime(currentDate);
            orderDetailUpdate.setLastModifierId(Constants.Order.TIME_TASK_USER_ID);
            int orderDetailRows = orderDetailDao.update(orderDetailUpdate, orderDetailWrapper);
            if (orderDetailRows > 0) {
                log.info(">>>>>>>>>>operation success, order has {} rows updated, order detail has {} rows updated", orderRows, orderDetailRows);
                return;
            } else {
                log.error(">>>>>>>>>>operation fail, order detail no rows has been update!");
            }
            // 发起订单完成通知
            this.notifications(MessageTypeEnum.ORDER_COMPLETE, orderIds);
        }
        log.error(">>>>>>>>>>print operation params: {}!", JSON.toJSONString(orderIds));
    }


    @Override
    public void automaticPraise() {

    }


    /**
     * Description:订单 已完成/已关闭 发起通知
     *
     * @Author: hxpeng
     * createTime: 2018/5/31
     * @param:
     **/
    private void notifications(MessageTypeEnum messageTypeEnum, List<Long> orderIds) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return;
        }
        for (Long orderId : orderIds) {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setMemberId(Constants.Order.TIME_TASK_USER_ID);
            messageDTO.setType(messageTypeEnum);
            messageDTO.getParams().put("orderId", orderId);
            messageSender.publicMessage(messageDTO);
        }
    }
}
