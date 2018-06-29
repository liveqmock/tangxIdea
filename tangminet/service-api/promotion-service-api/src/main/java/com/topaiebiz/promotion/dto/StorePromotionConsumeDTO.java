package com.topaiebiz.promotion.dto;

import lombok.Data;

/**
 * 店铺营销活动使用
 * Created by qiaolin on 2018/1/18.
 */
@Data
public class StorePromotionConsumeDTO {
    private Long storeId;
    private Long promotionId;
    private Integer type;
    private Long orderId;

    /**
     * 包邮活动ID，若没有使用包邮则此字段为null
     */
    private Long freightPromotionId;
}