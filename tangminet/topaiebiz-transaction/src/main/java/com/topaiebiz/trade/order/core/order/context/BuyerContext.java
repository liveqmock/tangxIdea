package com.topaiebiz.trade.order.core.order.context;

import com.topaiebiz.trade.order.po.common.BuyerBO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BuyerContext {

    private static final ThreadLocal<BuyerBO> context = new ThreadLocal<>();

    public static void set(BuyerBO buyerBO) {
        context.set(buyerBO);
    }

    public static BuyerBO get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}