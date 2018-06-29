package com.topaiebiz.promotion.mgmt.dto.coupon;

import lombok.Data;

import java.util.List;


/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 15:46 2018/5/11
 * @Modified by:
 */

@Data
public class CouponDto {

	/**
	 * 活动中的优惠券id
	 */
	private Long couponId;

	/**
	 * 活动中的优惠券数量
	 */
	private Integer couponNum;

	/**
	 * 增加的数量
	 */
	private Integer addNum;



}
