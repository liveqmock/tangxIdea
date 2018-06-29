package com.topaiebiz.trade.order.core.order.promotion.pattern;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.PROMOTION_NOT_VALID;

/***
 * @author yfeng
 * @date 2018-01-11 11:23
 */
@Component
@Slf4j
public class StorePromotionPattern extends BaseStorePromotionPattern {

    @Override
    protected void validatePromotionType(PromotionDTO promotionDTO) {
        if (PromotionTypeEnum.PROMOTION_TYPE_REDUCE_PRICE == promotionDTO.getType()) {
            return;
        }
        log.warn("input promotion {} has code : {}", promotionDTO.getId(), JSON.toJSONString(promotionDTO.getType()));
        throw new GlobalException(PROMOTION_NOT_VALID);
    }

    @Override
    public PromotionTypeEnum getMatchPromotionType() {
        return PromotionTypeEnum.PROMOTION_TYPE_REDUCE_PRICE;
    }

    @Override
    public void updateStoredOrderDiscount(StoreOrderBO storeOrderBO, PromotionDTO promotionDTO, BigDecimal promotionDiscount) {
        storeOrderBO.setStorePromotion(promotionDTO);
        storeOrderBO.setStoreDiscount(promotionDiscount);
    }
}