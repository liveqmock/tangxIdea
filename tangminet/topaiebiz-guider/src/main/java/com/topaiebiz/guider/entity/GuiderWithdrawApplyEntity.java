package com.topaiebiz.guider.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created by admin on 2018/5/30.
 * 提现申请
 */
@Data
@TableName("t_guider_withdraw_apply")
public class GuiderWithdrawApplyEntity extends BaseBizEntity<Long> {

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 手机号
     */
    private String telephone;

    /**
     * 提现方式
     */
    private Long withdrawWay;

    /**
     * 提现账户
     */
    private String withdrawAccount;

    /**
     * 提现金额
     */
    private String applyMoney;

    /**
     * 结算时间
     */
    private Date settlementTime;

    /**
     * 结算失败原因
     */
    private String settlementFailMsg;

    /**
     * 提交时间
     */
    private Date submitTime;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核状态   1待审核；2审核不通过；0审核通过
     */
    private Byte status;

    /**
     * 审核原因
     */
    private String auditReason;
}
