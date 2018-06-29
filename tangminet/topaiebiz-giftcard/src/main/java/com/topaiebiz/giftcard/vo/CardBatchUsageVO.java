package com.topaiebiz.giftcard.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 卡批次使用情况
 * @author: Jeff Chen
 * @date: created in 上午11:17 2018/3/21
 */
public class CardBatchUsageVO implements Serializable{

    /**
     * 发行数量
     */
    private Integer issueNum;
    /**
     * 卡号区间
     */
    private String cardNoSpan;

    /**
     * 激活数量
     */
    private Integer activeNum;
    /**
     * 绑定数量
     */
    private Integer boundNum;
    /**
     * 冻结数量
     */
    private Integer freezedNum;
    /**
     * 用完数量
     */
    private Integer useOutNum;
    /**
     * 过期数量
     */
    private Integer expiredNum;

    public CardBatchUsageVO() {
        this.activeNum = 0;
        this.issueNum = 0;
        this.boundNum = 0;
        this.cardNoSpan = "";
        this.expiredNum = 0;
        this.freezedNum = 0;
        this.useOutNum = 0;
    }

    public Integer getActiveNum() {
        return activeNum;
    }

    public void setActiveNum(Integer activeNum) {
        this.activeNum = activeNum;
    }

    public Integer getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(Integer issueNum) {
        this.issueNum = issueNum;
    }

    public String getCardNoSpan() {
        return cardNoSpan;
    }

    public void setCardNoSpan(String cardNoSpan) {
        this.cardNoSpan = cardNoSpan;
    }

    public Integer getBoundNum() {
        return boundNum;
    }

    public void setBoundNum(Integer boundNum) {
        this.boundNum = boundNum;
    }

    public Integer getFreezedNum() {
        return freezedNum;
    }

    public void setFreezedNum(Integer freezedNum) {
        this.freezedNum = freezedNum;
    }

    public Integer getUseOutNum() {
        return useOutNum;
    }

    public void setUseOutNum(Integer useOutNum) {
        this.useOutNum = useOutNum;
    }

    public Integer getExpiredNum() {
        return expiredNum;
    }

    public void setExpiredNum(Integer expiredNum) {
        this.expiredNum = expiredNum;
    }
}
