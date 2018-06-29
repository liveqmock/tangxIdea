package com.topaiebiz.trade.order.dto.pay;

import lombok.Data;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-01-17 22:16
 */
@Data
public class PayedSummaryDTO {

    /**
     * 礼卡已支付金额
     */
    private BigDecimal cardAmount = BigDecimal.ZERO;
    /**
     * 礼卡已支付金额
     */
    private BigDecimal balanceAmount = BigDecimal.ZERO;
    /**
     * 积分已支付金额
     */
    private BigDecimal scoreAmount = BigDecimal.ZERO;
    /**
     * 已使用积分数量
     */
    private Long usedScore;

    /**
     * 总共已经支付
     */
    private BigDecimal totalPayed = BigDecimal.ZERO;

    public void updatePrice() {
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(cardAmount).add(balanceAmount).add(scoreAmount);
        totalPayed = total;
    }
}
