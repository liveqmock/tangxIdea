package com.topaiebiz.trade.dto.pay;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Description 订单商品支付明细
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/18 21:01
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class GoodPayDTO implements Serializable {

    private static final long serialVersionUID = -5963471026577917623L;

    /**
     * 美礼卡抵扣金额
     */
    private BigDecimal cardPrice = BigDecimal.ZERO;

    /**
     * 积分抵扣金额
     */
    private BigDecimal scorePrice = BigDecimal.ZERO;

    /**
     * 余额抵扣金额
     */
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * cardNo -> amount
     **/
    private Map<String, BigDecimal> cardDetail = new HashMap();
}
