package com.topaiebiz.trade.order.core.order.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CartIdContext {

    private static final ThreadLocal<List<Long>> context = new ThreadLocal<>();

    public static void set(List<Long> cartIds) {
        context.set(cartIds);
    }

    public static List<Long> get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}