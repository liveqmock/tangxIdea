package com.topaiebiz.settlement.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class GoodsDetailDTO implements Serializable {
    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品原单价
     */
    private BigDecimal goodsPrice;

    /**
     * 商品数量
     */
    private Long goodsNum;

    /**
     * 平台补贴
     */
    private BigDecimal promPlatformAmount;

    /**
     * 店铺补贴
     */
    private BigDecimal promStoreAmount;

    /**
     * 支付金额
     */
    private BigDecimal payPrice;

    /** 佣金比例。小数形式。平台收取商家的佣金。*/
    private BigDecimal brokerageRatio;
}
