package com.nebulapaas.base.enumdata;

/**
 * Description 数据字典CODE 枚举类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/11 14:07
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public enum DataDictCodeEnum {

    LOGISTICS_COMPANY("logistics_company","物流公司")
    ;

    /**
     * 数据字段CODE
     */
    private String code;

    /**
     * 数据字典描述
     */
    private String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    DataDictCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
