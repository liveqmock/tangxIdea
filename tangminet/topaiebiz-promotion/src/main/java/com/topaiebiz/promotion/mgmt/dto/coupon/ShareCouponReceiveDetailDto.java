package com.topaiebiz.promotion.mgmt.dto.coupon;

import lombok.Data;
import sun.util.resources.ga.LocaleNames_ga;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 15:06 2018/5/17
 * @Modified by:
 */
@Data
public class ShareCouponReceiveDetailDto {

	/**
	 * 会员id
	 */
	private Long memberId;

	/**
	 * 会员电话
	 */
	private String phone;

	/**
	 * 会员小头像
	 */
	private String smallIcon;

	/**
	 * 领取总和
	 */
	private BigDecimal totalValue;

}

