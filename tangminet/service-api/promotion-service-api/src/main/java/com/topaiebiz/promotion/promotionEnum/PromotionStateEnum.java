package com.topaiebiz.promotion.promotionEnum;

/**
 * 活动状态枚举类
 * Created by Joe on 2018/1/6.
 */
public enum PromotionStateEnum {

    PROMOTION_STATE_NOT_RELEASE(0,"未发布"),
    PROMOTION_STATE_NOT_START(1,"未开始"),
    PROMOTION_STATE_ONGOING(2,"进行中"),
    PROMOTION_STATE_HAS_ENDED(3,"已结束"),
    PROMOTION_STATE_TERMINATED(4,"已终止"),
    PROMOTION_STATE_ABNORMAL(5,"活动异常"),
    PROMOTION_STATE_RELEASE(6,"已发布");


    private Integer code;
    private String value;

    private PromotionStateEnum(Integer code, String value) {
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

    public static PromotionStateEnum valueOf(Integer code){
        for (PromotionStateEnum item : values()){
            if (item.getCode().equals(code)){
                return item;
            }
        }
        return null;
    }
}
