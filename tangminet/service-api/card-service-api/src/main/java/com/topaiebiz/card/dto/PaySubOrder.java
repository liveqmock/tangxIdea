package com.topaiebiz.card.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 支付子订单信息
 * @author: Jeff Chen
 * @date: created in 上午9:49 2018/1/9
 */
public class PaySubOrder implements Serializable{

	private static final long serialVersionUID = 3923616415088898028L;

    /**
     * 店铺id
     */
	private Long storeId;
    /**
     * 店铺名称
     */
	private String storeName;
    /**
     * 订单金额
     */
	private BigDecimal amount;
    /**
     * 单号
     */
	private String orderSn;
    /**
     * 订单使用的卡列表
     */
	private  List<PayCard>   cardList;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getOrderSn() {
		return orderSn;
	}

	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}

    public List<PayCard> getCardList() {
        return cardList;
    }

    public void setCardList(List<PayCard> cardList) {
        this.cardList = cardList;
    }

    @Override
    public String toString() {
        return "PaySubOrder{" +
                "storeId=" + storeId +
                ", storeName='" + storeName + '\'' +
                ", amount=" + amount +
                ", orderSn='" + orderSn + '\'' +
                ", cardList=" + cardList +
                '}';
    }
}
