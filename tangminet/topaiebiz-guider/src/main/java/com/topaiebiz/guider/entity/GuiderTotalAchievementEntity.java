package com.topaiebiz.guider.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by admin on 2018/5/30.
 * 业务统计总表
 */
@Data
@TableName("t_guider_total_achievement")
public class GuiderTotalAchievementEntity extends BaseBizEntity<Long> {

    /**
     * 会员id
     */
    @Deprecated
    private Long memberId;

    /**
     * 会员上级id   默认是0
     */
    private Long srcMemberId;

    /**
     * 用户名称
     */
    @Deprecated
    private String userName;

    /**
     * 发展中用户数量
     */
    private Integer developingUserNum;

    /**
     * 发展完成用户的数量
     */
    private Integer developedUserNum;

    /**
     * 拉新总金额
     */
    private BigDecimal developedAwardMoney;

    /**
     * 已经完成的订单数量
     */
    private Integer orderCompleteNum;

    /**
     * 未完成的订单数量
     */
    private Integer orderUncompleteNum;

    /**
     * 订单总金额
     */
    private BigDecimal orderTotalMoney;

    /**
     * 订单奖金总金额
     */
    private BigDecimal orderAwardTotalMoney;

    /**
     * 订单预期奖金总金额
     */
    private BigDecimal orderExpectAwardTotalMoney;

    /**
     * 订单未完成的总金额
     */
    private BigDecimal orderUncompleteTotalMoney;

    /**
     * 已经提现金额
     */
    private BigDecimal hadWithdrawMoney;

    /**
     * 提现冻结金额
     */
    private BigDecimal withdrawFreezeMoney;

    public void init() {
        this.srcMemberId = 0L;
        this.developingUserNum = 0;
        this.developedUserNum = 0;
        this.developedAwardMoney = BigDecimal.ZERO;
        this.orderCompleteNum = 0;
        this.orderUncompleteNum = 0;
        this.orderTotalMoney = BigDecimal.ZERO;
        this.orderAwardTotalMoney = BigDecimal.ZERO;
        this.orderExpectAwardTotalMoney = BigDecimal.ZERO;
        this.orderUncompleteTotalMoney = BigDecimal.ZERO;
        this.hadWithdrawMoney = BigDecimal.ZERO;
        this.withdrawFreezeMoney = BigDecimal.ZERO;
    }
}
