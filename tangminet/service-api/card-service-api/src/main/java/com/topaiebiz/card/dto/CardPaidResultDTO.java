package com.topaiebiz.card.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description: 礼卡订单支付结果
 * @author: Jeff Chen
 * @date: created in 下午4:00 2018/1/29
 */
public class CardPaidResultDTO implements Serializable{

    /**
     * 礼卡订单号
     */
    private Long orderId;

    /**
     * wxpay微信 alipay支付宝
     */
    private String payCode;

    /**
     * 第三方单号
     */
    private String paySn;

    /**
     * 支付总额
     */
    private BigDecimal payAmount;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public String getPaySn() {
        return paySn;
    }

    public void setPaySn(String paySn) {
        this.paySn = paySn;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    @Override
    public String toString() {
        return "CardPaidResultDTO{" +
                "orderId=" + orderId +
                ", payCode='" + payCode + '\'' +
                ", paySn='" + paySn + '\'' +
                ", payAmount=" + payAmount +
                '}';
    }
}
