package com.topaiebiz.trade.order.dto.customer;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Description 会员端--订单分页
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/12 19:38
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class CmOrderPageDTO implements Serializable{

    private static final long serialVersionUID = 8705637349313611332L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 订单状态
     */
    private Integer orderState;

    /**
     * 实际付款
     */
    private BigDecimal payPrice;

    /**
     * 实际运费
     */
    private BigDecimal actualFreight;

    /**
     * 支付ID
     */
    private Long payId;

    /**
     * 延长收货 1：已延长，0：未延长
     */
    private Integer extendShip;

    /**
     * 是否已评价，1：已评价，0未评价
     */
    private Integer commentFlag;

    /**
     * 锁定状态。1 锁定，0 未锁定。
     */
    private Integer lockState;

    /**
     * 售后状态 0:无售后，1：售后中，2：已退款
     */
    private Integer refundState;

    /**
     * 订单明细集合
     */
    private List<CmOrderPageDetailDTO> cmOrderDetailDTOS = new ArrayList<>();

}
