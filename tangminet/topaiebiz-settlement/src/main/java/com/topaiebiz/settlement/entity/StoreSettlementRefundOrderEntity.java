package com.topaiebiz.settlement.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_set_store_settlement_refund_order")
public class StoreSettlementRefundOrderEntity extends BaseBizEntity<Long> implements Serializable {

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
     * 售后订单ID 。
     */
    private Long refundId;
    /**
     * 店铺ID 。
     */
    private Long storeId;
    /**
     * 订单ID 。
     */
    private Long orderId;

    /**
     * 退款完成时间。
     */
    private Date finishTime;

    /**
     * 退款申请时间。
     */
    private Date applyTime;

    /**
     * 订单退款总额（包含第三方，积分，余额等）。
     */
    private BigDecimal refundPrice;

    /**
     * 会员ID。
     */
    private Long memberId;

    /**
     * 商品总价。
     */
    private BigDecimal goodsTotal;

    /**
     * 应退运费。
     */
    private BigDecimal freight;

    /**
     * 应退税费。
     */
    private BigDecimal tax;

    /**
     * 应退平台营销贴现金额。
     */
    private BigDecimal promPlatformSum;

    /**
     * 应退店铺营销贴现金额。
     */
    private BigDecimal promStoreSum;

    /**
     * 应退使用积分支付的金额
     */
    private BigDecimal pointSum;

    /**
     * 应退使用现金支付额金额
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
     * 商品详情
     */
    private String goodsDetail;

    /**
     * 备注
     */
    private String memo;

}
