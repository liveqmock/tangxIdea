package com.topaiebiz.promotion.mgmt.dto.coupon;

import lombok.Data;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 16:12 2018/5/4
 * @Modified by:
 */
@Data
public class SkuStateDto {

	/**
	 * skuid
	 */
	private Long skuId;


	private byte state;

	/**
	 * 是否为发布进行中老数据 0-不是  1-是
	 */
	private Byte isReleaseData;

}
