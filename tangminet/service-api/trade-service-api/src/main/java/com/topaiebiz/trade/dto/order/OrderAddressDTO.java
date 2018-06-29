package com.topaiebiz.trade.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * Description 订单地址
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/12 17:53
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderAddressDTO implements Serializable {

    private static final long serialVersionUID = -150643864164928458L;

    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 收货人姓名, 收货人手机号, 省,市,区,详细地址
     */
    private String name;
    private String telephone;
    private String province;
    private String city;
    private String county;
    private String address;

    private String detailAddress;

    /**
     * 用户身份证号， 购买人姓名
     */
    private String memberIdCard;
    private String buyerName;
}
