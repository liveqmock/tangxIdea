package com.topaiebiz.trade.order.core.order.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CartMapContext {

    /**
     * skuId -> cartId map结构
     */
    private static final ThreadLocal<Map<Long,Long>> context = new ThreadLocal<>();

    public static void set(Map<Long,Long> cartIdMaps) {
        context.set(cartIdMaps);
    }

    public static Map<Long,Long> get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}