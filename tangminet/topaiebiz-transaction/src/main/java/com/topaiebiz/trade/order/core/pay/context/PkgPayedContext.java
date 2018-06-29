package com.topaiebiz.trade.order.core.pay.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PkgPayedContext {

    private static final ThreadLocal<Boolean> context = new ThreadLocal<>();

    public static void set(Boolean data) {
        context.set(data);
    }

    public static Boolean get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}