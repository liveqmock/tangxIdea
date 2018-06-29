package com.topaiebiz.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/23 10:55
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@NoArgsConstructor
public class AlipayParamsDTO implements Serializable {

    private static final long serialVersionUID = -2567540180813491822L;

    private String orderSubject;

    private String payId;

    private BigDecimal orderPrice;

    private String productCode;

    public AlipayParamsDTO(String orderSubject, String payId, BigDecimal orderPrice) {
        this.orderSubject = orderSubject;
        this.payId = payId;
        this.orderPrice = orderPrice;
        this.productCode = "QUICK_MSECURITY_PAY";
    }
}