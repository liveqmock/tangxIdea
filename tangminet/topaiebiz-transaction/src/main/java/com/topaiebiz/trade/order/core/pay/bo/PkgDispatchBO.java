package com.topaiebiz.trade.order.core.pay.bo;

import com.topaiebiz.trade.order.util.MathUtil;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/***
 * 站内支付分摊数据模型
 * @author yfeng
 * @date 2018-01-19 13:18
 */
@Data
public abstract class PkgDispatchBO {

    /**
     * 礼卡明细
     */
    private Map<String, BigDecimal> cardsDetail = new HashMap();
    private BigDecimal cardAmount = BigDecimal.ZERO;

    /**
     * 积分明细
     **/
    private BigDecimal score = BigDecimal.ZERO;
    private Long socreNum = 0L;

    /**
     * 余额
     */
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 站内支付已经分摊总额
     */
    private BigDecimal pkgDispatchAmount = BigDecimal.ZERO;

    /**
     * 当前礼卡清单中，允许支付的礼卡数量
     */
    private int matchCardCount;

    public void matchCardCountIncrease() {
        matchCardCount++;
    }

    abstract boolean isDispatchFinished();

    abstract BigDecimal getUndispatchPrice();

    public void cleanDispatch() {
        cardsDetail.clear();
        score = BigDecimal.ZERO;
        balance = BigDecimal.ZERO;
        updatePrice();
    }

    /**
     * 增加礼卡明细
     *
     * @param cardNo
     * @param cardAmount
     */
    public void putCardDetail(String cardNo, BigDecimal cardAmount) {
        cardsDetail.put(cardNo, cardAmount);
    }


    protected void preClean() {
    }

    protected void preUpdate() {

    }

    protected BigDecimal freightCardAmount(){
        return BigDecimal.ZERO;
    }

    /**
     * 联动更新
     * 1. 更具礼卡明细，计算总的礼卡分摊金额
     * 2. 根据积分数目计算对应的金额
     */
    public void updatePrice() {
        preUpdate();

        //礼卡
        BigDecimal cardTotal = freightCardAmount();
        if (MapUtils.isNotEmpty(cardsDetail)) {
            for (BigDecimal item : cardsDetail.values()) {
                cardTotal = cardTotal.add(item);
            }
        }
        cardAmount = cardTotal;

        //积分
        socreNum = MathUtil.getFenVal(score);

        //站内分摊总额
        pkgDispatchAmount = cardAmount.add(score).add(balance);
    }
}