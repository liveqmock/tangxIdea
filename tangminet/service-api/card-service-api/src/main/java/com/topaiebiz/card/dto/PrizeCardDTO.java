package com.topaiebiz.card.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description: 作为奖品的礼卡
 * @author: Jeff Chen
 * @date: created in 上午9:13 2018/2/26
 */
public class PrizeCardDTO implements Serializable {

    /**
     * 名称
     */
    private String cardName;
    /**
     * 封面
     */
    private String cover;
    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 面值
     */
    private BigDecimal faceValue;
    /**
     * 售价
     */
    private BigDecimal salePrice;

    public String getCardName() {
        return cardName;
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

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
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

    @Override
    public String toString() {
        return "PrizeCardDTO{" +
                "cardName='" + cardName + '\'' +
                ", cover='" + cover + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", faceValue=" + faceValue +
                ", salePrice=" + salePrice +
                '}';
    }
}
