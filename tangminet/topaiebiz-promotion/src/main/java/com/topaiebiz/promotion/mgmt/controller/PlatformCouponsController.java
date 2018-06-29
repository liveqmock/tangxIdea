package com.topaiebiz.promotion.mgmt.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.dto.coupon.CouponStoreDto;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import com.topaiebiz.promotion.mgmt.service.PlatformCouponsService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @Author tangx.w
 * @Description: 平台优惠券管理
 * @Date: Create in 10:40 2018/5/3
 * @Modified by:
 */
@RestController
@RequestMapping(path = "/promotion/coupon", method = RequestMethod.POST)
public class PlatformCouponsController {

	@Autowired
	private PlatformCouponsService platformCouponsService;


	/**
	 *@Author: tangx.w
	 *@Description:  添加平台优惠券
	 *@param  * @param null
	 *@Date: 2018/5/4 14:26
	 */
	@RequestMapping(path = "/addPromotionPlatformCoupon")
	@PermissionController(value = PermitType.PLATFORM, operationName = "添加平台优惠券")
	public ResponseInfo addPromotionPlatformCoupon(@RequestBody @Valid PromotionDto promotionDto, BindingResult result) throws GlobalException, ParseException {
		if (result.hasErrors()) {
			// 初始化非法参数的提示信息。
			IllegalParamValidationUtils.initIllegalParamMsg(result);
			// 获取非法参数异常信息对象，并抛出异常。
			throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
		}
		return new ResponseInfo(platformCouponsService.savePromotionPlatformCoupon(promotionDto));
	}

	/**
	 *@Author: tangx.w
	 *@Description:  保存店铺选择
	 *@param  * @param null
	 *@Date: 2018/5/4 14:26
	 */
	@RequestMapping(path = "/insertPlatformCouponStores")
	@PermissionController(value = PermitType.PLATFORM, operationName = "保存店铺选择")
	public ResponseInfo insertPlatformCouponStores(@RequestBody Map<String,Object> map){
		String storeIds = map.get("storeIds").toString();
		Long promotionId = Long.valueOf(map.get("promotionId").toString());
		if (promotionId==null){
			throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
		}
		platformCouponsService.insertPlatformCouponStores(storeIds,promotionId);
		return new ResponseInfo();
	}

	/**
	 *@Author: tangx.w
	 *@Description:  获取圈中的店铺列表
	 *@param  * @param null
	 *@Date: 2018/5/4 14:26
	 */
	@RequestMapping(path = "/getCouponStores")
	@PermissionController(value = PermitType.PLATFORM, operationName = "获取圈中的店铺列表")
	public ResponseInfo getCouponStores(@RequestBody CouponStoreDto couponStoreDto){
		return new ResponseInfo(platformCouponsService.getCouponStoreInfos(couponStoreDto));
	}


	/**
	 *@Author: tangx.w
	 *@Description:  取消平台优惠券
	 *@param  id
	 *@Date: 2018/5/4 14:26
	 */
	@RequestMapping(path = "/cancelPlatformCoupon/{id}")
	@PermissionController(value = PermitType.PLATFORM, operationName = "取消平台优惠券")
	public ResponseInfo cancel(@PathVariable Long id){
		if (id==null){
			throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
		}
		platformCouponsService.cancelPlatformCoupon(id);
		return new ResponseInfo();
	}

	/**
	 *@Author: tangx.w
	 *@Description:  发布平台优惠券
	 *@param  id
	 *@Date: 2018/5/4 14:26
	 */
	@RequestMapping(path = "/releasePlatformCoupon/{id}")
	@PermissionController(value = PermitType.PLATFORM, operationName = "发布平台优惠券")
	public ResponseInfo releasePlatformCoupon(@PathVariable Long id){
		if (id==null){
			throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
		}
		platformCouponsService.releasePlatformCoupon(id);
		return new ResponseInfo();
	}

	/**
	 *@Author: tangx.w
	 *@Description: 圈中店铺取消选择
	 *@param  map
	 *@Date: 2018/5/7 19:25
	 */
	@RequestMapping(path = "/cancelCouponStore")
	@PermissionController(value = PermitType.PLATFORM, operationName = "圈中店铺取消选择")
	public ResponseInfo cancelCouponStore(@RequestBody Map<String,Object> map){
		Long id = Long.valueOf(map.get("promotionId").toString());
		Long storeId = Long.valueOf(map.get("storeId").toString());
		if (id==null){
			throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
		}
		platformCouponsService.cancelCouponStore(storeId,id);
		return new ResponseInfo();
	}

	/**
	 *@Author: tangx.w
	 *@Description: 发布中的平台优惠券，修改完商品之后的保存
	 *@param  id
	 *@Date: 2018/5/7 19:25
	 */
	@RequestMapping(path = "/save/{id}")
	@PermissionController(value = PermitType.PLATFORM, operationName = "发布中的平台优惠券，修改完商品之后的保存")
	public ResponseInfo save(@PathVariable Long id){
		if (id==null){
			throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
		}
		platformCouponsService.save(id);
		return new ResponseInfo();
	}

	/**
	 *@Author: tangx.w
	 *@Description:  复制优惠券
	 *@param  * @param null
	 *@Date: 2018/5/4 14:26
	 */
	@RequestMapping(path = "/copyPlatformCoupon/{id}")
	@PermissionController(value = PermitType.PLATFORM, operationName = "复制优惠券")
	public ResponseInfo copyPlatformCoupon(@PathVariable Long id){
		if (id==null){
			throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
		}
		return new ResponseInfo(platformCouponsService.copyPlatformCoupon(id));
	}


}
