package com.topaiebiz.trade.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/17 9:12
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderCustomsResultDTO implements Serializable {
    private static final long serialVersionUID = -4591332109239699208L;
    /**
     * 报关途径
     * 微信/支付宝
     */
    private String reportWay;
    /**
     * 系统订单号
     */
    private Long mmgOrderId;

    /**
     * 系统报关ID
     */
    private String mmgReportId;

    /**
     * 第三方支付号
     */
    private String outTradeNo;
    /**
     * 支付宝报关号
     */
    private String alipayDeclareNo;
    /**
     * 支付宝报关结果code
     */
    private String alipayResultCode;
    /**
     * 支付宝报关结果描述
     */
    private String alipayResultDesc;
    /**
     * 微信结果code
     */
    private String wxResultCode;
    /**
     * 报关时间
     */
    private Date reportTime;
}