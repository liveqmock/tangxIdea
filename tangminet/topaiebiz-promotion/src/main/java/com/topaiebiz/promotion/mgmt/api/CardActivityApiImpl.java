package com.topaiebiz.promotion.mgmt.api;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.promotion.api.CardActivityApi;
import com.topaiebiz.promotion.constants.PromotionConstants;
import com.topaiebiz.promotion.dto.card.SecKillConsumeDTO;
import com.topaiebiz.promotion.mgmt.dao.*;
import com.topaiebiz.promotion.mgmt.entity.PromotionEntity;
import com.topaiebiz.promotion.mgmt.entity.PromotionPlatformUsageLogEntity;
import com.topaiebiz.promotion.mgmt.entity.PromotionStoreUsageLogEntity;
import com.topaiebiz.promotion.mgmt.entity.card.CardActivityEntity;
import com.topaiebiz.promotion.mgmt.entity.card.CardActivityItemEntity;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import com.topaiebiz.promotion.mgmt.service.CardActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.topaiebiz.promotion.constants.PromotionConstants.CacheKey.*;

@Slf4j
@Service
public class CardActivityApiImpl implements CardActivityApi {
    @Autowired
    private CardActivityDao cardActivityDao;
    @Autowired
    private CardActivityItemDao cardActivityItemDao;
    @Autowired
    private CardActivityService cardActivityService;
    @Autowired
    private PromotionDao promotionDao;
    @Autowired
    private PromotionPlatformUsageLogDao promotionPlatformUsageLogDao;
    @Autowired
    private PromotionStoreUsageLogDao promotionStoreUsageLogDao;
    @Autowired
    private RedisCache redisCache;

    @Override
    public Boolean checkSecKillRule(Long batchId, Long memberId) {
        log.info("礼卡秒杀核对秒杀规则, 礼卡发行批次ID-{} 会员ID {}", batchId, memberId);
        //查询当前礼卡秒杀的活动
        PromotionEntity promotionEntity = cardActivityService.queryPromotion();
        if (promotionEntity == null) {
            //活动不存在或无效
            throw new GlobalException(PromotionExceptionEnum.SEC_KILL_CARD_ACTIVITY_INVALID);
        }

        //查询当前场次
        CardActivityEntity cardActivity = cardActivityService.queryActivity(promotionEntity.getId(), true);
        if (cardActivity == null) {
            log.warn("秒杀活动还未开始，礼卡活动ID-{}", promotionEntity.getId());
            //cardActivity为null出现在每天第一场活动之前
            throw new GlobalException(PromotionExceptionEnum.SEC_KILL_CARD_NOT_STARTED_YET);
        }

        //判断本场次是否有该礼卡的活动
        CardActivityItemEntity itemEntity = queryActivityItem(cardActivity.getId(), batchId);
        if (itemEntity == null) {
            //此卡不在本场正在秒杀的活动中
            throw new GlobalException(PromotionExceptionEnum.CARD_CONFIG_NO_EXIST_IN_SEC_KILL_FIELD);
        }

        //判断用户购买限制
        Integer buyCount = getBuyCount(promotionEntity.getSponsorType(), promotionEntity.getId(), memberId);
        //购买次数不小于限制次数
        if (buyCount.compareTo(cardActivity.getBuyLimit()) > -1) {
            log.warn("购买次数不小于限制次数, 购买次数-{} 限制次数-{}", buyCount, cardActivity.getBuyLimit());
            //该会员达到购买上线了
            throw new GlobalException(PromotionExceptionEnum.SEC_KILL_CARD_LIMITED_FOR_MEMBER);
        }

        //无剩余库存
        if (itemEntity.getDayStorageRest().intValue() < 1) {
            log.warn("日剩余库存不足, 日剩余库存-{}", itemEntity.getDayStorageRest());
            //礼卡库存不足
            throw new GlobalException(PromotionExceptionEnum.SEC_KILL_CARD_LACK_OF_STOCK);
        }

        return true;
    }

