package com.topaiebiz.goods.enums;

/**
 * @description: 属性类型
 * @author: Jeff Chen
 * @date: created in 下午3:04 2018/5/19
 */
public enum AttrTypeEnum {
    //1 平台定义 2商家定义
    PLATFORM(1),
    MERCHANT(2)
    ;

    private Integer value;

    AttrTypeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
