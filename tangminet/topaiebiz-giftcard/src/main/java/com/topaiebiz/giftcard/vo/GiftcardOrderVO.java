package com.topaiebiz.giftcard.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 订单展示信息
 * @author: Jeff Chen
 * @date: created in 下午1:56 2018/1/18
 */
@Data
public class GiftcardOrderVO implements Serializable{


    private Long orderId;
    /**
     * 订单状态（继承老系统）：0-已取消 10-未支付 20-已付款 30-已发货 40-已完成
     */
    private Integer orderStatus;
    /**
     * 购买者名称
     */
    private String memberName;
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



}
