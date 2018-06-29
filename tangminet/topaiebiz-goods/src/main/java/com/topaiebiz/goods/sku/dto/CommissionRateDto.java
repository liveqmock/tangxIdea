package com.topaiebiz.goods.sku.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Description 配置商品佣金比例
 *
 * Author Hedda
 *
 * Date 2017年8月23日 下午5:24:37
 *
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 *
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class CommissionRateDto {

    /** 店铺id。*/
    @NotNull(message = "{validation.commissionRate.merchantId}")
    private Long merchantId;

    /** 类目id。*/
    @NotNull(message = "{validation.commissionRate.categoryId}")
    private Long categoryId;

    /** 佣金比例。小数形式。平台收取商家的佣金。*/
    @NotNull(message = "{validation.commissionRate.brokerageRatio}")
    private Double brokerageRatio;
}
