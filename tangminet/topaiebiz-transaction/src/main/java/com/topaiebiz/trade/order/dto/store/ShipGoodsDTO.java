package com.topaiebiz.trade.order.dto.store;

import lombok.Data;

import java.io.Serializable;

/**
 * Description 发货的商品DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/18 18:38
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class ShipGoodsDTO implements Serializable {

    private static final long serialVersionUID = 7988202002554470784L;

    private Long orderDetailId;

    private String goodName;

}
