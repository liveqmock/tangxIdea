package com.topaiebiz.promotion.promotionEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动类型枚举类
 * Created by Joe on 2018/1/9.
 */
@Getter
@AllArgsConstructor
public enum PromotionCondTypeEnum {

    FULL(1, "满"), PROMOTION_TYPE_PRICE(2, "每满");

    private Integer value;
    private String name;

    public static PromotionCondTypeEnum valueOf(Integer code) {
        for (PromotionCondTypeEnum item : values()) {
            if (item.getValue().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
