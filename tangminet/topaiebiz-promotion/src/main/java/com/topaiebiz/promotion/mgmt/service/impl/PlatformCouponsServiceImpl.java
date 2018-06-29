package com.topaiebiz.promotion.mgmt.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.dto.store.MerchantInfoDTO;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.promotion.constants.PromotionConstants;
import com.topaiebiz.promotion.mgmt.dao.PromotionDao;
import com.topaiebiz.promotion.mgmt.dao.PromotionLogDao;
import com.topaiebiz.promotion.mgmt.dao.PromotionStoresDao;
import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.dto.coupon.CouponStoreDto;
import com.topaiebiz.promotion.mgmt.entity.PromotionEntity;
import com.topaiebiz.promotion.mgmt.entity.PromotionLogEntity;
import com.topaiebiz.promotion.mgmt.entity.PromotionStoresEntity;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import com.topaiebiz.promotion.mgmt.service.PlatformCouponsService;
import com.topaiebiz.promotion.mgmt.util.PromotionUtils;
import com.topaiebiz.promotion.promotionEnum.PromotionGradeEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionPlatformCouponStateEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 15:38 2018/5/3
 * @Modified by:
 */

@Service
public class PlatformCouponsServiceImpl implements PlatformCouponsService {

	@Autowired
	private PromotionDao promotionDao;

	@Autowired
	private PromotionLogDao promotionLogDao;

	@Autowired
	private StoreApi storeApi;

	@Autowired
	private PromotionStoresDao promotionStoresDao;


	/**
	 * @param promotionDto
	 * @Author: tangx.w
	 * @Description: 创建店铺优惠券
	 * @Date: 2018/5/3 15:43
	 */
	@Override
	public Long savePromotionPlatformCoupon(PromotionDto promotionDto) throws ParseException {
		Date startDate = pareTime(promotionDto.getPromotionStart());
		Date endDate = pareTime(promotionDto.getPromotionEnd());
		promotionDto.setStartTime(startDate);
		promotionDto.setEndTime(endDate);
		PromotionEntity promotion = new PromotionEntity();
		if (promotionDto.getId() != null) {
			promotion = promotionDao.selectById(promotionDto.getId());
			if (promotion != null) {
				return updateCouponById(promotionDto, promotion);
			} else {
				throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
			}
		}
		// 当前用户ID(创建人编号)
		Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
		// 根据电话查到会员，获取所属店铺
		Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
		String userName = SecurityContextUtils.getCurrentUserDto().getUsername();
		BeanCopyUtil.copy(promotionDto, promotion);
		Integer operationType;
		if (PromotionConstants.IsRslease.TYPE_RELEASE.equals(promotionDto.getIsRelease())) {
			operationType = PromotionConstants.OperationType.TYPE_RELEASE;
			promotion.setMarketState(PromotionPlatformCouponStateEnum.PROMOTION_STATE_START.getCode());
		} else {
			operationType = PromotionConstants.OperationType.TYPE_NEWLY_ADDED;
			promotion.setMarketState(PromotionPlatformCouponStateEnum.PROMOTION_STATE_NOT_RELEASE.getCode());
		}
		String memo = "";
		promotion.setSponsorType(storeId);
		promotion.setCreatorId(creatorId);
		promotion.setUsedAmount(0);
		promotion.setCreatedTime(new Date());
		promotion.setGradeId(PromotionGradeEnum.PROMOTION_GRADE_PLATFORM.getCode());
		promotion.setTypeId(PromotionTypeEnum.PROMOTION_TYPE_COUPON.getCode());
		promotion.setDiscountType(PromotionConstants.DiscountType.THE_SALE);
		promotionDao.insert(promotion);
		PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(userName, operationType, memo, promotion.getId());
		promotionLogDao.insert(promotionLogEntity);
		return promotion.getId();
	}

	public Date pareTime(String time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.parse(time);
	}

