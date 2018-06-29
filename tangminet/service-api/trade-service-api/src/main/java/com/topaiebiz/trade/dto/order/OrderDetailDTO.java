package com.topaiebiz.trade.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Description 通用 订单详情DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/12 16:53
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderDetailDTO implements Serializable {

    private static final long serialVersionUID = -7661333248110786098L;

    private Long id;

    /**
     * 会员姓名
     */
    private String memberName;

    /**
     * 会员手机号
     */
    private String memberTelephone;

    /**
     * 店铺ID。
     */
    private Long storeId;

    /**
     * 店铺名称。
     */
    private String storeName;

    /**
     * 支付订单号。
     */
    private Long payId;

    /**
     * 订单时间。
     */
    private Date orderTime;

    /**
     * 订单状态。
     */
    private Integer orderState;

    /**
     * 售后状态
     */
    private Integer refundState;

    /**
     * 开票状态 。0 不开发票  1开具发票
     */
    private Integer invoiceState;

    /**
     * 锁定状态。1 锁定，0 未锁定。
     */
    private Integer lockState;

    /**
     * 商品总价格
     */
    private BigDecimal goodsTotal;

    /**
     * 总运费
     */
    private BigDecimal freightTotal;

    /**
     * 实际物流费用
     */
    private BigDecimal actualFreight;

    /**
     * 店铺优惠金额
     */
    private BigDecimal storeDiscount;

    /**
     * 店铺优惠券优惠金额
     */
    private BigDecimal storeCouponDiscount;

    /**
     * 平台优惠金额
     */
    private BigDecimal platformDiscount;

    /**
     * 营销优惠总额(不算包邮优惠)
     */
    private BigDecimal discountTotal;

    /**
     * 优惠后金额（实际支付金额）
     */
    private BigDecimal payPrice;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 支付方式
     */
    private String payType;

    /**
     * 确认收货时间
     */
    private Date receiveTime;

    /**
     * 发货时间
     */
    private Date shipmentTime;

    /**
     * 完成时间
     */
    private Date completeTime;

    /**
     * 延长收货 1：以延长，0：未延长
     */
    private Integer extendShip;

    /**
     * 使用积分数量, 余额, 美礼卡, 第三方支付金额, 第三方交易号
     */
    private Long scoreNum;
    private BigDecimal score = BigDecimal.ZERO;
    private BigDecimal balance = BigDecimal.ZERO;
    private BigDecimal cardPrice = BigDecimal.ZERO;
    private BigDecimal thirdPaymentAmount = BigDecimal.ZERO;
    private String outerPaySn;

    /**
     * 美礼卡支付金额明细
     */
    private String cardDetail;
    private String cardFreightDetail;

    /**
     * 商品详情DTO
     */
    private List<OrderGoodsDTO> orderGoodsDTOS;

    /**
     * 订单收货地址
     */
    private OrderAddressDTO orderAddressDTO;

    /**
     * 订单发票信息
     */
    private OrderInvoiceDTO orderInvoiceDTO;

    /**
     * 订单支付信息
     */
    private OrderPayDTO orderPayDTO;

    /**
     * 备注
     */
    private String memo;

    /**
     * 是否评论， 评论时间， 是否海淘
     */
    private Integer commentFlag;
    private Date commentDate;
    private Integer haitao;


    public void setScore(BigDecimal score) {
        if (null == score) {
            this.score = BigDecimal.ZERO;
        } else {
            this.score = score;
        }
    }

    public void setBalance(BigDecimal balance) {
        if (null == balance) {
            this.balance = BigDecimal.ZERO;
        } else {
            this.balance = balance;
        }
    }

    public void setCardPrice(BigDecimal cardPrice) {
        if (null == cardPrice) {
            this.cardPrice = BigDecimal.ZERO;
        } else {
            this.cardPrice = cardPrice;
        }
    }

    public void setThirdPaymentAmount(BigDecimal thirdPaymentAmount) {
        if (null == thirdPaymentAmount) {
            this.thirdPaymentAmount = BigDecimal.ZERO;
        } else {
            this.thirdPaymentAmount = thirdPaymentAmount;
        }
    }
}
