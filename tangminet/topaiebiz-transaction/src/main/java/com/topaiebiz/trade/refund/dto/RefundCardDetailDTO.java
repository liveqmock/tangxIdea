package com.topaiebiz.trade.refund.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/30 13:11
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class RefundCardDetailDTO implements Serializable {
    private static final long serialVersionUID = -5735518167682910560L;

    /**
     * 商品ID
     */
    private Long goodsId;

    private String goodsName;

}
