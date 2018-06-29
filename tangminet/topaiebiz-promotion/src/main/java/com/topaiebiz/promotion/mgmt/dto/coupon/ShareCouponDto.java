package com.topaiebiz.promotion.mgmt.dto.coupon;

import com.topaiebiz.promotion.mgmt.entity.PromotionEntity;
import com.topaiebiz.promotion.mgmt.entity.PromotionShareRrceiveEntity;
import lombok.Data;

import java.util.List;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 14:24 2018/5/16
 * @Modified by:
 */
@Data
public class ShareCouponDto {

	/**
	 * 活动ID
	 */
	private Long promotionId;

	/**
	 * 分享优惠券的分享密钥
	 */
	private String shareKey;

	/**
	 * 返回领取的优惠券
	 */
	private List<PromotionEntity> promotionList;

	/**
	 * 返回分享领取全纪录list，每个id求和
	 */
	private List<ShareCouponReceiveDetailDto> shareRrceiveList;

	/**
	 * 返回活动结果 0-正常领取 1-当日领取达到上限  2-活动期间领取数量达到上限 3-限制新用户领取 4-限制老用户领取
	 */
	private Integer reselut;

	/**
	 *优惠券列表
	 */
	private List<CouponDetilDto> couponDtoList;

	/**
	 *电话号码
	 */
	private String phone;


}
