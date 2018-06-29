package com.topaiebiz.promotion.mgmt.dto.coupon;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 15:49 2018/4/24
 * @Modified by:
 */
@Data
public class ActiveConfigDto {


    /**
     * 发行起始时间r
     */
    @NotNull(message = "{validation.promotion.startTime}")
    private String releaseStartTime;

    /**
     * 发行结束时间
     */
    @NotNull(message = "{validation.promotion.startTime}")
    private String releaseEndTime;

    /**
     * 每个ID每日限制领取数量
     */
    private Integer dayConfineAmount;


    /**
     * 领取方式
     */
    private String receiveType;

    /**
     * 限制领取用户类型 0-新用户，1-老用户
     */
    private Integer userType;

    /**
     * 分享限制领取人数
     */
    private Integer shareConfinePeopleAmount;


    /**
     * 分享优惠券发放总份数
     */
    private Integer numberOfCopies;

    /**
     * 分享优惠券没个id每次领取优惠券数量
     */
    private Integer receiveConfineAmount;


}
