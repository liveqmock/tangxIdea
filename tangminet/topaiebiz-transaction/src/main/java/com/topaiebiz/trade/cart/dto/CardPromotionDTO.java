package com.topaiebiz.trade.cart.dto;

import lombok.Data;

import java.util.Date;

/**
 * Description 购物车--商品--营销活动DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/11 11:12
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class CardPromotionDTO {

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 类型Code
     */
    private Integer typeCode;
    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动开始时间
     */
    private Date startTime;

    /**
     * 活动结束时间
     */
    private Date endTime;

}
