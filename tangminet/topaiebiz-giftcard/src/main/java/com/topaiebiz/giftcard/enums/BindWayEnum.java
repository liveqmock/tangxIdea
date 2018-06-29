package com.topaiebiz.giftcard.enums;

/**
 * @description: 绑定途径
 * @author: Jeff Chen
 * @date: created in 上午11:09 2018/1/27
 */
public enum BindWayEnum {

    ONE_KEY(1, "一键绑定"),
    CARD_PWD(2, "卡密绑定"),
    GET_GIVEN(3,"转赠领取")
    ;
    private Integer wayId;
    private String way;

    BindWayEnum(Integer wayId, String way) {
        this.wayId = wayId;
        this.way = way;
    }

    public Integer getWayId() {
        return wayId;
    }

    public String getWay() {
        return way;
    }
}