	public Long updateCouponById(PromotionDto promotionDto, PromotionEntity promotion) {
		Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
		String userName = SecurityContextUtils.getCurrentUserDto().getUsername();
		Integer operationType = PromotionConstants.OperationType.TYPE_EDIT;
		String memo = "";


		if(PromotionPlatformCouponStateEnum.PROMOTION_STATE_START.getCode().equals(promotion.getMarketState())){
			promotionDto.setMarketState(PromotionPlatformCouponStateEnum.PROMOTION_STATE_START.getCode());
		}
		//改变返回清空圈中店铺
		if(!promotionDto.getIsGoodsArea().equals(promotion.getIsGoodsArea())){
			EntityWrapper<PromotionStoresEntity> entityWrapper = new EntityWrapper<>();
			entityWrapper.eq("promotionId",promotion.getId());
			entityWrapper.eq("deletedFlag",PromotionConstants.DeletedFlag.DELETED_NO);
			List<PromotionStoresEntity> promotionStoresList = promotionStoresDao.selectList(entityWrapper);
			for(PromotionStoresEntity promotionStores : promotionStoresList){
				promotionStores.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
				promotionStoresDao.updateById(promotionStores);
			}
		}
		BeanCopyUtil.copy(promotionDto, promotion);
		if(PromotionConstants.IsRslease.TYPE_RELEASE.equals(promotionDto.getIsRelease())){
			promotion.setMarketState(PromotionPlatformCouponStateEnum.PROMOTION_STATE_START.getCode());
			if(PromotionPlatformCouponStateEnum.PROMOTION_STATE_START.getCode().equals(promotion.getMarketState())) {
				operationType = PromotionConstants.OperationType.TYPE_RELEASE;
			}
		}
		promotion.setLastModifierId(creatorId);
		promotion.setLastModifiedTime(new Date());
		PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(userName, operationType, memo, promotionDto.getId());
		promotionLogDao.insert(promotionLogEntity);
		promotionDao.updateById(promotion);
		return promotion.getId();
	}

	@Override
	public void insertPlatformCouponStores(String storeIds, Long promotionId) {
		List<Long> storeIdList = PromotionUtils.stringToList(storeIds);
		List<StoreInfoDetailDTO> storeList = storeApi.getStoreList(storeIdList);
		List<Long> merchantIdList = storeList.stream().map(e -> e.getMerchantId()).collect(Collectors.toList());
		List<MerchantInfoDTO> merchantInfoList = storeApi.getMerchantInfo(merchantIdList);
		Map<Long, Date> merchantEntryMap = getEntryTimeMap(merchantInfoList);
		Map<Long, String> merchantMap = getMerchantInfoMap(merchantInfoList);
		for (StoreInfoDetailDTO storeInfoDetail : storeList) {
			PromotionStoresEntity promotionStores = new PromotionStoresEntity();
			promotionStores.setPromotionId(promotionId);
			promotionStores.setIsReleaseData(PromotionConstants.IsRsleaseDate.TYPE_NO);
			promotionStores.setEntryTime(merchantEntryMap.get(storeInfoDetail.getMerchantId()));
			promotionStores.setStoreId(storeInfoDetail.getId());
			promotionStores.setName(storeInfoDetail.getName());
			promotionStores.setCreatedTime(new Date());
			promotionStores.setMerchantName(merchantMap.get(storeInfoDetail.getMerchantId()));
			promotionStores.setDiscountType(PromotionConstants.DiscountType.DISCOUNT);
			promotionStoresDao.insert(promotionStores);
		}

	}

	public Map<Long, String> getMerchantInfoMap(List<MerchantInfoDTO> merchantInfoList) {
		Map<Long, String> map = new HashMap<>();
		for (MerchantInfoDTO merchantInfo : merchantInfoList) {
			map.put(merchantInfo.getId(), merchantInfo.getName());
		}
		return map;
	}

	public Map<Long, Date> getEntryTimeMap(List<MerchantInfoDTO> merchantInfoList) {
		Map<Long, Date> map = new HashMap<>();
		for (MerchantInfoDTO merchantInfo : merchantInfoList) {
			map.put(merchantInfo.getId(), merchantInfo.getCreatedTime());
		}
		return map;
	}


