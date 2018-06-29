package com.topaiebiz.trade.order.service;

import com.topaiebiz.trade.order.dto.ordersubmit.*;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestStore;
import com.topaiebiz.trade.order.po.ordersubmit.PageInitPO;

import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-09 10:59
 */
public interface OrderSubmitService {

    GoodsSplitDTO loadInitPage(BuyerBO buyerBO, PageInitPO pageInitPO);

    List<PromotionInfoDTO> loadGoodsPromotions(BuyerBO buyerBO, Long goodsId, Long goodsNum);

    PromotionListDTO loadStoreCoupons(BuyerBO buyerBO, OrderRequestStore storeRequest);

    List<PromotionInfoDTO> storePromotions(BuyerBO buyerBO, OrderRequestStore storeRequest);

    List<PromotionInfoDTO> loadFreightPromotions(BuyerBO buyerBO, OrderRequestStore storeRequest);

    PromotionListDTO loadPlatformPromotions(BuyerBO buyerBO, OrderRequest orderRequest);

    OrderSummaryDTO getOrderSummary(BuyerBO buyerBO, OrderRequest orderRequest);

    OrderResult submitOrder(BuyerBO buyerBO, OrderRequest orderRequest);

    OrderAmountDTO caculateOrderAmount(BuyerBO buyerBO, OrderRequestStore storeRequest);
}