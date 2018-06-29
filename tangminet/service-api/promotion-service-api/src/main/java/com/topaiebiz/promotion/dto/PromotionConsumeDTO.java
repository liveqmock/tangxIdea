package com.topaiebiz.promotion.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 平台营销活动使用
 * Created by Joe on 2018/1/18.
 */
@Data
public class PromotionConsumeDTO {
    private Long payId;
    private Integer platformPromotionType;
    private Long platformPromotionId;
    private List<StorePromotionConsumeDTO> storePromotions = new ArrayList<>();
    private List<SinglePromotionConsumeDTO> singlePromotions = new ArrayList<>();
}