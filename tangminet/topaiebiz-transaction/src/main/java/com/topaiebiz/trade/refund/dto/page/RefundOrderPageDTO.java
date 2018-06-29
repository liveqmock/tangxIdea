package com.topaiebiz.trade.refund.dto.page;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description 平台--商家--售后订单DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/8 12:33
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class RefundOrderPageDTO implements Serializable {

    private static final long serialVersionUID = 1145212711086693417L;

    /**
     * 售后订单ID
     */
    private Long refundOrderId;

    /**
     * 商家名称
     */
    private String merchantName = "--暂为空--";

    /**
     * 所属店铺名称
     */
    private String storeName;

    /**
     * 依赖的支付订单编号
     */
    private String orderId;

    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 退还的美礼卡金额
     */
    private BigDecimal refundCardPrice;

    /**
     * 退还的积分
     */
    private Integer refundIntegralPrice;

    /**
     * 退还得金额
     */
    private BigDecimal refundPrice;

    /**
     * 申请时间
     */
    private Date refundTime;

    /**
     * 售后状态值
     */
    private Integer refundState;

    /**
     * 审核时间
     */
    private Date auditTime;

}
