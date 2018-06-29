package com.topaiebiz.pay.dto;

import com.nebulapaas.base.enumdata.PayMethodEnum;
import lombok.Data;
import java.io.Serializable;
/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/15 15:14
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class ReportCustomsDTO implements Serializable{
    private static final long serialVersionUID = 219455938325300563L;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 支付方式
     */
    private PayMethodEnum payMethodEnum;
/////////////////下面是支付宝的报关业务参数
    /**
     * 报关号
     */
    private String reportId;
    private String thirdTradeNo;
    private String amount;
    private String buyerName;
    private String buyerIdNo;
    /////////////////下面是微信的报关业务参数
    private String outTradeNo;
    private String transactionId;
}