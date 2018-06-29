package com.topaiebiz.settlement.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description： 店铺结算Entity。
 * <p>
 * Author Harry
 * <p>
 * Date 2017年10月31日 下午2:24:36
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@TableName("t_set_store_settlement")
public class StoreSettlementEntity extends BaseBizEntity<Long> implements Serializable {

    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = -8680872187627605238L;

    @TableId(type = IdType.AUTO)
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
     * 运费。
     */
    private BigDecimal freight;

    /**
     * 税费。
     */
    private BigDecimal tax;

    /**
     * 订单中使用现金支付额金额。
     */
    private BigDecimal cashSum;

    /**
     * 积分支付总额。
     */
    private BigDecimal pointSum;

    /**
     * 积分退款总额。
     */
    private BigDecimal pointRefundSum;

    /**
     * 用户余额。
     */
    private BigDecimal balanceSum;

    /**
     * 礼卡支付总额。
     */
    private BigDecimal cardSum;

    /**
     * 礼卡退款总额。
     */
    private BigDecimal cardRefundSum;

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
}