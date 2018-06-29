package com.topaiebiz.trade.order.util;

import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.dto.PromotionGoodsDTO;
import com.topaiebiz.promotion.promotionEnum.PromotionCondTypeEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-01-30 13:38
 */
public class PromotionUtil {

    public static String beautifulText(BigDecimal input) {
        return input.stripTrailingZeros().toPlainString();
    }

    /**
     * 针对店铺营销活动组装活动名称
     *
     * @return
     */
    public static String buildPromotionName(PromotionDTO promotionDTO) {
        Assert.notNull(promotionDTO, "promotion is null");
        PromotionTypeEnum type = promotionDTO.getType();

        if (PromotionTypeEnum.PROMOTION_TYPE_REDUCE_PRICE == type) {
            String discount = beautifulText(promotionDTO.getDiscountValue());
            String condValue = beautifulText(promotionDTO.getCondValue());
            //满减
            PromotionCondTypeEnum condType = PromotionCondTypeEnum.valueOf(promotionDTO.getCondType());
            return String.format("%s%s元减%s元", condType.getName(), condValue, discount);
        } else if (PromotionTypeEnum.PROMOTION_TYPE_STORE_COUPON == type || PromotionTypeEnum.PROMOTION_TYPE_COUPON_CODE == type) {
            String discount = beautifulText(promotionDTO.getDiscountValue());
            String condValue = beautifulText(promotionDTO.getCondValue());
            //优惠券
            return String.format("满%s元优惠%s元", condValue, discount);
        } else if (PromotionTypeEnum.PROMOTION_TYPE_FREE_SHIPPING == type) {
            String condValue = beautifulText(promotionDTO.getCondValue());
            //满包邮
            return String.format("满%s元包邮", condValue);
        } else {
            return promotionDTO.getName();
        }
    }


    public static PromotionGoodsDTO findPromotionGoods(PromotionDTO promotionDTO, Long skuId) {
        if (promotionDTO == null || CollectionUtils.isEmpty(promotionDTO.getLimitGoods())) {
            return null;
        }
        for (PromotionGoodsDTO promotionGoods : promotionDTO.getLimitGoods()) {
            if (skuId.equals(promotionGoods.getGoodsSkuId())) {
                return promotionGoods;
            }
        }
        return null;
    }
}
