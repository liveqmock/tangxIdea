package com.topaiebiz.trade.refund.enumdata;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.refund.exception.RefundOrderExceptionEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Description 售后原因
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/8 13:40
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public enum RefundReasonEnum {

    A(1, "商品与描述不符"),
    B(2, "质量问题"),
    C(3, "卖家发错货"),
    D(4, "未按约定时间发货"),
    E(5, "其他");

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

    RefundReasonEnum(Integer code, String desc) {
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
        Map<Integer, String> map = new HashMap<>(RefundReasonEnum.values().length);
        for (RefundReasonEnum refundReasonEnum : RefundReasonEnum.values()) {
            map.put(refundReasonEnum.getCode(), refundReasonEnum.getDesc());
        }
        return map;
    }

    /**
     * Description: 判断code 是否存在枚举中
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/26
     *
     * @param:
     **/
    public static boolean inValues(Integer code) {
        for (RefundReasonEnum refundReasonEnum : RefundReasonEnum.values()) {
            if (refundReasonEnum.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Description: 根据code 获取枚举
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/8
     *
     * @param:
     **/
    public static RefundReasonEnum getByCode(Integer code) {
        for (RefundReasonEnum refundReasonEnum : RefundReasonEnum.values()) {
            if (refundReasonEnum.getCode().equals(code)) {
                return refundReasonEnum;
            }
        }
        throw new GlobalException(RefundOrderExceptionEnum.AFTER_SALES_TYPE_ILLEGAL);
    }

}
