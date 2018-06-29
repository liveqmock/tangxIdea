package com.topaiebiz.giftcard.enums;

/**
 * @description: 转赠状态
 * @author: Jeff Chen
 * @date: created in 上午11:22 2018/1/27
 */
public enum GivenStatusEnum {

    NOT_GIVEN(0, "不可转赠"),
    CAN_GIVEN(1, "可转赠"),
    HAD_GIVEN(2,"已转赠")
    ;
    private int code;
    private String desc;

    GivenStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
