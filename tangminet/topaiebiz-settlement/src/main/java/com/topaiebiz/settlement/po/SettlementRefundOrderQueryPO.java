package com.topaiebiz.settlement.po;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

/***
 * @author yfeng
 * @date 2018-01-29 17:26
 */
@Data
public class SettlementRefundOrderQueryPO extends PagePO {

    private static final long serialVersionUID = 6314547508668393096L;
    /**
     * 原订单编号
     */
    private Long orderId;

    /**
     * 售后订单编号
     */
    private Long refundId;

    /**
     * 结算ID
     */
    private Long settlementId;

    /**
     * 会员ID。
     */
    private Long memberId;

    /**
     * 订单完成开始日期
     */
    private String finishStartTime;

    /**
     * 订单完成结束日期
     */
    private String finishEndTime;

    /**
     * 申请退款开始日期
     */
    private String applyStartTime;

    /**
     * 申请退款结束日期
     */
    private String applyEndTime;
}