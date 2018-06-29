package com.topaiebiz.trade.order.core.config;

import com.google.common.collect.Lists;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandlerChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/***
 * @author yfeng
 * @date 2018-01-09 10:25
 */
@Configuration
public class OrderSubmitConfig {

    public static final class OrderHandlerChain {
        public static final String PAGE_INIT = "pageInitChain";
        public static final String STORE_COUPON = "storeCouponChain";
        public static final String STORE_PROMOTION = "storePromotionChain";
        public static final String FREIGHT_PROMOTION = "freightPromotionChain";
        public static final String ORDER_AMOUNT = "orderAmountChain";
        public static final String PLATFORM_PROMOTION = "platformPromotionChain";
        public static final String ORDER_SUMMARY = "orderSummaryChain";
        public static final String ORDER_SUBMIT = "orderSubmitChain";
    }

    public static final String THREAD_POOL_BEAN = "tradeTaskExecutor";
    @Autowired
    private ApplicationContext springContext;

    private static List<String> pageInitHandlerNames = new ArrayList<>();
    private static List<String> storePromotionHandlerNames = new ArrayList<>();
    private static List<String> storeCouponHandlerNames = new ArrayList<>();
    private static List<String> freightPromotionHandlerNames = new ArrayList<>();
    private static List<String> orderAmountHandlerNames = new ArrayList<>();
    private static List<String> platformPromotionHandlerNames = new ArrayList<>();
    private static List<String> summaryHandlerNames = new ArrayList<>();
    private static List<String> orderSumbitHandlerNames = new ArrayList<>();

    static {
        //页面初始化handler列表
        pageInitHandlerNames.add("orderMemberLoadHandler");
        pageInitHandlerNames.add("cartTransformHandler");
        pageInitHandlerNames.add("storeLoadHandler");
        pageInitHandlerNames.add("goodsLoadHandler");
        pageInitHandlerNames.add("pageInitGoodsPromotionLoadHandler");
        pageInitHandlerNames.add("defaultAddressLoadHandler");
        pageInitHandlerNames.add("goodsFreightHandler");

        //店铺营销活动
        storePromotionHandlerNames.add("orderMemberLoadHandler");
        storePromotionHandlerNames.add("cartTransformHandler");
        storePromotionHandlerNames.add("storeLoadHandler");
        storePromotionHandlerNames.add("goodsLoadHandler");
        storePromotionHandlerNames.add("allPromotionLoadHandler");
        storePromotionHandlerNames.add("goodsPromotionValidateHandler");

        //店铺优惠券列表
        storeCouponHandlerNames.addAll(storePromotionHandlerNames);
        storeCouponHandlerNames.add("storePromotionValidateHandler");

        //店铺包邮活动列表handler列表
        freightPromotionHandlerNames.addAll(storePromotionHandlerNames);
        freightPromotionHandlerNames.add("defaultAddressLoadHandler");
        freightPromotionHandlerNames.add("goodsFreightHandler");
        freightPromotionHandlerNames.add("storePromotionValidateHandler");
        freightPromotionHandlerNames.add("storeCouponValidateHandler");
        freightPromotionHandlerNames.add("memberCouponValidateHandler");

        //店铺订单金额计算
        orderAmountHandlerNames.addAll(freightPromotionHandlerNames);
        orderAmountHandlerNames.add("freightPromotionValidateHandler");

        //平台可用活动列表handler列表
        platformPromotionHandlerNames.addAll(storePromotionHandlerNames);
        platformPromotionHandlerNames.add("addressLoadHandler");
        platformPromotionHandlerNames.add("goodsFreightHandler");
        platformPromotionHandlerNames.add("storePromotionValidateHandler");
        platformPromotionHandlerNames.add("storeCouponValidateHandler");
        platformPromotionHandlerNames.add("memberCouponValidateHandler");
        platformPromotionHandlerNames.add("freightPromotionValidateHandler");

        //订单摘要handler列表
        summaryHandlerNames.addAll(storePromotionHandlerNames);
        summaryHandlerNames.add("addressLoadHandler");
        summaryHandlerNames.add("goodsFreightHandler");
        summaryHandlerNames.add("storePromotionValidateHandler");
        summaryHandlerNames.add("storeCouponValidateHandler");
        summaryHandlerNames.add("memberCouponValidateHandler");
        summaryHandlerNames.add("freightPromotionValidateHandler");
        summaryHandlerNames.add("platformPromotionValidateHandler");

        //提交订单handler列表
        orderSumbitHandlerNames.addAll(storePromotionHandlerNames);
        orderSumbitHandlerNames.add("storeValidateHandler");
        orderSumbitHandlerNames.add("submitAddressValidateHandler");
        orderSumbitHandlerNames.add("haitaoValidateHandler");
        orderSumbitHandlerNames.add("goodsFreightValidateHandler");
        orderSumbitHandlerNames.add("storePromotionValidateHandler");
        orderSumbitHandlerNames.add("storeCouponValidateHandler");
        orderSumbitHandlerNames.add("memberCouponValidateHandler");
        orderSumbitHandlerNames.add("freightPromotionValidateHandler");
        orderSumbitHandlerNames.add("platformPromotionValidateHandler");
    }

