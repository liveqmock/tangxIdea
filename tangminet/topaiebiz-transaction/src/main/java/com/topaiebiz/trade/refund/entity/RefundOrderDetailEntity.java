package com.topaiebiz.trade.refund.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Description 售后订单明细类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/9 9:34
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_tsa_refund_order_detail")
public class RefundOrderDetailEntity extends BaseBizEntity<Long> {

    /**
     * 售后订单编号
     */
    private Long refundOrderId;

    /**
     * 支付订单明细ID
     */
    private Long orderDetailId;

    /**
     * 商品SKU ID
     */
    private Long goodSkuId;

    /**
     * 商品ID
     */
    private Long goodItemId;

    /**
     * 商品名称
     */
    private String goodName;

    /**
     * 商品属性
     */
    private String goodFileValue;

    /**
     * 商品图片路径
     */
    private String goodImgUrl;

    /**
     * 商品数量
     */
    private Integer goodNum;

    /**
     * 商品总价格
     */
    private BigDecimal goodTotalPrice;

    /**
     * 商品实际支付总价
     */
    private BigDecimal payPrice;
}
