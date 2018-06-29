package com.topaiebiz.giftcard.enums;

/**
 * @description: 礼卡日志类型
 * @author: Jeff Chen
 * @date: created in 下午4:34 2018/1/23
 */
public enum GiftcardLogTypeEnum {
    //日志类型：1-消费，2-退款，3-绑定，4-冻结，5-解冻，6-续期
    CONSUME(1, "消费"),
    REFUND(2, "退款"),
    BINDING(3, "绑定"),
    FREEZE(4, "冻结"),
    UNFREEZE(5, "解冻"),
    RENEWAL(6, "续期"),
    ;
    private int type;
    private String typeStr;

    GiftcardLogTypeEnum(int type, String typeStr) {
        this.type = type;
        this.typeStr = typeStr;
    }

    public int getType() {
        return type;
    }

    public String getTypeStr() {
        return typeStr;
    }
}
