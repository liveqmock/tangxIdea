package com.topaiebiz.giftcard.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 我的消费记录
 * @author: Jeff Chen
 * @date: created in 下午2:06 2018/1/24
 */
public class MyGiftcardLogVO  implements Serializable{

    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 变动金额
     */
    private BigDecimal amount;

    /**
     * 记录时间
     */
    private Date createdTime;

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

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
