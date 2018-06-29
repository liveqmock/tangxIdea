package com.topaiebiz.trade.order.dto.ordersubmit;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/***
 * @author yfeng
 * @date 2018-01-09 11:23
 */
@Data
public class PromotionInfoDTO {

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 活动名称
     */
    private String name;
    /**
     * 活动描述
     */
    private String description;

    /**
     * 类型Code
     */
    private Integer typeCode;
    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 单品活动和店铺活动的此字段会有值
     */
    private BigDecimal goodsPrice;

    /**
     * 店铺活动、店铺优惠券、平台优惠券的此字段会有值
     */
    private BigDecimal discount;

    /**
     * 活动开始时间
     */
    private Date startTime;

    /**
     * 活动结束时间
     */
    private Date endTime;

    /**
     * 是否有指定商品使用的限制
     */
    private Boolean limitGoods = false;

    /**
     * 条件类型
     */
    private Integer condType;

    /**
     * 条件值
     */
    private BigDecimal condValue;
    /**
     * 优惠值
     */
    private BigDecimal discountValue;
}