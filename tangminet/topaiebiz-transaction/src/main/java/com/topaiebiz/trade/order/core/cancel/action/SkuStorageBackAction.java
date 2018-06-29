package com.topaiebiz.trade.order.core.cancel.action;

import com.topaiebiz.goods.dto.sku.StorageUpdateDTO;
import com.topaiebiz.trade.order.core.cancel.CancelParamContext;
import com.topaiebiz.trade.order.facade.GoodsSkuServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-21 19:03
 */
@Component("skuStorageBackAction")
public class SkuStorageBackAction implements CancelAction {

    @Autowired
    private GoodsSkuServiceFacade skuServiceFacade;

    @Override
    public boolean action(BuyerBO buyerBO, CancelParamContext context) {
        List<StorageUpdateDTO> updates = new ArrayList<>();
        for (List<OrderDetailEntity> details : context.getDetaiMaps().values()) {
            for (OrderDetailEntity detail : details) {
                StorageUpdateDTO updateDTO = new StorageUpdateDTO();
                updateDTO.setSkuId(detail.getSkuId());
                updateDTO.setNum(detail.getGoodsNum().intValue());
                updateDTO.setItemId(detail.getItemId());
                updates.add(updateDTO);
            }
        }
        return skuServiceFacade.inscreaseStorages(context.getPayEntity().getId(), updates);
    }
}
