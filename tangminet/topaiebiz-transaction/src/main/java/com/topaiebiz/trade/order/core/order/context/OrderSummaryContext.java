package com.topaiebiz.trade.order.core.order.context;

import com.topaiebiz.trade.order.dto.ordersubmit.OrderSummaryDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderSummaryContext {

    private static final ThreadLocal<OrderSummaryDTO> context = new ThreadLocal<>();

    public static void set(OrderSummaryDTO datas) {
        context.set(datas);
    }

    public static OrderSummaryDTO get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}