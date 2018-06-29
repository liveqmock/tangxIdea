package com.topaiebiz.promotion.mgmt.dto.coupon;

import lombok.Data;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 13:44 2018/5/16
 * @Modified by:
 */

@Data
public class CouponSharePropertyDto {

	/**
	 * 分享入口开放起始时间
	 */
	private String shareCouponStartTime;

	/**
	 * 分享入口开放结束时间
	 */
	private String shareCouponEndTime;

	/**
	 * 优惠券分享链接
	 */
	private String shareCouponUrl;

	/**
	 * 优惠券分享标题
	 */
	private String shareCouponTitle;

	/**
	 * 优惠券分享内容
	 */
	private String shareCouponContent;

	/**
	 * 优惠券分享图片
	 */
	private String shareCouponPicture;

	/**
	 * 活动id
	 */
	private Long promotionId;

	/**
	 * 该活动是否开启
	 */
	private boolean isOpen;

	/**
	 * 分享key
	 */
	private String shareKey;
}
