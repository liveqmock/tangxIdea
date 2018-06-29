package com.topaiebiz.trade.order.dto.member;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description 会员管理--会员购买记录 DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/11 15:00
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MemberOrderPayDTO implements Serializable{

    private static final long serialVersionUID = -3553203281316157692L;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单下单时间
     */
    private Date orderTime;

    /**
     * 订单完成时间
     */
    private Date completeTime;

    /**
     * 支付类型
     */
    private String payType;

    /**
     * 订单消费金额
     */
    private BigDecimal orderTotal;

    /**
     * 实际支付金额
     */
    private BigDecimal payPrice;

}
