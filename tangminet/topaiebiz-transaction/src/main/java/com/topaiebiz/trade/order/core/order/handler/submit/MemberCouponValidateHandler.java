package com.topaiebiz.trade.order.core.order.handler.submit;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.order.core.order.context.BuyerContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.facade.PromotionServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestStore;
import com.topaiebiz.trade.order.util.MathUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.PROMOTION_NOT_VALID;

/***
 * @author yfeng
 * @date 2018-01-09 17:01
 */
@Component("memberCouponValidateHandler")
public class MemberCouponValidateHandler implements OrderSubmitHandler {

    @Autowired
    private PromotionServiceFacade promotionServiceFacade;

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        List<Long> couponIds = new ArrayList<>();
        BuyerBO buyerBO = BuyerContext.get();

        //step 1 : 收集优惠券ID
        //step 1.1 收集店铺优惠券ID
        for (OrderRequestStore storeOrderReq : orderRequest.getOrders()) {
            Long couponId = storeOrderReq.getCouponId();
            if (MathUtil.unvalidEntityId(couponId)) {
                continue;
            }
            couponIds.add(couponId);
        }
        //step 2.1 收集平台优惠券ID
        if (MathUtil.validEntityId(orderRequest.getPlatformPromotionId())) {
            couponIds.add(orderRequest.getPlatformPromotionId());
        }

        if (CollectionUtils.isEmpty(couponIds)) {
            //未使用优惠券，直接跳过此handler
            return;
        }

        //step 2 : 批量检查
        boolean checkResult = promotionServiceFacade.checkHoldStatus(buyerBO.getMemberId(), couponIds);
        if (!checkResult){
            throw new GlobalException(PROMOTION_NOT_VALID);
        }
    }
}