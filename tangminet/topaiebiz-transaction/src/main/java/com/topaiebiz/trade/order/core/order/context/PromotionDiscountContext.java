package com.topaiebiz.trade.order.core.order.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

/***
 *  此类用于记录单品活动、店铺活动、包邮活动、平台活动的优惠幅度，用于在计算可选优惠活动列表中排序作
 *  见 PromotionComparator 中的排序实现
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PromotionDiscountContext {

    private static final ThreadLocal<Map<Long,BigDecimal>> context = new ThreadLocal<Map<Long,BigDecimal>>(){
        @Override
        protected Map<Long,BigDecimal> initialValue() {
            return new HashMap();
        }
    };

    public static void set(Map<Long,BigDecimal> data) {
        context.set(data);
    }

    public static Map<Long,BigDecimal> get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}