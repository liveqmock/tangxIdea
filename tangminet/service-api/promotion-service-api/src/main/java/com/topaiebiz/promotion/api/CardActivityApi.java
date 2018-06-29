package com.topaiebiz.promotion.api;

import com.topaiebiz.promotion.dto.card.SecKillConsumeDTO;

/**
 * 礼卡秒杀活动
 */
public interface CardActivityApi {

    /**
     * 下单前，根据batchId匹配秒杀规则（C端）
     *
     * @param batchId  礼卡发行ID
     * @param memberId 当前用户ID
     * @return
     */
    Boolean checkSecKillRule(Long batchId, Long memberId);

    /**
     * 下单之后更新活动剩余库存并新增记录（包括场次库存和整个活动库存）（C端）
     *
     * @param secKillConsumeDTO 礼卡秒杀消费记录
     * @return
     */
    Boolean useSecKill(SecKillConsumeDTO secKillConsumeDTO);

    /**
     * 取消下单更新活动剩余库存和记录（包括场次库存和整个活动库存）（C端）
     *
     * @param secKillConsumeDTO 礼卡秒杀消费记录
     * @return
     */
    Boolean backSecKill(SecKillConsumeDTO secKillConsumeDTO);

    /**
     * 定时初始化当前活动的礼卡剩余库存
     */
    void initRestStorage();
}
