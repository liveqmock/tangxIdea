package com.topaiebiz.trade.dto.order.openapi;

import lombok.Data;

import java.io.Serializable;

/**
 * Description OPEN API-订单发货DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/6 19:20
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
 public class OrderExpressDTO implements Serializable {

    private static final long serialVersionUID = 8643013685371940411L;

    /**
     * 物流单号, 物流公司名称, 物流公司CODE, 物流公司ID, 订单编号, 店铺ID
     */
    private String expressNo;
    private String expressCompanyName;
    private String expressCompanyCode;
    private Long expressCompanyId;
    private Long mmgOrderId;
    private Long storeId;

}
