package com.topaiebiz.promotion.mgmt.service;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.dto.coupon.CouponStoreDto;
import com.topaiebiz.promotion.mgmt.entity.PromotionStoresEntity;

import java.text.ParseException;
import java.util.List;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 15:33 2018/5/3
 * @Modified by:
 */
public interface PlatformCouponsService {

	/**
	 *@Author: tangx.w
	 *@Description: 创建店铺优惠券
	 *@param  promotionDto
	 *@Date: 2018/5/3 15:42
	 */
	Long savePromotionPlatformCoupon(PromotionDto promotionDto) throws ParseException;

	/**
	 *@Author: tangx.w
	 *@Description: 插入平台优惠券店铺
	 *@param  storeIds,promotionId
	 *@Date: 2018/5/4 18:44
	 */
	void insertPlatformCouponStores(String storeIds,Long promotionId);

	/**
	 *@Author: tangx.w
	 *@Description: 获取圈中的店铺列表
	 *@param  couponStoreDto
	 *@Date: 2018/5/5 11:31
	 */
	PageInfo<CouponStoreDto> getCouponStoreInfos(CouponStoreDto couponStoreDto);

	/**
	 *@Author: tangx.w
	 *@Description: 取消平台优惠券
	 *@param  promotionId
	 *@Date: 2018/5/5 15:28
	 */
	void cancelPlatformCoupon(Long promotionId);

	/**
	 *@Author: tangx.w
	 *@Description: 发布平台优惠券
	 *@param  promotionId
	 *@Date: 2018/5/5 15:28
	 */
	void releasePlatformCoupon(Long promotionId);

	/**
	 *@Author: tangx.w
	 *@Description: 圈中店铺取消选择
	 *@param  storeId,id
	 *@Date: 2018/5/7 19:25
	 */
	void cancelCouponStore(Long storeId,Long id);

	/**
	 *@Author: tangx.w
	 *@Description: 发布中的平台优惠券，修改完商品之后的保存
	 *@param  id
	 *@Date: 2018/5/7 19:25
	 */
	void save(Long id);

	/**
	 *@Author: tangx.w
	 *@Description:  复制优惠券
	 *@param  * @param null
	 *@Date: 2018/5/4 14:26
	 */
	Long copyPlatformCoupon(Long id);

}
