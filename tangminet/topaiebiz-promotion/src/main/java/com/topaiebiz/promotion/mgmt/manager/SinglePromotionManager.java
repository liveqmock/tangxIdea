package com.topaiebiz.promotion.mgmt.manager;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Sets;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.promotion.mgmt.dao.PromotionDao;
import com.topaiebiz.promotion.mgmt.dao.PromotionGoodsDao;
import com.topaiebiz.promotion.mgmt.entity.PromotionEntity;
import com.topaiebiz.promotion.mgmt.entity.PromotionGoodsEntity;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEntry;
import com.topaiebiz.promotion.promotionEnum.PromotionStateEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum.SINGLE_PROMOTION_GOODS_REPEAT_ERROR;

/***
 * @author yfeng
 * @date 2018-06-26 21:00
 */
@Component
public class SinglePromotionManager {

    @Autowired
    private PromotionGoodsDao promotionGoodsDao;

    @Autowired
    private PromotionDao promotionDao;

    private Set<Integer> singlePromotionTypes() {
        return Sets.newHashSet(
                PromotionTypeEnum.PROMOTION_TYPE_SINGLE.getCode(),
                PromotionTypeEnum.PROMOTION_TYPE_PRICE.getCode(),
                PromotionTypeEnum.PROMOTION_TYPE_SECKILL.getCode()
        );
    }

    private Set<Integer> checkMarketStatus() {
        return Sets.newHashSet(
                PromotionStateEnum.PROMOTION_STATE_NOT_RELEASE.getCode(),
                PromotionStateEnum.PROMOTION_STATE_NOT_START.getCode(),
                PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode(),
                PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode(),
                PromotionStateEnum.PROMOTION_STATE_ABNORMAL.getCode()
        );
    }

    public void checkAddSinglePromotionGoods(PromotionEntity promotionEntity, List<PromotionGoodsEntity> promotionGoodsList) {
        if (promotionEntity == null) {
            return;
        }
        //非单品营销活动，直接返回
        Set<Integer> singleTypes = singlePromotionTypes();
        if (!singleTypes.contains(promotionEntity.getTypeId())) {
            return;
        }
        Date now = new Date();

        //查询系统中正常的未结束单品营销活动
        EntityWrapper<PromotionEntity> singleCond = new EntityWrapper<>();
        singleCond.in("typeId", singleTypes);
        singleCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        singleCond.gt("endTime", now);
        singleCond.in("marketState", checkMarketStatus());
        List<PromotionEntity> promotions = promotionDao.selectList(singleCond);
        if (CollectionUtils.isEmpty(promotions)) {
            return;
        }
        //抽取存在时间区间重叠的单品营销活动
        List<PromotionEntity> repeatePromotions = promotions.stream().filter(goingPromotion -> {
            if (goingPromotion.getStartTime().after(promotionEntity.getEndTime())) {
                return false;
            }
            if (goingPromotion.getEndTime().before(promotionEntity.getStartTime())) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());

        //查询是否存在其他单品活动中存在本次添加的商品,剔除当前promotionId
        List<Long> promotionIds = repeatePromotions.stream().map(item -> item.getId())
                .filter(item -> {
                    return !item.equals(promotionEntity.getId());
                }).collect(Collectors.toList());
        List<Long> skuIds = promotionGoodsList.stream().map(item -> item.getGoodsSkuId()).collect(Collectors.toList());
        EntityWrapper<PromotionGoodsEntity> cond = new EntityWrapper<>();
        cond.in("goodsSkuId", skuIds);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.in("promotionId", promotionIds);
        RowBounds rowBounds = new RowBounds(0, 100);
        List<PromotionGoodsEntity> goods = promotionGoodsDao.selectPage(rowBounds, cond);
        if (CollectionUtils.isEmpty(goods)) {
            return;
        }

        //存在重复，提示异常
        List<Long> itemIds = goods.stream().map(item -> {
            return item.getItemId();
        }).distinct().collect(Collectors.toList());
        throw buildGlobalException(itemIds);
    }

    private GlobalException buildGlobalException(List<Long> itemIds) {
        String msg = String.format("Item %s 已参与其它单品营销活动中，请检查当前活动商品再发布", JSON.toJSONString(itemIds));
        throw new GlobalException(new PromotionExceptionEntry(SINGLE_PROMOTION_GOODS_REPEAT_ERROR.getCode(), msg));
    }
}