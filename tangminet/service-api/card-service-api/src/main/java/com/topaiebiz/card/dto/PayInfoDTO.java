package com.topaiebiz.card.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 用卡的支付信息
 * @author: Jeff Chen
 * @date: created in 上午10:28 2018/1/9
 */
public class PayInfoDTO implements Serializable {
    private static final long serialVersionUID = -4817912240281349258L;

    /**
     * 消费的用户id
     */
    private Long memberId;
    /**
     * 用户手机号
     */
    private String memberPhone;
    /**
     * 用户名
     */
    private String memberName;
    /**
     * 礼卡消费的总金额
     */
    private BigDecimal totalAmount;
    /**
     * 支付密码
     */
    private String payPwd;
    /**
     * 支付单号
     */
    private String paySn;
    /**
     * 子订单列表
     */
    private List<PaySubOrder> subOrderList;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberPhone() {
        return memberPhone;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPayPwd() {
        return payPwd;
    }

    public void setPayPwd(String payPwd) {
        this.payPwd = payPwd;
    }

    public String getPaySn() {
        return paySn;
    }

    public void setPaySn(String paySn) {
        this.paySn = paySn;
    }

    public List<PaySubOrder> getSubOrderList() {
        return subOrderList;
    }

    public void setSubOrderList(List<PaySubOrder> subOrderList) {
        this.subOrderList = subOrderList;
    }

    @Override
    public String toString() {
        return "PayInfoDTO{" +
                "memberId=" + memberId +
                ", memberPhone='" + memberPhone + '\'' +
                ", memberName='" + memberName + '\'' +
                ", totalAmount=" + totalAmount +
                ", payPwd='" + payPwd + '\'' +
                ", paySn='" + paySn + '\'' +
                ", subOrderList=" + subOrderList +
                '}';
    }
}
