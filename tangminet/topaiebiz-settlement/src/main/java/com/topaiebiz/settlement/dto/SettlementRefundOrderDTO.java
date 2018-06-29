package com.topaiebiz.settlement.dto;

import com.nebulapaas.base.enumdata.PayMethodEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
public class SettlementRefundOrderDTO implements Serializable {

    private static final long serialVersionUID = 3185231844308502952L;
    /**
     * ID
     */
    private Long id;

    /**
     * 商家结算佣金。
     */
    private Long settlementId;

    /**
     * 售后订单ID 。
     */
    private Long refundId;

    /**
     * 订单ID 。
     */
    private Long orderId;

    /**
     * 退款完成时间。
     */
    private Date finishTime;

    /**
     * 退款申请时间。
     */
    private Date applyTime;

    /**
     * 订单退款总额（包含第三方，积分，余额等）。
     */
    private BigDecimal refundPrice;

    /**
     * 会员ID。
     */
    private Long memberId;

    /**
     * 商品总价。
     */
    private BigDecimal goodsTotal;

    /**
     * 应退运费。
     */
    private BigDecimal freight;

    /**
     * 应退税费。
     */
    private BigDecimal tax;

    /**
     * 应退平台营销贴现金额。
     */
    private BigDecimal promPlatformSum;

    /**
     * 应退店铺营销贴现金额。
     */
    private BigDecimal promStoreSum;

    /**
     * 应退使用积分支付的金额
     */
    private BigDecimal pointSum;

    /**
     * 应退使用现金支付额金额
     */
    private BigDecimal cashSum;

    /**
     * 用户余额。
     */
    private BigDecimal balanceSum;

    /**
     * 订单中使用美丽卡支付的金额
     */
    private BigDecimal cardSum;

    /**
     * 支付渠道
     */
    private String paymentChannel;

    /**
     * 三方支付流水号
     */
    private String paymentTradeNo;

    /**
     * 实际需要结算的金额
     */
    private BigDecimal settleSum;

    /**
     * 平台佣金
     */
    private BigDecimal platformCommission;

    /**
     * 商品详情
     */
    private String goodsDetail;

    /**
     * 备注
     */
    private String memo;

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = PayMethodEnum.getDescByName(paymentChannel);
    }
}