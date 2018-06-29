package com.topaiebiz.trade.order.core.order.promotion.pattern;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.promotionEnum.PromotionGradeEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderGoodsBO;
import com.topaiebiz.trade.order.core.order.context.PromotionDiscountContext;
import com.topaiebiz.trade.order.util.MathUtil;
import lombok.extern.slf4j.Slf4j;
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
public class FreightPromotionPattern extends BasePromotionPattern {

    @Override
    public PromotionGradeEnum matchPromotionGrade() {
        return PromotionGradeEnum.PROMOTION_GRADE_STORE;
    }

    @Override
    protected void validatePromotionType(PromotionDTO promotionDTO) {
        PromotionTypeEnum proType = promotionDTO.getType();
        if (PromotionTypeEnum.PROMOTION_TYPE_FREE_SHIPPING == proType) {
            return;
        }
        log.warn("input promotion {} has code : {}", promotionDTO.getId(), JSON.toJSONString(promotionDTO.getType()));
        throw new GlobalException(PROMOTION_NOT_VALID);
    }

    public boolean match(StoreOrderBO storeOrderBO, PromotionDTO promotionDTO) {
        if (!validate(promotionDTO)) {
            return false;
        }

        //step 1 : 获取校验商品范围
        List<StoreOrderGoodsBO> targetGoodsBoList = getTargetGoods(storeOrderBO, promotionDTO);

        //step 2 : 计算目标商品的总运费
        BigDecimal freightAmount = getFreightAmount(targetGoodsBoList);
        BigDecimal goodsPayAmount = getGoodsPayAmount(targetGoodsBoList);
        //step 3 : 比较目标商品总运费是否满足包邮条件
        boolean match = MathUtil.greateEq(goodsPayAmount, promotionDTO.getCondValue());
        log.warn("freight promotion {} match freight amount {} result : {}", JSON.toJSONString(promotionDTO), goodsPayAmount, match);

        if (match){
            //step 4 : 记录包邮活动的优惠幅度
            PromotionDiscountContext.get().put(promotionDTO.getId(), getFreightAmount(targetGoodsBoList));
        }
        return match;
    }

    private BigDecimal getFreightAmount(List<StoreOrderGoodsBO> targetGoodsBoList) {
        BigDecimal freightAmount = BigDecimal.ZERO;
        //取商品最大值作为订单的运费
        for (StoreOrderGoodsBO goodsBO : targetGoodsBoList) {
            if (MathUtil.greateEq(goodsBO.getFreight(),freightAmount)){
                freightAmount = goodsBO.getFreight();
            }
        }
        return freightAmount;
    }

    public void dispatch(StoreOrderBO storeOrderBO, PromotionDTO promotionDTO) {
        //step 1 : 获取校验商品范围
        List<StoreOrderGoodsBO> targetGoodsBoList = getTargetGoods(storeOrderBO, promotionDTO);

        //step 2 : 将每个商品的运费优惠设置与运费相同，即匹配单品全额免运费
        for (StoreOrderGoodsBO orderGoodsBO : targetGoodsBoList) {
            //step 2.1
            orderGoodsBO.setFreightDiscount(orderGoodsBO.getFreight());

            //step 2.2 更新单品价格
            orderGoodsBO.updatePrice();
        }

        //step 3 : 更新店铺订单价格
        storeOrderBO.setFreightPromotion(promotionDTO);
        storeOrderBO.updatePrice();
    }
}