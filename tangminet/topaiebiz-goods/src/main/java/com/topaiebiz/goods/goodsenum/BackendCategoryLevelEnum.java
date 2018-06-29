package com.topaiebiz.goods.goodsenum;

/**
 * Created by dell on 2018/1/24.
 */
public enum  BackendCategoryLevelEnum {

    BACKEND_LEVEL_ONE(1,"一级"),

    BACKEND_LEVEL_TWO(2,"二级"),

    BACKEND_LEVEL_THERE(3,"三级"),
    BACKEND_LEVEL_FOUR(4,"四级"),
    BACKEND_LEVEL_FIVE(5,"五级");

    private Integer code;
    private String value;

    private BackendCategoryLevelEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
