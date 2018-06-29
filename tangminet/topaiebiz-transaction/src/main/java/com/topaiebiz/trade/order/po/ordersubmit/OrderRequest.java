package com.topaiebiz.trade.order.po.ordersubmit;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yfeng
 * @date 2018.1.8
 */
@Data
public class OrderRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /*** 地址ID ***/
    private Long addressId;

    /*** 平台优惠券ID ***/
    private Long platformPromotionId;

    /*** 订单数据 ***/
    private List<OrderRequestStore> orders = new ArrayList<>();

    /*** 发票信息 ***/
    private OrderRequestInvoice invoice;

    /**
     * 身份证号码
     */
    private String idNum;
    /**
     * 购买人姓名
     */
    private String buyerName;

    private String ip;
    private String userAgent;
}