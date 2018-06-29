package com.topaiebiz.promotion.mgmt.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.promotion.api.PromotionApi;
import com.topaiebiz.promotion.constants.PromotionConstants;
import com.topaiebiz.promotion.dto.*;
import com.topaiebiz.promotion.mgmt.dao.*;
import com.topaiebiz.promotion.mgmt.dto.MemberCouponDto;
import com.topaiebiz.promotion.mgmt.dto.coupon.ActiveConfigDto;
import com.topaiebiz.promotion.mgmt.entity.*;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import com.topaiebiz.promotion.mgmt.util.PromotionUtils;
import com.topaiebiz.promotion.promotionEnum.PromotionGradeEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionPlatformCouponStateEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionStateEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.topaiebiz.promotion.promotionEnum.PromotionStateEnum.PROMOTION_STATE_ONGOING;

/**
 * Created by Joe on 2018/1/8.
 */
@Service
public class PromotionApiImpl implements PromotionApi {

    // 营销活动信息
    @Autowired
    private PromotionDao promotionDao;

    //营销活动商品信息
    @Autowired
    private PromotionGoodsDao promotionGoodsDao;

    // 会员优惠券
    @Autowired
    private MemberCouponDao memberCouponDao;

    // 店铺活动使用记录
    @Autowired
    private PromotionStoreUsageLogDao promotionStoreUsageLogDao;

    // 平台活动使用记录
    @Autowired
    private PromotionPlatformUsageLogDao promotionPlatformUsageLogDao;

    @Autowired
    private PromotionStoresDao promotionStoresDao;

    @Override
    public Map<Long, PromotionDTO> getSkuPromotionMap(List<Long> skuIds) {
        if (CollectionUtils.isEmpty(skuIds)) {
            return Collections.emptyMap();
        }

        Map<Long, List<PromotionDTO>> datas = getSkuPromotions(skuIds);
        Map<Long, PromotionDTO> resultMap = new HashMap<>();
        datas.entrySet().forEach(entry -> {
            Long skuId = entry.getKey();
            List<PromotionDTO> promotions = entry.getValue();
            if (CollectionUtils.isEmpty(promotions)) {
                return;
            }
            //将单品营销活动按照价格升序排序
            PromotionUtils.sortSinglePromotions(promotions, skuId);

            //取第一个，即价格最低的营销活动返回
            resultMap.put(skuId, promotions.get(0));
        });
        return resultMap;
    }

    @Override
    public Map<Long, List<PromotionDTO>> getSkuPromotions(List<Long> skuIds) {
        Map<Long, List<PromotionDTO>> map = new HashMap<>();
        //step 1 : 批量查询promotionGoods
        List<PromotionGoodsEntity> promotionGoodsEntities = batchQueryPromotionGoods(skuIds);
        if (CollectionUtils.isEmpty(promotionGoodsEntities)) {
            return Maps.newHashMap();
        }
        //step 2 : 批量查询promotion
        List<Long> promotionIds = promotionGoodsEntities.stream().map(promotion -> promotion.getPromotionId()).collect(Collectors.toList());
        // 去重
        promotionIds = promotionIds.stream().distinct().collect(Collectors.toList());
        Map<Long, PromotionEntity> promotionMap = batchQueryPromotions(promotionIds);

        //step 3 : 组装数据
        for (PromotionGoodsEntity promotionGoods : promotionGoodsEntities) {
            Long skuId = promotionGoods.getGoodsSkuId();
            // 根据skuId取出对应的活动集合
            List<PromotionDTO> promotionDTOs = map.get(skuId);
            // 将skuId与活动集合绑定
            if (promotionDTOs == null) {
                promotionDTOs = new ArrayList<>();
                map.put(skuId, promotionDTOs);
            }
            PromotionEntity promotionEntity = promotionMap.get(promotionGoods.getPromotionId());
            if (promotionEntity != null) {
                // 平台发布的秒杀活动
                if (promotionEntity.getGradeId().equals(PromotionGradeEnum.PROMOTION_GRADE_SINGLE.getCode()) && promotionEntity.getSponsorType() == null) {
                    if (promotionGoods.getState() == null) {
                        // 未参加审核
                        continue;
                    } else if (promotionGoods.getState() != PromotionConstants.AuditState.APPROVED_AUDIT) {
                        // 审核未通过
                        continue;
                    }
                }
                PromotionDTO promotionDTO = buildPromotionDTO(promotionEntity, promotionGoods);
                promotionDTOs.add(promotionDTO);
            }
        }
        return map;
    }


