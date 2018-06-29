package com.topaiebiz.trade.order.core.order.promotion.pattern;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.promotion.constants.PromotionConstants;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.dto.PromotionStoreDTO;
import com.topaiebiz.promotion.promotionEnum.PromotionCondTypeEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionGradeEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderGoodsBO;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.util.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.PROMOTION_NOT_VALID;

/***
 * @author yfeng
 * @date 2018-01-11 14:05
 */
@Slf4j
public abstract class BasePromotionPattern {

    abstract PromotionGradeEnum matchPromotionGrade();

    protected boolean validate(PromotionDTO promotionDTO) {
        Date now = new Date();
        //step 1 : 活动已经结束
        if (now.after(promotionDTO.getEndTime())) {
            log.info("promotionDTO {} end {} has finished", promotionDTO.getId(), promotionDTO.getEndTime());
            return false;
        }
        //step 2 : 活动未开始
        if (now.before(promotionDTO.getStartTime())) {
            log.info("promotionDTO {} startTime {} has finished", promotionDTO.getId(), promotionDTO.getStartTime());
            return false;
        }
        //step 3 : 类别不存在
        if (promotionDTO.getType() == null) {
            log.error("promotionDTO code is null");
            return false;
        }
        //step 4 : 营销级别匹配
        if (matchPromotionGrade() != promotionDTO.getGrade()) {
            log.error("promotion grade {} dose not match promotion id {} with grade {}",
                    matchPromotionGrade().getValue(), promotionDTO.getId(), promotionDTO.getGrade().getValue());
            throw new GlobalException(PROMOTION_NOT_VALID);
        }

        //step 5 : 校验类别
        validatePromotionType(promotionDTO);

        return true;
    }

