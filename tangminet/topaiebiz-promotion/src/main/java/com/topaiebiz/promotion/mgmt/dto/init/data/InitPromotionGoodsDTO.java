package com.topaiebiz.promotion.mgmt.dto.init.data;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 初始化
 */
@Data
public class InitPromotionGoodsDTO {

    /**
     * 营销活动
     */
    private Long promotionId;

    /**
     * 所属店铺
     */
    private Long storeId;

    /**
     * 所属商品
     */
    private Long itemId;

    /**
     * 商品SKU
     */
    private Long goodsSkuId;

    /**
     * 原有库存
     */
    private Integer repertoryNum;

    /**
     * 活动数量
     */
    private Integer promotionNum;

    /**
     * 活动价格
     */
    private BigDecimal promotionPrice;

    /**
     * ID限购
     */
    private Integer confineNum;

    /**
     * 优惠类型
     */
    private Integer discountType;

    /**
     * 优惠值
     */
    private BigDecimal discountValue;

    /**
     * 状态
     */
    private Integer state;
}
