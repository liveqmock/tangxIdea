package com.topaiebiz.trade.refund.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description 审核 参数 DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/21 13:58
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class RefundAuditdDTO implements Serializable{

    private static final long serialVersionUID = 3518878672322647849L;

    /**
     * 售后订单ID
     */
    @NotNull(message = "#{validation.refund.orderId}")
    private Long refundOrderId;

    /**
     * 1 通过； 0 拒绝
     */
    @NotNull(message = "#{validation.refund.result}")
    private Integer result;

    /**
     * 拒绝原因
     */
    private String refuseDescription;

}
