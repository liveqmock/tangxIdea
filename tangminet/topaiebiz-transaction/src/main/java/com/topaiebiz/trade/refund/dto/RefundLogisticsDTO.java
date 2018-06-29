package com.topaiebiz.trade.refund.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description 提交物流信息DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/22 17:40
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class RefundLogisticsDTO implements Serializable {
    private static final long serialVersionUID = 2370583649101005350L;

    /**
     * 售后订单ID
     */
    @NotNull(message = "#{validation.refundLogistics.refundOrderId}")
    private Long refundOrderId;

    /**
     * 物流公司ID
     */
    @NotNull(message = "#{validation.refundLogistics.logisticsCompanyId}")
    private Long logisticsCompanyId;

    /**
     * 物流编号
     */
    @NotNull(message = "#{validation.refundLogistics.logisticsNo}")
    private String logisticsNo;
}
