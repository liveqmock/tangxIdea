package com.topaiebiz.promotion.mgmt.controller;

import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.dto.coupon.ActiveCouponDto;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import com.topaiebiz.promotion.mgmt.service.CouponActivityService;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 9:57 2018/5/10
 * @Modified by:
 */

@RestController
@RequestMapping(path = "/promotion/CouponActivity", method = RequestMethod.POST)
public class CouponActivityController {

	@Autowired
	private CouponActivityService couponActivityService;

	/**
	 * @param * @param null
	 * @Author: tangx.w
	 * @Description: 添加优惠券活动
	 * @Date: 2018/5/10 15:23
	 */
	@RequestMapping(path = "/addCouponActivity")
	@PermissionController(value = PermitType.PLATFORM, operationName = "添加优惠券活动")
	public ResponseInfo addCouponActivity(@RequestBody  PromotionDto promotionDto) throws GlobalException, ParseException {
		return new ResponseInfo(couponActivityService.addCouponActivity(promotionDto));
	}

	/**
	 * @param promotionDto-id,subType
	 * @Author: tangx.w
	 * @Description: 选择优惠券
	 * @Date: 2018/5/10 16:30
	 */
	@RequestMapping(path = "/getCouponList")
	@PermissionController(value = PermitType.PLATFORM, operationName = "选择优惠券")
	public ResponseInfo getCouponList(@RequestBody PromotionDto promotionDto) {
		Long promotionId = promotionDto.getId();
		if (promotionId == null) {
			throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
		}
		int pageNo = promotionDto.getPageNo();
		int pageSize = promotionDto.getPageSize();
		PagePO pagePO = new PagePO();
		pagePO.setPageNo(pageNo);
		pagePO.setPageSize(pageSize);
		return new ResponseInfo(couponActivityService.getCouponList(pagePO, promotionDto));
	}

	/**
	 * @param map-promotionId,promotionIds
	 * @Author: tangx.w
	 * @Description: 保存选择优惠券
	 * @Date: 2018/5/10 16:30
	 */
	@RequestMapping(path = "/saveSelectedCoupons")
	@PermissionController(value = PermitType.PLATFORM, operationName = "保存选择优惠券")
	public ResponseInfo saveSelectedCoupons(@RequestBody Map<String, Object> map) {
		Long promotionId = Long.valueOf(map.get("promotionId").toString());
		String promotionIds = map.get("couponIds").toString();
		if (promotionId == null) {
			throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
		}
		couponActivityService.saveSelectedCoupons(promotionId, promotionIds);
		return new ResponseInfo();
	}


	/**
	 * @param promotionDto-id,name,discountValue,createdStartTime,createdEndTime
	 * @Author: tangx.w
	 * @Description: 获取选中的优惠券列表
	 * @Date: 2018/5/11 23:01
	 */
	@RequestMapping(path = "/getSelectedCoupons")
	@PermissionController(value = PermitType.PLATFORM, operationName = "获取选中的优惠券列表")
	public ResponseInfo getSelectedCoupons(@RequestBody PromotionDto promotionDto) {
		if (promotionDto.getId() == null) {
			throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
		}
		int pageNo = promotionDto.getPageNo();
		int pageSize = promotionDto.getPageSize();
		PagePO pagePO = new PagePO();
		pagePO.setPageNo(pageNo);
		pagePO.setPageSize(pageSize);
		return new ResponseInfo(couponActivityService.getSelectedCoupons(pagePO, promotionDto));
	}


	/**
	 * @param map-promotionId,couponId
	 * @Author: tangx.w
	 * @Description: 取消选择
	 * @Date: 2018/5/11 11:03
	 */
	@RequestMapping(path = "/cancelCoupon")
	@PermissionController(value = PermitType.PLATFORM, operationName = "取消选择")
	public ResponseInfo cancelCoupon(@RequestBody Map<String, Object> map) {
		Long promotionId = Long.valueOf(map.get("promotionId").toString());
		Long couponId = Long.valueOf(map.get("couponId").toString());
		if (promotionId == null) {
			throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
		}
		couponActivityService.cancelCoupon(promotionId, couponId);
		return new ResponseInfo();
	}


	/**
	 * @param activeCouponDto
	 * @Author: tangx.w
	 * @Description: 保存/发布
	 * @Date: 2018/5/11 11:03
	 */
	@RequestMapping(path = "/saveAcitivity")
	@PermissionController(value = PermitType.PLATFORM, operationName = "保存")
	public ResponseInfo saveAcitivity(@RequestBody ActiveCouponDto activeCouponDto) throws ParseException {

		Long promotionId = activeCouponDto.getPromotionId();
		if (promotionId == null) {
			throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
		}
		couponActivityService.saveAcitivity(activeCouponDto.getCouponIdList(), promotionId, activeCouponDto.getIsRelease(),activeCouponDto.getSubType());
		return new ResponseInfo();
	}


	/**
	 * @param id
	 * @Author: tangx.w
	 * @Description: 取消-新增的时候，复制的时候调用
	 * @Date: 2018/5/11 13:43
	 */
	@RequestMapping(path = "/cancel/{id}")
	@PermissionController(value = PermitType.PLATFORM, operationName = "取消-新增的时候，复制的时候调用")
	public ResponseInfo cancel(@PathVariable Long id) {
		if (id == null) {
			throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
		}
		couponActivityService.cancel(id);
		return new ResponseInfo();
	}

	/**
	 * @param id
	 * @Author: tangx.w
	 * @Description: 复制优惠券活动
	 * @Date: 2018/5/11 13:43
	 */
	@RequestMapping(path = "/copyCouponActive/{id}")
	@PermissionController(value = PermitType.PLATFORM, operationName = "复制优惠券活动")
	public ResponseInfo copyCouponActive(@PathVariable Long id) {
		if (id == null) {
			throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
		}
		return new ResponseInfo(couponActivityService.copyCouponActive(id));
	}


	/**
	 * @param promotionDto-id,memo
	 * @Author: tangx.w
	 * @Description: 停止优惠券活动
	 * @Date: 2018/5/11 14:42
	 */
	@RequestMapping(path = "/stopCouponActive")
	@PermissionController(value = PermitType.PLATFORM, operationName = "停止优惠券活动")
	public ResponseInfo stopCouponActive(@RequestBody PromotionDto promotionDto) {
		if (promotionDto.getId() == null) {
			throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
		}
		couponActivityService.stopCouponActive(promotionDto);
		return new ResponseInfo();
	}

	/**
	 *@Author: tangx.w
	 *@Description: 获取优惠券活动列表
	 *@param  promotionDto
	 *@Date: 2018/5/14 10:30
	 */
	@RequestMapping(path = "/getCouponActives")
	@PermissionController(value = PermitType.PLATFORM, operationName = "获取优惠券活动列表")
	public ResponseInfo getCouponActives(@RequestBody PromotionDto promotionDto) throws ParseException {
		return new ResponseInfo(couponActivityService.getCouponActives(promotionDto));
	}



}
