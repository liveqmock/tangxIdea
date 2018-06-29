package com.topaiebiz.giftcard.enums;

/**
 * @description: 卡状态（继承老系统）
 * @author: Jeff Chen
 * @date: created in 下午3:39 2018/1/8
 */
public enum CardUnitStatusEnum {
    //0-未激活 1-已激活(未绑定)  2-已绑定  3-冻结  4-清零(已用完) 5-已过期

    INACTIVED(0, "未激活"),
    ACTIVED(1, "已激活"),
    BOUND(2, "已绑定"),
    FREEZED(3, "冻结"),
    CLEAR(4, "清零"),
    EXPIRE(5,"已过期")
    ;
    private int statusCode;
    private String statusDesc;

    CardUnitStatusEnum(int statusCode, String statusDesc) {
        this.statusCode = statusCode;
        this.statusDesc = statusDesc;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }
}
