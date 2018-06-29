package com.topaiebiz.settlement.dto;

import com.topaiebiz.merchant.constants.MerchantConstants;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
public class SettlementDTO implements Serializable {

    private static final long serialVersionUID = -984880561385134526L;

    /**
     * ID
     */
    private Long id;

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
     * 计算开始日期。
     */
    private Date settleStartDate;

    /**
     * 结算结束日期。
     */
    private Date settleEndDate;

    /**
     * 商品总价。
     */
    private BigDecimal goodsTotal;

    /**
     * 订单中使用现金支付额金额。
     */
    private BigDecimal cashSum;

    /**
     * 订单中使用积分支付的金额。
     */
    private BigDecimal pointSum;

    /**
     * 订单中使用积分支付的金额。
     */
    private BigDecimal balanceSum;

    /**
     * 订单中使用美丽卡支付的金额。
     */
    private BigDecimal cardSum;

    /**
     * 支付总金额。
     */
    private BigDecimal paySum;

    /**
     * 营销活动商家扣补金额。
     */
    private BigDecimal promStoreSum;

    /**
     * 营销活动平台扣补金额。
     */
    private BigDecimal promPlatformSum;

    /**
     * 平台佣金。
     */
    private BigDecimal platformCommission;

    /**
     * 实际需要结算的金额
     */
    private BigDecimal settleSum;

    /**
     * 退款扣除金额
     */
    private BigDecimal refundSum;

    /**
     * 结算周期:月，半月，周，5天。
     */
    private String settleCycle;

    /**
     * 结算时间。
     */
    private Date settleTime;

    /**
     * 结算状态。1为已出账   2 已结算
     */
    private Integer state;

    /**
     * 备注
     */
    private String memo;

    public void setSettleCycle(String settleCycle) {
        this.settleCycle = MerchantConstants.SettleCycle.getValueByCode(settleCycle);
    }
}