package com.topaiebiz.settlement.po;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

/***
 * @author yfeng
 * @date 2018-01-29 17:26
 */
@Data
public class SettlementOrderQueryPO extends PagePO {

    private static final long serialVersionUID = 7978104061431025337L;
    /**
     * 订单编号
     */
    private Long orderId;

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
}