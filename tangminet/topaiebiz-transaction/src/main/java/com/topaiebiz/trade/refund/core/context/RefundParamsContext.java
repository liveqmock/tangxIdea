package com.topaiebiz.trade.refund.core.context;

import com.topaiebiz.trade.refund.dto.RefundLogisticsDTO;
import com.topaiebiz.trade.refund.dto.RefundSubmitDTO;
import com.topaiebiz.trade.refund.dto.common.ExecuteUserDTO;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Description 售后参数正文
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/2 10:11
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class RefundParamsContext implements Serializable {

    private static final long serialVersionUID = 2996247643568936157L;

    /**
     * 订单信息
     */
    private OrderEntity orderEntity;

    /**
     * 售后订单信息
     */
    private RefundOrderEntity refundOrderEntity;

    /**
     * 售后订单提交DTO
     */
    private RefundSubmitDTO refundSubmitDTO;

    /**
     * 用户寄回商品物流信息
     */
    private RefundLogisticsDTO refundLogisticsDTO;

    /**
     * 是否为修改操作
     */
    private boolean isUpdate = false;

    /**
     * 审核结果 true = 通过 or false = 拒绝
     */
    private boolean isAuditSuccess = false;

    /**
     * 是否整单退
     */
    private boolean isAllRefund = false;

    /**
     * 拒绝原因
     */
    private String refuseDescription;

    /**
     * 是否平台介入的售后订单
     */
    private boolean isPlatformInvolved = false;

    /**
     * 操作者
     */
    private ExecuteUserDTO executeUserDTO;
}
