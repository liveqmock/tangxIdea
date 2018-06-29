package com.topaiebiz.goods.enums;

/**
 * @description: 属性设置
 * @author: Jeff Chen
 * @date: created in 下午3:06 2018/5/19
 */
public enum  AttrSettingEnum {
    YES(1),
    NO(0)
    ;

    private Integer value;

    AttrSettingEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
