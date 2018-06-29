package com.topaiebiz.promotion.mgmt.service;

import com.topaiebiz.promotion.mgmt.dto.card.CardActivityConfigDTO;
import com.topaiebiz.promotion.mgmt.dto.card.CardSecKillDTO;
import com.topaiebiz.promotion.mgmt.dto.card.CardSecKillItemDTO;
import com.topaiebiz.promotion.mgmt.dto.floor.FloorCardDTO;
import com.topaiebiz.promotion.mgmt.entity.PromotionEntity;
import com.topaiebiz.promotion.mgmt.entity.card.CardActivityEntity;

import java.util.List;

public interface CardActivityService {

    /**
     * 根据cardId查询礼卡，查询当天的库存配置量
     */
    List<CardSecKillDTO> getSecKillList();

    /**
     * 获取礼卡活动配置列表（礼卡充值送、店铺联名卡）
     *
     * @param configCode 配置编号
     * @return
     */
    List<CardActivityConfigDTO> getConfigList(String configCode);

    /**
     * 查询当前的礼卡秒杀活动
     *
     * @return
     */
    PromotionEntity queryPromotion();

    /**
     * 查询礼卡秒杀活动指定的场次
     *
     * @param promotionId 礼卡秒杀活动ID
     * @param started     已经开始的场次  false-即将或已经开始，不要求已经开始 true-要求已经开始
     * @return
     */
    CardActivityEntity queryActivity(Long promotionId, Boolean started);

    /**
     * 获取礼卡秒杀活动场次列表
     *
     * @param promotionId 活动ID
     * @return
     */
    List<CardActivityEntity> queryCardActivityList(Long promotionId);

    /**
     * 根据楼层CODE查询楼层礼卡列表
     *
     * @param floorCode 楼层CODE
     * @return
     */
    List<FloorCardDTO> getFloorList(String floorCode);

    List<CardSecKillItemDTO> getSecKill();
}
