package com.topaiebiz.promotion.mgmt.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.basic.api.ConfigApi;
import com.topaiebiz.card.api.GiftCardApi;
import com.topaiebiz.card.dto.CardBatchDTO;
import com.topaiebiz.promotion.common.util.DozerUtils;
import com.topaiebiz.promotion.constants.PromotionConstants;
import com.topaiebiz.promotion.mgmt.dao.CardActivityDao;
import com.topaiebiz.promotion.mgmt.dao.CardActivityItemDao;
import com.topaiebiz.promotion.mgmt.dao.FloorCardDao;
import com.topaiebiz.promotion.mgmt.dao.PromotionDao;
import com.topaiebiz.promotion.mgmt.dto.card.CardActivityConfigDTO;
import com.topaiebiz.promotion.mgmt.dto.card.CardSecKillDTO;
import com.topaiebiz.promotion.mgmt.dto.card.CardSecKillItemDTO;
import com.topaiebiz.promotion.mgmt.dto.floor.FloorCardDTO;
import com.topaiebiz.promotion.mgmt.entity.FloorCardEntity;
import com.topaiebiz.promotion.mgmt.entity.PromotionEntity;
import com.topaiebiz.promotion.mgmt.entity.card.CardActivityEntity;
import com.topaiebiz.promotion.mgmt.entity.card.CardActivityItemEntity;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import com.topaiebiz.promotion.mgmt.service.CardActivityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.topaiebiz.promotion.constants.PromotionConstants.CacheKey.*;
import static com.topaiebiz.promotion.constants.PromotionConstants.SecKillState.*;
import static com.topaiebiz.promotion.promotionEnum.PromotionStateEnum.PROMOTION_STATE_ONGOING;
import static com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum.PROMOTION_TYPE_SEC_KILL_CARD;

@Slf4j
@Service
@Transactional
public class CardActivityServiceImpl implements CardActivityService {
    // 营销活动商品
    @Autowired
    private CardActivityItemDao cardActivityItemDao;
    @Autowired
    private ConfigApi configApi;
    @Autowired
    private CardActivityDao cardActivityDao;
    @Autowired
    private PromotionDao promotionDao;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private GiftCardApi giftCardApi;
    @Autowired
    private FloorCardDao floorCardDao;

    @Override
    public List<CardSecKillDTO> getSecKillList() {
        //从缓存获取礼卡秒杀列表
        List<CardSecKillDTO> secKillList = new ArrayList<>();
        //查询当前礼卡秒杀的活动
        PromotionEntity promotionEntity = queryPromotion();
        if (promotionEntity == null) {
            //活动不存在或无效
            throw new GlobalException(PromotionExceptionEnum.SEC_KILL_CARD_ACTIVITY_INVALID);
        }

        //查询活动的场次列表
        List<CardActivityEntity> activityEntities = queryCardActivityList(promotionEntity.getId());
        //没有活动场次
        if (activityEntities == null) {
            //礼卡秒杀活动未配置场次
            throw new GlobalException(PromotionExceptionEnum.SEC_KILL_CARD_NOT_CONFIGURED_YET);
        }
        Map<Long, CardActivityEntity> timeMap =
                activityEntities.stream().collect(
                        Collectors.toMap(CardActivityEntity::getId, cardActivityEntity -> cardActivityEntity)
                );
        List<Long> activityIds = activityEntities.stream().map(activityEntry -> activityEntry.getId()).collect(Collectors.toList());
        //查询场次的所有
        List<CardActivityItemEntity> itemEntities = queryCardActivitiesItemList(activityIds);
        //没有礼卡数据
        if (itemEntities == null) {
            return null;
        }
        //封装秒杀活动的礼卡信息
        List<CardSecKillItemDTO> itemList = packCardSecKillItem(itemEntities);

        for (Map.Entry<Long, CardActivityEntity> timeEntry : timeMap.entrySet()) {

            List<CardSecKillItemDTO> timeItemList = itemList.stream().filter(timeItem -> timeItem.getActivityId().equals(timeEntry.getKey())).collect(Collectors.toList());
            if (timeEntry.getValue() == null) {
                //礼卡秒杀活动配置有误
                throw new GlobalException(PromotionExceptionEnum.SEC_KILL_CARD_NOT_CONFIGURED_YET);
            }

            CardSecKillDTO secKill = new CardSecKillDTO();
            //获取状态
            Integer state = getSecKillState(timeEntry.getValue(), timeItemList);
            secKill.setState(state);
            //场次开始时间
            secKill.setStartTime(timeEntry.getValue().getStartTime().toString());
            //礼卡列表
            secKill.setItemList(timeItemList);
            secKillList.add(secKill);
        }
        //升序排序
        Collections.sort(secKillList);
        return secKillList;
    }

