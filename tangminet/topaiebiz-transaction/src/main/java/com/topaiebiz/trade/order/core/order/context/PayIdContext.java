package com.topaiebiz.trade.order.core.order.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***
 *  此类用于保存下单过程中生成的支付单ID
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PayIdContext {

    private static final ThreadLocal<Long> context = new ThreadLocal<>();

    public static void set(Long data) {
        context.set(data);
    }

    public static Long get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}