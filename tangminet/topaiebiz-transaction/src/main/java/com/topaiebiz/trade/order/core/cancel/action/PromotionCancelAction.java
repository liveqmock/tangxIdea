package com.topaiebiz.trade.order.core.cancel.action;

import com.topaiebiz.promotion.dto.PromotionConsumeDTO;
import com.topaiebiz.promotion.dto.SinglePromotionConsumeDTO;
import com.topaiebiz.promotion.dto.StorePromotionConsumeDTO;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import com.topaiebiz.trade.order.core.cancel.CancelParamContext;
import com.topaiebiz.trade.order.facade.PromotionServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum.PROMOTION_TYPE_REDUCE_PRICE;
import static com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum.PROMOTION_TYPE_STORE_COUPON;

/***
 * @author yfeng
 * @date 2018-01-21 19:04
 */
@Component("promotionCancelAction")
public class PromotionCancelAction implements CancelAction {

    @Autowired
    private PromotionServiceFacade promotionServiceFacade;

    @Override
    public boolean action(BuyerBO buyerBO, CancelParamContext context) {
        PromotionConsumeDTO param = buildPromotionConsumeDTO(context);
        if (MathUtil.validEntityId(param.getPlatformPromotionId()) || CollectionUtils.isNotEmpty(param.getStorePromotions()) || CollectionUtils.isNotEmpty(param.getSinglePromotions())) {
            return promotionServiceFacade.backPromotions(buyerBO.getMemberId(), param);
        }
        //没有需要执行回退的数据
        return true;
    }

    private PromotionConsumeDTO buildPromotionConsumeDTO(CancelParamContext paramContext) {
        PromotionConsumeDTO dto = new PromotionConsumeDTO();

        List<OrderEntity> orders = paramContext.getOrders();
        Long platformPromotionId = null;
        for (OrderEntity order : orders) {
            Long platPromId = order.getPlatformPromotionId();
            Long storePromId = order.getStorePromotionId();
            Long couponPromId = order.getStoreCouponId();
            Long freightPromId = order.getFreightPromotionId();

            //此订单无店铺级别和平台级别营销活动
            if (MathUtil.unvalidEntityId(platPromId) && MathUtil.unvalidEntityId(storePromId) && MathUtil.unvalidEntityId(freightPromId) && MathUtil.unvalidEntityId(couponPromId)) {
                continue;
            }
            if (MathUtil.validEntityId(platPromId)) {
                platformPromotionId = platPromId;
            }

            StorePromotionConsumeDTO storeConsumeDTO = new StorePromotionConsumeDTO();
            storeConsumeDTO.setStoreId(order.getStoreId());
            storeConsumeDTO.setOrderId(order.getId());
            dto.getStorePromotions().add(storeConsumeDTO);

            if (MathUtil.validEntityId(storePromId)) {
                storeConsumeDTO.setPromotionId(storePromId);
                storeConsumeDTO.setType(PROMOTION_TYPE_REDUCE_PRICE.getCode());
            }

            if (MathUtil.validEntityId(couponPromId)) {
                storeConsumeDTO.setPromotionId(couponPromId);
                storeConsumeDTO.setType(PROMOTION_TYPE_STORE_COUPON.getCode());
            }

            if (MathUtil.validEntityId(freightPromId)) {
                storeConsumeDTO.setFreightPromotionId(freightPromId);
            }
        }

        //单品优惠活动
        for (List<OrderDetailEntity> orderDetails : paramContext.getDetaiMaps().values()) {
            for (OrderDetailEntity detail : orderDetails) {
                if (MathUtil.validEntityId(detail.getPromotionId())) {
                    SinglePromotionConsumeDTO singleDTO = new SinglePromotionConsumeDTO();
                    singleDTO.setGoodsNum(detail.getGoodsNum().intValue());
                    singleDTO.setGoodsSkuId(detail.getSkuId());
                    singleDTO.setPromotionId(detail.getPromotionId());
                    dto.getSinglePromotions().add(singleDTO);
                }
            }
        }

        dto.setPlatformPromotionId(platformPromotionId);
        dto.setPlatformPromotionType(PromotionTypeEnum.PROMOTION_TYPE_COUPON.getCode());
        dto.setPayId(paramContext.getPayEntity().getId());
        return dto;
    }
}