    @Override
    public List<CardActivityConfigDTO> getConfigList(String configCode) {
        List<Long> idList = configApi.convertValueToList(configCode);
        //批量查询礼卡信息
        List<CardBatchDTO> batchList = giftCardApi.getCardBatchByIds(idList);
        //拼接礼卡信息
        return DozerUtils.maps(batchList, CardActivityConfigDTO.class);
    }

    /**
     * 查询当前的礼卡秒杀活动
     *
     * @return
     */
    @Override
    public PromotionEntity queryPromotion() {
        PromotionEntity promotionEntity = redisCache.get(SEC_KILL_CARD, PromotionEntity.class);
        //没有进行中的活动
        if (promotionEntity == null) {
            //查询当前时间是否有可用礼卡秒杀活动
            PromotionEntity promotionCond = new PromotionEntity();
            promotionCond.clearInit();
            //活动类型（9-礼卡秒杀）
            promotionCond.setTypeId(PROMOTION_TYPE_SEC_KILL_CARD.getCode());
            //活动状态（2-进行中）
            promotionCond.setMarketState(PROMOTION_STATE_ONGOING.getCode());
            promotionCond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            promotionEntity = promotionDao.selectOne(promotionCond);
            if (promotionEntity == null) {
                log.warn("queryPromotion-暂无礼卡秒杀活动！");
                return null;
            }
            //过期时间5分钟
            redisCache.set(SEC_KILL_CARD, promotionEntity, 300);
        }
        return promotionEntity;
    }

    @Override
    public CardActivityEntity queryActivity(Long promotionId, Boolean started) {
        CardActivityEntity cardActivity = redisCache.get(SEC_KILL_CARD_ACTIVITY, CardActivityEntity.class);

        if (cardActivity == null) {
            List<CardActivityEntity> activityList = queryCardActivityList(promotionId);

            if (CollectionUtils.isEmpty(activityList)) {
                log.warn("queryPromotion-没有礼卡秒杀活动配置！礼卡秒杀活动ID {}", promotionId);
                return null;
            }
            LocalTime now = LocalTime.now();
            for (CardActivityEntity activity : activityList) {
                if (started) {
                    if (now.isAfter(activity.getEndTime()) || now.isBefore(activity.getStartTime())) {
                        continue;
                    }
                    //过期时间半分钟
                    redisCache.set(SEC_KILL_CARD_ACTIVITY, activity, 30);
                    cardActivity = activity;
                } else {
                    if (now.isBefore(activity.getEndTime())) {
                        //过期时间半分钟
                        redisCache.set(SEC_KILL_CARD_ACTIVITY, activity, 30);
                        cardActivity = activity;
                        break;
                    }
                }
            }
        }
        return cardActivity;
    }

    @Override
    public List<CardActivityEntity> queryCardActivityList(Long promotionId) {
        List<CardActivityEntity> activityList = redisCache.getListValue(SEC_KILL_CARD_ACTIVITIES, CardActivityEntity.class);
        if (CollectionUtils.isEmpty(activityList)) {
            //查询活动场次开始时间列表
            EntityWrapper<CardActivityEntity> activityWrapper = new EntityWrapper<>();
            activityWrapper.eq("promotionId", promotionId);
            activityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            activityWrapper.orderBy("startTime");
            activityList = cardActivityDao.selectList(activityWrapper);
            if (CollectionUtils.isEmpty(activityList)) {
                log.warn("queryCardActivityList-没有配置礼卡秒杀活动场次！");
                return null;
            }
            //过期时间一分钟
            redisCache.set(SEC_KILL_CARD_ACTIVITIES, activityList, 60);
        }
        return activityList;
    }

