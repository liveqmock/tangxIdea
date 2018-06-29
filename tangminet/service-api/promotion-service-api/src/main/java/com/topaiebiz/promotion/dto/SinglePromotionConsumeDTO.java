package com.topaiebiz.promotion.dto;

import lombok.Data;

/**
 * 单品活动使用
 * Created by Joe on 2018/1/22.
 */
@Data
public class SinglePromotionConsumeDTO {

    private Long goodsSkuId;

    private Integer goodsNum;

    private Long promotionId;
}
