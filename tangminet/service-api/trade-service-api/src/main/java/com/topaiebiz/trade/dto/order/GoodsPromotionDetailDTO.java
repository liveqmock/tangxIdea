package com.topaiebiz.trade.dto.order;

import lombok.Data;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-01-17 17:36
 */
@Data
public class GoodsPromotionDetailDTO {
    /**
     * 运费优惠
     */
    private BigDecimal freightDiscount = BigDecimal.ZERO;

    /**
     * 单品优惠总额
     */
    private BigDecimal goodsDiscount = BigDecimal.ZERO;
    /**
     * 店铺优惠总额
     */
    private BigDecimal storeDiscount = BigDecimal.ZERO;
    /**
     * 平台优惠总额
     */
    private BigDecimal platformDiscount = BigDecimal.ZERO;
    /**
     * 优惠总额
     */
    protected BigDecimal totalPromotion = BigDecimal.ZERO;
}
