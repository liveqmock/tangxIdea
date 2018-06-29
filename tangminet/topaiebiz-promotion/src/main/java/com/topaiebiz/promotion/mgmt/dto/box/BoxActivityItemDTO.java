package com.topaiebiz.promotion.mgmt.dto.box;

import lombok.Data;

/**
 * 宝箱奖品配置
 */
@Data
public class BoxActivityItemDTO {
    /**
     * 活动开宝箱ID
     */
    private Long promotionBoxId;
    /**
     * 奖品ID
     */
    private Long awardId;
    /**
     * 奖品类型
     */
    private Integer awardType;
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
    /**
     * 实物奖品配置
     */
    private String resContent;
}
