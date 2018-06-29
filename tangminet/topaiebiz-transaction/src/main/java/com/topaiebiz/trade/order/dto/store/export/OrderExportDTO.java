package com.topaiebiz.trade.order.dto.store.export;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description 商家订单导出数据模型DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/8 9:39
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
public class OrderExportDTO implements Serializable {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单创建时间
     */
    private String orderTime;

    /**
     * 订单金额
     */
    private BigDecimal orderPrice = BigDecimal.ZERO;

    /**
     * 订单状态
     */
    private String orderState;

    /**
     * 支付单号
     */
    private Long payId;

    /**
     * 支付方式
     */
    private String payMethod;

    /**
     * 支付时间
     */
    private String payTime;

    /**
     * 美礼卡支付金额
     */
    private BigDecimal cardPrice = BigDecimal.ZERO;

    /**
     * 余额支付金额
     */
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 物流公司名称
     */
    private String expressCompanyName;

    /**
     * 物流单号
     */
    private String expressNo;

    /**
     * 退款金额
     */
    private BigDecimal refundPrice = BigDecimal.ZERO;

    /**
     * 订单完成时间
     */
    private String orderCompleteTime;

    /**
     * 是否评价
     */
    private String isComment;

    /**
     * 订单所属店铺ID
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 用户名称
     */
    private String memberName;



    /////////////////////////
    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人证件号
     */
    private String receiverCardNo;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String county;


    /**
     * 收货地址
     */
    private String detailAddress;

    /////////////////////////

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品ID
     */
    private Long itemId;

    /**
     * 商品属性规格
     */
    private String fieldValue;

    /**
     * 商品条形码
     */
    private String barCode;

    /**
     * 商品货号
     */
    private String itemCode;

    /**
     * 商品数量
     */
    private Integer goodNum;


}
