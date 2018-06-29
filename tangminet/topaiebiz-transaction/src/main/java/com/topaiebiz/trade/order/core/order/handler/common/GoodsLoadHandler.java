package com.topaiebiz.trade.order.core.order.handler.common;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderGoodsBO;
import com.topaiebiz.trade.order.core.order.context.SkuContext;
import com.topaiebiz.trade.order.core.order.context.SkuIdContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.facade.GoodsSkuServiceFacade;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestGoods;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.GOODS_LOAD_FAIL;
import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.GOODS_STORE_NOT_VALID;
import static com.topaiebiz.transaction.order.merchant.exception.StoreOrderExceptionEnum.PARAM_NOT_VALID;

/***
 * @author yfeng
 * @date 2018-01-09 17:55
 */
@Slf4j
@Component("goodsLoadHandler")
public class GoodsLoadHandler implements OrderSubmitHandler {

    @Autowired
    private GoodsSkuServiceFacade goodsSkuServiceFacade;

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        log.info("orderRequest >>>>> {}", JSON.toJSONString(orderRequest));
        log.info("SkuIdContext.get() is {}", JSON.toJSONString(SkuIdContext.get()));

        validateOrderRequest(orderRequest);

        //step 1 : 载入Sku ID数据
        if (SkuIdContext.get() == null) {
            saveSkuIdContext(orderRequest);
        }

        //step 2 : 批量加载Sku信息
        log.info("SkuIdContext.get() is {}", JSON.toJSONString(SkuIdContext.get()));
        if (SkuContext.get() == null) {
            //查询Sku信息，放入线程上下文
            List<Long> skuIds = SkuIdContext.get();
            Map<Long, GoodsSkuDTO> skuMap = goodsSkuServiceFacade.getGoodsSkuMap(skuIds);
            SkuContext.set(skuMap);
        }

        //step 3 : 将sku信息渲染到OrderSubmitContext中
        renderContext(submitContext, orderRequest);
    }

    private void validateOrderRequest(OrderRequest orderRequest) {
        for (OrderRequestStore requestStore : orderRequest.getOrders()) {
            for (OrderRequestGoods goodsRequest : requestStore.getGoodsList()) {
                if (goodsRequest.getGoodsNum() <= 0) {
                    throw new GlobalException(PARAM_NOT_VALID);
                }
            }
        }
    }

    private void renderContext(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        Map<Long, GoodsSkuDTO> skuMap = SkuContext.get();
        log.info("skuMap is {}", JSON.toJSONString(skuMap));
        Map<Long, StoreOrderBO> storeOrderMap = submitContext.getStoreOrderMap();

        for (OrderRequestStore orderStore : orderRequest.getOrders()) {
            Long storeId = orderStore.getStoreId();
            StoreOrderBO storeOrderBO = storeOrderMap.get(storeId);
            for (OrderRequestGoods requestGoods : orderStore.getGoodsList()) {
                GoodsSkuDTO skuDTO = skuMap.get(requestGoods.getGoodsId());
                if (skuDTO == null) {
                    throw new GlobalException(GOODS_LOAD_FAIL);
                }
                //根据SKU信息构建一个StoreOrderGoodsBO对象
                StoreOrderGoodsBO storeOrderGoodsBO = buildStoreOrderGoods(skuDTO, requestGoods);
                //更新商品价格信息
                storeOrderGoodsBO.updatePrice();
                if (!skuDTO.getItem().getBelongStore().equals(storeId)) {
                    //商品和店铺关系不符合
                    throw new GlobalException(GOODS_STORE_NOT_VALID);
                }
                storeOrderBO.getGoodsList().add(storeOrderGoodsBO);
            }
            //更新订单价格信息
            storeOrderBO.updatePrice();
        }
    }

    private StoreOrderGoodsBO buildStoreOrderGoods(GoodsSkuDTO skuDTO, OrderRequestGoods requestGoods) {
        StoreOrderGoodsBO goodsBO = new StoreOrderGoodsBO();
        goodsBO.setPromotionId(requestGoods.getPromotionId());
        goodsBO.setGoods(skuDTO);
        goodsBO.setGoodsNum(requestGoods.getGoodsNum());
        return goodsBO;
    }

    private void saveSkuIdContext(OrderRequest orderRequest) {
        List<Long> skuIds = new ArrayList<>();
        for (OrderRequestStore orderItem : orderRequest.getOrders()) {
            for (OrderRequestGoods requestGoods : orderItem.getGoodsList()) {
                skuIds.add(requestGoods.getGoodsId());
            }
        }
        SkuIdContext.set(skuIds);
    }
}