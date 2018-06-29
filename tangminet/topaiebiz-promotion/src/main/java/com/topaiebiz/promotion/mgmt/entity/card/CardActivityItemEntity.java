package com.topaiebiz.promotion.mgmt.entity.card;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 宝箱奖品配置
 */
@TableName("t_pro_promotion_card_item")
@Data
public class CardActivityItemEntity extends BaseBizEntity<Long> {
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
