package com.topaiebiz.trade.order.core.pay.bo;

import com.topaiebiz.trade.order.util.MathUtil;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-19 12:56
 */
@Data
public class StorePayBO extends PkgDispatchBO {

    /**
     * 店铺订单ID
     */
    private Long orderId;
    /**
     * 店铺ID
     */
    private Long storeId;
    /**
     * 店铺名称
     */
    private String storeName;
    /**
     * 应付金额
     */
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private BigDecimal freightPrice = BigDecimal.ZERO;
    private BigDecimal goodsPrice = BigDecimal.ZERO;

    /**
     * 支付详情ID - 支付详情
     **/
    private List<GoodsPayBO> goodsPayDetails = new ArrayList<>();

    private Map<String, BigDecimal> cardsFreightDetail = new HashMap();
    private BigDecimal freightScore = BigDecimal.ZERO;
    private BigDecimal freightBalance = BigDecimal.ZERO;

    @Override
    public boolean isDispatchFinished() {
        return MathUtil.sameValue(totalPrice, getPkgDispatchAmount());
    }

    @Override
    public BigDecimal getUndispatchPrice() {
        return totalPrice.subtract(getPkgDispatchAmount());
    }

    public BigDecimal getUndispatchDispatchFreight() {
        BigDecimal cardDispatchAmount = BigDecimal.ZERO;
        if (MapUtils.isNotEmpty(cardsFreightDetail)) {
            for (BigDecimal dipatch : cardsFreightDetail.values()) {
                cardDispatchAmount = cardDispatchAmount.add(dipatch);
            }
        }
        return freightPrice.subtract(cardDispatchAmount).subtract(freightBalance).subtract(freightScore);
    }

    @Override
    protected void preClean() {
        for (GoodsPayBO goodsPay : goodsPayDetails) {
            goodsPay.cleanDispatch();
        }
    }

    /**
     * 汇总每个商品的分摊结果到店铺订单上来
     */
    @Override
    protected void preUpdate() {
        Map<String, BigDecimal> storeCardsDetail = new HashMap<>();
        BigDecimal score = BigDecimal.ZERO;
        BigDecimal balance = BigDecimal.ZERO;
        for (GoodsPayBO goodsPay : goodsPayDetails) {
            goodsPay.updatePrice();

            //商品的积分支付求和
            score = score.add(goodsPay.getScore());

            //商品的余额支付求和
            balance = balance.add(goodsPay.getBalance());

            //商品礼卡支付汇总
            Map<String, BigDecimal> goodsCardDetail = goodsPay.getCardsDetail();
            for (Map.Entry<String, BigDecimal> freightCardEntry : goodsCardDetail.entrySet()) {
                String cardNo = freightCardEntry.getKey();
                BigDecimal cardAmount = freightCardEntry.getValue();
                if (storeCardsDetail.containsKey(cardNo)) {
                    BigDecimal val = storeCardsDetail.get(cardNo).add(cardAmount);
                    storeCardsDetail.put(cardNo, val);
                } else {
                    storeCardsDetail.put(cardNo, cardAmount);
                }
            }
        }

        score = score.add(freightScore);
        balance = balance.add(freightBalance);
        //属性赋值
        setCardsDetail(storeCardsDetail);
        setScore(score);
        setBalance(balance);
    }

    @Override
    protected BigDecimal freightCardAmount() {
        BigDecimal cardTotal = BigDecimal.ZERO;
        if (MapUtils.isNotEmpty(cardsFreightDetail)) {
            for (BigDecimal item : cardsFreightDetail.values()) {
                cardTotal = cardTotal.add(item);
            }
        }
        return cardTotal;
    }
}