package com.topaiebiz.trade.order.core.pay.util;

import com.topaiebiz.trade.order.core.pay.bo.GoodsPayBO;
import com.topaiebiz.trade.order.core.pay.bo.StorePayBO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-20 16:22
 */
@Component
public class BalanceDispatcher extends AbstractDispatcher {

    @Override
    Map<Long, BigDecimal> getGoodsDispatchLimitMap(List<StorePayBO> storePayDetails) {
        Map<Long, BigDecimal> goodsRestMap = new HashMap();
        for (StorePayBO storePayBO : storePayDetails) {
            for (GoodsPayBO goodsPay : storePayBO.getGoodsPayDetails()) {
                goodsRestMap.put(goodsPay.getDetailId(), goodsPay.getPayPrice());
            }
        }
        return goodsRestMap;
    }

    @Override
    void dispatchGoodsAmount(GoodsPayBO goodsPay, BigDecimal curDispatch) {
        goodsPay.setBalance(curDispatch);
    }

    @Override
    void dispatchFreightAmount(StorePayBO storePay, BigDecimal curDispatch) {
        storePay.setFreightBalance(curDispatch);
    }
}