package com.topaiebiz.giftcard.enums;

/**
 * @description: 卡属性
 * @author: Jeff Chen
 * @date: created in 下午9:42 2018/2/6
 */
public enum CardAttrEnum {
    //卡属性：1-普通卡 2-联名卡 3-活动卡
    COMMON(1, "普通卡"),
    JOINT(2, "联名卡"),
    ACTIVITY(3, "活动卡"),
    ;

    private int id ;
    private String attr;

    CardAttrEnum(int id, String attr) {
        this.id = id;
        this.attr = attr;
    }

    public int getId() {
        return id;
    }

    public String getAttr() {
        return attr;
    }
}
