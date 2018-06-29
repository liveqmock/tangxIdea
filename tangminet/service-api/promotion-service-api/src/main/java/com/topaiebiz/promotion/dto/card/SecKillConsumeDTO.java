package com.topaiebiz.promotion.dto.card;

import lombok.Data;

/**
 * 礼卡秒杀下单消费
 */
@Data
public class SecKillConsumeDTO {
    /**
     * 礼卡发行ID
     */
    private Long batchId;
    /**
     * 会员ID
     */
    private Long memberId;
    /**
     * 订单ID
     */
    private Long orderId;
}
