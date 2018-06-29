package com.topaiebiz.trade.order.dto.customer;

import com.topaiebiz.trade.dto.order.OrderAddressDTO;
import com.topaiebiz.trade.dto.order.OrderGoodsDTO;
import com.topaiebiz.trade.dto.order.OrderInvoiceDTO;
import com.topaiebiz.trade.dto.order.OrderPayDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Description 用户订单详情DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/12 20:57
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class CmOrderDetailDTO implements Serializable {

    private static final long serialVersionUID = 8828345291768750155L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 下单时间
     */
    private Date orderTime;

    /**
     * 发货时间
     */
    private Date shipmentTime;

    /**
     * 收货时间
     */
    private Date receiveTime;

    /**
     * 订单状态
     */
    private Integer orderState;

    /**
     * 0 无售后， 1 售后中， 2 已退款， 3 平台已拒绝
     */
    private Integer refundState;

    /**
     * 锁定状态。1 锁定，0 未锁定。
     */
    private Integer lockState;


    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 商品总价
     */
    private BigDecimal goodsTotal;

    /**
     * 店铺营销活动优惠金额
     */
    private BigDecimal storeDiscount;

    /**
     * 店铺优惠券优惠金额
     */
    private BigDecimal storeCouponDiscount;

    /**
     * 平台优惠
     */
    private BigDecimal platformDiscount;

    /**
     * 总运费
     */
    private BigDecimal freightTotal;

    /**
     * 实际运费
     */
    private BigDecimal actualFreight;

    /**
     * 延长收货 1：已延长，0：未延长
     */
    private Integer extendShip;

    /**
     * 是否已评价 1已评价， 0 未评价
     */
    private Integer commentFlag;

    /**
     * 用户留言
     */
    private String memo;

    /**
     * 订单地址DTO
     */
    private OrderAddressDTO orderAddressDTO;

    /**
     * 订单发票DTO
     */
    private OrderInvoiceDTO orderInvoiceDTO;

    /**
     * 订单商品明细
     */
    private List<OrderGoodsDTO> orderGoodsDTOS;

    /**
     * 支付订单明细
     */
    private OrderPayDTO orderPayDTO;
}
