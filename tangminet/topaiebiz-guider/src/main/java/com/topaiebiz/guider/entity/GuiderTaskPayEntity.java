package com.topaiebiz.guider.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by admin on 2018/5/30.
 * 任务订单业绩表
 */
@Data
@TableName("t_guider_task_pay")
public class GuiderTaskPayEntity extends BaseBizEntity<Long> {


    /**
     * 会员ID。
     */
    private Long taskId;

    /**
     * 上级会员id
     */
    private Long srcMemberId;

    /**
     * 会员id
     */
    private Long memberId;


    /**
     * 某一个用户支付单排序ID
     */
    private Integer sortingId;

    /**
     * 支付单ID
     */
    private Long payId;

    /**
     * 导购支付单 状态(仅用于导购模块---srcMemberId  和 orderStatus 共同影响其状态)
     */
    private Integer payStatus;

    /**
     * 订单支付成功时间
     */
    private Date payTime;

    /**
     * 奖励类型
     */
    private Integer awardType;

    /**
     * 订单实际支付金额
     */
    private BigDecimal payMoney;

    /**
     * 运费金额
     */
    private BigDecimal freightMoney;

    /**
     * 退款金额
     */
    private BigDecimal refundMoney;

    /**
     * 奖励比例，百分之n 中的n
     */
    private BigDecimal awardRate;

    /**
     * 奖励金额
     */
    private BigDecimal awardMoney;


}
