package com.topaiebiz.giftcard.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description: 支付成功后返回的订单信息
 * @author: Jeff Chen
 * @date: created in 上午9:44 2018/1/30
 */
public class OrderSuccVO implements Serializable {

    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 卡批次id
     */
    private Long batchId;
    /**
     * 卡名称
     */
    private String cardName;
    /**
     * 卡封面
     */
    private String cover;

    /**
     * 卡数量
     */
    private Integer cardNum;
    /**
     * 面值
     */
    private BigDecimal faceValue;
    /**
     * 售价
     */
    private BigDecimal salePrice;
    /**
     * 使用范围
     */
    private String scope;
    /**
     * 失效时间
     */
    private Date deadTime;
    /**
     * 是否可转赠：0否 1是
     */
    private Integer givenFlag;
    /**
     * 卡号列表
     */
    private List<String> cardList;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getCardName() {
        return cardName;
    }

    public Integer getCardNum() {
        return cardNum;
    }

    public void setCardNum(Integer cardNum) {
        this.cardNum = cardNum;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Date getDeadTime() {
        return deadTime;
    }

    public void setDeadTime(Date deadTime) {
        this.deadTime = deadTime;
    }

    public List<String> getCardList() {
        return cardList;
    }

    public void setCardList(List<String> cardList) {
        this.cardList = cardList;
    }

    public Integer getGivenFlag() {
        return givenFlag;
    }

    public void setGivenFlag(Integer givenFlag) {
        this.givenFlag = givenFlag;
    }
}
