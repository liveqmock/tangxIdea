package com.topaiebiz.trade.order.core.order.promotion.pattern;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.dto.PromotionGoodsDTO;
import com.topaiebiz.promotion.promotionEnum.PromotionGradeEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderGoodsBO;
import com.topaiebiz.trade.order.core.order.context.BuyerContext;
import com.topaiebiz.trade.order.core.order.context.PromotionDiscountContext;
import com.topaiebiz.trade.order.dao.OrderDetailDao;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.trade.order.util.PromotionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.PROMOTION_NOT_VALID;
import static com.topaiebiz.transaction.common.util.OrderStatusEnum.ORDER_CANCELLATION;

/***
 * @author yfeng
 * @date 2018-01-11 10:03
 */
@Component
@Slf4j
public class GoodsPromotionPattern extends BasePromotionPattern {

    @Autowired
    private OrderDetailDao orderDetailsDao;

    @Override
    public PromotionGradeEnum matchPromotionGrade() {
        return PromotionGradeEnum.PROMOTION_GRADE_SINGLE;
    }

    @Override
    public void validatePromotionType(PromotionDTO promotionDTO) {
        PromotionTypeEnum proType = promotionDTO.getType();
        /**
         * 单品营销活动只允许：一口价、秒杀、单品折扣
         */
        if (PromotionTypeEnum.PROMOTION_TYPE_PRICE == proType
                || PromotionTypeEnum.PROMOTION_TYPE_SECKILL == proType
                || PromotionTypeEnum.PROMOTION_TYPE_SINGLE == proType) {
            return;
        }
        log.warn("input promotion {} has code : {}", promotionDTO.getId(), JSON.toJSONString(promotionDTO.getType()));
        throw new GlobalException(PROMOTION_NOT_VALID);
    }

    public boolean match(StoreOrderGoodsBO orderGoodsBO, PromotionDTO promotionDTO) {
        //step 1 : 营销活动对象校验
        if (!validate(promotionDTO)) {
            return false;
        }

        //step 2 : 查找promotion底下圈定的当前商品配置
        if (CollectionUtils.isEmpty(promotionDTO.getLimitGoods())) {
            log.warn("promotion {} has no limited goods", promotionDTO.getId());
            return false;
        }
        Long goodsId = orderGoodsBO.getGoods().getId();
        PromotionGoodsDTO curPromotionGoods = PromotionUtil.findPromotionGoods(promotionDTO, goodsId);
        if (curPromotionGoods == null) {
            log.warn("goods {} dose not contain goodsId: {}", JSON.toJSONString(promotionDTO.getLimitGoods()), goodsId);
            return false;
        }

        //step 3 : 营销配置了更高价
        BigDecimal goodsPrice = orderGoodsBO.getGoods().getPrice();
        BigDecimal promotionPrice = curPromotionGoods.getPromotionPrice();
        if (MathUtil.less(goodsPrice, promotionPrice)) {
            log.error("goods {} price : {} but the promotion {} price is :{}", goodsId, goodsPrice, promotionDTO.getId(), promotionPrice);
            return false;
        }

        //step 3 : 校验营销活动库存限制
        Integer saleVolume = curPromotionGoods.getQuantitySales() == null ? 0 : curPromotionGoods.getQuantitySales();
        if (saleVolume + orderGoodsBO.getGoodsNum() > curPromotionGoods.getPromotionNum()) {
            log.warn("promotion has storage limit : {} , sale num is {} and current goods num is {}", curPromotionGoods.getPromotionNum(), saleVolume, orderGoodsBO.getGoodsNum());
            return false;
        }

        //step 4 : 个人购买数量限制
        BuyerBO buyerBO = BuyerContext.get();
        Long limitNum = curPromotionGoods.getConfineNum();
        if (limitNum == null || limitNum < 1) {
            //limit为null或0表示无限购
            log.warn("promotionId:{} has member buy volume limit:{}", buyerBO.getMemberId(), limitNum);
            return true;
        }
        //查询过往用户此营销活动购买销量
        Integer memberVolume = promotionMemberVolume(buyerBO.getMemberId(), promotionDTO.getId(), goodsId);
        log.warn("memberId:{} promotionId:{} memberLimit:{} and member historyVolume:{} currentBuyNum:{}", buyerBO.getMemberId(), promotionDTO.getId(), limitNum, memberVolume, orderGoodsBO.getGoodsNum());
        if (orderGoodsBO.getGoodsNum() + memberVolume > limitNum) {
            return false;
        }
        //记录优惠的优惠金额
        PromotionDiscountContext.get().put(promotionDTO.getId(), promotionDTO.getDiscountValue());
        return true;
    }

    public void dispatch(StoreOrderGoodsBO orderGoodsBO, PromotionDTO promotionDTO) {
        orderGoodsBO.setGoodsPromotion(promotionDTO);
        //更新价格信息
        orderGoodsBO.updatePrice();
    }

    /**
     * 查询某个单品活动的历史销量
     *
     * @param promotionId 营销活动
     * @return
     */
    private Integer promotionMemberVolume(Long memberId, Long promotionId, Long skuId) {
        Integer value = orderDetailsDao.countSkuHistoryVolume(memberId, promotionId, skuId, ORDER_CANCELLATION.getCode());
        return value == null ? 0 : value;
    }
}