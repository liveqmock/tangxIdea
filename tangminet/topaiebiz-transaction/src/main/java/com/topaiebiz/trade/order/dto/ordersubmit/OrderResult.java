package com.topaiebiz.trade.order.dto.ordersubmit;

import lombok.Data;

/***
 * @author yfeng
 * @date 2018-01-16 20:52
 */
@Data
public class OrderResult {
    private String payId;

    public static OrderResult buildResult(Long payId) {
        OrderResult result = new OrderResult();
        result.setPayId(payId.toString());
        return result;
    }
}