package com.topaiebiz.trade.dto.order;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Description 订单发货参数DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/13 14:31
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderDeliveryDTO implements Serializable {

    private static final long serialVersionUID = -935141090548217903L;

    @NotNull(message = "#{validation.delivery.orderId}")
    private Long orderId;

    private List<Long> orderDetailIds;

    @NotNull(message = "#{validation.delivery.expressComId}")
    private Long expressComId;

    private String expressComName;

    @NotNull(message = "#{validation.delivery.expressNo}")
    private String expressNo;

    /**
     * 物流公司CODE
     */
    private String expressComCode;

    /**
     * 发货人ID (仅用于后台传参)
     */
    private Long memberId;

    /**
     * 店铺ID
     */
    private Long storeId;
}
