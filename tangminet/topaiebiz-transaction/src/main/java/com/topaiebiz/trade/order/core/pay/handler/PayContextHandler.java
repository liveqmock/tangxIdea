package com.topaiebiz.trade.order.core.pay.handler;

import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;

/***
 * @author yfeng
 * @date 2017-03-26 14:13
 */
public interface PayContextHandler {

    void prepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest);

}