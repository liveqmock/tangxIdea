package com.topaiebiz.trade.order.dto.ordersubmit;

import com.topaiebiz.trade.order.util.MathUtil;
import lombok.Data;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-01-09 11:23
 */
@Data
public class OrderSummaryDTO {
    private BigDecimal goodsAmount = BigDecimal.ZERO;
    private BigDecimal freightAmount = BigDecimal.ZERO;
    private BigDecimal storePromotion = BigDecimal.ZERO;
    private BigDecimal storeCoupon = BigDecimal.ZERO;
    private BigDecimal platformPromotion = BigDecimal.ZERO;

    private BigDecimal payAmount = BigDecimal.ZERO;

    /**
     * 是否是0元订单
     *
     * @return
     */
    public boolean zeroAmountOrder() {
        return MathUtil.sameValue(payAmount, BigDecimal.ZERO);
    }
}