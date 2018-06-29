package com.topaiebiz.goods.sku.dto;

import com.topaiebiz.promotion.dto.PromotionDTO;
import lombok.Data;

/**
 * Created by dell on 2018/1/25.
 */
@Data
public class StoreCouponDTO extends PromotionDTO {
    /**
     * 是否持有此优惠券
     */
    private boolean hasHold;
}
