package com.topaiebiz.settlement.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-03-27 20:20
 */
@Data
public class SettlementRefundOrderStatisDTO implements Serializable{
    private static final long serialVersionUID = 658151311006920284L;

    private BigDecimal pointSum;
    private BigDecimal cardSum;
    private BigDecimal balanceSum;
    private BigDecimal cashSum;

    private BigDecimal promStoreSum;
    private BigDecimal promPlatformSum;
    private BigDecimal freight;
    private BigDecimal tax;

    private BigDecimal refundPrice;
    private BigDecimal platformCommission;
    private BigDecimal settleSum;
}