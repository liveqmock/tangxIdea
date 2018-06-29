package com.topaiebiz.trade.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description 订单商品
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/12 17:56
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderGoodsDTO implements Serializable {

    private static final long serialVersionUID = -1968903117661461340L;

    /**
     * 订单明细ID， 商品名称， 商品图片， 商品属性， 商品货号， itemCode
     */
    private Long id;
    private Long orderId;
    private String name;
    private String goodsImage;
    private String fieldValue;
    private String goodsSerial;
    private String itemCode;

    /**
     * 商品ID, skuId, 商品数量
     */
    private Long itemId;
    private Long skuId;
    private Integer goodsNum;

    /**
     * 商品单价， 商品总价， 商品实际单价， 优惠金额
     */
    private BigDecimal goodsPrice;
    private BigDecimal totalPrice;
    private BigDecimal payPrice;
    private BigDecimal discount;

    /**
     * 售后状态， 备注
     */
    private Integer refundState;
    private String memo;

    /**
     * 物流公司ID， 物流公司名称， 物流编号
     */
    private Long expressComId;
    private String expressComName;
    private String expressNo;


    public OrderGoodsDTO() {
        this.goodsNum = 0;
        this.goodsPrice = BigDecimal.ZERO;
        this.totalPrice = BigDecimal.ZERO;
        this.payPrice = BigDecimal.ZERO;
        this.discount = BigDecimal.ZERO;
    }
}