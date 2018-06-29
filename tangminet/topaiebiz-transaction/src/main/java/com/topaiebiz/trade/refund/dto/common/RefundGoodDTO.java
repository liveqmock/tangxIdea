package com.topaiebiz.trade.refund.dto.common;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Description 售后的商品集合
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/9 12:30
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
public class RefundGoodDTO {

    /**
     * 支付订单明细ID
     */
    private Long orderDetailId;

    /**
     * 商品ID
     */
    private Long itemId;

    /**
     * 商品SKU ID
     */
    private Long goodSkuId;

    /**
     * 商品名称
     */
    private String goodName;

    /**
     * 商品属性
     */
    private String goodFileValue;

    /**
     * 商品图片路径
     */
    private String goodImgUrl;

    /**
     * 商品数量
     */
    private Integer goodNum;

    /**
     * 商品原总价格
     */
    private BigDecimal goodTotalPrice;

    /**
     * 商品实际支付总价
     */
    private BigDecimal payPrice;

}
