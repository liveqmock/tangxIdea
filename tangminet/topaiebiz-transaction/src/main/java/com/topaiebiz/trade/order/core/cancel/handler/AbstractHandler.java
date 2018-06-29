package com.topaiebiz.trade.order.core.cancel.handler;

import com.topaiebiz.trade.order.core.cancel.CancelParamContext;
import com.topaiebiz.trade.order.po.common.BuyerBO;

/***
 * @author yfeng
 * @date 2018-01-21 22:08
 */
public abstract class AbstractHandler {
   public abstract void handle(BuyerBO buyerBO, Long payId, CancelParamContext context);
}
