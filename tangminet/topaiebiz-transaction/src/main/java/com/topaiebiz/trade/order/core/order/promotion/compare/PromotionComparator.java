package com.topaiebiz.trade.order.core.order.promotion.compare;

import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.trade.order.core.order.context.PromotionDiscountContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-11 15:39
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PromotionComparator implements Comparator<PromotionDTO> {
    private static PromotionComparator instance = new PromotionComparator();

    public static PromotionComparator getInstance() {
        return instance;
    }

    @Override
    public int compare(PromotionDTO o1, PromotionDTO o2) {
        Map<Long, BigDecimal> discountMap = PromotionDiscountContext.get();
        BigDecimal o1Discount = discountMap.get(o1.getId());
        BigDecimal o2Discount = discountMap.get(o2.getId());

        //按照优惠幅度降序排列
        if (o1Discount == null || o2Discount == null){
            return 0;
        }
        return o2Discount.compareTo(o1Discount);
    }
}