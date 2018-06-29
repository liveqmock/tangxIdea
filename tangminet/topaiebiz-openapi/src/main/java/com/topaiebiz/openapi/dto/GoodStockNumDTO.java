package com.topaiebiz.openapi.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/22 16:28
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
public class GoodStockNumDTO implements Serializable{

    private static final long serialVersionUID = 6411302513521640742L;

    /**
     * 货号
     */
    private String articleNumber;

    /**
     * 库存数量
     */
    private Long stockNum;

    /**
     * 店铺ID
     */
    private Long storeId;

}
