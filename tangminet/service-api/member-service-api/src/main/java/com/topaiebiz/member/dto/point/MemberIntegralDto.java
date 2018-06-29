package com.topaiebiz.member.dto.point;

import com.topaiebiz.member.dto.member.MemberDto;
import lombok.Data;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017/12/21 15:06
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MemberIntegralDto {

    // 用户
    private MemberDto memberEntity;

    // 积分
    private Long integral;


    ///////////// 增加
    // 获取类型
    private Long gainType;

    // 订单ID
    private Long orderId;

    /**
     * 消费金额。
     */
    private Double costMoney;

    /**
     * 获取积分。
     */
    private Long gainScore;

    /**
     * 拉取的时候记录。
     */
    private Long scoreSource;

    ///////////// 扣除

    /**
     * 使用积分
     */
    private Long usageScore;

    /**
     * 抵扣金额
     */
    private Double deductibleAmount;


    /**
     * 备注
     */
    private String memo;

}
