package com.topaiebiz.trade.order.core.order.handler.submit;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.context.PromotionsContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.core.order.promotion.pattern.StoreCouponPattern;
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
@Component("storeCouponValidateHandler")
public class StoreCouponValidateHandler implements OrderSubmitHandler {

    @Autowired
    private StoreCouponPattern storeCouponPattern;

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        Map<Long, PromotionDTO> promotionDTOMap = PromotionsContext.get();
        Map<Long, StoreOrderBO> storeOrderMap = submitContext.getStoreOrderMap();

        for (OrderRequestStore storeOrderReq : orderRequest.getOrders()) {
            Long couponId = storeOrderReq.getCouponId();
            if (!MathUtil.validEntityId(couponId)) {
                continue;
            }

            PromotionDTO storeCouponDTO = promotionDTOMap.get(couponId);
            if (storeCouponDTO == null) {
                throw new GlobalException(PROMOTION_NOT_EXISTS);
            }
            StoreOrderBO storeOrderBO = storeOrderMap.get(storeOrderReq.getStoreId());
            //校验店铺订单是否满足此优惠活动
            if (!storeCouponPattern.match(storeOrderBO, storeCouponDTO)) {
                throw new GlobalException(PROMOTION_NOT_VALID);
            }
            //将活动价格渲染到店铺订单
            storeCouponPattern.dispatch(storeOrderBO, storeCouponDTO);
            //更新店铺订单价格信息
            storeOrderBO.updatePrice();
        }
    }
}