package com.topaiebiz.promotion.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 活动商品信息
 * Created by Joe on 2018/1/8.
 */
@Data
public class PromotionGoodsDTO implements Serializable {

    /**
     * 活动商品ID
     */
    private Long id;

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
    private Long repertoryNum;

    /**
     * 活动数量
     */
    private Long promotionNum;

    /**
     * 锁定数量
     */
    private Long lockedNum;

    /**
     * 活动价格
     */
    private BigDecimal promotionPrice;

    /**
     * ID限购
     */
    private Long confineNum;

    /**
     * 优惠类型
     */
    private Integer discountType;

    /**
     * 优惠值
     */
    private Double discountValue;

    /**
     * 优惠赠品
     */
    private Long giveawayGoods;

    /**
     * 平台补贴
     */
    private Double platformPrice;

    /**
     * 活动销量
     */
    private Integer quantitySales;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 备注
     */
    private String memo;

}
