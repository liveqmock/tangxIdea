package com.topaiebiz.member.point.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * Description： 会员积分获取记录
 * <p>
 * <p>
 * Author Scott.Yang
 * <p>
 * Date 2017年9月28日 下午3:10:56
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_mem_member_point_log")
@Data
public class MemberPointLogEntity extends BaseBizEntity<Long> {

    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = -5719053409251769064L;


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
    private Integer beforePoint;

    /**
     * 积分变化额度 -表示减少+表示增加
     **/
    private Integer pointChange;

    /**
     * 变化后积分
     **/
    private Integer afterPoint;

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


    @TableField(exist = false)
    /** 最后修改人编号。取值为最后修改人的全局唯一主键标识符。 */
    private Long lastModifierId;

    /**
     * 最后修改时间。默认取值为null，当发生修改时取系统的当前时间。
     */
    @TableField(exist = false)
    private Date lastModifiedTime;


    @Deprecated
    @TableField(exist = false)
    private Long gainType;

    @Deprecated
    @TableField(exist = false)
    private Long gainScore;

    @Deprecated
    @TableField(exist = false)
    private Long orderId;

    @Deprecated
    @TableField(exist = false)
    private Double CostMoney;
}