    @Override
    public Boolean useSecKill(SecKillConsumeDTO secKillConsumeDTO) {
        log.info("下单之后更新活动剩余库存并新增记录, 礼卡发行批次ID-batchId:" + secKillConsumeDTO.getBatchId()
                + ", 会员ID-memberId:" + secKillConsumeDTO.getMemberId() + ", 订单ID-orderId:" + secKillConsumeDTO.getOrderId());
        //查询当前礼卡秒杀的活动
        PromotionEntity promotionEntity = cardActivityService.queryPromotion();
        if (promotionEntity == null) {
            return false;
        }
        //获取该礼卡活动中的配置记录
        CardActivityItemEntity itemEntity = getActivityItem(promotionEntity.getId(), secKillConsumeDTO.getBatchId());
        if (itemEntity == null) {
            //此卡不在本次礼卡活动中
            return false;
        }
        //锁定库存
        Integer count = cardActivityItemDao.reduceStock(itemEntity.getId(), 1);
        if (count == 0) {
            log.warn("下订单更新库存失败，活动配置ID-itemId:" + itemEntity.getId()
                    + ", 订单ID-orderId:" + secKillConsumeDTO.getOrderId());
            return false;
        }

        //删除活动场次该礼卡的缓存
        redisCache.delete(SEC_KILL_CARD_ACTIVITY_PREFIX + secKillConsumeDTO.getBatchId().toString());
        //新增活动使用记录
        count = addItemLog(promotionEntity.getSponsorType(), promotionEntity.getId(), secKillConsumeDTO);
        if (count == 0) {
            log.warn("下订单新增使用礼卡活动记录失败，活动发起者ID-sponsorId:" + promotionEntity.getSponsorType()
                    + ", 礼卡秒杀活动ID-promotionId:" + promotionEntity.getId()
                    + ", 订单ID-orderId:" + secKillConsumeDTO.getOrderId());
            return false;
        }
        redisCache.delete(SEC_KILL_CARD_COUNT_PREFIX + secKillConsumeDTO.getMemberId());
        return true;
    }

    @Override
    public Boolean backSecKill(SecKillConsumeDTO secKillConsumeDTO) {
        log.info("取消下单更新活动剩余库存和记录, 礼卡发行批次ID-batchId:" + secKillConsumeDTO.getBatchId()
                + ", 会员ID-memberId:" + secKillConsumeDTO.getMemberId() + ", 订单ID-orderId:" + secKillConsumeDTO.getOrderId());
        //查询当前礼卡秒杀的活动
        PromotionEntity promotionEntity = cardActivityService.queryPromotion();
        if (promotionEntity == null) {
            return false;
        }
        //获取该礼卡活动中的配置记录
        CardActivityItemEntity itemEntity = getActivityItem(promotionEntity.getId(), secKillConsumeDTO.getBatchId());
        if (itemEntity == null) {
            //此卡不在本次礼卡活动中
            return false;
        }
        //释放库存
        Integer count = cardActivityItemDao.backStock(itemEntity.getId(), 1);
        //删除活动使用记录
        if (count == 0) {
            log.warn("取消订单更新库存失败，活动配置ID-itemId:" + itemEntity.getId()
                    + ", 订单ID-orderId:" + secKillConsumeDTO.getOrderId());
            return false;
        }
        //删除活动场次该礼卡的缓存
        redisCache.delete(SEC_KILL_CARD_ACTIVITY_PREFIX + secKillConsumeDTO.getBatchId().toString());
        //新增活动使用记录
        count = delItemLog(promotionEntity.getSponsorType(), promotionEntity.getId(), secKillConsumeDTO);
        if (count == 0) {
            log.warn("取消订单更新使用礼卡活动记录失败，活动发起者ID-sponsorId:" + promotionEntity.getSponsorType()
                    + ", 礼卡秒杀活动ID-promotionId:" + promotionEntity.getId()
                    + ", 订单ID-orderId:" + secKillConsumeDTO.getOrderId());
            return false;
        }
        redisCache.delete(SEC_KILL_CARD_COUNT_PREFIX + secKillConsumeDTO.getMemberId());
        return true;
    }

