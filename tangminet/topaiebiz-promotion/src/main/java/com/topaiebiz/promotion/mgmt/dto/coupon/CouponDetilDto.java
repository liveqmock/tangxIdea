package com.topaiebiz.promotion.mgmt.dto.coupon;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 21:59 2018/5/17
 * @Modified by:
 */
@Data
public class CouponDetilDto {

	private Long couponId;

	private Integer num;

	private BigDecimal discountValue;
}
