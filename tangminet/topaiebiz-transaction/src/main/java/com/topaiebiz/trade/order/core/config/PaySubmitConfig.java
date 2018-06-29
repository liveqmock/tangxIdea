package com.topaiebiz.trade.order.core.config;

import com.google.common.collect.Lists;
import com.topaiebiz.trade.order.core.pay.handler.PayContextHandler;
import com.topaiebiz.trade.order.core.pay.handler.PayContextHandlerChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-17 22:04
 */
@Configuration
public class PaySubmitConfig {
    public static final class PayHandlerChain {
        public static final String SUMMARY_CHAIN = "summaryChain";
        public static final String SUBMIT_CHAIN = "submitChain";


    }

    @Autowired
    private ApplicationContext springContext;

    private static List<String> summaryHandlerNames = new ArrayList<>();
    private static List<String> sumbitHandlerNames = new ArrayList<>();

    static {
        //页面初始化handler列表
        summaryHandlerNames.add("memberLoadHandler");
        summaryHandlerNames.add("payConfigLoadHandler");
        summaryHandlerNames.add("orderLoadHandler");
        summaryHandlerNames.add("pkgSummaryHandler");
        summaryHandlerNames.add("assetLoadHandler");
        summaryHandlerNames.add("cardLoadHandler");
        summaryHandlerNames.add("pwdStatusHandler");

        //提交站内支付handler列表
        sumbitHandlerNames.add("memberLoadHandler");
        sumbitHandlerNames.add("orderLoadHandler");
        sumbitHandlerNames.add("amountValidateHandler");
        sumbitHandlerNames.add("pwdValidateHandler");
        sumbitHandlerNames.add("payConfigLoadHandler");
        sumbitHandlerNames.add("assetLoadHandler");
        sumbitHandlerNames.add("cardDispatchHandler");
        sumbitHandlerNames.add("scoreDispatchHandler");
        sumbitHandlerNames.add("balanceDispatchHandler");
    }

    @Bean(name = PayHandlerChain.SUMMARY_CHAIN)
    public PayContextHandlerChain summaryChain() {
        List<PayContextHandler> handlers = loadHandlers(summaryHandlerNames);
        return new PayContextHandlerChain(handlers);
    }

    @Bean(name = PayHandlerChain.SUBMIT_CHAIN)
    public PayContextHandlerChain submitChain() {
        List<PayContextHandler> handlers = loadHandlers(sumbitHandlerNames);
        return new PayContextHandlerChain(handlers);
    }

    private List<PayContextHandler> loadHandlers(List<String> handlerNames) {
        List<PayContextHandler> handlers = Lists.newArrayList();
        for (String handlerName : handlerNames) {
            PayContextHandler handler = springContext.getBean(handlerName, PayContextHandler.class);
            handlers.add(handler);
        }
        return handlers;
    }
}