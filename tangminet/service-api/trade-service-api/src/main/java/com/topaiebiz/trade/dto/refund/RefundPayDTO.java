package com.topaiebiz.trade.dto.refund;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description 支付模块--售后DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/17 11:22
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class RefundPayDTO implements Serializable {

    private static final long serialVersionUID = -7789296308598453213L;

    /**
     * 售后订单ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 退款金额
     */
    private BigDecimal refundPrice;

    /**
     * 原支付订单的金额
     */
    private BigDecimal orderPrice;


}
