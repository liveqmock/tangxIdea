package com.topaiebiz.transaction.order.merchant.dto;

import lombok.Data;

/**
 * Description 商家/平台 订单列表查询参数DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017/11/28 11:16
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderListParamsDto {

    /**
     * 订单ID
     **/
    private Long id;

    /**
     * 订单状态
     **/
    private Integer orderState;

    /**
     * 订单商品名称
     **/
    private String goodsName;

    /**
     * 所属店铺
     **/
    private Long belongStore;

    /**
     * 订单类型（1：平台，2：店铺）
     **/
    private Integer orderType;
}