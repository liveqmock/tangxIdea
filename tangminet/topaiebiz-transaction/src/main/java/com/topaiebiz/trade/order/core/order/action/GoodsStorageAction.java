package com.topaiebiz.trade.order.core.order.action;

import com.topaiebiz.goods.dto.sku.StorageUpdateDTO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderGoodsBO;
import com.topaiebiz.trade.order.core.order.context.PayIdContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.facade.GoodsSkuServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-09 14:34
 */
@Component("goodsStorageAction")
public class GoodsStorageAction extends AbstractAction {

    @Autowired
    private GoodsSkuServiceFacade goodsSkuServiceFacade;

    @Override
    public boolean action(BuyerBO buyer, OrderSubmitContext paramContext, OrderRequest orderRequest) {
        Long payId = PayIdContext.get();
        return goodsSkuServiceFacade.descreaseStorages(payId, buildStorageParams(paramContext));
    }

    private List<StorageUpdateDTO> buildStorageParams(OrderSubmitContext paramContext) {
        List<StorageUpdateDTO> result = new ArrayList<>();
        for (StoreOrderBO storeOrderBO : paramContext.getStoreOrderMap().values()) {
            for (StoreOrderGoodsBO orderGoodsBO : storeOrderBO.getGoodsList()) {
                StorageUpdateDTO updateDTO = new StorageUpdateDTO();
                updateDTO.setSkuId(orderGoodsBO.getGoods().getId());
                updateDTO.setItemId(orderGoodsBO.getGoods().getItemId());
                updateDTO.setNum(orderGoodsBO.getGoodsNum().intValue());
                result.add(updateDTO);
            }
        }
        return result;
    }

    @Override
    public void rollback(BuyerBO buyer, OrderSubmitContext paramContext, OrderRequest orderRequest) {
        Long payId = PayIdContext.get();
        goodsSkuServiceFacade.inscreaseStorages(payId, buildStorageParams(paramContext));
    }
}