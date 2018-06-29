package com.topaiebiz.trade.order.core.order.util;

import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.dto.ordersubmit.OrderSummaryDTO;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-01-16 19:32
 */
public class OrderSummaryUtil {

    public static OrderSummaryDTO buildSummary(OrderSubmitContext orderContext) {
        OrderSummaryDTO orderSummaryDTO = new OrderSummaryDTO();

        BigDecimal goodsAmount = BigDecimal.ZERO;
        BigDecimal freightAmount = BigDecimal.ZERO;
        BigDecimal storePromotion = BigDecimal.ZERO;
        BigDecimal storeCoupon = BigDecimal.ZERO;
        BigDecimal platformPromotion = BigDecimal.ZERO;
        BigDecimal payAmount = BigDecimal.ZERO;

        for (StoreOrderBO storeOrderBO : orderContext.getStoreOrderMap().values()) {
            //商品价格 = 商品原价- 单品优惠
            goodsAmount = goodsAmount.add(storeOrderBO.getGoodsAmount()).subtract(storeOrderBO.getTotalGoodsDiscount());
            //店铺满减
            storePromotion = storePromotion.add(storeOrderBO.getStoreDiscount());
            //店铺优惠券
            storeCoupon = storeCoupon.add(storeOrderBO.getStoreCouponDiscount());
            //邮费 = 商品邮费 - 包邮优惠
            freightAmount = freightAmount.add(storeOrderBO.getGoodsFreight()).subtract(storeOrderBO.getFreightDiscount());
            //平台优惠
            platformPromotion = platformPromotion.add(storeOrderBO.getPlatformDiscount());
            //应付金额
            payAmount = payAmount.add(storeOrderBO.getPayPrice());
        }
        orderSummaryDTO.setGoodsAmount(goodsAmount);
        orderSummaryDTO.setStorePromotion(storePromotion);
        orderSummaryDTO.setStoreCoupon(storeCoupon);
        orderSummaryDTO.setFreightAmount(freightAmount);
        orderSummaryDTO.setPlatformPromotion(platformPromotion);
        orderSummaryDTO.setPayAmount(payAmount);
        return orderSummaryDTO;
    }
}
