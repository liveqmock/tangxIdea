package com.topaiebiz.trade.order.dto.store.statistics;

import lombok.Data;

import java.io.Serializable;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/13 10:00
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class RefundStatisticsDTO implements Serializable {

    private static final long serialVersionUID = 1980142567936098949L;

    /**
     * 全部售后
     */
    private Integer allRefundCounts = 0;

    /**
     * 待处理
     */
    private Integer waitProcessRefundCounts = 0;

    /**
     * 已处理
     */
    private Integer processedRefundCounts = 0;

    /**
     * 已拒绝
     */
    private Integer rejectedRefundCounts = 0;



}
