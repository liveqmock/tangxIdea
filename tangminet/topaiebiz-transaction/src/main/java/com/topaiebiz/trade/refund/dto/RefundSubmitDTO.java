package com.topaiebiz.trade.refund.dto;

import com.topaiebiz.trade.refund.dto.common.RefundGoodDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Description 售后订单提交
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/28 18:59
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class RefundSubmitDTO implements Serializable {

    private static final long serialVersionUID = 5225851355236845367L;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 已发货，运输中，可以申请退货退款
     */
    private boolean isSending = false;

    /**
     * 重新申请
     */
    private Boolean ifReapply;

    /**
     * （修改）
     * 售后记录ID
     */
    private Long refundId;

    /**
     * 售后类型
     */
    @NotNull(message = "#{validation.refund.refundType}")
    private Integer refundType;

    /**
     * 售后原因
     */
    @NotNull(message = "#{validation.refund.refundReasonCode}")
    private Integer refundReasonCode;

    /**
     * 售后商品件数
     */
    private Integer refundGoodsNum;

    /**
     * 售后退款金额(用户手动输入的金额)
     */
    private BigDecimal refundPrice;

    /**
     * 最多可退金额(仅显示)
     */
    private BigDecimal mostRefundPrice;

    /**
     * 售后退款金额中包含的运费
     */
    private BigDecimal refundFreight = BigDecimal.ZERO;

    /**
     * 售后退款金额中包含的美礼卡金额
     */
    private BigDecimal refundCardPrice = BigDecimal.ZERO;

    /**
     * 售后退款金额中包含的积分
     */
    private BigDecimal refundIntegralPrice = BigDecimal.ZERO;

    /**
     * 售后退款金额中包含的用户余额
     */
    private BigDecimal refundBalance = BigDecimal.ZERO;

    /**
     * 售后退款金额中包含的三分支付金额
     */
    private BigDecimal refundThirdAmount = BigDecimal.ZERO;

    /**
     * 售后退款说明
     */
    private String refundDescription;

    /**
     * 售后上传图片1
     */
    private String refundImg1;

    /**
     * 售后上传图片2
     */
    private String refundImg2;

    /**
     * 售后上传图片3
     */
    private String refundImg3;

    /**
     * 售后商品明细ID集合
     */
    private Set<Long> orderDetailIds;

    /**
     * 售后商品明细
     */
    private List<RefundGoodDTO> refundGoodDTOS;

}
