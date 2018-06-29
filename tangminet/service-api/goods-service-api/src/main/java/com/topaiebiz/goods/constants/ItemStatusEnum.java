package com.topaiebiz.goods.constants;

/**
 * Created by dell on 2018/1/12.
 */
public enum ItemStatusEnum {

    ITEM_STATUS_NEWENTRY(1, "新录入"),
    ITEM_STATUS_PUTAWAY(2, "已上架"),
    ITEM_STATUS_REMOVE(3, "已下架"),
    ITEM_STATUS_INVIOLATIONODTHESHELVES(4, "违规下架"),
    ITEM_STATUS_FREEZE(0, "冻结");
    private Integer code;
    private String value;

    private ItemStatusEnum(Integer code, String value) {
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

    public boolean match(Integer value) {
        return this.getCode().equals(value);
    }
}