    @Override
    public Map<Long, List<PromotionDTO>> getStorePromotions(List<Long> storeIds) {
        if (CollectionUtils.isEmpty(storeIds)) {
            return new HashMap<>();
        }
        Map<Long, List<PromotionDTO>> map = new HashMap<>();
        Date now = new Date();
        EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        entityWrapper.eq("gradeId", PromotionGradeEnum.PROMOTION_GRADE_STORE.getCode());
        entityWrapper.in("sponsorType", storeIds);
        entityWrapper.andNew();
        entityWrapper.eq("marketState", PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
        entityWrapper.or();
        entityWrapper.eq("marketState", PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode());
        entityWrapper.gt("endTime", now);
        entityWrapper.lt("startTime", now);
        // 店铺下活动集合
        List<PromotionEntity> promotionEntities = promotionDao.selectList(entityWrapper);
        if (CollectionUtils.isNotEmpty(promotionEntities)) {
            List<PromotionEntity> promotionList = new ArrayList<>();
            for (PromotionEntity promotion : promotionEntities) {
                if (promotion.getTypeId().equals(PromotionTypeEnum.PROMOTION_TYPE_STORE_COUPON.getCode())) {
                    if (promotion.getUsedAmount() != null) {
                        if (promotion.getAmount() != null && promotion.getUsedAmount() >= promotion.getAmount()) {
                            continue;
                        }
                    }
                }
                promotionList.add(promotion);
            }
            List<PromotionDTO> promotionDTOS = buildPromotionDTOS(promotionList);
            // 查询活动所属商品
            getStorePromotionGoodsList(promotionDTOS);
            for (PromotionDTO promotionDTO : promotionDTOS) {
                Long storeId = promotionDTO.getSponsorType();
                List<PromotionDTO> promotionDTOList = map.get(storeId);
                if (null == promotionDTOList) {
                    promotionDTOList = new ArrayList<>();
                    map.put(storeId, promotionDTOList);
                }
                promotionDTOList.add(promotionDTO);
            }
        }

        return map;
    }

    @Override
    public List<PromotionDTO> getStoreCoupons(Long memberId, Long storeId) {
        List<PromotionDTO> promotionDTOS = new ArrayList<>();
        // 会员优惠券
        MemberCouponEntity memberCouponEntity = new MemberCouponEntity();
        memberCouponEntity.clearInit();
        //查询条件
        memberCouponEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
        memberCouponEntity.setMemberId(memberId);
        memberCouponEntity.setStoreId(storeId);
        memberCouponEntity.setUsageState(PromotionConstants.UsageState.USAGE_NO);
        List<MemberCouponEntity> memberCouponEntities = memberCouponDao.selectList(new EntityWrapper<>(memberCouponEntity));
        List<Long> promotionIds = memberCouponEntities.stream().map(promotion -> promotion.getCouponId()).collect(Collectors.toList());
        // 去重
        promotionIds = promotionIds.stream().distinct().collect(Collectors.toList());
        // 获取活动所选商品
        Map<Long, PromotionDTO> promotionByIds = getPromotionByIds(promotionIds);
        for (Long promotionId : promotionIds) {
            if (promotionByIds.get(promotionId) != null) {
                promotionDTOS.add(promotionByIds.get(promotionId));
            }
        }
        return promotionDTOS;
    }

    @Override
    public Map<Long, PromotionDTO> getPromotionByIds(List<Long> promotionIds) {
        Map<Long, PromotionDTO> map = new HashMap<>();
        // 当前时间
        Date now = new Date();
        // 针对店铺优惠券增加活动状态查询
        List<Integer> marketStates = new ArrayList<>();
        marketStates.add(PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
        marketStates.add(PromotionStateEnum.PROMOTION_STATE_TERMINATED.getCode());
        marketStates.add(PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode());

        // 子类型
        List<Integer> subTypeList = new ArrayList<>();
        subTypeList.add(PromotionConstants.SubType.ORDINARY_COUPONS);
        subTypeList.add(PromotionConstants.SubType.COUPON_SHARE);
        subTypeList.add(PromotionConstants.SubType.COUPON_LOTTERY);
        EntityWrapper<PromotionEntity> promotionWrapper = new EntityWrapper<>();
        // 查询条件
        promotionWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        promotionWrapper.in("id", promotionIds);
        promotionWrapper.gt("endTime", now);
        promotionWrapper.lt("startTime", now);
        promotionWrapper.andNew();
        promotionWrapper.in("marketState", marketStates);
        promotionWrapper.or();
        promotionWrapper.eq("marketState", PromotionPlatformCouponStateEnum.PROMOTION_STATE_START.getCode());
        promotionWrapper.eq("typeId", PromotionTypeEnum.PROMOTION_TYPE_COUPON.getCode());
        promotionWrapper.in("subType", subTypeList);
        if (CollectionUtils.isEmpty(promotionIds)) {
            return map;
        }
        List<PromotionEntity> promotionEntities = promotionDao.selectList(promotionWrapper);
        if (CollectionUtils.isEmpty(promotionEntities)) {
            return map;
        }
        for (PromotionEntity promotionEntity : promotionEntities) {
            PromotionDTO promotionDTO = new PromotionDTO();
            BeanCopyUtil.copy(promotionEntity, promotionDTO);
            promotionDTO.setType(PromotionTypeEnum.valueOf(promotionEntity.getTypeId()));
            promotionDTO.setGrade(PromotionGradeEnum.valueOf(promotionEntity.getGradeId()));
            map.put(promotionDTO.getId(), promotionDTO);
        }
        List<PromotionDTO> promotionDTOS = new ArrayList<>(map.values());
        loadStoreOrGoodsLimit(promotionDTOS);
        return map;
    }


    /**
     * 复制数据
     *
     * @param promotionEntity
     * @param promotionGoods
     * @return
     */
    private PromotionDTO buildPromotionDTO(PromotionEntity promotionEntity, PromotionGoodsEntity promotionGoods) {
        PromotionDTO promotionDTO = new PromotionDTO();
        BeanCopyUtil.copy(promotionEntity, promotionDTO);
        promotionDTO.setType(PromotionTypeEnum.valueOf(promotionEntity.getTypeId()));
        promotionDTO.setGrade(PromotionGradeEnum.valueOf(promotionEntity.getGradeId()));

        PromotionGoodsDTO promotionGoodsDTO = new PromotionGoodsDTO();
        BeanCopyUtil.copy(promotionGoods, promotionGoodsDTO);

        promotionDTO.getLimitGoods().add(promotionGoodsDTO);
        return promotionDTO;
    }

    @Override
    public List<PromotionDTO> getPlatformPromotions() {
        PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.clearInit();
        // 查询条件
        promotionEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
        promotionEntity.setMarketState(PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
        promotionEntity.setGradeId(PromotionGradeEnum.PROMOTION_GRADE_PLATFORM.getCode());
        // 可用的平台活动
        List<PromotionEntity> promotionEntities = promotionDao.selectList(new EntityWrapper<>(promotionEntity));
        List<PromotionDTO> promotionDTOS = buildPromotionDTOS(promotionEntities);
        loadStoreOrGoodsLimit(promotionDTOS);
        return promotionDTOS;
    }

    private List<PromotionDTO> buildPromotionDTOS(List<PromotionEntity> promotionEntities) {
        List<PromotionDTO> promotionDTOS = new ArrayList<>();
        for (PromotionEntity promotion : promotionEntities) {
            PromotionDTO promotionDTO = new PromotionDTO();
            BeanCopyUtil.copy(promotion, promotionDTO);
            promotionDTO.setType(PromotionTypeEnum.valueOf(promotion.getTypeId()));
            promotionDTO.setGrade(PromotionGradeEnum.valueOf(promotion.getGradeId()));
            promotionDTOS.add(promotionDTO);
        }
        return promotionDTOS;
    }

    @Override
    public List<PromotionDTO> getPlatformPromotions(Long memberId) {
        // 会员优惠券
        MemberCouponEntity memberCouponEntity = new MemberCouponEntity();
        memberCouponEntity.clearInit();
        //查询条件
        memberCouponEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
        memberCouponEntity.setMemberId(memberId);
        memberCouponEntity.setUsageState(PromotionConstants.UsageState.USAGE_NO);
        List<MemberCouponEntity> memberCouponEntities = memberCouponDao.selectList(new EntityWrapper<>(memberCouponEntity));
        if (CollectionUtils.isEmpty(memberCouponEntities)) {
            return Collections.emptyList();
        }

        List<Long> promotionIds = memberCouponEntities.stream().map(item -> item.getCouponId()).collect(Collectors.toList());
        // 当前时间
        Date now = new Date();
        // 针对店铺优惠券增加活动状态查询
        List<Integer> marketStates = new ArrayList<>();
        marketStates.add(PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
        marketStates.add(PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode());
        // 子类型
        List<Integer> subTypeList = new ArrayList<>();
        subTypeList.add(PromotionConstants.SubType.ORDINARY_COUPONS);
        subTypeList.add(PromotionConstants.SubType.COUPON_SHARE);
        subTypeList.add(PromotionConstants.SubType.COUPON_LOTTERY);
        // 查询条件
        EntityWrapper<PromotionEntity> cond = new EntityWrapper<PromotionEntity>();
        cond.in("id", promotionIds);
        cond.eq("gradeId", PromotionGradeEnum.PROMOTION_GRADE_PLATFORM.getCode());
        cond.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        cond.gt("endTime", now);
        cond.lt("startTime", now);
        cond.andNew();
        cond.in("marketState", marketStates);
        cond.or();
        cond.eq("marketState", PromotionPlatformCouponStateEnum.PROMOTION_STATE_START.getCode());
        cond.eq("typeId", PromotionTypeEnum.PROMOTION_TYPE_COUPON.getCode());
        cond.in("subType", subTypeList);
        List<PromotionEntity> promotions = promotionDao.selectList(cond);
        if (CollectionUtils.isEmpty(promotions)) {
            return Collections.emptyList();
        }

        List<PromotionDTO> promotionDTOS = new ArrayList<>();
        for (PromotionEntity promotion : promotions) {
            PromotionDTO promotionDTO = new PromotionDTO();
            BeanCopyUtil.copy(promotion, promotionDTO);
            promotionDTO.setType(PromotionTypeEnum.valueOf(promotion.getTypeId()));
            promotionDTO.setGrade(PromotionGradeEnum.valueOf(promotion.getGradeId()));
            promotionDTOS.add(promotionDTO);
        }
        loadStoreOrGoodsLimit(promotionDTOS);
        return promotionDTOS;
    }

    public void loadStoreOrGoodsLimit(List<PromotionDTO> promotionDTOS) {
        if (CollectionUtils.isEmpty(promotionDTOS)) {
            return;
        }
        //需要查询关联商品的平台优惠券
        List<PromotionDTO> platformGoodsPromotionDTOS = promotionDTOS.stream().filter(item -> {
            Integer goodsArea = item.getIsGoodsArea();
            if (PromotionGradeEnum.PROMOTION_GRADE_PLATFORM == item.getGrade()
                    && (PromotionConstants.IsGoodsArea.NOT_ALL.equals(goodsArea)
                    || PromotionConstants.IsGoodsArea.EXCLUDING_PART_OF_THE_GOODS.equals(goodsArea))) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        //加載圈定或排除商品信息
        if (CollectionUtils.isNotEmpty(platformGoodsPromotionDTOS)) {
            getPromotionGoodsList(platformGoodsPromotionDTOS, PromotionGradeEnum.PROMOTION_GRADE_PLATFORM);
        }

        //需要查询关联商品的店铺优惠券
        List<PromotionDTO> goodsPromotionDTOS = promotionDTOS.stream().filter(item -> {
            Integer goodsArea = item.getIsGoodsArea();
            if (PromotionGradeEnum.PROMOTION_GRADE_PLATFORM != item.getGrade()
                    && (PromotionConstants.IsGoodsArea.NOT_ALL.equals(goodsArea)
                    || PromotionConstants.IsGoodsArea.EXCLUDING_PART_OF_THE_GOODS.equals(goodsArea))) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        //加載圈定或排除商品信息
        if (CollectionUtils.isNotEmpty(goodsPromotionDTOS)) {
            getPromotionGoodsList(goodsPromotionDTOS, PromotionGradeEnum.PROMOTION_GRADE_STORE);
        }

        //需要查询关联店铺的优惠券
        List<PromotionDTO> storePromotionDTOS = promotionDTOS.stream().filter(item -> {
            Integer goodsArea = item.getIsGoodsArea();
            if (PromotionConstants.IsGoodsArea.INCLUDE_STORE.equals(goodsArea)
                    || PromotionConstants.IsGoodsArea.EXCLUDE_STORE.equals(goodsArea)) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        // 加载圈定或排除店铺信息
        if (CollectionUtils.isNotEmpty(storePromotionDTOS)) {
            getPromotionStoreList(storePromotionDTOS);
        }

    }

    private void getPromotionStoreList(List<PromotionDTO> promotionDTOS) {
        List<Long> promotionIds = promotionDTOS.stream().map(promotion -> promotion.getId()).collect(Collectors.toList());
        EntityWrapper<PromotionStoresEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        entityWrapper.in("promotionId", promotionIds);
        List<PromotionStoresEntity> promotionStoresEntities = promotionStoresDao.selectList(entityWrapper);
        if (CollectionUtils.isNotEmpty(promotionStoresEntities)) {
            for (PromotionStoresEntity promotionStores : promotionStoresEntities) {
                for (PromotionDTO promotion : promotionDTOS) {
                    if (promotionStores.getPromotionId().equals(promotion.getId())) {
                        PromotionStoreDTO promotionStoreDTO = new PromotionStoreDTO();
                        BeanCopyUtil.copy(promotionStores, promotionStoreDTO);
                        promotion.getStoreDTOS().add(promotionStoreDTO);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public Boolean usePromotions(Long memberId, PromotionConsumeDTO promotionConsumeDTO) {
        // 使用结果
        boolean results = true;
        if (promotionConsumeDTO == null) {
            return results;
        }
        // 是否使用平台活动
        if (promotionConsumeDTO.getPlatformPromotionId() != null) {
            // 平台活动使用记录
            PromotionPlatformUsageLogEntity promotionPlatformUsageLogEntity = new PromotionPlatformUsageLogEntity();
            promotionPlatformUsageLogEntity.setOrderId(promotionConsumeDTO.getPayId());
            promotionPlatformUsageLogEntity.setPromotionId(promotionConsumeDTO.getPlatformPromotionId());
            promotionPlatformUsageLogEntity.setMemberId(memberId);
            promotionPlatformUsageLogEntity.setCreatorId(memberId);
            promotionPlatformUsageLogEntity.setCreatedTime(new Date());
            promotionPlatformUsageLogDao.insert(promotionPlatformUsageLogEntity);
            if (promotionConsumeDTO.getPlatformPromotionType() == PromotionTypeEnum.PROMOTION_TYPE_COUPON.getCode()) {
                // 优惠券使用
                MemberCouponEntity memberCouponEntity = new MemberCouponEntity();
                memberCouponEntity.clearInit();
                // 查询条件
                memberCouponEntity.setMemberId(memberId);
                memberCouponEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
                memberCouponEntity.setUsageState(PromotionConstants.UsageState.USAGE_NO);
                memberCouponEntity.setCouponId(promotionConsumeDTO.getPlatformPromotionId());
                // 查询该会员拥有的该店铺下的指定优惠券
                List<MemberCouponEntity> memberCouponEntities = memberCouponDao.selectList(new EntityWrapper<>(memberCouponEntity));
                if (CollectionUtils.isEmpty(memberCouponEntities)) {
                    // 没有可用优惠券
                    throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
                }
                // 改变优惠券使用状态
                MemberCouponEntity couponEntity = memberCouponEntities.get(0);
                couponEntity.setUsageState(PromotionConstants.UsageState.USAGE_YES);
                couponEntity.setOrderId(promotionConsumeDTO.getPayId());
                memberCouponDao.updateById(couponEntity);
            }
        }
        // 订单所用店铺级营销活动
        List<StorePromotionConsumeDTO> storePromotions = promotionConsumeDTO.getStorePromotions();
        // 消费/回退标识，0-退回，1-消费
        Integer usage = PromotionConstants.UsageState.USAGE_YES;
        // 是否使用店铺活动
        if (!CollectionUtils.isEmpty(storePromotions)) {
            // 店铺活动使用记录
            storePromotionUsage(memberId, storePromotions, usage);
        }
        if (!CollectionUtils.isEmpty(promotionConsumeDTO.getSinglePromotions())) {
            // 单品活动使用记录
            singlePromotionUsage(promotionConsumeDTO.getSinglePromotions(), usage);
        }

        return results;
    }

    @Override
    @Transactional
    public Boolean backPromotions(Long memberId, PromotionConsumeDTO promotionConsumeDTO) {
        // 退回结果
        boolean back = true;
        if (promotionConsumeDTO == null) {
            return back;
        }
        // 是否使用平台活动
        if (promotionConsumeDTO.getPlatformPromotionId() != null) {
            // 平台活动使用记录
            PromotionPlatformUsageLogEntity promotionPlatformUsageLogEntity = new PromotionPlatformUsageLogEntity();
            promotionPlatformUsageLogEntity.clearInit();
            promotionPlatformUsageLogEntity.setOrderId(promotionConsumeDTO.getPayId());
            promotionPlatformUsageLogEntity.setPromotionId(promotionConsumeDTO.getPlatformPromotionId());
            promotionPlatformUsageLogEntity.setMemberId(memberId);
            PromotionPlatformUsageLogEntity promotionPlatformUsage = promotionPlatformUsageLogDao.selectOne(promotionPlatformUsageLogEntity);
            promotionPlatformUsage.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
            promotionPlatformUsageLogDao.updateById(promotionPlatformUsage);
            if (promotionConsumeDTO.getPlatformPromotionType() == PromotionTypeEnum.PROMOTION_TYPE_COUPON.getCode()) {
                // 优惠券使用
                MemberCouponEntity memberCouponEntity = new MemberCouponEntity();
                memberCouponEntity.clearInit();
                // 查询条件
                memberCouponEntity.setMemberId(memberId);
                memberCouponEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
                memberCouponEntity.setUsageState(PromotionConstants.UsageState.USAGE_YES);
                memberCouponEntity.setCouponId(promotionConsumeDTO.getPlatformPromotionId());
                memberCouponEntity.setOrderId(promotionConsumeDTO.getPayId());
                // 该会员在该订单下使用的该优惠券
                MemberCouponEntity memberCoupon = memberCouponDao.selectOne(memberCouponEntity);
                if (memberCoupon != null) {
                    memberCoupon.setUsageState(PromotionConstants.UsageState.USAGE_NO);
                    memberCoupon.setOrderId(promotionConsumeDTO.getPayId());
                    memberCouponDao.updateById(memberCoupon);
                }
            }
        }
        // 订单所用店铺级营销活动
        List<StorePromotionConsumeDTO> storePromotions = promotionConsumeDTO.getStorePromotions();
        // 消费/回退标识，0-退回，1-消费
        Integer usage = PromotionConstants.UsageState.USAGE_NO;
        // 是否使用店铺活动
        if (!CollectionUtils.isEmpty(storePromotions)) {
            // 店铺活动使用记录
            storePromotionUsage(memberId, storePromotions, usage);
        }
        if (!CollectionUtils.isEmpty(promotionConsumeDTO.getSinglePromotions())) {
            // 单品活动使用记录
            singlePromotionUsage(promotionConsumeDTO.getSinglePromotions(), usage);
        }
        return back;
    }

    @Override
    public Integer getCouponNum(Long memberId, Long storeId) {
        MemberCouponDto memberCouponDto = new MemberCouponDto();
        memberCouponDto.setMemberId(memberId);
        memberCouponDto.setStoreId(storeId);
        Integer couponNum = memberCouponDao.selectCouponNum(memberCouponDto);
        return couponNum;
    }

    @Override
    public PromotionDTO getSeckill(Long goodsSkuId) {
        List<Long> goodsSkuIds = new ArrayList<>();
        goodsSkuIds.add(goodsSkuId);
        // 调用方法查询该skuId的单品活动
        Map<Long, List<PromotionDTO>> skuPromotions = getSkuPromotions(goodsSkuIds);
        List<PromotionDTO> promotionDTOS = skuPromotions.get(goodsSkuId);
        if (CollectionUtils.isEmpty(promotionDTOS)) {
            return null;
        }
        // 返参集合
        List<PromotionDTO> promotions = new ArrayList<>();
        // 将秒杀数据挑选出来
        for (PromotionDTO promotionDTO : promotionDTOS) {
            if (promotionDTO.getType() == PromotionTypeEnum.PROMOTION_TYPE_SECKILL) {
                promotions.add(promotionDTO);
            }
        }
        if (CollectionUtils.isEmpty(promotions)) {
            return null;
        }
        return promotions.get(0);
    }

    @Override
    public List<PromotionDTO> getSinglePromotions(Long goodsSkuId) {
        List<Long> goodsSkuIds = Lists.newArrayList(goodsSkuId);
        // 调用方法查询该skuId的单品活动
        Map<Long, List<PromotionDTO>> skuPromotions = getSkuPromotions(goodsSkuIds);
        if (MapUtils.isEmpty(skuPromotions)) {
            return Lists.newArrayList();
        }
        List<PromotionDTO> promotionDTOS = skuPromotions.get(goodsSkuId);
        if (CollectionUtils.isEmpty(promotionDTOS)) {
            return Lists.newArrayList();
        }
        List<PromotionDTO> delList = new ArrayList<>();
        for (PromotionDTO promotionDTO : promotionDTOS) {
            if (promotionDTO.getType().equals(PromotionTypeEnum.PROMOTION_TYPE_SECKILL)) {
                delList.add(promotionDTO);
            }
        }
        promotionDTOS.removeAll(delList);
        return promotionDTOS;
    }

    @Override
    public Boolean checkHoldStatus(Long memberId, List<Long> couponPromIds) {
        if (CollectionUtils.isEmpty(couponPromIds)) {
            return false;
        }
        if (memberId == null) {
            return false;
        }

        List<Long> promotionIds = couponPromIds.stream().distinct().collect(Collectors.toList());
        List<Long> memberCouponIds = memberCouponDao.countAvailByMemberIdAndCouponId(memberId, promotionIds);
        if (CollectionUtils.isEmpty(memberCouponIds)) {
            return false;
        }

        return couponPromIds.size() == memberCouponIds.size();
    }

    /**
     * 商品下架
     *
     * @param itemIds
     */
    @Override
    @Transactional
    public void goodsSuspendSales(Long[] itemIds) {
        List<Long> itemIdList = Arrays.asList(itemIds);
        EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("itemId", itemIdList);
        if (CollectionUtils.isNotEmpty(itemIdList)) {
            promotionGoodsDao.delete(entityWrapper);
        }
    }

    /**
     * 根据goodsSkuId查询店铺和平台优惠券
     *
     * @param itemId
     * @return
     */
    @Override
    public List<PromotionDTO> getPlatFormCouponBySku(Long itemId, Long storeId) throws ParseException {
        // 返参
        List<PromotionDTO> promotionDTOS = new ArrayList<>();
        List<PromotionEntity> promotionEntitys = new ArrayList<>();

        // 店铺可用优惠券
        List<PromotionEntity> promotionStoreEntityList = goodsDetailsStoreCoupon(itemId, storeId);
        promotionEntitys.addAll(promotionStoreEntityList);

        // 平台可用优惠券
        List<PromotionEntity> ploatformCouponEntities = goodsDetailsPloatformCoupon(itemId, storeId);
        promotionEntitys.addAll(ploatformCouponEntities);

        for (PromotionEntity promotionEntity : promotionEntitys) {
            if (promotionEntity.getUsedAmount() != null) {
                if (promotionEntity.getAmount() != null && promotionEntity.getUsedAmount() >= promotionEntity.getAmount()) {
                    continue;
                }
            }
            PromotionDTO promotionDTO = new PromotionDTO();
            BeanCopyUtil.copy(promotionEntity, promotionDTO);
            promotionDTO.setType(PromotionTypeEnum.valueOf(promotionEntity.getTypeId()));
            promotionDTO.setGrade(PromotionGradeEnum.valueOf(promotionEntity.getGradeId()));
            promotionDTOS.add(promotionDTO);
        }
        return promotionDTOS;
    }

    @Override
    public List<PromotionDTO> getStorePromotionList(Long storeId, Integer type) {
        PromotionEntity promotionEntity = new PromotionEntity();
        promotionEntity.clearInit();
        // 查询条件
        promotionEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
        promotionEntity.setMarketState(PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
        promotionEntity.setSponsorType(storeId);
        promotionEntity.setTypeId(type);
        List<PromotionEntity> promotionEntities = promotionDao.selectList(new EntityWrapper<>(promotionEntity));
        if (CollectionUtils.isEmpty(promotionEntities)) {
            return new ArrayList<>();
        }
        List<PromotionDTO> promotionDTOS = buildPromotionDTOS(promotionEntities);
        getStorePromotionGoodsList(promotionDTOS);
        return promotionDTOS;
    }


    /***************************************************公共方法***********************************************/

    /**
     * 商品详情页查询店铺优惠券-（getPlatFormCouponBySku）
     */
    private List<PromotionEntity> goodsDetailsStoreCoupon(Long itemId, Long storeId) throws ParseException {
        // 可用活动
        List<PromotionEntity> usableList = new ArrayList<>();
        List<Integer> isGoodsAreaLisr = new ArrayList<>();
        isGoodsAreaLisr.add(PromotionConstants.IsGoodsArea.NOT_ALL);
        isGoodsAreaLisr.add(PromotionConstants.IsGoodsArea.ALL);
        isGoodsAreaLisr.add(PromotionConstants.IsGoodsArea.EXCLUDING_PART_OF_THE_GOODS);

        // 针对店铺优惠券增加活动状态查询
        List<Integer> marketStates = new ArrayList<>();
        marketStates.add(PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
        marketStates.add(PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode());

        //查询条件
        EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
        // 查询条件
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        entityWrapper.eq("gradeId", PromotionGradeEnum.PROMOTION_GRADE_STORE.getCode());
        entityWrapper.eq("typeId", PromotionTypeEnum.PROMOTION_TYPE_STORE_COUPON.getCode());
        entityWrapper.eq("sponsorType", storeId);
        entityWrapper.in("marketState", marketStates);
        List<PromotionEntity> promotionEntityList = promotionDao.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(promotionEntityList)) {
            return new ArrayList<>();
        }
        // 将指定商品可用/不可用的活动筛选出来
        List<PromotionEntity> filtrateList = new ArrayList<>();
        for (PromotionEntity promotion : promotionEntityList) {
            if (promotion.getIsGoodsArea().equals(PromotionConstants.IsGoodsArea.NOT_ALL) || promotion.getIsGoodsArea().equals(PromotionConstants.IsGoodsArea.EXCLUDING_PART_OF_THE_GOODS)) {
                filtrateList.add(promotion);
            } else {
                usableList.add(promotion);
            }
        }
        if (CollectionUtils.isNotEmpty(filtrateList)) {
            // 批量查询promotion
            List<Long> ids = filtrateList.stream().map(promotion -> promotion.getId()).collect(Collectors.toList());
            // 活动下所选商品查询条件
            EntityWrapper<PromotionGoodsEntity> promotionGoodsEntityWrqpper = new EntityWrapper<>();
            promotionGoodsEntityWrqpper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
            promotionGoodsEntityWrqpper.in("promotionId", ids);
            List<PromotionGoodsEntity> promotionGoodsEntityList = promotionGoodsDao.selectList(promotionGoodsEntityWrqpper);
            // key:活动id，Value:所属商品集合
            Map<Long, List<PromotionGoodsEntity>> map = new HashMap<>();
            // 组装数据
            for (PromotionEntity promotionEntity : filtrateList) {
                List<PromotionGoodsEntity> promotionGoodsList = map.get(promotionEntity.getId());
                if (CollectionUtils.isEmpty(promotionGoodsList)) {
                    promotionGoodsList = new ArrayList<>();
                    map.put(promotionEntity.getId(), promotionGoodsList);
                }
                for (PromotionGoodsEntity promotionGoods : promotionGoodsEntityList) {
                    if (promotionEntity.getId().equals(promotionGoods.getPromotionId())) {
                        promotionGoodsList.add(promotionGoods);
                    }
                }
                List<PromotionGoodsEntity> promotionGoodsEntities = map.get(promotionEntity.getId());
                if (CollectionUtils.isNotEmpty(promotionGoodsEntities)) {
                    if (promotionEntity.getIsGoodsArea().equals(PromotionConstants.IsGoodsArea.NOT_ALL)) {
                        for (PromotionGoodsEntity promotionGoods : promotionGoodsEntities) {
                            if (itemId.equals(promotionGoods.getItemId())) {
                                usableList.add(promotionEntity);
                                break;
                            }
                        }
                    } else if (promotionEntity.getIsGoodsArea().equals(PromotionConstants.IsGoodsArea.EXCLUDING_PART_OF_THE_GOODS)) {
                        boolean exist = true;
                        for (PromotionGoodsEntity promotionGoods : promotionGoodsEntities) {
                            if (itemId.equals(promotionGoods.getItemId())) {
                                exist = false;
                                break;
                            }
                        }
                        if (exist) {
                            usableList.add(promotionEntity);
                        }
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(usableList)) {
            // 删除时间在有效期之外的活动
            List<PromotionEntity> delList = new ArrayList<>();
            for (PromotionEntity promotion : usableList) {
                ActiveConfigDto activeConfigDto = JSONObject.parseObject(promotion.getActiveConfig(), ActiveConfigDto.class);
                if (null != activeConfigDto) {
                    if (!(StringUtils.isBlank(activeConfigDto.getReleaseStartTime()) && StringUtils.isBlank(activeConfigDto.getReleaseEndTime()))) {
                        Date now = new Date();
                        Date startTime = PromotionUtils.pareTime(activeConfigDto.getReleaseStartTime());
                        Date endTime = PromotionUtils.pareTime(activeConfigDto.getReleaseEndTime());
                        if (!(startTime.getTime() < now.getTime() && now.getTime() < endTime.getTime())) {
                            delList.add(promotion);
                        }
                    }
                }
            }
            usableList.removeAll(delList);
        }
        return usableList;
    }

    /**
     * 商品详情页查询平台优惠券-（getPlatFormCouponBySku）
     */
    private List<PromotionEntity> goodsDetailsPloatformCoupon(Long itemId, Long storeId) {
        Date now = new Date();
        // 可用活动
        List<PromotionEntity> usableList = new ArrayList<>();
        // 查询条件,旧版平台优惠券
        EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        entityWrapper.eq("gradeId", PromotionGradeEnum.PROMOTION_GRADE_PLATFORM.getCode());
        entityWrapper.eq("marketState", PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
        entityWrapper.eq("typeId", PromotionTypeEnum.PROMOTION_TYPE_COUPON.getCode());
        entityWrapper.eq("isGoodsArea", PromotionConstants.IsGoodsArea.ALL);
        entityWrapper.gt("endTime", now);
        entityWrapper.lt("startTime", now);
        entityWrapper.isNull("subType");
        // 旧版全平台可用优惠券
        List<PromotionEntity> promotionEntities = promotionDao.selectList(entityWrapper);
        usableList.addAll(promotionEntities);
        EntityWrapper<PromotionGoodsEntity> promotionGoodsEntityWrapper = new EntityWrapper<>();
        promotionGoodsEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        promotionGoodsEntityWrapper.eq("itemId", itemId);
        promotionGoodsEntityWrapper.eq("state", PromotionConstants.AuditState.APPROVED_AUDIT);
        List<PromotionGoodsEntity> promotionGoodsEntityList = promotionGoodsDao.selectList(promotionGoodsEntityWrapper);
        // 批量查询promotion
        List<Long> promotionIds = promotionGoodsEntityList.stream().map(promotion -> promotion.getPromotionId()).collect(Collectors.toList());
        // 去重
        promotionIds = promotionIds.stream().distinct().collect(Collectors.toList());
        // 指定该商品可用的旧版平台优惠券
        EntityWrapper<PromotionEntity> promotionWrapper = new EntityWrapper<>();
        promotionWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        promotionWrapper.eq("gradeId", PromotionGradeEnum.PROMOTION_GRADE_PLATFORM.getCode());
        promotionWrapper.eq("marketState", PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
        promotionWrapper.eq("typeId", PromotionTypeEnum.PROMOTION_TYPE_COUPON.getCode());
        promotionWrapper.gt("endTime", now);
        promotionWrapper.lt("startTime", now);
        promotionWrapper.isNull("subType");
        promotionWrapper.eq("isGoodsArea", PromotionConstants.IsGoodsArea.NOT_ALL);
        promotionWrapper.in("id", promotionIds);
        List<PromotionEntity> promotionEntityList = promotionDao.selectList(promotionWrapper);
        usableList.addAll(promotionEntityList);

        List<Integer> isGoodsAreaList = new ArrayList<>();
        isGoodsAreaList.add(PromotionConstants.IsGoodsArea.ALL_STORE);
        isGoodsAreaList.add(PromotionConstants.IsGoodsArea.INCLUDE_STORE);
        isGoodsAreaList.add(PromotionConstants.IsGoodsArea.EXCLUDE_STORE);

        // 查询条件，指定店铺与全平台可用新版优惠券
        EntityWrapper<PromotionEntity> ploatformCouponEntityWrapper = new EntityWrapper<>();
        ploatformCouponEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        ploatformCouponEntityWrapper.eq("gradeId", PromotionGradeEnum.PROMOTION_GRADE_PLATFORM.getCode());
        ploatformCouponEntityWrapper.eq("marketState", PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
        ploatformCouponEntityWrapper.eq("typeId", PromotionTypeEnum.PROMOTION_TYPE_COUPON.getCode());
        ploatformCouponEntityWrapper.gt("endTime", now);
        ploatformCouponEntityWrapper.lt("startTime", now);
        ploatformCouponEntityWrapper.isNotNull("subType");
        ploatformCouponEntityWrapper.in("isGoodsArea", isGoodsAreaList);
        List<PromotionEntity> entityList = promotionDao.selectList(ploatformCouponEntityWrapper);
        if (CollectionUtils.isEmpty(entityList)) {
            return usableList;
        }
        // 将指定店铺可用/不可用的活动筛选出来
        List<PromotionEntity> filtrateList = new ArrayList<>();
        for (PromotionEntity promotion : entityList) {
            if (promotion.getIsGoodsArea().equals(PromotionConstants.IsGoodsArea.ALL_STORE)) {
                usableList.add(promotion);
            } else {
                filtrateList.add(promotion);
            }
        }
        if (CollectionUtils.isNotEmpty(filtrateList)) {
            // 批量查询promotion
            List<Long> ids = filtrateList.stream().map(promotion -> promotion.getId()).collect(Collectors.toList());
            EntityWrapper<PromotionStoresEntity> promotionStoreEntityWrapper = new EntityWrapper<>();
            promotionStoreEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
            promotionStoreEntityWrapper.in("promotionId", ids);
            List<PromotionStoresEntity> promotionStoresEntities = promotionStoresDao.selectList(promotionStoreEntityWrapper);
            // key:活动id，Value:所属商品集合
            Map<Long, List<PromotionStoresEntity>> map = new HashMap<>();
            // 组装数据
            for (PromotionEntity promotionEntity : filtrateList) {
                List<PromotionStoresEntity> promotionStoresList = map.get(promotionEntity.getId());
                if (CollectionUtils.isEmpty(promotionStoresList)) {
                    promotionStoresList = new ArrayList<>();
                    map.put(promotionEntity.getId(), promotionStoresList);
                }
                for (PromotionStoresEntity promotionStores : promotionStoresEntities) {
                    if (promotionEntity.getId().equals(promotionStores.getPromotionId())) {
                        promotionStoresList.add(promotionStores);
                    }
                }
                List<PromotionStoresEntity> promotionStoresEntityList = map.get(promotionEntity.getId());
                if (CollectionUtils.isNotEmpty(promotionStoresEntityList)) {
                    if (promotionEntity.getIsGoodsArea().equals(PromotionConstants.IsGoodsArea.INCLUDE_STORE)) {
                        for (PromotionStoresEntity promotionStoresEntity : promotionStoresEntityList) {
                            if (storeId.equals(promotionStoresEntity.getStoreId())) {
                                usableList.add(promotionEntity);
                                break;
                            }
                        }
                    } else if (promotionEntity.getIsGoodsArea().equals(PromotionConstants.IsGoodsArea.EXCLUDE_STORE)) {
                        boolean exist = true;
                        for (PromotionStoresEntity promotionStoresEntity : promotionStoresEntityList) {
                            if (storeId.equals(promotionStoresEntity.getStoreId())) {
                                exist = false;
                                break;
                            }
                        }
                        if (exist) {
                            usableList.add(promotionEntity);
                        }
                    }
                }
            }
        }
        return usableList;
    }

    /**
     * 查询sku商品所属活动
     *
     * @param skuIds
     * @return
     */
    private List<PromotionGoodsEntity> batchQueryPromotionGoods(List<Long> skuIds) {
        EntityWrapper<PromotionGoodsEntity> promotionGoodsWrapper = new EntityWrapper<>();
        // 查询条件
        promotionGoodsWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        promotionGoodsWrapper.in("goodsSkuId", skuIds);
        if (CollectionUtils.isEmpty(skuIds)) {
            return new ArrayList<>();
        }
        // 活动商品集合
        return promotionGoodsDao.selectList(promotionGoodsWrapper);
    }

    /**
     * 批量查询营销活动转map
     *
     * @param promotionIds
     * @return
     */
    private Map<Long, PromotionEntity> batchQueryPromotions(List<Long> promotionIds) {

        EntityWrapper<PromotionEntity> promotionWrapper = new EntityWrapper<>();

        // 查询条件
        Date now = new Date();
        promotionWrapper.in("id", promotionIds);
        if (CollectionUtils.isEmpty(promotionIds)) {
            return Maps.newHashMap();
        }
        promotionWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        promotionWrapper.eq("gradeId", PromotionGradeEnum.PROMOTION_GRADE_SINGLE.getCode());
        promotionWrapper.andNew();
        promotionWrapper.eq("marketState", PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
        promotionWrapper.gt("endTime", now);
        promotionWrapper.lt("startTime", now);
        promotionWrapper.or();
        promotionWrapper.eq("marketState", PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
        promotionWrapper.gt("endTime", now);
        promotionWrapper.lt("startTime", now);
        // 商品所属营销活动
        List<PromotionEntity> promotions = promotionDao.selectList(promotionWrapper);

        if (CollectionUtils.isEmpty(promotions)) {
            return Maps.newHashMap();
        }
        return promotions.stream().collect(Collectors.toMap(PromotionEntity::getId, item -> item));
    }

    /**
     * 查询活动所属商品集合
     *
     * @return
     */
    public PromotionDTO getPromotionGoods(PromotionDTO promotionDTO) {
        // 查询活动所属商品集合
        PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
        List<PromotionGoodsDTO> promotionGoodsDTOS = new ArrayList<>();
        // 清空默认条件
        promotionGoodsEntity.clearInit();
        // 查询条件
        promotionGoodsEntity.setState(PromotionConstants.AuditState.APPROVED_AUDIT);
        promotionGoodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
        promotionGoodsEntity.setPromotionId(promotionDTO.getId());
        List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(new EntityWrapper<>(promotionGoodsEntity));
        for (PromotionGoodsEntity promotionGoods : promotionGoodsEntities) {
            PromotionGoodsDTO promotionGoodsDTO = new PromotionGoodsDTO();
            BeanCopyUtil.copy(promotionGoods, promotionGoodsDTO);
            promotionGoodsDTOS.add(promotionGoodsDTO);
        }
        promotionDTO.setLimitGoods(promotionGoodsDTOS);
        return promotionDTO;
    }

    /**
     * 查询活动集合所属商品
     *
     * @param promotionDTOS
     * @return
     */
    public List<PromotionDTO> getPromotionGoodsList(List<PromotionDTO> promotionDTOS, PromotionGradeEnum gradeEnum) {
        if (CollectionUtils.isEmpty(promotionDTOS)) {
            return new ArrayList<>();
        }
        List<Long> promotionIds = promotionDTOS.stream().map(promotion -> promotion.getId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(promotionIds)) {
            return new ArrayList<>();
        }
        EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        //平台活动商品需要审核
        if (PromotionGradeEnum.PROMOTION_GRADE_PLATFORM == gradeEnum) {
            entityWrapper.eq("state", PromotionConstants.AuditState.APPROVED_AUDIT);
        }
        entityWrapper.in("promotionId", promotionIds);
        List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(entityWrapper);
        // 组装数据
        for (PromotionGoodsEntity promotionGoods : promotionGoodsEntities) {
            Long promotionId = promotionGoods.getPromotionId();
            for (PromotionDTO promotionDTO : promotionDTOS) {
                if (promotionId.equals(promotionDTO.getId())) {
                    PromotionGoodsDTO promotionGoodsDTO = new PromotionGoodsDTO();
                    BeanCopyUtil.copy(promotionGoods, promotionGoodsDTO);
                    promotionDTO.getLimitGoods().add(promotionGoodsDTO);
                }
            }
        }
        return promotionDTOS;
    }

    public List<PromotionDTO> getPloatformPromotionGoodsList(List<PromotionDTO> promotionDTOS) {
        return getPromotionGoodsList(promotionDTOS, PromotionGradeEnum.PROMOTION_GRADE_PLATFORM);
    }

    public List<PromotionDTO> getStorePromotionGoodsList(List<PromotionDTO> promotionDTOS) {
        return getPromotionGoodsList(promotionDTOS, PromotionGradeEnum.PROMOTION_GRADE_STORE);
    }


    /**
     * 店铺活动使用记录
     *
     * @param storePromotions
     */
    private void storePromotionUsage(Long memberId, List<StorePromotionConsumeDTO> storePromotions, Integer usageState) {
        for (StorePromotionConsumeDTO storePromotion : storePromotions) {

            if (storePromotion.getPromotionId() != null) {
                PromotionStoreUsageLogEntity promotionStoreUsageLogEntity = new PromotionStoreUsageLogEntity();
                if (usageState == PromotionConstants.UsageState.USAGE_YES) {
                    /**
                     * 活动使用
                     */
                    // 店铺活动使用记录
                    promotionStoreUsageLogEntity.setOrderId(storePromotion.getOrderId());
                    promotionStoreUsageLogEntity.setPromotionId(storePromotion.getPromotionId());
                    promotionStoreUsageLogEntity.setMemberId(memberId);
                    promotionStoreUsageLogEntity.setStoreId(storePromotion.getStoreId());
                    promotionStoreUsageLogEntity.setCreatorId(memberId);
                    promotionStoreUsageLogEntity.setCreatedTime(new Date());
                    promotionStoreUsageLogDao.insert(promotionStoreUsageLogEntity);
                    if (storePromotion.getType() == PromotionTypeEnum.PROMOTION_TYPE_STORE_COUPON.getCode()) {
                        // 优惠券使用
                        MemberCouponEntity memberCouponEntity = new MemberCouponEntity();
                        memberCouponEntity.clearInit();
                        // 查询条件
                        memberCouponEntity.setMemberId(memberId);
                        memberCouponEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
                        memberCouponEntity.setUsageState(PromotionConstants.UsageState.USAGE_NO);
                        memberCouponEntity.setStoreId(storePromotion.getStoreId());
                        memberCouponEntity.setCouponId(storePromotion.getPromotionId());
                        // 查询该会员拥有的该店铺下的指定优惠券
                        List<MemberCouponEntity> memberCouponEntities = memberCouponDao.selectList(new EntityWrapper<>(memberCouponEntity));
                        if (CollectionUtils.isEmpty(memberCouponEntities)) {
                            // 没有可用优惠券
                            throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
                        }
                        // 改变优惠券使用状态
                        MemberCouponEntity couponEntity = memberCouponEntities.get(0);
                        couponEntity.setUsageState(usageState);
                        couponEntity.setOrderId(storePromotion.getOrderId());
                        memberCouponDao.updateById(couponEntity);
                    }
                } else if (usageState == PromotionConstants.UsageState.USAGE_NO) {
                    /**
                     * 活动回退
                     */
                    // 店铺活动使用记录
                    promotionStoreUsageLogEntity.clearInit();
                    promotionStoreUsageLogEntity.setOrderId(storePromotion.getOrderId());
                    promotionStoreUsageLogEntity.setPromotionId(storePromotion.getPromotionId());
                    promotionStoreUsageLogEntity.setMemberId(memberId);
                    promotionStoreUsageLogEntity.setStoreId(storePromotion.getStoreId());
                    PromotionStoreUsageLogEntity storeUsageLogEntity = promotionStoreUsageLogDao.selectOne(promotionStoreUsageLogEntity);
                    storeUsageLogEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
                    promotionStoreUsageLogDao.updateById(storeUsageLogEntity);
                    if (storePromotion.getType() == PromotionTypeEnum.PROMOTION_TYPE_STORE_COUPON.getCode()) {
                        // 优惠券使用
                        MemberCouponEntity memberCouponEntity = new MemberCouponEntity();
                        memberCouponEntity.clearInit();
                        // 查询条件
                        memberCouponEntity.setMemberId(memberId);
                        memberCouponEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
                        memberCouponEntity.setUsageState(PromotionConstants.UsageState.USAGE_YES);
                        memberCouponEntity.setStoreId(storePromotion.getStoreId());
                        memberCouponEntity.setCouponId(storePromotion.getPromotionId());
                        memberCouponEntity.setOrderId(storePromotion.getOrderId());
                        // 查询该会员拥有的该店铺下的指定优惠券
                        MemberCouponEntity memberCoupon = memberCouponDao.selectOne(memberCouponEntity);
                        if (memberCoupon != null) {
                            // 改变优惠券使用状态
                            memberCoupon.setUsageState(usageState);
                            memberCoupon.setOrderId(storePromotion.getOrderId());
                            memberCouponDao.updateById(memberCoupon);
                        }

                    }
                }
            } else if (storePromotion.getFreightPromotionId() != null) {
                PromotionStoreUsageLogEntity promotionStoreUsageLogEntity = new PromotionStoreUsageLogEntity();
                if (usageState == PromotionConstants.UsageState.USAGE_YES) {
                    // 包邮活动使用记录
                    promotionStoreUsageLogEntity.setOrderId(storePromotion.getOrderId());
                    promotionStoreUsageLogEntity.setPromotionId(storePromotion.getFreightPromotionId());
                    promotionStoreUsageLogEntity.setMemberId(memberId);
                    promotionStoreUsageLogEntity.setStoreId(storePromotion.getStoreId());
                    promotionStoreUsageLogEntity.setCreatorId(memberId);
                    promotionStoreUsageLogEntity.setCreatedTime(new Date());
                    promotionStoreUsageLogDao.insert(promotionStoreUsageLogEntity);
                } else if (usageState == PromotionConstants.UsageState.USAGE_NO) {
                    // 包邮活动使用记录回退
                    promotionStoreUsageLogEntity.clearInit();
                    // 查询条件
                    promotionStoreUsageLogEntity.setOrderId(storePromotion.getOrderId());
                    promotionStoreUsageLogEntity.setPromotionId(storePromotion.getFreightPromotionId());
                    promotionStoreUsageLogEntity.setMemberId(memberId);
                    promotionStoreUsageLogEntity.setStoreId(storePromotion.getStoreId());
                    PromotionStoreUsageLogEntity storeUsageLogEntity = promotionStoreUsageLogDao.selectOne(promotionStoreUsageLogEntity);
                    storeUsageLogEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
                    promotionStoreUsageLogDao.updateById(promotionStoreUsageLogEntity);
                }
            }
        }
    }

    /**
     * 单品活动使用
     *
     * @param singlePromotionConsumes
     */
    private void singlePromotionUsage(List<SinglePromotionConsumeDTO> singlePromotionConsumes, Integer usage) {
        for (SinglePromotionConsumeDTO singlePromotion : singlePromotionConsumes) {
            EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
            // 查询条件
            entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
            entityWrapper.eq("promotionId", singlePromotion.getPromotionId());
            entityWrapper.eq("goodsSkuId", singlePromotion.getGoodsSkuId());
            entityWrapper.orderBy("id", true);
            List<PromotionGoodsEntity> promotionGoodsEntityList = promotionGoodsDao.selectList(entityWrapper);
            if (CollectionUtils.isEmpty(promotionGoodsEntityList)) {
                continue;
            }
            PromotionGoodsEntity promotionGoods = promotionGoodsEntityList.get(0);
            if (usage == PromotionConstants.UsageState.USAGE_YES) {
                // 商品销量
                if (promotionGoods.getQuantitySales() != null) {
                    Integer quantitySales = promotionGoods.getQuantitySales() + singlePromotion.getGoodsNum();
                    promotionGoods.setQuantitySales(quantitySales);
                } else {
                    promotionGoods.setQuantitySales(singlePromotion.getGoodsNum());
                }
            } else if (usage == PromotionConstants.UsageState.USAGE_NO) {
                // 商品销量
                Integer quantitySales = promotionGoods.getQuantitySales() - singlePromotion.getGoodsNum();
                promotionGoods.setQuantitySales(quantitySales);
            }
            promotionGoodsDao.updateById(promotionGoods);
        }
    }

    /**
     * @param promotionId
     * @Author: tangx.w
     * @Description: 根据promotionId查询已经被选中的item
     * @Date: 2018/5/2 16:38
     */
    @Override
    public List<Long> getSelectItemIds(Long promotionId) {
        if (null == promotionId) {
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        }
        EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("promotionId", promotionId);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionGoodsEntity> promotionEntities = promotionGoodsDao.selectList(entityWrapper);
        Set<Long> itemIdSet = promotionEntities.stream().map(e -> e.getItemId()).collect(Collectors.toSet());
        return new ArrayList(itemIdSet);
    }

    @Override
    public Boolean hasSecKill(List<Long> itemIds) {
        //获取商品下的所有活动商品配置
        EntityWrapper<PromotionGoodsEntity> pgCond = new EntityWrapper<>();
        pgCond.in("itemId", itemIds);
        pgCond.eq("state", PromotionConstants.AuditState.APPROVED_AUDIT);
        pgCond.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionGoodsEntity> promotionGoodsList = promotionGoodsDao.selectList(pgCond);
        if (CollectionUtils.isEmpty(promotionGoodsList)) {
            return false;
        }

        //获取活动ID集合
        List<Long> promotionIds = promotionGoodsList.stream().map(promotionGoods -> promotionGoods.getPromotionId()).distinct().collect(Collectors.toList());
        //获取正在进行中的活动
        EntityWrapper<PromotionEntity> pCond = new EntityWrapper<>();
        pCond.in("id", promotionIds);
        pCond.eq("typeId", PromotionTypeEnum.PROMOTION_TYPE_SECKILL.getCode());
        pCond.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        pCond.eq("marketState", PROMOTION_STATE_ONGOING.getCode());
        pCond.gt("endTime", new Date());
        List<PromotionEntity> promotionList = promotionDao.selectList(pCond);
        if (CollectionUtils.isEmpty(promotionList)) {
            return false;
        }
        return true;
    }

    /**
     * @param promotionId
     * @Author: tangx.w
     * @Description: 根据promotionId查询已经圈中的店铺id
     * @Date: 2018/5/4 11:11
     */
    @Override
    public List<Long> getStoreIdListByPromotionId(Long promotionId) {
        EntityWrapper<PromotionStoresEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("promotionId", promotionId);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionStoresEntity> promotionStoresList = promotionStoresDao.selectList(entityWrapper);
        return promotionStoresList.stream().map(e -> e.getStoreId()).collect(Collectors.toList());
    }
}
