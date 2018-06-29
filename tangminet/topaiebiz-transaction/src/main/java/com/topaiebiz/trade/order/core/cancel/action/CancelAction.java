package com.topaiebiz.trade.order.core.cancel.action;

import com.topaiebiz.trade.order.core.cancel.CancelParamContext;
import com.topaiebiz.trade.order.po.common.BuyerBO;

/***
 * @author yfeng
 * @date 2018-01-21 22:05
 */
public interface CancelAction {
    boolean action(BuyerBO buyerBO, CancelParamContext context);
}
