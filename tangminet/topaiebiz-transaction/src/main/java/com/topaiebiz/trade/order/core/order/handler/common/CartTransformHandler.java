package com.topaiebiz.trade.order.core.order.handler.common;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.cart.util.CartHelper;
import com.topaiebiz.trade.order.core.order.context.BuyerContext;
import com.topaiebiz.trade.order.core.order.context.CartIdContext;
import com.topaiebiz.trade.order.core.order.context.CartMapContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestGoods;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestStore;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.transaction.cart.entity.ShoppingCartEntity;
import com.topaiebiz.transaction.order.merchant.exception.StoreOrderExceptionEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 将OrderRequest中购物车来源入参转换为goodsId + goodsNum的形式，为续组件统一处理模型打下基础
 * @author yfeng
 * @date 2018-01-09 10:10
 */
@Component("cartTransformHandler")
public class CartTransformHandler implements OrderSubmitHandler {

    @Autowired
    private CartHelper cartHelper;

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        BuyerBO buyerBO = BuyerContext.get();
        //step 1 : 加载所有购物车ID
        List<Long> cartIds = new ArrayList<>();
        for (OrderRequestStore requestItem : orderRequest.getOrders()) {
            for (OrderRequestGoods goodsRequest : requestItem.getGoodsList()) {
                if (MathUtil.validEntityId(goodsRequest.getCartId())) {
                    cartIds.add(goodsRequest.getCartId());
                }
            }
        }
        if (CollectionUtils.isEmpty(cartIds)) {
            return;
        }

        //step 2 : 将购物车ID放入线程上下文
        CartIdContext.set(cartIds);

        //step 3 : 加载购物车信息
        Map<Long, List<ShoppingCartEntity>> storeCartsMap = cartHelper.queryCarts(buyerBO.getMemberId(), cartIds);
        validaeCarts(storeCartsMap, cartIds);

        //step 4 : 将goodsId + goodsNum写入每个OrderRequestGoods实体中
        Map<Long,Long> cartIdMap = new HashMap<>();
        for (OrderRequestStore requestItem : orderRequest.getOrders()) {
            Long storeId = requestItem.getStoreId();
            List<ShoppingCartEntity> carts = storeCartsMap.get(storeId);
            List<OrderRequestGoods> requestGoodsList = requestItem.getGoodsList();
            transforCartsInfo(cartIdMap, carts, requestGoodsList);
        }
        CartMapContext.set(cartIdMap);
    }

    private void transforCartsInfo(Map<Long,Long> skuToCartIdMap, List<ShoppingCartEntity> carts, List<OrderRequestGoods> requestGoodsList) {
        //step 1 : cartId->cartEntity的map
        Map<Long, ShoppingCartEntity> cartIdMap = new HashMap<>();
        for (ShoppingCartEntity cart : carts) {
            cartIdMap.put(cart.getId(), cart);
            skuToCartIdMap.put(cart.getGoodsId(), cart.getId());
        }
        //step 2 : 数据渲染
        for (OrderRequestGoods orderRequestGoods : requestGoodsList) {
            ShoppingCartEntity cart = cartIdMap.get(orderRequestGoods.getCartId());
            orderRequestGoods.setPromotionId(orderRequestGoods.getPromotionId());
            orderRequestGoods.setGoodsId(cart.getGoodsId());
            orderRequestGoods.setGoodsNum(cart.getGoodsNum());
        }
    }

    /**
     * 校验查询出的购物车数量与入参cartIds数量是否一致
     *
     * @param storeCartsMap
     * @param cartIds
     */
    private void validaeCarts(Map<Long, List<ShoppingCartEntity>> storeCartsMap, List<Long> cartIds) {
        //step 1 : 计算购物车数量
        int cartCount = 0;
        for (List<ShoppingCartEntity> cartsInStore : storeCartsMap.values()) {
            if (CollectionUtils.isNotEmpty(cartsInStore)) {
                cartCount += cartsInStore.size();
            }
        }

        //step 2 : 比较购物车数量和购物车ID数量是否匹配
        if (cartCount != cartIds.size()) {
            throw new GlobalException(StoreOrderExceptionEnum.PARAM_NOT_VALID);
        }
    }
}