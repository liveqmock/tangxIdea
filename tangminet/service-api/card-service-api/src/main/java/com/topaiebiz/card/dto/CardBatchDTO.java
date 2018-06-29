package com.topaiebiz.card.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description: 卡批次信息
 * @author: Jeff Chen
 * @date: created in 上午11:09 2018/2/12
 */
public class CardBatchDTO implements Serializable{

    /**
     * 批次id
     */
    private Long batchId;
    /**
     * 封面
     */
    private String cover;
    /**
     * 卡名称
     */
    private String cardName;
    /**
     * 面值
     */
    private BigDecimal faceValue;
    /**
     * 售价
     */
    private BigDecimal salePrice;
    /**
     * 库存
     */
    private Integer qty;

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
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

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "CardBatchDTO{" +
                "batchId=" + batchId +
                ", cover='" + cover + '\'' +
                ", cardName='" + cardName + '\'' +
                ", faceValue=" + faceValue +
                ", salePrice=" + salePrice +
                ", qty=" + qty +
                '}';
    }
}