	/**
	 * @param couponStoreDto
	 * @Author: tangx.w
	 * @Description: 获取圈中的店铺列表
	 * @Date: 2018/5/5 11:31
	 */
	@Override
	public PageInfo<CouponStoreDto> getCouponStoreInfos(CouponStoreDto couponStoreDto) {
		PagePO pagePO = new PagePO();
		pagePO.setPageNo(couponStoreDto.getPageNo());
		pagePO.setPageSize(couponStoreDto.getPageSize());
		Page<CouponStoreDto> page = PageDataUtil.buildPageParam(pagePO);
		List<CouponStoreDto> cuponStoreDtos = promotionStoresDao.slectPromotionStoresList(page, couponStoreDto);
		page.setRecords(cuponStoreDtos);
		return PageDataUtil.copyPageInfo(page);
	}

	/**
	 * @param promotionId
	 * @Author: tangx.w
	 * @Description: 取消平台优惠券id
	 * @Date: 2018/5/5 15:28
	 */
	@Override
	public void cancelPlatformCoupon(Long promotionId) {
		//删除平台优惠券
		delPromotionEntity(promotionId);
		//删除优惠券圈中店铺
		delPromotionStroes(promotionId);
	}


	public void delPromotionEntity(Long promotionId) {
		EntityWrapper<PromotionEntity> entityEntityWrapper = new EntityWrapper<>();
		entityEntityWrapper.eq("id", promotionId);
		List<PromotionEntity> promotionList = promotionDao.selectList(entityEntityWrapper);
		for (PromotionEntity promotion : promotionList) {
			promotion.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
			promotionDao.updateById(promotion);
		}
	}

	public void delPromotionStroes(Long promotionId) {
		EntityWrapper<PromotionStoresEntity> entityEntityWrapper = new EntityWrapper<>();
		entityEntityWrapper.eq("promotionId", promotionId);
		List<PromotionStoresEntity> promotionStoresList = promotionStoresDao.selectList(entityEntityWrapper);
		for (PromotionStoresEntity promotionStores : promotionStoresList) {
			promotionStores.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
			promotionStoresDao.updateById(promotionStores);
		}
	}

	/**
	 *@Author: tangx.w
	 *@Description: 发布平台优惠券
	 *@param  promotionId
	 *@Date: 2018/5/5 15:28
	 */
	@Override
	public void releasePlatformCoupon(Long promotionId){
		Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
		String userName = SecurityContextUtils.getCurrentUserDto().getUsername();
		Integer operationType =PromotionConstants.OperationType.TYPE_RELEASE;
		String memo ="";
		EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("id",promotionId);
		entityWrapper.eq("deletedFlag",PromotionConstants.DeletedFlag.DELETED_NO);
		List<PromotionEntity> promotionList = promotionDao.selectList(entityWrapper);
		PromotionEntity promotion = promotionList.get(0);
		//判断优惠券活动是否过期
		promotion.setMarketState(PromotionPlatformCouponStateEnum.PROMOTION_STATE_START.getCode());
		//更新圈中店铺信息
		updateCouponStore(promotionId);
		PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(userName,operationType,memo,promotionId);
		promotionLogEntity.setCreatorId(creatorId);
		promotionLogEntity.setCreatedTime(new Date());
		promotionLogDao.insert(promotionLogEntity);
		promotionDao.updateById(promotion);
	}

	public void updateCouponStore(Long promotionId){
		EntityWrapper<PromotionStoresEntity> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("promotionId",promotionId);
		entityWrapper.eq("deletedFlag",PromotionConstants.DeletedFlag.DELETED_NO);
		List<PromotionStoresEntity> promotionStoresList = promotionStoresDao.selectList(entityWrapper);
		for(PromotionStoresEntity promotionStore : promotionStoresList){
			promotionStore.setIsReleaseData(PromotionConstants.IsRsleaseDate.TYPE_YES);
			promotionStoresDao.updateById(promotionStore);
		}
	}

