package com.topaiebiz.trade.refund.dto.page;

import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.trade.refund.enumdata.RefundProcessEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description 平台--商家--分页查询条件
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/10 10:55
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Data
public class RefundPageParamsDTO implements Serializable {

    private static final long serialVersionUID = -3339641604627636758L;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 售后类型（0：仅退款；1：退货退款）
     */
    private Integer refundType = Constants.Refund.REFUND;

    /**
     * 售后申请得时间
     */
    private Date refundTime;

    /**
     * 售后金额
     */
    private BigDecimal refundPrice;

    /**
     * 操作状态 (待处理)
     */
    private Integer processState = RefundProcessEnum.WAIT.getCode();

    /**
     * 平台介入
     */
    private Integer pfInvolved;

    /**
     * 分页
     */
    private PagePO pagePO = new PagePO();

}
