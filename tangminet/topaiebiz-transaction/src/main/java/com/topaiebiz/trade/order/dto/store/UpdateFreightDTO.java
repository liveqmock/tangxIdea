package com.topaiebiz.trade.order.dto.store;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/26 10:13
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class UpdateFreightDTO implements Serializable{
    private static final long serialVersionUID = -5012405548017804970L;

    /**
     * 订单ID
     */
    @NotNull(message = "#{validation.updateaFreight.orderId}")
    private Long orderId;

    /**
     * 运费
     */
    @NotNull(message = "#{validation.updateaFreight.freight}")
    private BigDecimal freight;

}
