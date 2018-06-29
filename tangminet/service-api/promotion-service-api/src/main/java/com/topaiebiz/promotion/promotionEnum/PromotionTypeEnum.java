package com.topaiebiz.promotion.promotionEnum;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * 活动类型枚举类
 * Created by Joe on 2018/1/9.
 */
public enum PromotionTypeEnum {

    PROMOTION_TYPE_SINGLE(1, "单品折扣"),
    PROMOTION_TYPE_PRICE(2, "一口价"),
    PROMOTION_TYPE_REDUCE_PRICE(3, "满减"),
    PROMOTION_TYPE_COUPON(4, "平台优惠券"),
    PROMOTION_TYPE_COUPON_CODE(5, "平台优惠码"),
    PROMOTION_TYPE_SECKILL(6, "秒杀"),
    PROMOTION_TYPE_STORE_COUPON(7, "店铺优惠券"),
    PROMOTION_TYPE_FREE_SHIPPING(8, "包邮"),
    PROMOTION_TYPE_SEC_KILL_CARD(9, "礼卡秒杀"),
    PROMOTION_TYPE_OPEN_BOX(10, "开宝箱"),
    PROMOTION_TYPE_COUPON_ACTIVE(11, "优惠券活动");


    private Integer code;
    private String value;

    private PromotionTypeEnum(Integer code, String value) {
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

    public static PromotionTypeEnum valueOf(Integer code) {
        for (PromotionTypeEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static Set<Integer> singlePromotionTypes() {
        return Sets.newHashSet(
                PROMOTION_TYPE_SINGLE.getCode(),
                PROMOTION_TYPE_PRICE.getCode(),
                PROMOTION_TYPE_SECKILL.getCode()
        );
    }
}
