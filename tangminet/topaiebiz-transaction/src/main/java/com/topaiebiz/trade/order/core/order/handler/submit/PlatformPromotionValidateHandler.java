package com.topaiebiz.trade.order.core.order.handler.submit;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.trade.order.core.order.context.PromotionsContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.core.order.promotion.pattern.PlatformPromotionPattern;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.PROMOTION_NOT_EXISTS;
import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.PROMOTION_NOT_VALID;

/***
 * @author yfeng
 * @date 2018-01-09 17:01
 */
@Component("platformPromotionValidateHandler")
public class PlatformPromotionValidateHandler implements OrderSubmitHandler {

    @Autowired
    private PlatformPromotionPattern platformPromotionPattern;

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        Map<Long, PromotionDTO> promotionDTOMap = PromotionsContext.get();

        //未使用平台优惠活动
        if (!MathUtil.validEntityId(orderRequest.getPlatformPromotionId())) {
            return;
        }
        PromotionDTO promotionDTO = promotionDTOMap.get(orderRequest.getPlatformPromotionId());
        if (promotionDTO == null) {
            throw new GlobalException(PROMOTION_NOT_EXISTS);
        }
        if (!platformPromotionPattern.match(submitContext, promotionDTO)) {
            throw new GlobalException(PROMOTION_NOT_VALID);
        }
        platformPromotionPattern.dispatch(submitContext, promotionDTO);
        submitContext.setPlatformPromotion(promotionDTO);
    }
}
