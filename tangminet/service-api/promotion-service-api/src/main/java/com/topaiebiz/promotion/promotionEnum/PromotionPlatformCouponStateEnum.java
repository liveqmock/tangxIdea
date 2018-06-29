package com.topaiebiz.promotion.promotionEnum;

/**
 * 活动状态枚举类
 * Created by Joe on 2018/1/6.
 */
public enum PromotionPlatformCouponStateEnum {

    PROMOTION_STATE_NOT_RELEASE(0,"未发布"),
    PROMOTION_STATE_START(1,"已发布"),
    PROMOTION_STATE_HAS_ENDED(2,"已过期");


    private Integer code;
    private String value;

    private PromotionPlatformCouponStateEnum(Integer code, String value) {
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

    public static PromotionPlatformCouponStateEnum valueOf(Integer code){
        for (PromotionPlatformCouponStateEnum item : values()){
            if (item.getCode().equals(code)){
                return item;
            }
        }
        return null;
    }
}
