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
 * @date 2018-01-20 14:15
 */
@Component
public class ScoreDispatcher extends AbstractDispatcher {

    private BigDecimal PERCENT_DENOMINATOR = new BigDecimal(100);

    @Override
    void dispatchGoodsAmount(GoodsPayBO goodsPay, BigDecimal curDispatch) {
        goodsPay.setScore(curDispatch);
    }

    @Override
    void dispatchFreightAmount(StorePayBO storePay, BigDecimal curDispatch) {
        storePay.setFreightScore(curDispatch);
    }

    @Override
    Map<Long, BigDecimal> getGoodsDispatchLimitMap(List<StorePayBO> storePayDetails) {
        Map<Long, BigDecimal> goodsRestMap = new HashMap();
        for (StorePayBO storePayBO : storePayDetails) {
            for (GoodsPayBO goodsPay : storePayBO.getGoodsPayDetails()) {
                BigDecimal socreRate = goodsPay.getScoreRate() == null ? BigDecimal.ZERO : goodsPay.getScoreRate();
                goodsRestMap.put(goodsPay.getDetailId(), socreRate.divide(PERCENT_DENOMINATOR).multiply(goodsPay.getPayPrice()));
            }
        }
        return goodsRestMap;
    }
}