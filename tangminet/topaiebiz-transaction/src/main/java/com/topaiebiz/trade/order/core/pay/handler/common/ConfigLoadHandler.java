package com.topaiebiz.trade.order.core.pay.handler.common;

import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.context.PayConfigContext;
import com.topaiebiz.trade.order.core.pay.handler.PayContextHandler;
import com.topaiebiz.trade.order.dto.pay.PayConfiguration;
import com.topaiebiz.trade.order.facade.ConfigServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-19 11:22
 */
@Component("payConfigLoadHandler")
public class ConfigLoadHandler  implements PayContextHandler {

    @Autowired
    private ConfigServiceFacade configServiceFacade;

    @Override
    public void prepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        PayConfiguration payConfig = configServiceFacade.getPayConfig();
        PayConfigContext.set(payConfig);
    }
}