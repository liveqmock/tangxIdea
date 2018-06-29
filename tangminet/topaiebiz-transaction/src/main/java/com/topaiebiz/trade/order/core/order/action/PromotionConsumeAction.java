package com.topaiebiz.trade.order.core.order.action;

import com.topaiebiz.promotion.dto.PromotionConsumeDTO;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.dto.SinglePromotionConsumeDTO;
import com.topaiebiz.promotion.dto.StorePromotionConsumeDTO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderGoodsBO;
import com.topaiebiz.trade.order.core.order.context.PayIdContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.facade.PromotionServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-09 10:12
 */
@Slf4j
@Component("promotionConsumeAction")
public class PromotionConsumeAction extends AbstractAction {

    @Autowired
    private PromotionServiceFacade promotionServiceFacade;

    @Override
    public boolean action(BuyerBO buyer, OrderSubmitContext paramContext, OrderRequest orderRequest) {
        PromotionConsumeDTO promotionConsumeDTO = buildPromotionConsumeDTO(paramContext);
        if (!needAction(promotionConsumeDTO)) {
            //没有营销活动操作，直接返回true
            return true;
        }
        return promotionServiceFacade.usePromotions(buyer.getMemberId(), promotionConsumeDTO);
    }

    @Override
    public void rollback(BuyerBO buyer, OrderSubmitContext paramContext, OrderRequest orderRequest) {
        PromotionConsumeDTO promotionConsumeDTO = buildPromotionConsumeDTO(paramContext);
        if (!needAction(promotionConsumeDTO)) {
            //没有营销活动操作，直接返回
            return;
        }
        promotionServiceFacade.backPromotions(buyer.getMemberId(), promotionConsumeDTO);
    }

    private PromotionConsumeDTO buildPromotionConsumeDTO(OrderSubmitContext paramContext) {
        PromotionConsumeDTO dto = new PromotionConsumeDTO();
        dto.setPayId(PayIdContext.get());

        //平台活动数据
        PromotionDTO platformPromotion = paramContext.getPlatformPromotion();
        if (platformPromotion != null) {
            dto.setPlatformPromotionId(platformPromotion.getId());
            dto.setPlatformPromotionType(platformPromotion.getType().getCode());
        }

        //店铺级活动记录
        for (StoreOrderBO storeOrderBO : paramContext.getStoreOrderMap().values()) {
            //单品营销活动
            for (StoreOrderGoodsBO goodsBO : storeOrderBO.getGoodsList()) {
                if (goodsBO.getGoodsPromotion() != null) {
                    SinglePromotionConsumeDTO singleDTO = new SinglePromotionConsumeDTO();
                    singleDTO.setGoodsNum(goodsBO.getGoodsNum().intValue());
                    singleDTO.setGoodsSkuId(goodsBO.getGoods().getId());
                    singleDTO.setPromotionId(goodsBO.getGoodsPromotion().getId());
                    dto.getSinglePromotions().add(singleDTO);
                }
            }

            PromotionDTO storePromotion = storeOrderBO.getStorePromotion();
            PromotionDTO freightPromotion = storeOrderBO.getFreightPromotion();
            PromotionDTO couponPromotion = storeOrderBO.getStoreCoupon();

            if (freightPromotion == null && storePromotion == null && couponPromotion == null) {
                continue;
            }

            StorePromotionConsumeDTO storeConsumeDTO = new StorePromotionConsumeDTO();
            storeConsumeDTO.setStoreId(storeOrderBO.getStore().getId());
            storeConsumeDTO.setOrderId(storeOrderBO.getOrderId());
            //店铺优惠活动
            if (storePromotion != null) {
                storeConsumeDTO.setPromotionId(storePromotion.getId());
                storeConsumeDTO.setType(storePromotion.getType().getCode());
            }

            //优惠券活动
            if (couponPromotion != null) {
                storeConsumeDTO.setPromotionId(couponPromotion.getId());
                storeConsumeDTO.setType(couponPromotion.getType().getCode());
            }

            //包邮活动
            if (freightPromotion != null) {
                storeConsumeDTO.setFreightPromotionId(freightPromotion.getId());
            }
            dto.getStorePromotions().add(storeConsumeDTO);


        }

        return dto;
    }

    /**
     * 检查是否需要操作
     *
     * @param consumeDTO
     * @return
     */
    private boolean needAction(PromotionConsumeDTO consumeDTO) {
        if (consumeDTO == null) {
            return false;
        }
        if (consumeDTO.getPlatformPromotionId() != null) {
            return true;
        }
        if (CollectionUtils.isNotEmpty(consumeDTO.getStorePromotions())) {
            return true;
        }
        if (CollectionUtils.isNotEmpty(consumeDTO.getSinglePromotions())) {
            return true;
        }
        return false;
    }


}