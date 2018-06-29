package com.topaiebiz.trade.order.core.order.handler.submit;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.context.PromotionsContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.core.order.promotion.pattern.StorePromotionPattern;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestStore;
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
@Component("storePromotionValidateHandler")
public class StorePromotionValidateHandler implements OrderSubmitHandler {
    @Autowired
    private StorePromotionPattern storePromotionPattern;

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        Map<Long, PromotionDTO> promotionDTOMap = PromotionsContext.get();
        Map<Long, StoreOrderBO> storeOrderMap = submitContext.getStoreOrderMap();
        for (OrderRequestStore storeOrderReq : orderRequest.getOrders()) {
            Long promotionId = storeOrderReq.getPromotionId();
            if (!MathUtil.validEntityId(promotionId)) {
                continue;
            }
            PromotionDTO storePromotionDTO = promotionDTOMap.get(promotionId);
            if (storePromotionDTO == null) {
                throw new GlobalException(PROMOTION_NOT_EXISTS);
            }
            StoreOrderBO storeOrderBO = storeOrderMap.get(storeOrderReq.getStoreId());
            //校验店铺订单是否满足此优惠活动
            if (!storePromotionPattern.match(storeOrderBO, storePromotionDTO)) {
                throw new GlobalException(PROMOTION_NOT_VALID);
            }
            //将活动价格渲染到店铺订单
            storePromotionPattern.dispatch(storeOrderBO, storePromotionDTO);
        }
    }
}