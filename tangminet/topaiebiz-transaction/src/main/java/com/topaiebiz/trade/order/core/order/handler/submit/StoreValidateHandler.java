package com.topaiebiz.trade.order.core.order.handler.submit;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.constants.MerchantConstants;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.trade.order.core.order.context.StoreContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.STORE_FROZE_ERROR;

/***
 * @author yfeng
 * @date 2018-03-05 13:57
 */
@Component("storeValidateHandler")
public class StoreValidateHandler implements OrderSubmitHandler{

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        Map<Long, StoreInfoDetailDTO> storesMap = StoreContext.get();
        for (StoreInfoDetailDTO store : storesMap.values()) {
            //店铺已经冻结
            if (MerchantConstants.StoreStatus.FROZED.equals(store.getChangeState())){
                throw new GlobalException(STORE_FROZE_ERROR);
            }
        }
    }
}