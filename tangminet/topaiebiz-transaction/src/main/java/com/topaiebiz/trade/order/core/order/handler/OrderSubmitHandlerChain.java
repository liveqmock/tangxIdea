package com.topaiebiz.trade.order.core.order.handler;

import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-09 10:25
 */
@Slf4j
public class OrderSubmitHandlerChain {

    private List<OrderSubmitHandler> handlers;

    public OrderSubmitHandlerChain(List<OrderSubmitHandler> handlers) {
        this.handlers = handlers;
    }

    public OrderSubmitContext process(OrderRequest orderRequest) {
        OrderSubmitContext orderSubmitContext = new OrderSubmitContext();
        for (OrderSubmitHandler handler : handlers) {
            log.info(">>>>>>>>>>>>>>>>>>>>>>>> {}", handler.getClass().getName());
            handler.handle(orderSubmitContext, orderRequest);
        }
        return orderSubmitContext;
    }
}