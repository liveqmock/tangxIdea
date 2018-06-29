package com.topaiebiz.trade.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Description
 *
 * @Author hxpeng
 * <p>
 * Date 2018/6/1 17:26
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class GuiderOrderDetailDTO implements Serializable {
    private static final long serialVersionUID = 7328640665466227281L;

    /**
     * 订单ID
     */
    private Long id;
    /**
     * 会员编号 / 会员姓名 / 会员手机号
     */
    private Long memberId;
    private String memberName;
    private String memberTelephone;

    /**
     * 店铺id / 店铺名称
     */
    private Long storeId;
    private String storeName;

    /**
     * 订单状态 / 售后状态( 0:无售后，1：售后中，2：已退款) / 发票状态 / 锁定状态。(1 锁定，0 未锁定) / 配送方式（1：配送2：自提）/ 延长收货(1：以延长，0：未延长)
     */
    private Integer orderState;
    private Integer refundState;
    private Integer invoiceState;
    private Integer lockState;
    private Short deliveryType;
    private Integer extendShip;

    /**
     * 商品总价格 / 总运费 / 运费营销活动ID / 运费营销活动优惠幅度 / 实际物流费用
     */
    private BigDecimal goodsTotal;
    private BigDecimal freightTotal;
    private Long freightPromotionId;
    private BigDecimal freightDiscount;
    private BigDecimal actualFreight;

    /**
     * 订单总金额 / 店铺营销活动 / 店铺优惠金额 / 店铺优惠券活动 / 店铺优惠券金额
     */
    private BigDecimal orderTotal;
    private Long storePromotionId;
    private BigDecimal storeDiscount;
    private Long storeCouponId;
    private BigDecimal storeCouponDiscount;

    /**
     * 使用的平台优惠 / 平台优惠使用的金额 / 优惠总额（不计算运费优惠在内）
     */
    private Long platformPromotionId;
    private BigDecimal platformDiscount;
    private BigDecimal discountTotal;

    /**
     * 优惠后金额（实际支付金额） / 支付方式 / 支付订单号 / 第三方交易单号 / 退款金额
     */
    private BigDecimal payPrice;
    private String payType;
    private Long payId;
    private String outerPaySn;
    private BigDecimal refundPrice;

    /**
     * 用户备注
     */
    private String memo;

    /**
     * 订单时间 / 支付时间 / 订单发货实际 / 确认收货时间 / 完成时间
     */
    private Date orderTime;
    private Date payTime;
    private Date shipmentTime;
    private Date receiveTime;
    private Date completeTime;

    /**
     * 使用积分数量 / 积分金额 / 余额 / 美礼卡 / 第三方支付
     */
    private Long scoreNum;
    private BigDecimal score;
    private BigDecimal balance;
    private BigDecimal cardPrice;
    private BigDecimal thirdPaymentAmount;
    /**
     * 礼卡商品支付记录
     * cardNo->cardAmount
     * map的JSON序列化字符串
     */
    private String cardDetail;
    /**
     * 礼卡运费支付记录
     * cardNo->cardAmount
     * map的JSON序列化字符串
     */
    private String cardFreightDetail;

    /**
     * 是否评论
     */
    private Integer commentFlag;
    private Date commentDate;

    /**
     * 是否海淘 / 下单IP / 浏览器客户端
     */
    private Integer haitao;
    private String ip;
    private String userAgent;


    /**
     * 订单收货地址 / 订单商品信息
     */
    private OrderAddressDTO orderAddressDTO;
    private List<OrderGoodsDTO> orderGoodsDTOS;
}
