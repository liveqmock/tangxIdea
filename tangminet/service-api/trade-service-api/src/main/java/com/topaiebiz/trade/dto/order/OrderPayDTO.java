package com.topaiebiz.trade.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description 支付信息
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/18 16:05
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderPayDTO implements Serializable {

    private static final long serialVersionUID = 8416354248449973007L;

    /**
     * payId
     */
    private Long id;

    /**
     * 支付金额
     */
    private BigDecimal payPrice;

    /**
     * 支付状态
     */
    private Integer payState;

    /**
     * 支付类型
     */
    private String payType;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 第三方支付流水号
     */
    private String outerPaySn;

    /**
     * 美礼卡金额
     */
    private BigDecimal cardPrice;

    /**
     * 使用积分
     */
    private Long scoreNum;

    /**
     * 积分抵扣金额
     */
    private BigDecimal scorePrice;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 第三方支付金额
     */
    private BigDecimal thirdPaymentAmount;


    public void setThirdPaymentAmount(BigDecimal thirdPaymentAmount) {
        if (BigDecimal.ZERO.compareTo(thirdPaymentAmount) >= 1){
            this.thirdPaymentAmount = BigDecimal.ZERO;
        }else{
            this.thirdPaymentAmount = thirdPaymentAmount;
        }
    }
}
