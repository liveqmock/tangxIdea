package com.topaiebiz.promotion.mgmt.dto;

import com.topaiebiz.promotion.promotionEnum.PromotionGradeEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 * 
 * Description 营销活动(发起报名DTO)
 * 
 * 
 * Author Joe 
 *    
 * Date 2017年12月8日 下午3:30:25 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class PromotionEnrolDto {
	
	/**
	 * ID
	 */
	private Long id;

	/**
	 * 活动发起者
	 */
	private Long sponsorType;

	/**
	 * 营销级别
	 */
	private PromotionGradeEnum gradeId;

	/**
	 * 营销类型
	 */
	private PromotionTypeEnum typeId;

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
	 * 限制会员类型
	 */
	private Long memberTypeId;

	/**
	 * 限制会员级别
	 */
	private Long memberGradeId;

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

	/**
	 * 发放数量
	 */
	private Integer amount;

	/**
	 * 已领数量
	 */
	private Integer usedAmount;

	/**
	 * 限制数额
	 */
	private Integer confineAmount;

	/**
	 * 平台补贴比例
	 */
	private Double platformRatio;

	/**
	 * 备注
	 */
	private String memo;
	
	/**
	 * 报名标题
	 */
	@NotNull(message = "{validation.promotion.applyTitle}")
	private String applyTitle;

	/**
	 * 报名开始时间
	 */
	private Date applyStartTime;

	/**
	 * 报名开始时间
	 */
	@NotNull(message = "{validation.promotion.startTime}")
	private String promotionEnrolStart;

	/**
	 * 报名结束时间
	 */
	@NotNull(message = "{validation.promotion.endTime}")
	private String promotionEnrolEnd;

	/**
	 * 报名结束时间
	 */
	private Date applyEndTime;

	/**
	 * 报名说明
	 */
	@NotNull(message = "{validation.promotion.applyDesc}")
	@Length(max = 40, message = "{validation.promotion.descriptionLength}")
	private String applyDesc;

	/**
	 * 店铺等级要求
	 */
	@NotNull(message = "{validation.promotion.storeGrade}")
	private Long storeGrade;

	/**
	 * 最少报名商品数
	 */
	@NotNull(message = "{validation.promotion.mixProductNum}")
	@DecimalMin(value = "1", message = "{validation.promotion.mixProductNumLeast}")
	private Integer mixProductNum;

	/**
	 * 最大报名商品数
	 */
	@NotNull(message = "{validation.promotion.maxProductNum}")
	@DecimalMin(value = "1", message = "{validation.promotion.maxProductNumMost}")
	private Integer maxProductNum;

	/**
	 * 报名状态
	 */
	private Integer state;

	/**
	 * 活动状态
	 */
	private Integer marketState;

	/**
	 * 已使用数
	 */
	private Integer usedQuantity;

	/**
	 * 创建时间
	 */
	private Date createdTime;

}
