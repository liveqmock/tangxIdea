package com.topaiebiz.promotion.dto;

import com.topaiebiz.promotion.promotionEnum.PromotionGradeEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 营销活动信息
 * Created by Joe on 2018/1/8.
 */
@Data
public class PromotionDTO implements Serializable {

    /**
     * 活动id
     */
    private Long id;

    /**
     * 活动发起者
     */
    private Long sponsorType;

    /**
     * 营销级别
     */
    private PromotionGradeEnum grade;

    /**
     * 营销类型
     */
    private PromotionTypeEnum type;

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

    /**
     * 活动说明
     */
    private String description;

    /**
     * 是否指定商品可用
     */
    private Integer isGoodsArea;

    /**
     * 限订指定可用的商品集合，若为空则标识无限制自由使用
     */
    private List<PromotionGoodsDTO> limitGoods = new ArrayList<>();

    /**
     * 限订指定可用/不可用的店铺集合
     */
    private List<PromotionStoreDTO> storeDTOS = new ArrayList<>();

    /**
     * 条件类型
     */
    private Integer condType;

    /**
     * 条件值
     */
    private BigDecimal condValue;

    /**
     * 优惠类型
     */
    private Integer discountType;

    /**
     * 优惠值
     */
    private BigDecimal discountValue;
}