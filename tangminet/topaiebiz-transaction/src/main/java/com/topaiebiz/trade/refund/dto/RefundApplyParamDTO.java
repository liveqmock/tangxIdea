package com.topaiebiz.trade.refund.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/17 16:56
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class RefundApplyParamDTO implements Serializable {

    private static final long serialVersionUID = 2353218059485088444L;

    /**
     * 待售后的订单明细Ids 集合
     */
    private Set<Long> orderDetailIds;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 售后ID
     */
    private Long refundId;
}
