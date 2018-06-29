package com.topaiebiz.promotion.mgmt.api;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.promotion.api.PromotionTaskApi;
import com.topaiebiz.promotion.constants.PromotionConstants;
import com.topaiebiz.promotion.dto.schedule.TaskContext;
import com.topaiebiz.promotion.mgmt.dao.PromotionDao;
import com.topaiebiz.promotion.mgmt.dao.PromotionGoodsDao;
import com.topaiebiz.promotion.mgmt.entity.PromotionEntity;
import com.topaiebiz.promotion.mgmt.entity.PromotionGoodsEntity;
import com.topaiebiz.promotion.promotionEnum.PromotionStateEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-06-28 17:32
 */
public class PromotionTaskApiImpl implements PromotionTaskApi {

    @Autowired
    private PromotionDao promotionDao;

    @Autowired
    private PromotionGoodsDao promotionGoodsDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void promotionStartTask(TaskContext taskContext) {
        /** 审核通过*/
        Integer through = 0;
        /** 开始活动 */
        EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        entityWrapper.le("startTime", new Date());
        entityWrapper.gt("endTime", new Date());
        entityWrapper.eq("marketState", PromotionStateEnum.PROMOTION_STATE_NOT_START.getCode());
        List<PromotionEntity> promotionStartEntities = promotionDao.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(promotionStartEntities)) {
            return;
        }

        for (PromotionEntity promotionEntity : promotionStartEntities) {
            PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
            promotionGoodsEntity.clearInit();
            promotionGoodsEntity.setPromotionId(promotionEntity.getId());
            promotionGoodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
            List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(new EntityWrapper<>(promotionGoodsEntity));
            /** 该活动没有参与活动的商品且没有选中全平台商品,活动异常*/
            if (CollectionUtils.isEmpty(promotionGoodsEntities) && !(PromotionConstants.IsGoodsArea.ALL.equals(promotionEntity.getIsGoodsArea()))) {
                promotionEntity.setMarketState(PromotionStateEnum.PROMOTION_STATE_ABNORMAL.getCode());
                promotionEntity.setLastModifiedTime(new Date());
                promotionDao.updateById(promotionEntity);
                saveToTaskContext(promotionEntity, taskContext);
                continue;
            }
            if (PromotionConstants.IsGoodsArea.ALL.equals(promotionEntity.getIsGoodsArea())) {
                promotionEntity.setMarketState(PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
                promotionEntity.setLastModifiedTime(new Date());
                promotionDao.updateById(promotionEntity);
                saveToTaskContext(promotionEntity, taskContext);
                continue;
            }
            /** 是否为平台发布商品*/
            if (promotionEntity.getSponsorType() == null) {
                for (PromotionGoodsEntity promotionGoods : promotionGoodsEntities) {
                    if (promotionGoods.getState() != null) {
                        if (PromotionConstants.AuditState.APPROVED_AUDIT.equals(promotionGoods.getState())) {
                            through++;
                        }
                    }
                }
                /** 商品审核通过数量为0,活动异常*/
                if (through == 0) {
                    promotionEntity.setMarketState(PromotionStateEnum.PROMOTION_STATE_ABNORMAL.getCode());
                    promotionEntity.setLastModifiedTime(new Date());
                } else {
                    promotionEntity.setMarketState(PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
                    promotionEntity.setLastModifiedTime(new Date());
                }
                promotionDao.updateById(promotionEntity);
            } else {
                promotionEntity.setMarketState(PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
                promotionEntity.setLastModifiedTime(new Date());
                promotionDao.updateById(promotionEntity);
            }
            saveToTaskContext(promotionEntity, taskContext);
        }
    }

    private void saveToTaskContext(PromotionEntity promotion, TaskContext taskContext) {
        //仅仅变更为开始或结束的营销活动需要被记录到任务上下文中
        if (PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode().equals(promotion.getMarketState())
                || PromotionStateEnum.PROMOTION_STATE_HAS_ENDED.getCode().equals(promotion.getMarketState())) {
            if (PromotionTypeEnum.singlePromotionTypes().contains(promotion.getTypeId())) {
                //增加到单品优惠活动中
                taskContext.getSinglePromotionIds().add(promotion.getId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void promotionFinishTask(TaskContext taskContext) {
        EntityWrapper<PromotionEntity> cond = new EntityWrapper<>();
        cond.eq("marketState",PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
        cond.lt("endTime", new Date());
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

        List<PromotionEntity> promotions = promotionDao.selectList(cond);
        if (CollectionUtils.isEmpty(promotions)) {
            return;
        }
        for (PromotionEntity promotion : promotions) {
            promotion.setMarketState(PromotionStateEnum.PROMOTION_STATE_HAS_ENDED.getCode());
            promotion.setLastModifiedTime(new Date());
            promotionDao.updateById(promotion);
            saveToTaskContext(promotion, taskContext);
        }
    }
}