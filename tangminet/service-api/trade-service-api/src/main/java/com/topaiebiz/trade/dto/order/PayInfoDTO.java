package com.topaiebiz.trade.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description 订单支付相关信息DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/17 11:41
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class PayInfoDTO implements Serializable{

    private static final long serialVersionUID = -8174108003069148550L;

    /**
     * 订单支付 ID
     */
    private Long payId;

    /**
     * 实际支付价格
     */
    private BigDecimal payPrice;

    /**
     * 支付状态
     */
    private Integer payState;

}
