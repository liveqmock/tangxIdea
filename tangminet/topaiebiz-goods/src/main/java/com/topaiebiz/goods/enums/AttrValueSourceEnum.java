package com.topaiebiz.goods.enums;

/**
 * @description: 属性值来源
 * @author: Jeff Chen
 * @date: created in 下午3:10 2018/5/19
 */
public enum AttrValueSourceEnum {
    //1 平台定义 2 商家定义
    PLATFORM(1),
    MERCHANT(2)
    ;

    private Integer value;

    AttrValueSourceEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
