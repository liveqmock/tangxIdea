package com.topaiebiz.settlement.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_set_store_settlement_order")
public class StoreSettlementOrderEntity extends BaseBizEntity<Long> implements Serializable {

    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = -6563101944321896965L;

    /**
     * 商家结算佣金。
     */
    private Long settlementId;
    /**
     * 店铺ID 。
     */
    private Long storeId;
    /**
     * 订单ID 。
     */
    private Long orderId;

    /**
     * 订单完成时间。
     */
    private Date finishTime;

    /**
     * 付款时间。
     */
    private Date payTime;

    /**
     * 优惠后金额（实际支付金额）。
     */
    private BigDecimal payPrice;

    /**
     * 会员ID。
     */
    private Long memberId;

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
     * 平台营销贴现金额。
     */
    private BigDecimal promPlatformSum;

    /**
     * 店铺营销贴现金额。
     */
    private BigDecimal promStoreSum;

    /**
     * 订单中使用积分支付的金额
     */
    private BigDecimal pointSum;

    /**
     * 订单中使用现金支付额金额
     */
    private BigDecimal cashSum;

    /**
     * 用户余额。
     */
    private BigDecimal balanceSum;

    /**
     * 订单中使用美丽卡支付的金额
     */
    private BigDecimal cardSum;

    /**
     * 支付渠道
     */
    private String paymentChannel;

    /**
     * 三方支付流水号
     */
    private String paymentTradeNo;

    /**
     * 实际需要结算的金额
     */
    private BigDecimal settleSum;

    /**
     * 平台佣金
     */
    private BigDecimal platformCommission;

    /**
     * 佣金详情
     */
    private String commissionDetail;

    /**
     * 商品详情
     */
    private String goodsDetail;

    /**
     * 备注
     */
    private String memo;

}
