package com.topaiebiz.trade.order.core.pay.context;

import com.topaiebiz.trade.order.dto.pay.PayConfiguration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PayConfigContext {

    private static final ThreadLocal<PayConfiguration> context = new ThreadLocal<>();

    public static void set(PayConfiguration data) {
        context.set(data);
    }

    public static PayConfiguration get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}