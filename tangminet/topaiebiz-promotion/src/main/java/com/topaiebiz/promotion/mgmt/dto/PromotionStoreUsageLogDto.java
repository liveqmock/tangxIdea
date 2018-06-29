package com.topaiebiz.promotion.mgmt.dto;

import java.math.BigDecimal;

/**
 * 
 * Description 店铺活动使用记录DTO
 * 
 * 
 * Author Administrator
 * 
 * Date 2017年10月16日 下午5:15:11
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public class PromotionStoreUsageLogDto {

	/**
	 * ID
	 */
	private Long id;

	/**
	 * 商家订单编号
	 */
	private Long orderId;

	/**
	 * 营销活动
	 */
	private Long promotionId;

	/**
	 * 会员编号
	 */
	private Long memberId;

	/**
	 * 优惠码编号
	 */
	private Long couponId;

	/**
	 * 所属商家
	 */
	private Long storeId;

	/**
	 * 优惠金额
	 */
	private BigDecimal price;

	/**
	 * 备注
	 */
	private String memo;

}