    @Override
    public List<FloorCardDTO> getFloorList(String floorCode) {
        //从缓存中取配置楼层信息
        List<FloorCardDTO> results = redisCache.getListValue(FLOOR_CARDS_PREFIX + floorCode, FloorCardDTO.class);
        if (CollectionUtils.isNotEmpty(results)) {
            return results;
        }
        //根据楼层code，查询商品楼层列表
        EntityWrapper<FloorCardEntity> cond = new EntityWrapper<>();
        cond.eq("floorCode", floorCode);
        cond.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<FloorCardEntity> entities = floorCardDao.selectList(cond);
        if (CollectionUtils.isEmpty(entities)) {
            log.warn("------------没有配置礼卡楼层数据，楼层CODE {}", floorCode);
            return null;
        }
        //匹配商品信息
        List<FloorCardDTO> floorCards = convertToDTO(entities);
        //过期时间为5分钟
        redisCache.set(FLOOR_CARDS_PREFIX + floorCode, floorCards, 300);
        return floorCards;
    }

    @Override
    public List<CardSecKillItemDTO> getSecKill() {
        //查询当前礼卡秒杀的活动
        PromotionEntity promotionEntity = queryPromotion();
        if (promotionEntity == null) {
            //活动不存在或无效
            throw new GlobalException(PromotionExceptionEnum.SEC_KILL_CARD_ACTIVITY_INVALID);
        }

        //查询当前场次
        CardActivityEntity cardActivity = queryActivity(promotionEntity.getId(), false);
        if (cardActivity == null) {
            log.warn("秒杀活动还未开始，礼卡活动ID-{}", promotionEntity.getId());
            //cardActivity为null出现在每天第一场活动之前
            throw new GlobalException(PromotionExceptionEnum.SEC_KILL_CARD_NOT_STARTED_YET);
        }

        //查询场次的所有
        List<CardActivityItemEntity> itemEntities = queryCardActivityItems(cardActivity.getId());
        //没有礼卡数据
        if (itemEntities == null) {
            return null;
        }
        //封装秒杀活动的礼卡信息
        List<CardSecKillItemDTO> itemList = packCardSecKillItem(itemEntities);
        //活动即将开始
        if (LocalTime.now().isBefore(cardActivity.getStartTime())) {
            for (CardSecKillItemDTO item : itemList) {
                item.setState(STARTED_YET);
            }
        }
        return itemList;
    }

    /**
     * 获取礼卡秒杀活动所有场次礼卡配置列表
     *
     * @param activityIds
     * @return
     */
    private List<CardActivityItemEntity> queryCardActivitiesItemList(List<Long> activityIds) {
        List<CardActivityItemEntity> itemList = redisCache.getListValue(SEC_KILL_CARD_ACTIVITIES_ITEMS, CardActivityItemEntity.class);
        if (CollectionUtils.isEmpty(itemList)) {
            EntityWrapper<CardActivityItemEntity> itemWrapper = new EntityWrapper<>();
            itemWrapper.in("activityId", activityIds);
            itemWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            itemList = cardActivityItemDao.selectList(itemWrapper);
            if (CollectionUtils.isEmpty(itemList)) {
                log.warn("queryCardActivitiesItemList-礼卡秒杀活动没有配置礼卡！");
                return null;
            }
            //过期时间一分钟
            redisCache.set(SEC_KILL_CARD_ACTIVITIES_ITEMS, itemList, 60);
        }
        return itemList;
    }

    /**
     * 获取礼卡秒杀活动所有场次礼卡配置列表
     *
     * @param activityId
     * @return
     */
    private List<CardActivityItemEntity> queryCardActivityItems(Long activityId) {
        List<CardActivityItemEntity> itemList = redisCache.getListValue(SEC_KILL_CARD_ACTIVITY_ITEMS, CardActivityItemEntity.class);
        if (CollectionUtils.isEmpty(itemList)) {
            EntityWrapper<CardActivityItemEntity> itemWrapper = new EntityWrapper<>();
            itemWrapper.eq("activityId", activityId);
            itemWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            itemList = cardActivityItemDao.selectList(itemWrapper);
            if (CollectionUtils.isEmpty(itemList)) {
                log.warn("queryCardActivityItemList-礼卡秒杀活动没有配置礼卡！");
                return null;
            }
            //过期时间一分钟
            redisCache.set(SEC_KILL_CARD_ACTIVITY_ITEMS, itemList, 60);
        }
        return itemList;
    }

