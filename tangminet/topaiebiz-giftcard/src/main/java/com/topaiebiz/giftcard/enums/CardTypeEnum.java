package com.topaiebiz.giftcard.enums;

/**
 * @description: 卡的类型
 * @author: Jeff Chen
 * @date: created in 上午10:41 2017/12/23
 */
public enum CardTypeEnum {

    BRAND_CARD(1, "品牌卡"),
    FREE_CARD(2, "自由卡"),
    STORE_CARD(3, "联名卡");

    private Integer cardId;
    private String cardName;

    CardTypeEnum(Integer cardId, String cardName) {
        this.cardId = cardId;
        this.cardName = cardName;
    }

    public Integer getCardId() {
        return cardId;
    }

    public String getCardName() {
        return cardName;
    }
}
