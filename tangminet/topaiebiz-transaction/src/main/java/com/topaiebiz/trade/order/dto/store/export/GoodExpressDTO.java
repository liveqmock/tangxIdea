package com.topaiebiz.trade.order.dto.store.export;

import lombok.Data;

import java.io.Serializable;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/8 14:35
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class GoodExpressDTO implements Serializable {
    private static final long serialVersionUID = -8561012565880293787L;

    /**
     * 物流公司名称
     */
    private String expressCompanyName;

    /**
     * 物流编号
     */
    private String expressNo;

    /**
     * 订单ID
     */
    private Long orderId;

}
