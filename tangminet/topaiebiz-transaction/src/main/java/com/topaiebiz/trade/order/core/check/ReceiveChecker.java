package com.topaiebiz.trade.order.core.check;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Description 未收货超时自动收货
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/1 12:35
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class ReceiveChecker extends AbstractOrderChecker {

    @Override
    public boolean check(OrderEntity orderEntity) {
        boolean timeCheck = super.checkTimeOut(orderEntity.getShipmentTime(), Constants.Order.SHIP_AUDIT_RECEIVE_SECONDS);
        if (timeCheck){
            // 修改订单状态
            Long orderId = orderEntity.getId();
            Date currentDate = new Date();
            OrderEntity orderUpdate = new OrderEntity();
            orderUpdate.cleanInit();
            orderUpdate.setId(orderId);
            orderUpdate.setOrderState(OrderStatusEnum.HAVE_BEEN_RECEIVED.getCode());
            orderUpdate.setReceiveTime(currentDate);
            orderUpdate.setLastModifiedTime(currentDate);
            boolean result = super.updateOrderState(orderUpdate);
            if (result){
                log.info("----------The order:{} overtime Not received, has been automatically received", orderId);

                // 更新订单明细状态
                EntityWrapper<OrderDetailEntity> orderDetailEntityWrapper = new EntityWrapper<>();
                orderDetailEntityWrapper.eq("orderId", orderId);
                orderDetailEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

                OrderDetailEntity orderDetailUpdate = new OrderDetailEntity();
                orderDetailUpdate.cleanInit();
                orderDetailUpdate.setOrderState(OrderStatusEnum.HAVE_BEEN_RECEIVED.getCode());
                orderDetailUpdate.setReceiveTime(currentDate);
                orderDetailUpdate.setLastModifiedTime(currentDate);

                super.updateOrderDetailState(orderDetailUpdate, orderDetailEntityWrapper);
            }
        }
        return timeCheck;
    }
}
