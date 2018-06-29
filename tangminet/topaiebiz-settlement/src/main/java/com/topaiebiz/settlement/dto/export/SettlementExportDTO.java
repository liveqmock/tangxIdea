package com.topaiebiz.settlement.dto.export;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Description： 店铺结算Dto。
 * <p>
 * Author Harry
 * <p>
 * Date 2017年10月31日 下午2:20:00
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class SettlementExportDTO {

    /**
     * 结算单号
     */
    private Long id;

    /**
     * 计算开始日期。
     */
    private String settleStartDate;

    /**
     * 结算结束日期。
     */
    private String settleEndDate;

    /**
     * 商家Id。
     */
    private Long merchantId;

    /**
     * 商家名称。
     */
    private String merchantName;

    /**
     * 店铺id。
     */
    private Long storeId;

    /**
     * 店铺名称。
     */
    private String storeName;

    /**
     * 商品总价。
     */
    private BigDecimal goodsTotal;

    /**
     * 订单中使用现金支付额金额。
     */
    private BigDecimal cashSum;

    /**
     * 订单中使用美丽卡支付的金额。
     */
    private BigDecimal cardSum;

    /**
     * 营销活动商家扣补金额。
     */
    private BigDecimal promStoreSum;

    /**
     * 营销活动平台扣补金额。
     */
    private BigDecimal promPlatformSum;

    /**
     * 订单中使用积分支付的金额。
     */
    private BigDecimal pointSum;

    /**
     * 平台佣金。
     */
    private BigDecimal platformCommission;

    /**
     * 退款扣除金额
     */
    private BigDecimal refundSum;

    /**
     * 实际需要结算的金额
     */
    private BigDecimal settleSum;

    /**
     * 结算周期:月，半月，周，5天。
     */
    private String settleCycle;

    /**
     * 结算时间。
     */
    private String settleTime;

    /**
     * 结算状态。1-待商家审核，2-待商务审核，3-待财务审核，4-已结算
     */
    private String state;

}