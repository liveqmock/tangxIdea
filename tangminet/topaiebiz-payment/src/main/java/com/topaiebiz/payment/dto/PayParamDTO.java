package com.topaiebiz.payment.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description 支付参数DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/17 12:28
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class PayParamDTO implements Serializable {

    private static final long serialVersionUID = -8909875792689970682L;

    private String sessionId;

    /**
     * 待支付订单号
     */
    @NotNull(message = "#{validation.pay.params.orderPayId}")
    private Long orderPayId;

    /**
     * 待支付订单类型 good：商品， card：美礼卡
     */
    @NotNull(message = "#{validation.pay.params.orderType}")
    private String orderType;

    /**
     * 支付者ip
     */
    private String ip;
}
