package com.topaiebiz.settlement.dto;

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
public class SettlementDetailDTO implements Serializable {

    private static final long serialVersionUID = -229204512542190055L;

    /**
     * 店铺名称。
     */
    private String storeName;

    /**
     * 店铺id。
     */
    private Long storeId;

    /**
     * 结算单号
     */
    private Long id;

    /**
     * 商家名称。
     */
    private String merchantName;

    /**
     * 计算开始日期。
     */
    private Date settleStartDate;

    /**
     * 结算结束日期。
     */
    private Date settleEndDate;

    /**
     * 结算时间。
     */
    private Date createdTime;

    /**
     * 结算状态。1-待商家审核，2-待商务审核，3-待财务审核，4-已结算
     */
    private Integer state;

    /**
     * 商品总价。
     */
    private BigDecimal goodsTotal;

    /**
     * 运费。
     */
    private BigDecimal freight;

    /**
     * 税费。
     */
    private BigDecimal tax;

    /**
     * 店铺贴现。
     */
    private BigDecimal promStoreSum;
    /**
     * 平台贴现。
     */
    private BigDecimal promPlatformSum;

    /**
     * 平台佣金。
     */
    private BigDecimal platformCommission;

    /**
     * 退款订单佣金。
     */
    private BigDecimal refundCommission;

    /**
     * 实际需要结算的金额
     */
    private BigDecimal settleSum;

    /**
     * 退款扣除金额
     */
    private BigDecimal refundSum;

    /**
     * 积分支付总额。
     */
    private BigDecimal pointSum;

    /**
     * 积分结算总额。
     */
    private BigDecimal pointSettleSum;

    /**
     * 积分退款总额。
     */
    private BigDecimal pointRefundSum;

    /**
     * 礼卡支付总额。
     */
    private BigDecimal cardSum;

    /**
     * 礼卡结算总额。
     */
    private BigDecimal cardSettleSum;

    /**
     * 礼卡退款总额。
     */
    private BigDecimal cardRefundSum;

    /**
     * 银行账号
     */
    private String settleAccount;

    /**
     * 开户银行
     */
    private String settleBankName;

    /**
     * 开户人姓名
     */
    private String settleAccountName;

    /**
     * 开户支行银联号
     */
    private String settleBankNum;
}