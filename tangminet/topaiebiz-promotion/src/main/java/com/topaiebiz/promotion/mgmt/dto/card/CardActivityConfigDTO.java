package com.topaiebiz.promotion.mgmt.dto.card;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 礼卡活动配置列表-礼卡充值送、店铺联名卡（C端）
 */
@Data
public class CardActivityConfigDTO {
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
}