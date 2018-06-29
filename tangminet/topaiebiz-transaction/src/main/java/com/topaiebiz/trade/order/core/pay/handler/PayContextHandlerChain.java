package com.topaiebiz.trade.order.core.pay.handler;

import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import lombok.AllArgsConstructor;

import java.util.List;

/***
 * @author yfeng
 * @date 2017-03-26 14:17
 */
@AllArgsConstructor
public class PayContextHandlerChain {

    private List<PayContextHandler> handlers;

    public PayParamContext prepareParamContext(BuyerBO buyer, PayRequest payRequest) {
        PayParamContext paramContext = new PayParamContext();
        for (PayContextHandler handler : handlers) {
            handler.prepare(buyer, paramContext, payRequest);
        }
        return paramContext;
    }
}