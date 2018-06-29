package com.topaiebiz.trade.order.dto.store.export;

import lombok.Data;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/8 20:52
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
public class OrderDetailExportDTO {

    /**
     * 商品编码
     */
    private Long itemId;

    /**
     * 商品货号
     */
    private String itemCode;

    /**
     * 商品名称
     */
    private String name;

}
