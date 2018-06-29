package com.topaiebiz.trade.order.core.check;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.topaiebiz.trade.order.dao.OrderDao;
import com.topaiebiz.trade.order.dao.OrderDetailDao;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Description 订单状态检查 基类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/1 12:30
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Component
public abstract class AbstractOrderChecker {

    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderDetailDao orderDetailDao;

    public abstract boolean check(OrderEntity orderEntity);

    /**
    *
    * Description: 检查状态时间是否超时
    *
    * Author: hxpeng
    * createTime: 2018/2/1
    *
    * @param:
    **/
    boolean checkTimeOut(Date startTime, Long timeOutSecond) {
        return null != startTime && Duration.between(startTime.toInstant(), Instant.now()).getSeconds() > timeOutSecond;
    }

    boolean updateOrderState(OrderEntity updateEntity){
        return orderDao.updateById(updateEntity) > 0;
    }

    void updateOrderDetailState(OrderDetailEntity orderDetailEntity, EntityWrapper<OrderDetailEntity> entityWrapper){
        orderDetailDao.update(orderDetailEntity, entityWrapper);
    }
}
