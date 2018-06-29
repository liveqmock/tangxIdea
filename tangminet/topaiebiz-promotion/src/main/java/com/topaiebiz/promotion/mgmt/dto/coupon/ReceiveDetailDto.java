package com.topaiebiz.promotion.mgmt.dto.coupon;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 21:58 2018/5/17
 * @Modified by:
 */
@Data
public class ReceiveDetailDto {

	private List<CouponDetilDto> couponDetilList;

	private BigDecimal TotalVelue;
}
