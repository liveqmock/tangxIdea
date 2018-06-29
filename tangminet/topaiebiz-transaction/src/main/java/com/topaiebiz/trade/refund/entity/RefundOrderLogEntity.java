package com.topaiebiz.trade.refund.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Description 退款日志
 * <p>
 *
 * @Author hxpeng
 * <p>
 * Date 2018/4/3 19:37
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("t_tsa_refund_log")
public class RefundOrderLogEntity extends BaseBizEntity<Long> {

    private static final long serialVersionUID = 3839493050558903297L;

    private Long refundOrderId;
    private Long orderId;

    /**
     * 各个途径的金额 是否已退 以及各个的退款时间
     */
    private BigDecimal refundCardPrice;
    private String refundCardResult;
    private Date refundCardTime;
    private BigDecimal refundAssetPrice;
    private String refundAssetResult;
    private Date refundAssetTime;
    private BigDecimal refundAmounts;
    private String refundAmountsResult;
    private Date refundAmountsTime;

    /**
     * 部分退款 还是 全部已退款
     */
    private String refundState;

    /**
     * 定时器退款次数
     */
    private Integer taskExecCount;

    /**
     * 退款异常信息
     */
    private String refundErrorMsg;

    public RefundOrderLogEntity(Long refundOrderId) {
        this.cleanInit();
        this.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        this.refundOrderId = refundOrderId;
    }

    public boolean hasRefundSuccess() {
        return Constants.Refund.REFUND_YES.equals(this.refundAssetResult) || Constants.Refund.REFUND_YES.equals(this.refundAmountsResult) || Constants.Refund.REFUND_YES.equals(this.refundCardResult);
    }
}
