package com.topaiebiz.trade.order.core.order.promotion.pattern;

import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.promotionEnum.PromotionGradeEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderGoodsBO;
import com.topaiebiz.trade.order.core.order.context.PromotionDiscountContext;
import com.topaiebiz.trade.order.util.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-11 11:23
 */
@Slf4j
public abstract class BaseStorePromotionPattern extends BasePromotionPattern {

    public abstract PromotionTypeEnum getMatchPromotionType();

    @Override
    public PromotionGradeEnum matchPromotionGrade() {
        return PromotionGradeEnum.PROMOTION_GRADE_STORE;
    }

    public boolean match(StoreOrderBO storeOrderBO, PromotionDTO promotionDTO) {
        //step 1 : 营销活动对象校验
        if (!validate(promotionDTO)) {
            return false;
        }
        //step 2 : 获取校验商品范围
        List<StoreOrderGoodsBO> targetGoodsBoList = getTargetGoods(storeOrderBO, promotionDTO);
        if (CollectionUtils.isEmpty(targetGoodsBoList)) {
            log.warn("store promotion {} for order has no target goods", promotionDTO.getId());
            return false;
        }
        //step 3 : 计算是否匹配优惠条件
        BigDecimal goodsPayAmount = getGoodsPayAmount(targetGoodsBoList);
        if (MathUtil.greateEq(goodsPayAmount, promotionDTO.getCondValue())) {
            //step 4 : 记录优惠的优惠金额
            BigDecimal promotionDiscount = getPromotonDiscount(promotionDTO, goodsPayAmount);
            PromotionDiscountContext.get().put(promotionDTO.getId(), promotionDiscount);
            return true;
        }
        log.info("promotion goods with amount {} is less than promotion {} match condition {}", goodsPayAmount, promotionDTO.getId(), promotionDTO.getCondValue());
        return false;
    }

    public void dispatch(StoreOrderBO storeOrderBO, PromotionDTO promotionDTO) {
        //step 1 : 获取校验商品范围
        List<StoreOrderGoodsBO> targetGoodsBoList = getTargetGoods(storeOrderBO, promotionDTO);
        //step 2 : 商品总支付金额
        BigDecimal goodsPayAmount = getGoodsPayAmount(targetGoodsBoList);
        //step 3 : 应该优惠的金额幅度
        BigDecimal promotionDiscount = getPromotonDiscount(promotionDTO, goodsPayAmount);
        //step 4 : 分摊店铺优惠信息到每个商品
        dispatchStorePromotion(targetGoodsBoList, promotionDiscount, getMatchPromotionType());
        updateStoredOrderDiscount(storeOrderBO, promotionDTO, promotionDiscount);

        //step 5 : 更新订单价格数据
        storeOrderBO.updatePrice();
    }

    public abstract void updateStoredOrderDiscount(StoreOrderBO storeOrderBO, PromotionDTO promotionDTO, BigDecimal promotionDiscount);

}