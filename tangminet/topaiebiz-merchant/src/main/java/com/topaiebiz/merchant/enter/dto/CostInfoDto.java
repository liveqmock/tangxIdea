package com.topaiebiz.merchant.enter.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Description: 费用信息dto
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年11月3日 下午4:02:28
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class CostInfoDto implements Serializable {

    /**
     * 商家入驻信息的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。
     */
    private Long id;

    /**
     * 应缴费用 、实缴费用
     */
    private Double PaymentPrice;

    /**
     * 支付凭证图片
     */
    private String payImage;

    /**
     * 描述
     */
    private String memo;

}
