package com.topaiebiz.trade.order.core.pay.util;

import com.topaiebiz.trade.order.core.pay.bo.GoodsPayBO;
import com.topaiebiz.trade.order.core.pay.bo.StorePayBO;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-19 13:52
 */
public class StorePayUtil {
    private static final BigDecimal USEFUL_SOCRE_RATE = new BigDecimal(100);

    /**
     * 计算积分可以使用总额
     * @param storePays
     * @return
     */
    public static BigDecimal getScoreSupportAmount(List<StorePayBO> storePays) {
        BigDecimal scoreLimit = BigDecimal.ZERO;
        for (StorePayBO storePay : storePays) {
            //计算商品可用积分总额
            for (GoodsPayBO goodsPay : storePay.getGoodsPayDetails()) {
                if (goodsPay.getScoreRate() != null){
                    BigDecimal curLimit = goodsPay.getScoreRate().divide(MathUtil.fenRate) .multiply(goodsPay.getPayPrice());
                    scoreLimit = scoreLimit.add(curLimit);
                }
            }
            //增加运费对应金额
            scoreLimit = scoreLimit.add(storePay.getFreightPrice());
        }
        return scoreLimit;
    }


    public static List<StorePayBO> buildStorePayDetails(List<OrderEntity> orderList, Map<Long, List<OrderDetailEntity>> orderDetailsMap) {
        List<StorePayBO> storePays = new ArrayList<>();
        for (OrderEntity orderEntity : orderList) {
            //店铺应支付信息详情
            StorePayBO storePayBO = new StorePayBO();
            storePayBO.setStoreId(orderEntity.getStoreId());
            storePayBO.setStoreName(orderEntity.getStoreName());
            storePayBO.setOrderId(orderEntity.getId());
            storePayBO.setFreightPrice(orderEntity.getActualFreight() == null ? BigDecimal.ZERO : orderEntity.getActualFreight());
            BigDecimal goodsPayPrice = orderEntity.getPayPrice().subtract(storePayBO.getFreightPrice());
            storePayBO.setGoodsPrice(goodsPayPrice);
            storePayBO.setTotalPrice(orderEntity.getPayPrice());

            //计算店铺的商品快照应支付详情
            List<OrderDetailEntity> orderDetails = orderDetailsMap.get(orderEntity.getId());
            for (OrderDetailEntity orderDetailEntity : orderDetails) {
                GoodsPayBO goodsPay = new GoodsPayBO();
                goodsPay.setDetailId(orderDetailEntity.getId());
                goodsPay.setSkuId(orderDetailEntity.getSkuId());
                goodsPay.setItemId(orderDetailEntity.getItemId());
                goodsPay.setGoodsName(orderDetailEntity.getName());
                goodsPay.setScoreRate(orderDetailEntity.getScoreRate());
                goodsPay.setPayPrice(orderDetailEntity.getPayPrice());
                goodsPay.updatePrice();
                //记录到店铺支付详情里面
                storePayBO.getGoodsPayDetails().add(goodsPay);
            }

            storePayBO.updatePrice();
            storePays.add(storePayBO);
        }
        return storePays;
    }
}
