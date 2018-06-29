package com.topaiebiz.giftcard.enums;

/**
 * @description: 卡介质枚举
 * @author: Jeff Chen
 * @date: created in 下午3:34 2017/12/18
 */
public enum CardMediumEnum {

    //介质id继承老系统的
    SOLID_CARD(0,"实体卡"),
    ELECT_CARD(4, "电子卡");

    private int mediumId;
    private String mediumName;

    CardMediumEnum(int mediumId, String mediumName) {
        this.mediumId = mediumId;
        this.mediumName = mediumName;
    }

    public int getMediumId() {
        return mediumId;
    }

    public String getMediumName() {
        return mediumName;
    }

    public static CardMediumEnum getByMediumId(int mediumId) {
        for (CardMediumEnum medium : CardMediumEnum.values()) {
            if (medium.getMediumId() == mediumId) {
                return medium;
            }
        }
        return ELECT_CARD;
    }
}
