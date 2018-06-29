package com.topaiebiz.member.dto.point;

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
@Data
public class PointLogDto {

    /**
     * 会员等级的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。
     */
    private Long id;


    /**
     * 会员ID
     **/
    private Long memberId;


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

    private Date createdTime;


    private Integer crmPointChange;


}