    @Override
    public void initRestStorage() {
        //删除缓存
        redisCache.delete(SEC_KILL_CARD);
        redisCache.delete(SEC_KILL_CARD_ACTIVITY);

        //查询当前礼卡秒杀的活动
        PromotionEntity promotionEntity = cardActivityService.queryPromotion();
        if (promotionEntity == null) {
            return;
        }
        //查询当前活动的场次信息列表
        List<CardActivityEntity> activityList = cardActivityService.queryCardActivityList(promotionEntity.getId());
        //活动场次ID列表
        List<Long> activityIds = activityList.stream().map(activity -> activity.getId()).collect(Collectors.toList());
        //批量设置活动礼卡剩余库存
        cardActivityItemDao.batchUpdateRestStorage(activityIds);
    }

    /**
     * 获取该礼卡活动中的配置记录
     *
     * @param promotionId 活动ID
     * @param batchId     礼卡发行批次ID
     * @return
     */
    private CardActivityItemEntity getActivityItem(Long promotionId, Long batchId) {
        //查询当前场次
        CardActivityEntity cardActivity = cardActivityService.queryActivity(promotionId, true);
        if (cardActivity == null) {
            log.warn("秒杀活动还未开始，礼卡秒杀活动ID-promotionId:" + promotionId);
            //cardActivity为null出现在每天第一场活动之前
            return null;
        }

        //判断本场次是否有该礼卡的活动
        CardActivityItemEntity itemEntity = queryActivityItem(cardActivity.getId(), batchId);
        if (itemEntity == null) {
            //此卡不在本次礼卡活动中
            return null;
        }
        return itemEntity;
    }

    /**
     * 获取正在进行的活动场次的礼卡配置
     *
     * @param activityId 礼卡活动场次ID
     * @param batchId    礼卡发行ID
     * @return
     */
    private CardActivityItemEntity queryActivityItem(Long activityId, Long batchId) {
        CardActivityItemEntity itemEntity = redisCache.get(SEC_KILL_CARD_ACTIVITY_PREFIX + batchId.toString(), CardActivityItemEntity.class);
        if (itemEntity == null) {
            //判断本场次是否有该礼卡的活动
            CardActivityItemEntity itemCond = new CardActivityItemEntity();
            itemCond.cleanInit();
            itemCond.setBatchId(batchId);
            itemCond.setActivityId(activityId);
            itemCond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            itemEntity = cardActivityItemDao.selectOne(itemCond);
            if (itemEntity == null) {
                log.warn("queryPromotion-礼卡不在本场活动中！礼卡秒杀活动场次ID-activityId：" + activityId);
                return null;
            }

            //过期时间1分钟
            redisCache.set(SEC_KILL_CARD_ACTIVITY_PREFIX + batchId.toString(), itemEntity, 30);
        }
        return itemEntity;
    }

    /**
     * 获取会员在整场活动购买礼卡数量
     *
     * @param sponsorId   活动发起者ID（平台活动-空，店铺活动-店铺ID）
     * @param promotionId 活动ID
     * @param memberId    当前用户ID
     * @return
     */
    private Integer getBuyCount(Long sponsorId, Long promotionId, Long memberId) {
        Integer buyCount = redisCache.getInt(SEC_KILL_CARD_COUNT_PREFIX + memberId);
        if (buyCount == null) {
            //查询整场活动的限制用户购买次数
            if (sponsorId == null) {
                EntityWrapper<PromotionPlatformUsageLogEntity> condWrapper = new EntityWrapper<>();
                condWrapper.eq("memberId", memberId);
                condWrapper.eq("promotionId", promotionId);
                condWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
                buyCount = promotionPlatformUsageLogDao.selectCount(condWrapper);
            } else {
                EntityWrapper<PromotionStoreUsageLogEntity> condWrapper = new EntityWrapper<>();
                condWrapper.eq("memberId", memberId);
                condWrapper.eq("promotionId", promotionId);
                condWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
                buyCount = promotionStoreUsageLogDao.selectCount(condWrapper);
            }
            //过期时间5分钟
            redisCache.set(SEC_KILL_CARD_COUNT_PREFIX + memberId, buyCount, 300);
        }

        return buyCount;
    }

