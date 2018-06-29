package com.topaiebiz.trade.order.dto.store;

import com.topaiebiz.trade.order.dto.common.OrderPageDetailDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description 商家 分页
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/12 9:57
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
public class StoreOrderPageDTO implements Serializable {

    private static final long serialVersionUID = 1086919735162933968L;

    /**
     * 订单ID，支付订单ID，订单时间，店铺名称，收货者名称，收货人电话，订单状态，实付款，实际运费，总优惠
     */
    private Long id;
    private Long payId;
    private Date orderTime;
    private String storeName;
    private String receiverName;
    private String telephone;
    private Integer orderState;
    private BigDecimal payPrice;
    private BigDecimal actualFreight;
    private BigDecimal discountTotal;

    /**
     * 售后状态 0:无售后，1：售后中，2：已退款，3：平台已拒绝
     */
    private Integer refundState;
    /**
     * 锁定状态。1 锁定，0 未锁定。
     */
    private Integer lockState;

    /**
     * 购买人姓名，收货人地址，用户身份证号（打印发货单需要）
     */
    private String buyerName;
    private String address;
    private String memberIdCard;

    /**
     * 用户备注, 最近的备注
     */
    private String memo;
    private String latestRemarks;

    /**
     * 订单明细集合
     */
    private List<OrderPageDetailDTO> orderPageDetailDTOS = new ArrayList<>();


    public StoreOrderPageDTO() {
        this.payPrice = BigDecimal.ZERO;
        this.actualFreight = BigDecimal.ZERO;
        this.discountTotal = BigDecimal.ZERO;
    }
}

