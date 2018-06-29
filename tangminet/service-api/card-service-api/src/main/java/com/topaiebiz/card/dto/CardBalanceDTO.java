package com.topaiebiz.card.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description: 可用/不可用余额
 * @author: Jeff Chen
 * @date: created in 下午3:05 2018/1/23
 */
public class CardBalanceDTO implements Serializable{

    /**
     * 可用余额
     */
    private BigDecimal balance;

    /**
     * 不可用余额
     */
    private BigDecimal freezeBalance;

    public CardBalanceDTO() {
        this.balance = BigDecimal.ZERO;
        this.freezeBalance = BigDecimal.ZERO;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFreezeBalance() {
        return freezeBalance;
    }

    public void setFreezeBalance(BigDecimal freezeBalance) {
        this.freezeBalance = freezeBalance;
    }
}
