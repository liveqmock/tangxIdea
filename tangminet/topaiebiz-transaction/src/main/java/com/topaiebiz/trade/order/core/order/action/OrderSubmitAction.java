package com.topaiebiz.trade.order.core.order.action;

import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;

/***
 * @author yfeng
 * @date 2018-01-09 10:09
 */
public interface OrderSubmitAction {

    boolean action(BuyerBO buyer, OrderSubmitContext paramContext, OrderRequest orderRequest);

    void rollback(BuyerBO buyer, OrderSubmitContext paramContext, OrderRequest orderRequest);
}
