package com.topaiebiz.promotion.mgmt.dto.coupon;

import lombok.Data;

import java.util.List;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 10:18 2018/5/16
 * @Modified by:
 */
@Data
public class ActiveCouponDto {

	private List<CouponDto> couponIdList;

	private Integer isRelease;

	private Long promotionId;

	private Integer subType;
}
