package com.topaiebiz.settlement.dto;

import lombok.Data;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-03-28 10:28
 */
@Data
public class GoodsCommissionDetailDTO {
    /**
     * 商品SKU ID
     */
    private Long skuId;
    /**
     * 商品原始总价
     */
    private BigDecimal goodsTotal;
    /**
     * 支付金额
     */
    private BigDecimal payPrice;
    /**
     * 平台补贴
     */
    private BigDecimal promPlatformAmount;
    /**
     * 店铺补贴
     */
    private BigDecimal promStoreAmount;

    /**
     * 佣金金额
     */
    private BigDecimal commissionAmount;
}