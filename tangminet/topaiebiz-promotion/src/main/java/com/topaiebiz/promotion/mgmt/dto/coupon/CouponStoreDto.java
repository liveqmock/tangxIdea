package com.topaiebiz.promotion.mgmt.dto.coupon;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 14:34 2018/5/4
 * @Modified by:
 */
@Data
public class CouponStoreDto  extends PagePO implements Serializable {

	/**
	 * 优惠券id
	 */
	private Long promotionId;

	/**
	 * 公司名称
	 */
	private String merchantName;

	/**
	 * 店铺名称
	 */
	private String name;

	/**
	 * 店铺id
	 */
	private Long storeId;

	/**
	 * 时间
	 */
	private Date entryTime;

	/**
	 * 是否为发布进行中老数据 0-不是  1-是
	 */
	private Byte isReleaseData;


}
