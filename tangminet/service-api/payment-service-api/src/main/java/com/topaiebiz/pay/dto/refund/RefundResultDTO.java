package com.topaiebiz.pay.dto.refund;

import lombok.Data;

import java.io.Serializable;

/**
 * Description 退款结果
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/19 13:59
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class RefundResultDTO implements Serializable {

    private static final long serialVersionUID = -9129135032524861424L;

    /**
     * 退款结果 Constants.java
     * 1成功，0失败
     */
    private Integer resultCode;

    /**
     * 第三方流水号
     */
    private String callBackNo;

}
