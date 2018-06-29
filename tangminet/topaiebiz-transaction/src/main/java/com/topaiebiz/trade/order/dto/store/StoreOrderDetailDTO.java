package com.topaiebiz.trade.order.dto.store;

import com.topaiebiz.trade.order.dto.common.OrderRemarkDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Description 商家端--订单详情DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/17 15:05
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Data
public class StoreOrderDetailDTO implements Serializable {
    private static final long serialVersionUID = 8645833040253489637L;


    /**
     * 订单号，支付单号，下单时间，支付时间，支付方式，第三方支付流水号，订单完成时间，订单关闭时间，订单状态，售后状态
     * 发货时间， 收货时间
     */
    private Long id;
    private Long payId;
    private Date orderTime;
    private Date payTime;
    private String payType;
    private String outerPaySn;
    private Date completeTime;
    private Date closeTime;
    private Integer orderState;
    private Integer refundState;
    private Date deliveryTime;
    private Date receiverTime;

    /**
     * 第三方支付金额，余额支付金额，积分支付金额，美礼卡支付金额，平台优惠抵扣，店铺优惠活动抵扣，店铺优惠券抵扣，实际运费支付金额，最终实付金额
     * 商品总价， 商品总数量
     */
    private BigDecimal thirdPaymentAmount;
    private BigDecimal balance;
    private BigDecimal score;
    private BigDecimal cardPrice;
    private BigDecimal platformDiscount;
    private BigDecimal storeDiscount;
    private BigDecimal storeCouponDiscount;
    private BigDecimal actualFreight;
    private BigDecimal payPrice;
    private BigDecimal goodsTotalPrice;
    private Long goodsTotalNum;

    /**
     * 商品明细
     */
    private List<StoreOrderGoodsDTO> storeOrderGoodsDTOS;

    /**
     * 收货人信息：姓名，地址，手机号
     */
    private String receiverName;
    private String address;
    private String receiverTelephone;

    /**
     * 实名认证信息：姓名，身份证号码
     */
    private String buyerName;
    private String idNum;

    /**
     * 下单会员信息： 姓名，手机号
     */
    private String memberName;
    private String memberTelephone;

    /**
     * 订单物流信息：物流公司名称，物流公司Id，物流编号
     */
    private String expressComName;
    private Long expressComId;
    private String expressNo;

    /**
     * 订单备注
     */
    private List<OrderRemarkDTO> remarkDTOS;


    public StoreOrderDetailDTO() {
        this.thirdPaymentAmount = BigDecimal.ZERO;
        this.balance = BigDecimal.ZERO;
        this.score = BigDecimal.ZERO;
        this.cardPrice = BigDecimal.ZERO;
        this.platformDiscount = BigDecimal.ZERO;
        this.storeDiscount = BigDecimal.ZERO;
        this.storeCouponDiscount = BigDecimal.ZERO;
        this.actualFreight = BigDecimal.ZERO;
        this.payPrice = BigDecimal.ZERO;
    }


    public void resetThirdPaymentAmount() {
        this.thirdPaymentAmount = this.payPrice.subtract(this.score).subtract(this.cardPrice).subtract(this.balance);
    }

}
