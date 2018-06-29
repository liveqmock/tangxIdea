package com.topaiebiz.card.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 礼卡精简信息
 * @author: Jeff Chen
 * @date: created in 上午9:49 2018/1/9
 */
public class BriefCardDTO implements Serializable {
    private static final long serialVersionUID = 6912861981597044501L;

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 卡片名称
     */
    private String cardName;
    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 适用范围，参考ApplyScopeEnum
     */
    private Integer applyScope;

    /**
     * 店铺id：applyScope=1时为空，applyScope=2时可用店铺id，applyScope=3时不可用店铺id
     */
    private List<Long> storeIds;

    /**
     * applyScope = 4 适用的商品列表 v0.3
     */
    private List<Long> goodsIds;

    /**
     * 从1-10结算优先级逐级提升，即优先级高的美礼卡先结算
     */
    private Integer priority;

    /**
     * 到期时间戳
     */
    private Integer expiredTime;

    /**
     * 发卡时间戳
     */
    private Integer issuedTime;


    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getApplyScope() {
        return applyScope;
    }

    public void setApplyScope(Integer applyScope) {
        this.applyScope = applyScope;
    }

    public List<Long> getStoreIds() {
        return storeIds;
    }

    public void setStoreIds(List<Long> storeIds) {
        this.storeIds = storeIds;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Integer expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Integer getIssuedTime() {
        return issuedTime;
    }

    public void setIssuedTime(Integer issuedTime) {
        this.issuedTime = issuedTime;
    }

    public List<Long> getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(List<Long> goodsIds) {
        this.goodsIds = goodsIds;
    }

    @Override
    public String toString() {
        return "BriefCardDTO{" +
                "cardNo='" + cardNo + '\'' +
                ", cardName='" + cardName + '\'' +
                ", amount=" + amount +
                ", applyScope=" + applyScope +
                ", storeIds=" + storeIds +
                ", goodsIds=" + goodsIds +
                ", priority=" + priority +
                ", expiredTime=" + expiredTime +
                ", issuedTime=" + issuedTime +
                '}';
    }
}
