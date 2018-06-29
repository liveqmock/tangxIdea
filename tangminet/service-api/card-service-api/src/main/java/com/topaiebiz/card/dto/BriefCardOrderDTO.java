package com.topaiebiz.card.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description: 礼卡订单精简信息
 * @author: Jeff Chen
 * @date: created in 下午4:06 2018/1/29
 */
public class BriefCardOrderDTO implements Serializable{

    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 支付金额
     */
    private BigDecimal payAmount;
    /**
     * 订单状态 CardOrderStatusEnum
     */
    private Integer orderStatus;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return "BriefCardOrderDTO{" +
                "orderId=" + orderId +
                ", payAmount=" + payAmount +
                ", orderStatus=" + orderStatus +
                '}';
    }
}
