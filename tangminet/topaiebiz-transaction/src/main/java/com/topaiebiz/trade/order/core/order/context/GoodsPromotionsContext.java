package com.topaiebiz.trade.order.core.order.context;

import com.topaiebiz.promotion.dto.PromotionDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-09 19:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GoodsPromotionsContext {

    private static final ThreadLocal<Map<Long, List<PromotionDTO>>> context = new ThreadLocal<>();

    public static void set(Map<Long, List<PromotionDTO>> datas) {
        context.set(datas);
    }

    public static Map<Long, List<PromotionDTO>> get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}