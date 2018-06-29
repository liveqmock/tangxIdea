package com.topaiebiz.trade.order.dto.ordersubmit;

import lombok.Data;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-02-02 15:29
 */
@Data
public class OrderAmountDTO {
    private BigDecimal payPrice;
    private BigDecimal freightAmount;
}