    /**
     * 封装秒杀活动的礼卡信息
     *
     * @param itemEntities 秒杀活动的礼卡列表
     * @return
     */
    private List<CardSecKillItemDTO> packCardSecKillItem(List<CardActivityItemEntity> itemEntities) {
        List<CardSecKillItemDTO> itemList = BeanCopyUtil.copyList(itemEntities, CardSecKillItemDTO.class);
        //通过batchIds集合，查询礼卡集合
        List<Long> batchIds = itemEntities.stream().map(itemEntity -> itemEntity.getBatchId()).collect(Collectors.toList());
        List<CardBatchDTO> batchList = giftCardApi.getCardBatchByIds(batchIds);
        //拼接礼卡信息
        if (CollectionUtils.isEmpty(batchList)) {
            return itemList;
        }

        Map<Long, CardBatchDTO> cardBatchDTOMap = batchList.stream().collect(Collectors.toMap(CardBatchDTO::getBatchId, item -> item));

        for (CardSecKillItemDTO item : itemList) {
            CardBatchDTO cardBatchDTO = cardBatchDTOMap.get(item.getBatchId());
            if (cardBatchDTO == null) {
                continue;
            }
            item.setCardName(cardBatchDTO.getCardName());
            item.setCover(cardBatchDTO.getCover());
            item.setFaceValue(cardBatchDTO.getFaceValue());
            item.setSalePrice(cardBatchDTO.getSalePrice());

            if (item.getDayStorageRest() <= 0) {   //已抢光
                item.setState(NO_STORAGE);
            } else {    //进行中
                item.setState(STARTING);
            }
        }

        return itemList;
    }

    private Integer getSecKillState(CardActivityEntity cardActivity, List<CardSecKillItemDTO> timeItemList) {
        LocalTime nowTime = LocalTime.now();
        //活动场次开始时间
        LocalTime startTime = cardActivity.getStartTime();
        //活动场次结束时间
        LocalTime endTime = cardActivity.getEndTime();

        if (startTime == null
                || endTime == null) {
            //礼卡秒杀活动未配置有误
            throw new GlobalException(PromotionExceptionEnum.SEC_KILL_CARD_NOT_CONFIGURED_YET);
        }
        if (startTime.isAfter(nowTime)) {//未开始
            return STARTED_YET;
        } else if (startTime.isBefore(nowTime) && endTime.isAfter(nowTime)) {
            Long count = timeItemList.stream().filter(timeItem -> timeItem.getDayStorageRest().intValue() > 0).count();
            if (count.equals(0L)) {//已抢光
                return NO_STORAGE;
            } else {//进行中
                return STARTING;
            }

        } else {//已结束
            return FINISHED;
        }
    }

    /**
     * 转换成DTO，匹配商品信息
     *
     * @param entities
     * @return
     */
    private List<FloorCardDTO> convertToDTO(List<FloorCardEntity> entities) {
        List<FloorCardDTO> floorCards = DozerUtils.maps(entities, FloorCardDTO.class);
        //获取礼卡批次ID集合
        List<Long> batchIds = entities.stream().map(entity -> entity.getBatchId()).collect(Collectors.toList());
        List<CardBatchDTO> batchList = giftCardApi.getCardBatchByIds(batchIds);

        Map<Long, CardBatchDTO> cardBatchDTOMap = batchList.stream().collect(Collectors.toMap(CardBatchDTO::getBatchId, item -> item));
        if (CollectionUtils.isEmpty(batchList)) {
            log.warn("------------礼卡信息未找到，请核对数据！");
        } else {
            //拼接礼卡信息
            for (FloorCardDTO item : floorCards) {
                CardBatchDTO cardBatchDTO = cardBatchDTOMap.get(item.getBatchId());
                if (cardBatchDTO == null) {
                    continue;
                }
                item.setCover(cardBatchDTO.getCover());
                item.setFaceValue(cardBatchDTO.getFaceValue());
            }
        }
        return floorCards;
    }

}
