package com.topaiebiz.trade.dto.order.params;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Description 订单查询条件--提供给API的
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/27 9:16
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderQueryParams implements Serializable {
    private static final long serialVersionUID = -830070212125224768L;

    /**
     * 店铺ID, 订单状态, 订单ID
     */
    private Long storeId;
    private Integer orderState;
    private Long orderId;


    /**
     * 订单创建查询起始时间, 订单创建查询结束时间,
     */
    private Date orderStartTime;
    private Date orderEndTime;


    /**
     * 分页
     */
    private Integer pageNo;
    private Integer pageSize;
}
