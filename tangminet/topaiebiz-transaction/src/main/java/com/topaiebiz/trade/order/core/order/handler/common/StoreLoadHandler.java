package com.topaiebiz.trade.order.core.order.handler.common;

import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.context.StoreContext;
import com.topaiebiz.trade.order.core.order.context.StoreIdContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.facade.StoreServiceFacade;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/***
 * 加载
 * @author yfeng
 * @date 2018-01-09 17:55
 */
@Component("storeLoadHandler")
public class StoreLoadHandler implements OrderSubmitHandler {

    @Autowired
    private StoreServiceFacade storeServiceFacade;

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        // step 1 : 批量查询店铺信息
        List<Long> storeIds = orderRequest.getOrders().stream().map(store -> store.getStoreId()).collect(Collectors.toList());
        Map<Long, StoreInfoDetailDTO> storesMap = storeServiceFacade.getStoreMap(storeIds);

        // step 2 : 计算店铺的海淘标记
        boolean hasHaitaoOrder = false;
        for (StoreInfoDetailDTO store : storesMap.values()) {
            if (OrderConstants.HaitaoFlag.YES.equals(store.getHaitao())) {
                hasHaitaoOrder = true;
                break;
            }
        }
        submitContext.setHasHaitaoOrder(hasHaitaoOrder);

        // step 2 : 将店铺信息放入线程上下文
        StoreContext.set(storesMap);
        StoreIdContext.set(storeIds);

        // step 3 : 渲染订单请求的店铺信息
        renderContext(submitContext, storesMap, orderRequest);
    }

    private void renderContext(OrderSubmitContext submitContext, Map<Long, StoreInfoDetailDTO> storesMap, OrderRequest orderRequest) {
        for (OrderRequestStore orderRequestStore : orderRequest.getOrders()) {
            StoreInfoDetailDTO storeInfo = storesMap.get(orderRequestStore.getStoreId());

            StoreOrderBO storeOrderBO = new StoreOrderBO();
            storeOrderBO.setStore(storeInfo);

            //增加 StoreOrderBO
            submitContext.getStoreOrderMap().put(storeInfo.getId(), storeOrderBO);
        }

    }
}