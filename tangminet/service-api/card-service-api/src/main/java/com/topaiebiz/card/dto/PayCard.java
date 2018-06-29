package com.topaiebiz.card.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description: 支付的卡信息
 * @author: Jeff Chen
 * @date: created in 上午10:41 2018/1/9
 */
public class PayCard implements Serializable{
    private static final long serialVersionUID = 3713086599866005072L;

    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 变动的金额
     */
    private BigDecimal amount;
    /**
     * 支付的商品id
     */
    private Long goodsId;
    /**
     * 支付的商品名称
     */
    private String goodsName;

    /**
     * 店铺id
     */
    private Long storeId;
    /**
     * 店铺名称
     */
    private String storeName;

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public String toString() {
        return "PayCard{" +
                "cardNo='" + cardNo + '\'' +
                ", amount=" + amount +
                ", goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", storeId=" + storeId +
                ", storeName='" + storeName + '\'' +
                '}';
    }
}
