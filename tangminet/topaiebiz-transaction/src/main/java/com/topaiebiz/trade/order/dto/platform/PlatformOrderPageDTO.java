package com.topaiebiz.trade.order.dto.platform;

import com.topaiebiz.trade.order.dto.common.OrderPageDetailDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description 平台 分页
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
public class PlatformOrderPageDTO implements Serializable{

    private static final long serialVersionUID = -952007600722937832L;
    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单时间
     */
    private Date orderTime;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 收货者名称
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String telephone;

    /**
     * 订单状态
     */
    private Integer orderState;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 实付款
     */
    private BigDecimal payPrice;

    /**
     * 实际运费
     */
    private BigDecimal actualFreight;

    /**
     * 订单明细集合
     */
    private List<OrderPageDetailDTO> orderPageDetailDTOS = new ArrayList<>();

}
