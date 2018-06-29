package com.topaiebiz.transaction.order.merchant.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Description 订单报关结果表
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/16 17:41
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_tsa_order_custom_result")
public class OrderCustomsResultEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = -611058701244507644L;

    /**
     * 报关途径
     * 微信/支付宝
     */
    private String reportWay;

    /**
     * 系统订单号
     */
    private Long mmgOrderId;

    /**
     * 系统报关编号
     */
    private String mmgReportId;

    /**
     * 第三方支付号
     */
    private String outTradeNo;

    /**
     * 支付宝报关号
     */
    private String alipayDeclareNo;

    /**
     * 支付宝报关结果code
     */
    private String alipayResultCode;

    /**
     * 支付宝报关结果描述
     */
    private String alipayResultDesc;

    /**
     * 微信结果code
     */
    private String wxResultCode;

    /**
     * 报关时间
     */
    private Date reportTime;


}
