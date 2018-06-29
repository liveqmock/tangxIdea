package com.topaiebiz.trade.order.po.pay;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author yfeng
 * @date 2018.1.8
 */
@Data
public class PayRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long payId;
    private BigDecimal cardAmount = BigDecimal.ZERO;
    private BigDecimal balance = BigDecimal.ZERO;
    private BigDecimal score = BigDecimal.ZERO;

    private String thirdPayMethod;
    private BigDecimal thirdPayAmount = BigDecimal.ZERO;
    private String password;
}