package com.topaiebiz.trade.order.core.order.promotion.pattern;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.promotionEnum.PromotionGradeEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderGoodsBO;
import com.topaiebiz.trade.order.core.order.context.PromotionDiscountContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.util.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.PROMOTION_NOT_VALID;

/***
 * @author yfeng
 * @date 2018-01-11 11:24
 */
@Slf4j
@Component
public class PlatformPromotionPattern extends BasePromotionPattern {

    @Override
    public PromotionGradeEnum matchPromotionGrade() {
        return PromotionGradeEnum.PROMOTION_GRADE_PLATFORM;
    }

    @Override
    protected void validatePromotionType(PromotionDTO promotionDTO) {
        PromotionTypeEnum proType = promotionDTO.getType();
        if (PromotionTypeEnum.PROMOTION_TYPE_COUPON == proType) {
            return;
        }
        log.warn("input promotion {} has code : {}", promotionDTO.getId(), JSON.toJSONString(promotionDTO.getType()));
        throw new GlobalException(PROMOTION_NOT_VALID);
    }

    public boolean match(OrderSubmitContext orderContext, PromotionDTO promotionDTO) {
        if (!validate(promotionDTO)) {
            return false;
        }

        //step 1 : 获取校验商品范围
        List<StoreOrderGoodsBO> targetGoodsBoList = getTargetGoods(orderContext, promotionDTO);
        if(CollectionUtils.isEmpty(targetGoodsBoList)){
            return false;
        }
        //step 2 : 计算商品应付总额
        BigDecimal goodsPayAmount = getGoodsPayAmount(targetGoodsBoList);

        //step 3 : 计算是否匹配
        boolean match = MathUtil.greateEq(goodsPayAmount, promotionDTO.getCondValue());
        if (match){
            //step 4 : 记录优惠的优惠金额
            BigDecimal promotionDiscount = getPromotonDiscount(promotionDTO, goodsPayAmount);
            PromotionDiscountContext.get().put(promotionDTO.getId(), promotionDiscount);
        }
        return match;
    }

    public void dispatch(OrderSubmitContext orderContext, PromotionDTO promotionDTO) {
        //step 1 : 获取校验商品范围
        List<StoreOrderGoodsBO> targetGoodsBoList =  getTargetGoods(orderContext, promotionDTO);

        //step 2 : 商品总支付金额
        BigDecimal goodsPayAmount = getGoodsPayAmount(targetGoodsBoList);

        //step 3 : 应该优惠的金额幅度
        BigDecimal promotionDiscount = getPromotonDiscount(promotionDTO, goodsPayAmount);

        //step 4 : 分摊优惠信息到每个商品
        dispatchPlatformPromotion(targetGoodsBoList, promotionDiscount);

        //step 5 : 更新订单价格数据
        for (StoreOrderBO storeOrderBO : orderContext.getStoreOrderMap().values()){
            storeOrderBO.updatePrice();
        }
    }
}