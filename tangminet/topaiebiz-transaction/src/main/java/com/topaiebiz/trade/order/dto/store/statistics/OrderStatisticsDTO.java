package com.topaiebiz.trade.order.dto.store.statistics;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/13 10:00
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderStatisticsDTO implements Serializable {

    private static final long serialVersionUID = 3039486791619085924L;

    /**
     * 全部订单
     */
    private Integer allOrderCounts = 0;

    /**
     * 未支付订单数
     */
    private Integer unPayOrderCounts = 0;

    /**
     * 未发货订单数
     */
    private Integer unShipOrderCounts = 0;

    /**
     * 未收货订单数
     */
    private Integer unReceivedOrderCounts = 0;



}
