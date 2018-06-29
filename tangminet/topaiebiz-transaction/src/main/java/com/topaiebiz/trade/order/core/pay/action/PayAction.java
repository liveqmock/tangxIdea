package com.topaiebiz.trade.order.core.pay.action;

import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;

/***
 * @author yfeng
 * @date 2018-01-09 10:09
 */
public interface PayAction {

    boolean action(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest);

    void rollback(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest);
}
