package com.topaiebiz.trade.order.dto.store;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description 修改发货地址
 * 需求改为 新的收货地址 拼接在memo(备注)中
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/18 18:54
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class UpdateAddressDTO implements Serializable{

    private static final long serialVersionUID = -2474758689279205278L;

    /**
     * 订单ID
     */
    @NotNull(message = "#{validation.updateaAddress.orderId}")
    private Long orderId;

    /**
     * 修改的收货地址
     */
    @NotNull(message = "#{validation.updateaAddress.address}")
    private String address;

    /**
     * 商家手动输入的运费
     */
    @NotNull(message = "#{validation.updateaAddress.freight}")
    private BigDecimal freight;
}
