package com.topaiebiz.settlement.dto.export;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Description： 店铺结算Dto。
 * <p>
 * Author Harry
 * <p>
 * Date 2017年10月31日 下午2:20:00
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class SettlementOrderExportDTO {

    /**
     * 订单ID 。
     */
    private Long orderId;

    /**
     * 订单完成时间。
     */
    private String finishTime;

    /**
     * 付款时间。
     */
    private String payTime;

    /**
     * 会员ID。
     */
    private Long memberId;

    /**
     * 商品总价。
     */
    private BigDecimal goodsTotal;

    /**
     * 运费。
     */
    private BigDecimal freight;

    /**
     * 税费。
     */
    private BigDecimal tax;

    /**
     * 店铺营销贴现金额。
     */
    private BigDecimal promStoreSum;

    /**
     * 平台营销贴现金额。
     */
    private BigDecimal promPlatformSum;

    /**
     * 订单中使用积分支付的金额
     */
    private BigDecimal pointSum;

    /**
     * 订单中使用美丽卡支付的金额
     */
    private BigDecimal cardSum;

    /**
     * 用户余额。
     */
    private BigDecimal balanceSum;

    /**
     * 订单中使用现金支付额金额
     */
    private BigDecimal cashSum;

    /**
     * 支付渠道
     */
    private String paymentChannel;

    /**
     * 三方支付流水号
     */
    private String paymentTradeNo;

    /**
     * 平台佣金
     */
    private BigDecimal platformCommission;

    /**
     * 实际需要结算的金额
     */
    private BigDecimal settleSum;

    /**
     * 商品详情
     */
    private String goodsDetail;
}