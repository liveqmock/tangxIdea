package com.topaiebiz.trade.order.core.pay.util;

import com.topaiebiz.trade.order.core.pay.bo.GoodsPayBO;
import com.topaiebiz.trade.order.core.pay.bo.StorePayBO;
import com.topaiebiz.trade.order.util.MathUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-20 16:24
 */
public abstract class AbstractDispatcher {

    abstract void dispatchGoodsAmount(GoodsPayBO goodsPay, BigDecimal curDispatch);

    abstract void dispatchFreightAmount(StorePayBO storePay, BigDecimal curDispatch);

    /**
     * 获取单品分摊限制清单
     * 1. 积分支付得将积分可兑换比例算入
     * 2. 余额无限制，即为商品应付金额
     *
     * @return
     */
    abstract Map<Long, BigDecimal> getGoodsDispatchLimitMap(List<StorePayBO> storePayDetails);

    public BigDecimal dispatch(BigDecimal amountLimit, List<StorePayBO> storePayDetails) {
        Map<Long, BigDecimal> goodsRestMap = getGoodsDispatchLimitMap(storePayDetails);

        BigDecimal dispatchRest = amountLimit;
        storeDispatch:
        for (StorePayBO storePayBO : storePayDetails) {
            //若店铺订单已经分摊完毕，则跳过此店铺
            if (storePayBO.isDispatchFinished() || MathUtil.sameValue(dispatchRest, BigDecimal.ZERO)) {
                continue storeDispatch;
            }
            goodsDispatch:
            for (GoodsPayBO goodsPay : storePayBO.getGoodsPayDetails()) {
                //此商品已经使用站内支付分摊完毕，跳过此商品
                if (goodsPay.isDispatchFinished() || MathUtil.sameValue(dispatchRest, BigDecimal.ZERO)) {
                    continue goodsDispatch;
                }
                //此商品剩余可分配金额
                BigDecimal goodsRest = goodsRestMap.get(goodsPay.getDetailId());

                //取礼卡剩余、商品剩余、用户提交礼卡分摊剩余中最小值进行分摊，防止计算超额
                BigDecimal curDispatch = MathUtil.min(dispatchRest, goodsPay.getUndispatchPrice(), goodsRest);

                //此商品无法在做分摊，直接跳过
                if (MathUtil.sameValue(curDispatch, BigDecimal.ZERO)) {
                    continue goodsDispatch;
                }
                dispatchGoodsAmount(goodsPay, curDispatch);

                //记录分摊消耗
                dispatchRest = dispatchRest.subtract(curDispatch);
                //更新商品剩余分摊
                goodsRestMap.put(goodsPay.getDetailId(), goodsRest.subtract(curDispatch));
                goodsPay.updatePrice();
            }

            //运费分摊
            BigDecimal freightDispatch = MathUtil.min(dispatchRest, storePayBO.getUndispatchDispatchFreight());
            if (MathUtil.greaterThanZero(freightDispatch)) {
                dispatchFreightAmount(storePayBO, freightDispatch);
                //记录分摊消耗
                dispatchRest = dispatchRest.subtract(freightDispatch);
            }

            storePayBO.updatePrice();
        }

        return amountLimit.subtract(dispatchRest);
    }
}