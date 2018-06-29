package com.topaiebiz.pay.dto.refund;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description 退款参数DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/19 11:01
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class RefundParamDTO implements Serializable{

    private static final long serialVersionUID = 2546271599319580195L;

    /**
     * 支付订单号
     * (和payCallbackNo 不能同时为空， 二选一 或者 都填)
     */
    private String payId;

    /**
     * 第三方支付流水号
     * (和orderId 不能同时为空， 二选一 或者 都填)
     */
    private String payCallbackNo;

    /**
     * 订单类型， payment模块 Constants.java 类
     * (仅：good或card)
     */
    private String orderType;

    /**
     * 售后订单号
     * (不能为空)
     */
    private String refundOrderId;

    /**
     * 订单支付金额
     * (不能为空)
     */
    private BigDecimal payPrice;

    /**
     * 支付方式， payment模块 Constants.java 类
     * (仅：alipay或wxpay)
     */
    private String payType;

    /**
     * 售后订单金额
     * (不能为空)
     */
    private BigDecimal refundPrice;

    /**
     * 售后原因
     * (可为空)
     */
    private String refundReason;

}
