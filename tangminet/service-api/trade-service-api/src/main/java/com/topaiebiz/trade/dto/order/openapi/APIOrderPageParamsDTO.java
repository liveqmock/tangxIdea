package com.topaiebiz.trade.dto.order.openapi;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/26 17:41
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class APIOrderPageParamsDTO implements Serializable{
    private static final long serialVersionUID = -8127863750246864198L;

    /**
     * 订单状态, 订单ID, 订单创建查询起始时间, 订单创建查询结束时间, 当前页, 当前页面大小
     */
    private Integer orderState;
    private Long orderId;
    private Date orderStartTime;
    private Date orderEndTime;
    private Integer pageNo;
    private Integer pageSize;
}
