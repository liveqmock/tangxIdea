package com.topaiebiz.promotion.mgmt.dto.card;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 礼卡秒杀-礼卡详情（C端）
 */
@Data
public class CardSecKillItemDTO {
    /**
     * 礼卡秒杀活动ID
     */
    private Long activityId;
    /**
     * 礼卡发行ID
     */
    private Long batchId;
    /**
     * 礼卡名称
     */
    private String cardName;
    /**
     * 礼卡封面
     */
    private String cover;
    /**
     * 礼卡面值
     */
    private BigDecimal faceValue;
    /**
     * 礼卡售价
     */
    private BigDecimal salePrice;
    /**
     * 每天库存配置值
     */
    private Integer dayStorage;
    /**
     * 每天库存剩余
     */
    private Integer dayStorageRest;
    /**
     * 状态(0-即将开始，1-进行中，2-已抢光)
     */
    private Integer state;
}
