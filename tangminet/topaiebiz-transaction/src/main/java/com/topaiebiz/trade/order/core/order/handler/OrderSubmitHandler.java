package com.topaiebiz.trade.order.core.order.handler;

import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;

/***
 * @author yfeng
 * @date 2018-01-09 10:14
 */
public interface OrderSubmitHandler {

    void handle(OrderSubmitContext submitContext, OrderRequest orderRequest);
}