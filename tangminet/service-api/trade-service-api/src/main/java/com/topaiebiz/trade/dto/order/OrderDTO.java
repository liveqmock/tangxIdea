package com.topaiebiz.trade.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/***
 * @author yfeng
 * @date 2018-03-26 10:52
 */
@Data
public class OrderDTO implements Serializable{
    private static final long serialVersionUID = 5957858859389241312L;
    private Long id;
    /**
     * 会员编号
     */
    private Long memberId;

    /**
     * 会员姓名
     */
    private String memberName;

    /**
     * 会员手机号
     */
    private String memberTelephone;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 订单时间
     */
    private Date orderTime;

    /**
     * 订单状态
     */
    private Integer orderState;

    /**
     * 售后状态 0:无售后，1：售后中，2：已退款
     */
    private Integer refundState;

    /**
     * 发票状态
     */
    private Integer invoiceState;

    /**
     * 锁定状态。1 锁定，0 未锁定。
     */
    private Integer lockState;

    /**
     * 配送方式（1：配送2：自提）
     */
    private Short deliveryType;

    /**
     * 商品总价格
     */
    private BigDecimal goodsTotal;

    /**
     * 总运费
     */
    private BigDecimal freightTotal;

    /**
     * 运费营销活动ID。
     */
    private Long freightPromotionId;
    /**
     * 运费营销活动优惠幅度。
     */
    private BigDecimal freightDiscount;
    /**
     * 实际物流费用
     */
    private BigDecimal actualFreight;

    /**
     * 订单总金额
     */
    private BigDecimal orderTotal;

    /**
     * 所使用的店铺营销活动ID。
     */
    private Long storePromotionId;

    /**
     * 店铺优惠金额
     */
    private BigDecimal storeDiscount;
    /**
     * 店铺优惠券活动ID。
     */
    private Long storeCouponId;

    /**
     * 店铺优惠金额
     */
    private BigDecimal storeCouponDiscount;

    /**
     * 使用的平台优惠
     */
    private Long platformPromotionId;

    /**
     * 平台优惠使用的金额
     */
    private BigDecimal platformDiscount;

    /**
     * 优惠总额（不计算运费优惠在内）
     */
    private BigDecimal discountTotal;

    /**
     * 优惠后金额（实际支付金额）
     */
    private BigDecimal payPrice;

    /**
     * 支付方式
     **/
    private String payType;

    /**
     * 支付订单号。
     */
    private Long payId;

    /**
     * 备注。
     */
    private String memo;

    /**
     * 确认收货时间
     */
    private Date shipmentTime;
    private Date receiveTime;
    /**
     * 完成时间
     */
    private Date completeTime;
    /**
     * 延长收货 1：以延长，0：未延长
     */
    private Integer extendShip;

    /**
     * 使用积分数量
     */
    private Long scoreNum;
    private BigDecimal score;
    private BigDecimal balance;
    private BigDecimal cardPrice;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 是否评论
     */
    private Integer commentFlag;
    private Date commentDate;
    private Integer haitao;
}