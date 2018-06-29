package com.topaiebiz.trade.order.core.cancel.handler;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.topaiebiz.trade.order.core.cancel.CancelParamContext;
import com.topaiebiz.trade.order.dao.OrderDao;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/***
 * @author yfeng
 * @date 2018-01-21 19:05
 */
@Order(2)
@Component("orderCancelLoadHandler")
public class OrderLoadHandler extends AbstractHandler {
    @Autowired
    private OrderDao orderDao;

    @Override
    public void handle(BuyerBO buyerBO, Long payId, CancelParamContext context) {
        //step 1 : 查询店铺订单
        OrderEntity cond = new OrderEntity();
        cond.cleanInit();
        cond.setMemberId(buyerBO.getMemberId());
        cond.setPayId(payId);
        List<OrderEntity> orders = orderDao.selectList(new EntityWrapper<>(cond));

        //step 2 : 保存到上下文
        List<Long> orderIds = orders.stream().map(item -> item.getId()).collect(Collectors.toList());
        context.setOrderIds(orderIds);
        context.setOrders(orders);
    }
}