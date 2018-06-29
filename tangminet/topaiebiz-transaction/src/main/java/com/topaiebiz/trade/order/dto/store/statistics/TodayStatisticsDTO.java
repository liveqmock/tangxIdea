package com.topaiebiz.trade.order.dto.store.statistics;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description 今日统计
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/13 10:06
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@NoArgsConstructor
public class TodayStatisticsDTO implements Serializable{
    private static final long serialVersionUID = 31979668633307000L;

    /**
     * 下单人数
     */
    private Integer orderCustomersNum = 0;

    /**
     * 订单总金额
     */
    private BigDecimal orderTotalAmount = BigDecimal.ZERO;

    /**
     * 新增会员
     */
    private Integer newCustomersNum = 0;


    public TodayStatisticsDTO(Integer orderCustomersNum, BigDecimal orderTotalAmount, Integer newCustomersNum) {
        this.orderCustomersNum = orderCustomersNum;
        this.orderTotalAmount = orderTotalAmount;
        this.newCustomersNum = newCustomersNum;
    }
}
