package com.topaiebiz.settlement.po;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

/***
 * @author yfeng
 * @date 2018-01-29 17:26
 */
@Data
public class SettlementQueryPO extends PagePO {
    private static final long serialVersionUID = -7551068413341079554L;

    /**
     * 结算单号
     */
    private Long id;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 结算状态。待商家审核，待商务审核，待财务审核，已结算。
     */
    private Integer state;

    /**
     * 结算开始日期
     */
    private String settleStartDate;

    /**
     * 结算结束日期
     */
    private String settleEndDate;

    /**
     * 结算周期
     */
    private String settleCycle;
}