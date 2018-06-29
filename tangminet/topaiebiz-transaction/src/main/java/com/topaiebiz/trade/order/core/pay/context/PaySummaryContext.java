package com.topaiebiz.trade.order.core.pay.context;

import com.topaiebiz.trade.order.dto.pay.PaySummaryDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/***
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaySummaryContext {

    private static final ThreadLocal<PaySummaryDTO> context = new ThreadLocal<>();

    public static void set(PaySummaryDTO data) {
        context.set(data);
    }

    public static PaySummaryDTO get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}