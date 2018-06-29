package com.topaiebiz.trade.refund.dto.state;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * Description 修改支付订单的售后状态 参数DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/2 14:42
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderRefundStateDTO implements Serializable {

    private static final long serialVersionUID = 8901171822776385385L;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单明细ID集合
     */
    private Set<Long> orderDetailIds;

    /**
     * 是否需要同时更新订单明细的售后状态
     */
    private Boolean updateDetails = false;

    /**
     * 支付订单的售后状态
     */
    private Integer orderRefundState;

    /**
     * 是否需要更新订单的锁属性
     */
    private boolean needLock = false;
}
