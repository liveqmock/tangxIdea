package com.topaiebiz.card.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 礼卡退款单信息
 * @author: Jeff Chen
 * @date: created in 上午11:05 2018/1/9
 */
public class RefundOrderDTO implements Serializable {
    private static final long serialVersionUID = -6654620880194294770L;
    /**
     * 消费的用户id
     */
    private Long memberId;
    /**
     * 用户手机号
     */
    private String memberPhone;
    /**
     * 用户名称
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
     * 退款单号
     */
    private String orderNo;
    /**
     * 退款的卡列表
     */
    private List<PayCard> payCardList;

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public List<PayCard> getPayCardList() {
        return payCardList;
    }

    public void setPayCardList(List<PayCard> payCardList) {
        this.payCardList = payCardList;
    }

    @Override
    public String toString() {
        return "RefundOrderDTO{" +
                "memberId=" + memberId +
                ", memberPhone='" + memberPhone + '\'' +
                ", memberName='" + memberName + '\'' +
                ", totalAmount=" + totalAmount +
                ", payPwd='" + payPwd + '\'' +
                ", paySn='" + paySn + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", payCardList=" + payCardList +
                '}';
    }
}
