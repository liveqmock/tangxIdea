package com.topaiebiz.trade.order.dto.store.statistics;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/13 12:18
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MemberOrderCountDTO implements Serializable{

    private static final long serialVersionUID = 9199987815950253641L;

    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 订单数量
     */
    private Integer orderCount;

    /**
     * 订单总金额
     */
    private BigDecimal orderTotalAmount;

}
