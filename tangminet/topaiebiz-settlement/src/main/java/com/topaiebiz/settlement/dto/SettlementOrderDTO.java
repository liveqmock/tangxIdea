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
public class SettlementOrderDTO implements Serializable {

    private static final long serialVersionUID = -5871634323284757962L;
    /**
     * ID
     */
    private Long id;

    /**
     * 商家结算佣金。
     */
    private Long settlementId;

    /**
     * 订单ID 。
     */
    private Long orderId;

    /**
     * 订单完成时间。
     */
    private Date finishTime;

    /**
     * 付款时间。
     */
    private Date payTime;

    /**
     * 优惠后金额（实际支付金额）。
     */
    private BigDecimal payPrice;

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
     * 平台营销贴现金额。
     */
    private BigDecimal promPlatformSum;

    /**
     * 店铺营销贴现金额。
     */
    private BigDecimal promStoreSum;

    /**
     * 订单中使用积分支付的金额
     */
    private BigDecimal pointSum;

    /**
     * 订单中使用现金支付额金额
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