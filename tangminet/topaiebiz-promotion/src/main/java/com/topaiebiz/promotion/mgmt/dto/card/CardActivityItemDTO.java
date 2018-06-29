package com.topaiebiz.promotion.mgmt.dto.card;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardActivityItemDTO {
    /**
     * ID
     */
    private Long id;
    /**
     * 活动场次ID
     */
    private Long activityId;
    /**
     * 礼卡Id
     */
    private Long batchId;
    /**
     * 秒杀价格
     */
    private BigDecimal secKillPrice;
    /**
     * 活动库存，固定不变
     */
    private Integer totalStorage;
    /**
     * 剩余总库存
     */
    private Integer totalStorageRest;
    /**
     * 每天库存配置值，固定不变
     */
    private Integer dayStorage;
    /**
     * 每天库存剩余
     */
    private Integer dayStorageRest;
}