    @Bean(name = THREAD_POOL_BEAN)
    public ExecutorService getTradeExecutors() {
        int corePoolSize = 2;
        int maxPoolSize = 5;
        long keepAlive = 1;
        CustomizableThreadFactory threadFactory = new CustomizableThreadFactory("Trade-task-");
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(100);
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAlive, TimeUnit.SECONDS, queue, threadFactory);
    }

    @Bean(name = OrderHandlerChain.PAGE_INIT)
    public OrderSubmitHandlerChain pageInitChain() {
        List<OrderSubmitHandler> handlers = loadHandlers(pageInitHandlerNames);
        return new OrderSubmitHandlerChain(handlers);
    }

    @Bean(name = OrderHandlerChain.STORE_PROMOTION)
    public OrderSubmitHandlerChain storePromotionChain() {
        List<OrderSubmitHandler> handlers = loadHandlers(storePromotionHandlerNames);
        return new OrderSubmitHandlerChain(handlers);
    }

    @Bean(name = OrderHandlerChain.STORE_COUPON)
    public OrderSubmitHandlerChain storeCouponChain() {
        List<OrderSubmitHandler> handlers = loadHandlers(storeCouponHandlerNames);
        return new OrderSubmitHandlerChain(handlers);
    }

    @Bean(name = OrderHandlerChain.FREIGHT_PROMOTION)
    public OrderSubmitHandlerChain freightPromotionChain() {
        List<OrderSubmitHandler> handlers = loadHandlers(freightPromotionHandlerNames);
        return new OrderSubmitHandlerChain(handlers);
    }

    @Bean(name = OrderHandlerChain.ORDER_AMOUNT)
    public OrderSubmitHandlerChain orderAmountChain() {
        List<OrderSubmitHandler> handlers = loadHandlers(orderAmountHandlerNames);
        return new OrderSubmitHandlerChain(handlers);
    }

    @Bean(name = OrderHandlerChain.PLATFORM_PROMOTION)
    public OrderSubmitHandlerChain platformPromotionChain() {
        List<OrderSubmitHandler> handlers = loadHandlers(platformPromotionHandlerNames);
        return new OrderSubmitHandlerChain(handlers);
    }

    @Bean(name = OrderHandlerChain.ORDER_SUMMARY)
    public OrderSubmitHandlerChain orderSummaryPromotionChain() {
        List<OrderSubmitHandler> handlers = loadHandlers(summaryHandlerNames);
        return new OrderSubmitHandlerChain(handlers);
    }

    @Bean(name = OrderHandlerChain.ORDER_SUBMIT)
    public OrderSubmitHandlerChain orderSubmitPromotionChain() {
        List<OrderSubmitHandler> handlers = loadHandlers(orderSumbitHandlerNames);
        return new OrderSubmitHandlerChain(handlers);
    }

    private List<OrderSubmitHandler> loadHandlers(List<String> handlerNames) {
        handlerNames = handlerNames.stream().distinct().collect(Collectors.toList());
        List<OrderSubmitHandler> handlers = Lists.newArrayList();
        for (String handlerName : handlerNames) {
            OrderSubmitHandler handler = springContext.getBean(handlerName, OrderSubmitHandler.class);
            handlers.add(handler);
        }
        return handlers;
    }
}