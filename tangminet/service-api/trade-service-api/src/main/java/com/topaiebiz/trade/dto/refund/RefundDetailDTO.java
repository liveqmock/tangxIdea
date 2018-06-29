package com.topaiebiz.trade.dto.refund;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-03-28 11:26
 */
@Data
public class RefundDetailDTO implements Serializable {
    private static final long serialVersionUID = -1205579162039545734L;
    /**
     * 售后订单编号
     */
    private Long refundOrderId;

    /**
     * 支付订单明细ID
     */
    private Long orderDetailId;

    /**
     * 商品SKU ID
     */
    private Long goodSkuId;

    /**
     * 商品ID
     */
    private Long goodItemId;

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
     * 商品总价格
     */
    private BigDecimal goodTotalPrice;

    /**
     * 商品实际支付总价
     */
    private BigDecimal payPrice;
}
