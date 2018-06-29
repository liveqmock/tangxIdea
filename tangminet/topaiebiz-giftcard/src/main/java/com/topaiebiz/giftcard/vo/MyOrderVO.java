package com.topaiebiz.giftcard.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 我的订单详情
 * @author: Jeff Chen
 * @date: created in 下午3:12 2018/1/24
 */
@Data
public class MyOrderVO {

    private Long orderId;
    /**
     * 订单状态（继承老系统）：0-已取消 10-未支付 20-已付款 30-已发货 40-已完成
     */
    private Integer orderStatus;


    /**
     * 订单提交必带，防止重复提交
     */
    private String orderKey;

    /**
     * 卡名称
     */
    private String cardName;
    /**
     * 面值
     */
    private BigDecimal faceValue;

    /**
     * 售价
     */
    private BigDecimal salePrice;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;
    /**
     * 订单总额
     */
    private BigDecimal orderAmount;
    /**
     * 卡数量
     */
    private Integer cardNum;
    /**
     * 封面
     */
    private String cover;
    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 失效时间
     */
    private Date deadTime;
    /**
     * 适用范围
     */
    private String scope;

    /**
     * 标签id
     */
    private Long labelId;

    /**
     * 支付方式
     */
    private String payWay;
}
