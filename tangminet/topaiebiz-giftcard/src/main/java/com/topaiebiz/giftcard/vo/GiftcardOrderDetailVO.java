package com.topaiebiz.giftcard.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 礼卡订单详情
 * @author: Jeff Chen
 * @date: created in 下午4:37 2018/1/18
 */
@Data
public class GiftcardOrderDetailVO extends GiftcardOrderVO{
    /**
     * 订单总金额
     */
    private BigDecimal orderAmount;
    /**
     * 支付通道：wxpay-微信 alipay-支付宝
     */
    private String payCode;
    /**
     * 第三方交易号
     */
    private String paySn;
    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    private String phone;
}
