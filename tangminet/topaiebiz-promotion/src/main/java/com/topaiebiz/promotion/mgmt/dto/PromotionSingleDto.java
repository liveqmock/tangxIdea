package com.topaiebiz.promotion.mgmt.dto;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.topaiebiz.promotion.promotionEnum.PromotionGradeEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 
 * Description 单品活动DTO
 * 
 * 
 * Author Joe
 * 
 * Date 2017年9月30日 下午1:33:51
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class PromotionSingleDto {

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
	@NotNull(message = "{validation.promotion.name}")
	@Length(max = 15, message = "{validation.promotion.nameLength}")
	private String name;

	/**
	 * 显示标题
	 */
	private String showTitle;

	/**
	 * 显示类型
	 */
	private Integer showType;

	/**
	 * 活动开始时间
	 */
	private Date startTime;

	/**
	 * 活动开始时间（接收参数）
	 */
	@NotNull(message = "{validation.promotion.startTime}")
	private String promotionStart;

	/**
	 * 活动结束时间（接收参数）
	 */
	@NotNull(message = "{validation.promotion.endTime}")
	private String promotionEnd;

	/**
	 * 活动结束时间
	 */
	private Date endTime;

	/**
	 * 活动说明
	 */
	@Length(max = 40, message = "{validation.promotion.descriptionLength}")
	private String description;

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
	 * 报名开始时间
	 */
	private Date applyStartTime;

	/**
	 * 报名结束时间
	 */
	private Date applyEndTime;

	/**
	 * 报名说明
	 */
	private String applyDesc;

	/**
	 * 店铺等级要求
	 */
	private Long storeGrade;

	/**
	 * 最少报名商品数
	 */
	private Integer mixProductNum;

	/**
	 * 最大报名商品数
	 */
	private Integer maxProductNum;

	/**
	 * 报名状态
	 */
	private Integer state;

	/**
	 * 活动状态
	 */
	@NotNull(message = "{validation.promotion.marketState}")
	private Integer marketState;

	/**
	 * 优惠总额
	 */
	private BigDecimal totalDiscount;

	/**
	 * 删除标识
	 */
	private byte deletedFlag;

}
