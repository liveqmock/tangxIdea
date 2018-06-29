package com.topaiebiz.trade.order.dto.customer;

import lombok.Data;

import java.io.Serializable;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/7 14:59
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class EvaluateGoodDTO implements Serializable{

    private static final long serialVersionUID = 5342506098334548826L;

    /**
     * 订单明细ID
     */
    private Long orderDetailId;

    /**
     * 商品ITEM id
     */
    private Long itemId;

    /**
     * 商品SKU id
     */
    private Long skuId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品图片
     */
    private String goodsImage;

}
