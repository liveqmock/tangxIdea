package com.topaiebiz.trade.order.core.order.handler.pageinit;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.trade.cart.util.CartHelper;
import com.topaiebiz.trade.order.core.order.context.SkuContext;
import com.topaiebiz.trade.order.core.order.context.SkuIdContext;
import com.topaiebiz.trade.order.facade.GoodsSkuServiceFacade;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestGoods;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestStore;
import com.topaiebiz.trade.order.po.ordersubmit.PageInitPO;
import com.topaiebiz.transaction.cart.entity.ShoppingCartEntity;
import com.topaiebiz.transaction.order.merchant.exception.StoreOrderExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-10 15:09
 */
@Slf4j
@Component
public class OrderRequestHandler {

    @Autowired
    private GoodsSkuServiceFacade skuServiceFacade;

    @Autowired
    private CartHelper cartHelper;

    public OrderRequest loadOrderRequest(Long memberId, PageInitPO pageInitPO) {
        log.info(">>>> pageInitPO: {}", JSON.toJSONString(pageInitPO));
        OrderRequest orderRequest = new OrderRequest();
        if (pageInitPO.cartMode()) {
            Map<Long, List<ShoppingCartEntity>> storeCartsMap = cartHelper.queryCarts(memberId, pageInitPO.getCartIds());
            for (Map.Entry<Long, List<ShoppingCartEntity>> entry : storeCartsMap.entrySet()) {
                Long storeId = entry.getKey();
                List<ShoppingCartEntity> carts = entry.getValue();
                orderRequest.getOrders().add(buildOrderRequest(storeId, carts));
            }
        } else {
            //直接下单模式
            Long skuId = pageInitPO.getGoodsId();
            GoodsSkuDTO skuDTO = skuServiceFacade.getGoodsSku(skuId);
            if (skuDTO == null) {
                throw new GlobalException(StoreOrderExceptionEnum.SKU_NOT_EXIST);
            }
            skuServiceFacade.loadSaleAttributes(Lists.newArrayList(skuDTO));

            //将SKU信息存入线程上下文
            SkuIdContext.set(Lists.newArrayList(skuId));
            SkuContext.set(Lists.newArrayList(skuDTO));

            orderRequest.getOrders().add(buildOrderRequest(skuDTO, pageInitPO.getGoodsNum()));
        }
        return orderRequest;
    }

    private OrderRequestStore buildOrderRequest(GoodsSkuDTO skuDTO, Long goodsNum) {
        List<OrderRequestGoods> requestGoodsList = new ArrayList<>();

        OrderRequestGoods requestGoods = new OrderRequestGoods();
        requestGoods.setGoodsId(skuDTO.getId());
        requestGoods.setGoodsNum(goodsNum);
        requestGoodsList.add(requestGoods);

        return OrderRequestStore.build(skuDTO.getItem().getBelongStore(), requestGoodsList);
    }

    private OrderRequestStore buildOrderRequest(Long storeId, List<ShoppingCartEntity> carts) {
        List<OrderRequestGoods> requestGoodsList = new ArrayList<>();
        for (ShoppingCartEntity cart : carts) {
            //构建单个商品请求
            OrderRequestGoods requestGoods = new OrderRequestGoods();
            requestGoods.setGoodsId(cart.getGoodsId());
            requestGoods.setGoodsNum(cart.getGoodsNum());
            requestGoods.setCartId(cart.getId());

            //加入店铺订单商品清单
            requestGoodsList.add(requestGoods);
        }
        return OrderRequestStore.build(storeId, requestGoodsList);
    }
}
