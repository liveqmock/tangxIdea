package com.topaiebiz.trade.order.dto.store;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/17 15:14
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
public class StoreOrderGoodsDTO implements Serializable {
    private static final long serialVersionUID = 7524459905658888324L;

    /**
     * sku id，商品货号，图片地址，商品名称，规格，单价，优惠金额，数量，总价，运费
     */
    private Long skuId;
    private String goodsSerial;
    private String goodsImage;
    private String name;
    private String fieldValue;
    private BigDecimal goodsPrice;
    private BigDecimal discount;
    private Integer goodsNum;
    private BigDecimal totalPrice;


    public StoreOrderGoodsDTO() {
        this.goodsPrice = BigDecimal.ZERO;
        this.goodsNum = 0;
        this.totalPrice = BigDecimal.ZERO;
    }
}
