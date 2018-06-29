package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * Description 平台活动使用记录表
 * 
 * 
 * Author Joe
 * 
 * Date 2017年9月27日 下午4:01:40
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_pro_promotion_platform_usage_log")
@Data
public class PromotionPlatformUsageLogEntity extends BaseEntity<Long> {

	/** 序列化版本号。 */
	@TableField(exist = false)
	private static final long serialVersionUID = -2929579912946831737L;

	/**
	 * 平台订单编号
	 */
	private Long orderId;

	/**
	 * 会员编号
	 */
	private Long memberId;

	/**
	 * 营销活动
	 */
	private Long promotionId;

	/**
	 * 优惠码编号
	 */
	private Long couponId;

	/**
	 * 优惠金额
	 */
	private BigDecimal price;

	/**
	 * 备注
	 */
	private String memo;
	private Long creatorId;
	private Date createdTime = new Date();

	public void clearInit() {
		this.createdTime = null;
		super.cleanInit();
	}
}
