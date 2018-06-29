package com.topaiebiz.trade.refund.enumdata;

import java.util.HashMap;
import java.util.Map;

/**
 * Description 售后--处理状态--枚举类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/11 13:29
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public enum RefundProcessEnum {

    WAIT(0, "待处理"),

    ALREADY(1, "已处理"),

    REFUSE(2, "已拒绝"),

    RETURNING(3, "退款中"),

    REFUNDED(4, "已退款"),

    CANCEL(5, "已取消"),;

    private Integer code;

    private String desc;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    RefundProcessEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * Description: 获取所有枚举并转化为集合
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/8
     *
     * @param:
     **/
    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>(RefundProcessEnum.values().length);
        for (RefundProcessEnum refundProcessEnum : RefundProcessEnum.values()) {
            map.put(refundProcessEnum.getCode(), refundProcessEnum.getDesc());
        }
        return map;
    }

}