    /**
     * 新增活动记录
     *
     * @param sponsorId         活动发起者ID（平台活动-空，店铺活动-店铺ID）
     * @param promotionId       活动ID
     * @param secKillConsumeDTO 礼卡秒杀下单消费
     * @return
     */
    private Integer addItemLog(Long sponsorId, Long promotionId, SecKillConsumeDTO secKillConsumeDTO) {
        log.info("下单新增使用礼卡活动记录，订单ID-orderId:" + secKillConsumeDTO.getOrderId()
                + ", 礼卡秒杀活动ID-promotionId:" + promotionId
                + ", 会员ID-memberId:" + secKillConsumeDTO.getMemberId()
                + ", 活动发起者ID-sponsorId:" + sponsorId);
        //查询整场活动的限制用户购买次数
        if (sponsorId == null) {
            PromotionPlatformUsageLogEntity platformLog = new PromotionPlatformUsageLogEntity();
            platformLog.setOrderId(secKillConsumeDTO.getOrderId());
            platformLog.setPromotionId(promotionId);
            platformLog.setMemberId(secKillConsumeDTO.getMemberId());
            platformLog.setCreatorId(secKillConsumeDTO.getMemberId());
            platformLog.setCreatedTime(new Date());
            return promotionPlatformUsageLogDao.insert(platformLog);
        } else {
            PromotionStoreUsageLogEntity storeLog = new PromotionStoreUsageLogEntity();
            storeLog.setOrderId(secKillConsumeDTO.getOrderId());
            storeLog.setPromotionId(promotionId);
            storeLog.setMemberId(secKillConsumeDTO.getMemberId());
            storeLog.setStoreId(sponsorId);
            storeLog.setCreatorId(secKillConsumeDTO.getMemberId());
            storeLog.setCreatedTime(new Date());
            return promotionStoreUsageLogDao.insert(storeLog);
        }
    }

    /**
     * 编辑活动记录
     *
     * @param sponsorId         活动发起者ID（平台活动-空，店铺活动-店铺ID）
     * @param promotionId       活动ID
     * @param secKillConsumeDTO 礼卡秒杀下单消费
     * @return
     */
    private Integer delItemLog(Long sponsorId, Long promotionId, SecKillConsumeDTO secKillConsumeDTO) {
        log.info("取消订单删除使用礼卡活动记录，orderId:" + secKillConsumeDTO.getOrderId()
                + ", 礼卡秒杀活动ID-promotionId:" + promotionId
                + ", 会员ID-memberId:" + secKillConsumeDTO.getMemberId()
                + ", 活动发起者ID-sponsorId:" + sponsorId);
        //查询整场活动的限制用户购买次数
        if (sponsorId == null) {
            EntityWrapper<PromotionPlatformUsageLogEntity> logCond = new EntityWrapper<>();
            logCond.eq("orderId", secKillConsumeDTO.getOrderId());
            logCond.eq("memberId", secKillConsumeDTO.getMemberId());
            logCond.eq("promotionId", promotionId);
            PromotionPlatformUsageLogEntity platformLog = new PromotionPlatformUsageLogEntity();
            platformLog.clearInit();
            platformLog.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
            return promotionPlatformUsageLogDao.update(platformLog, logCond);
        } else {
            EntityWrapper<PromotionStoreUsageLogEntity> logCond = new EntityWrapper<>();
            logCond.eq("orderId", secKillConsumeDTO.getOrderId());
            logCond.eq("memberId", secKillConsumeDTO.getMemberId());
            logCond.eq("promotionId", promotionId);
            logCond.eq("storeId", sponsorId);
            PromotionStoreUsageLogEntity storeLog = new PromotionStoreUsageLogEntity();
            storeLog.clearInit();
            storeLog.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
            return promotionStoreUsageLogDao.update(storeLog, logCond);
        }
    }
}