	/**
	 *@Author: tangx.w
	 *@Description: 判断时间是否在当前时间之后，是，返回true，否则返回false
	 *@param  date
	 *@Date: 2018/5/7 9:49
	 */
	public boolean judgeDate(Date date){
		Date now = new Date();
		if (now.getTime()>date.getTime()){
			return false;
		}else{
			return true;
		}
	}

	/**
	 *@Author: tangx.w
	 *@Description: 圈中店铺取消选择
	 *@param  storeId,id
	 *@Date: 2018/5/7 19:25
	 */
	@Override
	public void cancelCouponStore(Long storeId,Long id){
		EntityWrapper<PromotionStoresEntity> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("promotionId",id);
		entityWrapper.eq("storeId",storeId);
		List<PromotionStoresEntity> promotionStoresList = promotionStoresDao.selectList(entityWrapper);
		for (PromotionStoresEntity promotionStores : promotionStoresList){
			promotionStores.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
			promotionStoresDao.updateById(promotionStores);
		}
	}

	/**
	 *@Author: tangx.w
	 *@Description: 发布中的平台优惠券，修改完商品之后的保存
	 *@param  id
	 *@Date: 2018/5/7 19:25
	 */
	@Override
	public void save(Long id){
		EntityWrapper<PromotionStoresEntity> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("promotionId",id);
		entityWrapper.eq("isReleaseData",PromotionConstants.IsRsleaseDate.TYPE_NO);
		List<PromotionStoresEntity> promotionStoresList = promotionStoresDao.selectList(entityWrapper);
		for(PromotionStoresEntity promotionStores : promotionStoresList){
			promotionStores.setIsReleaseData(PromotionConstants.IsRsleaseDate.TYPE_YES);
			promotionStoresDao.updateById(promotionStores);
		}
	}

	/**
	 * @param promotionId
	 * @Author: tangx.w
	 * @Description: 复制优惠券id
	 * @Date: 2018/5/7 9:17
	 */
	@Override
	public Long copyPlatformCoupon(Long promotionId) {
		Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
		String userName = SecurityContextUtils.getCurrentUserDto().getUsername();
		Integer operationType =PromotionConstants.OperationType.TYPE_NEWLY_ADDED;
		String memo ="";
		//复制优惠券
		Long newPromotionId = copyCoupon(promotionId);
		//复制优惠券店铺
		copyCouponStores(newPromotionId, promotionId);
		PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(userName,operationType,memo,newPromotionId);
		promotionLogEntity.setCreatorId(creatorId);
		promotionLogEntity.setCreatedTime(new Date());
		promotionLogDao.insert(promotionLogEntity);
		return newPromotionId;
	}

	public Long copyCoupon(Long promotionId) {
		EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("id", promotionId);
		entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
		List<PromotionEntity> promotionList = promotionDao.selectList(entityWrapper);
		if (promotionList == null) {
			throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
		}
		PromotionEntity promotion = promotionList.get(0);
		promotion.setMarketState(PromotionPlatformCouponStateEnum.PROMOTION_STATE_NOT_RELEASE.getCode());
		promotion.setId(null);
		promotion.setCreatedTime(new Date());
		promotionDao.insert(promotion);
		return promotion.getId();
	}

	public void copyCouponStores(Long newPromotionId, Long promotionId) {
		EntityWrapper<PromotionStoresEntity> entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("promotionId",promotionId);
		entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
		List<PromotionStoresEntity> PromotionStoresList = promotionStoresDao.selectList(entityWrapper);
		for(PromotionStoresEntity promotionStores : PromotionStoresList){
			promotionStores.setPromotionId(newPromotionId);
			promotionStores.setIsReleaseData(PromotionConstants.IsRsleaseDate.TYPE_NO);
			promotionStores.setId(null);
			promotionStores.setCreatedTime(new Date());
			promotionStoresDao.insert(promotionStores);
		}
	}
}
