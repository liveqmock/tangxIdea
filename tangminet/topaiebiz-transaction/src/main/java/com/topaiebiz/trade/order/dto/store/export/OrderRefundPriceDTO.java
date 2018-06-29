package com.topaiebiz.trade.order.dto.store.export;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/8 19:35
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderRefundPriceDTO implements Serializable{

    private static final long serialVersionUID = 1941286687837488990L;

    private Long orderId;

    private BigDecimal refundPrice;
}
