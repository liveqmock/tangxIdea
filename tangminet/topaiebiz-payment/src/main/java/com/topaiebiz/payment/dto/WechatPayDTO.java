package com.topaiebiz.payment.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/10 21:13
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class WechatPayDTO implements Serializable{


    private static final long serialVersionUID = -2851112570141971342L;

    /**
     * 微信支付报文
     */
    private Map<String, String> resultMap;

    /**
     * 是否已支付
     */
    private Boolean hasBeenPay;

    /**
     * 订单商品类型
     * card / good
     */
    private String goodType;

}