    protected List<StoreOrderGoodsBO> getTargetGoods(OrderSubmitContext orderContext, PromotionDTO promotionDTO) {
        List<StoreOrderGoodsBO> targetGoodsList = new ArrayList<>();
        List<Long> limitStoreIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(promotionDTO.getStoreDTOS())) {
            for (PromotionStoreDTO storeDTO : promotionDTO.getStoreDTOS()) {
                limitStoreIds.add(storeDTO.getStoreId());
            }
        }
        Integer goodsArea = promotionDTO.getIsGoodsArea();
        for (StoreOrderBO storeOrderBO : orderContext.getStoreOrderMap().values()) {
            List<StoreOrderGoodsBO> goodsBOList = storeOrderBO.getGoodsList();
            Long storeId = storeOrderBO.getStore().getId();
            if (PromotionConstants.IsGoodsArea.ALL_STORE.equals(goodsArea)) {
                targetGoodsList.addAll(goodsBOList);
            } else if (PromotionConstants.IsGoodsArea.INCLUDE_STORE.equals(goodsArea) && limitStoreIds.contains(storeId)) {
                targetGoodsList.addAll(goodsBOList);
            } else if (PromotionConstants.IsGoodsArea.EXCLUDE_STORE.equals(goodsArea) && !limitStoreIds.contains(storeId)) {
                targetGoodsList.addAll(goodsBOList);
            } else {
                List<StoreOrderGoodsBO> storeGoodsList = getTargetGoods(storeOrderBO, promotionDTO);
                if (CollectionUtils.isEmpty(storeGoodsList)) {
                    continue;
                }
                targetGoodsList.addAll(storeGoodsList);
            }
        }
        return targetGoodsList;
    }

    /**
     * 根据店铺订单和营销活动计算优惠覆盖商品范围
     *
     * @param storeOrderBO 店铺订单
     * @param promotionDTO 优惠活动
     * @return
     */
    protected List<StoreOrderGoodsBO> getTargetGoods(StoreOrderBO storeOrderBO, PromotionDTO promotionDTO) {
        List<StoreOrderGoodsBO> goodsBOList = storeOrderBO.getGoodsList();

        //全店铺商品可用
        if (PromotionConstants.IsGoodsArea.ALL.equals(promotionDTO.getIsGoodsArea())) {
            return goodsBOList;
        }

        //设置了圈定或排除商品，但是没有配置，则直接禁止
        if (CollectionUtils.isEmpty(promotionDTO.getLimitGoods())) {
            return Collections.emptyList();
        }
        Integer goodsArea = promotionDTO.getIsGoodsArea();
        //读取活动圈定的商品集合
        Set<Long> goodsIdSet = new HashSet<>();
        promotionDTO.getLimitGoods().forEach(goodsBo -> {
            goodsIdSet.add(goodsBo.getGoodsSkuId());
        });

        //查找此单中在活动范围的商品
        List<StoreOrderGoodsBO> targetResult = new ArrayList<>();
        goodsBOList.forEach(item -> {
            if (PromotionConstants.IsGoodsArea.NOT_ALL.equals(goodsArea) && goodsIdSet.contains(item.getGoods().getId())) {
                targetResult.add(item);
            } else if (PromotionConstants.IsGoodsArea.EXCLUDING_PART_OF_THE_GOODS.equals(goodsArea) && !goodsIdSet.contains(item.getGoods().getId())) {
                targetResult.add(item);
            }
        });

        List<Long> goodsIds = targetResult.stream().map(item -> item.getGoods().getId()).collect(Collectors.toList());
        log.info("promotion {} target goodsIds : {}", promotionDTO.getId(), JSON.toJSONString(goodsIds));
        return targetResult;
    }

    /**
     * 校验营销活动类型
     *
     * @param promotionDTO
     */

    protected abstract void validatePromotionType(PromotionDTO promotionDTO);

    /**
     * 计算商品的应付总额
     *
     * @param targetGoodsBoList 目标商品集合
     * @return
     */
    public BigDecimal getGoodsPayAmount(List<StoreOrderGoodsBO> targetGoodsBoList) {
        BigDecimal goodsPayAmount = BigDecimal.ZERO;
        for (StoreOrderGoodsBO item : targetGoodsBoList) {
            goodsPayAmount = goodsPayAmount.add(item.getPayAmount());
        }
        return goodsPayAmount;
    }

    public void dispatchStorePromotion(List<StoreOrderGoodsBO> targetGoodsBoList, BigDecimal promotionDiscount, PromotionTypeEnum promotionType) {
        dispatchPromotion(targetGoodsBoList, promotionDiscount, promotionType);
    }

    public void dispatchPlatformPromotion(List<StoreOrderGoodsBO> targetGoodsBoList, BigDecimal promotionDiscount) {
        dispatchPromotion(targetGoodsBoList, promotionDiscount, PromotionTypeEnum.PROMOTION_TYPE_COUPON);
    }

    /**
     * 计算店铺优惠券或平台优惠券可以优惠的金额
     *
     * @return
     */
    public BigDecimal getPromotonDiscount(PromotionDTO promotionDTO, BigDecimal goodsPayAmount) {
        if (PromotionCondTypeEnum.FULL.getValue().equals(promotionDTO.getCondType())) {
            //满X减Y
            return MathUtil.min(promotionDTO.getDiscountValue(), goodsPayAmount);
        } else {
            //每满X减Y
            BigDecimal matchCount = goodsPayAmount.divide(promotionDTO.getCondValue(), 0, BigDecimal.ROUND_DOWN);
            BigDecimal discountAmount = promotionDTO.getDiscountValue().multiply(matchCount);
            return MathUtil.min(discountAmount, goodsPayAmount);
        }
    }

    private void fillDiscount(StoreOrderGoodsBO goodsItem, BigDecimal discount, PromotionTypeEnum promotionType) {
        if (PromotionTypeEnum.PROMOTION_TYPE_REDUCE_PRICE == promotionType) {
            goodsItem.setStoreDiscount(discount);
        } else if (PromotionTypeEnum.PROMOTION_TYPE_STORE_COUPON == promotionType) {
            goodsItem.setStoreCouponDiscount(discount);
        } else {
            goodsItem.setPlatformDiscount(discount);
        }
        goodsItem.updatePrice();
    }

    private void dispatchPromotion(List<StoreOrderGoodsBO> targetGoodsBoList, BigDecimal promotionDiscount, PromotionTypeEnum promotionType) {
        BigDecimal goodsTotalPayAmount = getGoodsPayAmount(targetGoodsBoList);

        int count = targetGoodsBoList.size();
        if (count == 1) {
            //单个商品将当前优惠全部分摊给当前商品
            StoreOrderGoodsBO goodsItem = targetGoodsBoList.get(0);
            fillDiscount(goodsItem, promotionDiscount, promotionType);
        } else {
            //多个商品情况，按照商品店铺优惠前价格分摊，前n-1按照比例，最后一个优惠剩余
            BigDecimal cost = BigDecimal.ZERO;
            for (int i = 0; i < count; i++) {
                StoreOrderGoodsBO goodsItem = targetGoodsBoList.get(i);
                if (i == count - 1) {
                    BigDecimal curCouponAmount = promotionDiscount.subtract(cost);
                    fillDiscount(goodsItem, curCouponAmount, promotionType);
                } else {
                    BigDecimal goodsPayAmount = goodsItem.getPayAmount();
                    BigDecimal curCouponAmount = MathUtil.cuculate(promotionDiscount, goodsPayAmount, goodsTotalPayAmount);
                    fillDiscount(goodsItem, curCouponAmount, promotionType);
                    cost = cost.add(curCouponAmount);
                }
            }
        }
    }
}