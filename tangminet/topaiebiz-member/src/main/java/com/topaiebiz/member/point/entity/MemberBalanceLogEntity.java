package com.topaiebiz.member.point.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ward on 2018-01-17.
 */
@TableName("t_mem_member_balance_log")
@Data
public class MemberBalanceLogEntity extends BaseEntity<Long> {

    /**
     * 日志ID
     **/
    private Long id;

    /**
     * 会员ID
     **/
    private Long memberId;

    /**
     * 用户名
     **/
    private String userName;

    /**
     * 会员手机号
     **/
    private String telephone;

    /**
     * 变化前余额
     **/
    private BigDecimal beforeBalance;

    /**
     * 余额变化额度 -表示减少+表示增加
     **/
    private BigDecimal balanceChange;

    /**
     * 变化后余额
     **/
    private BigDecimal afterBalance;

    /**
     * 余额变化的操作code
     ***/
    private String operateType;

    /**
     * '操作说明'
     **/
    private String operateDesc;

    /**
     * 交易单号等唯一标示用于解决幂等性
     */
    private String operateSn;

    /**
     * 备注
     */
    private String meno;

    private Long creatorId;

    private Date createdTime = new Date();
}