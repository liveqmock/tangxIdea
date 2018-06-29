package com.topaiebiz.promotion.promotionEnum;

/**
 * 活动级别枚举类
 * Created by Joe on 2018/1/9.
 */
public enum PromotionGradeEnum {

    PROMOTION_GRADE_SINGLE(1,"单品级"),

    PROMOTION_GRADE_STORE(2,"订单级"),

    PROMOTION_GRADE_PLATFORM(3,"支付级");

    private Integer code;
    private String value;

    private PromotionGradeEnum(Integer code, String value) {
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

    public static PromotionGradeEnum valueOf(Integer code){
        for (PromotionGradeEnum item : values()){
            if (item.getCode().equals(code)){
                return item;
            }
        }
        return null;
    }
}
