package com.topaiebiz.trade.order.core.pay.action;

import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.exception.PaymentExceptionEnum;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.util.OrdersQueryUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-20 17:12
 */
@Component
public class PayActionChain implements InitializingBean {

    @Autowired
    private CardAction cardAction;

    @Autowired
    private ScoreAction scoreAction;

    @Autowired
    private OrderAction orderAction;

    private List<PayAction> actions;

    @Override
    public void afterPropertiesSet() throws Exception {
        actions = new ArrayList<>();
        actions.add(scoreAction);
        actions.add(cardAction);
        actions.add(orderAction);
    }

    public boolean action(BuyerBO buyerBO, PayParamContext payParamContext, PayRequest payRequest) {
        boolean hasFailOperation = false;
        List<PayAction> successComponents = new ArrayList<>();

        for (PayAction submitComponent : actions) {
            boolean succss = submitComponent.action(buyerBO, payParamContext, payRequest);
            if (succss) {
                successComponents.add(submitComponent);
            } else {
                hasFailOperation = true;
                break;
            }
        }

        if (hasFailOperation) {
            throw new GlobalException(PaymentExceptionEnum.PAY_FAIL_ERROR);
        }
        return payParamContext.isPkgFull();
    }
}