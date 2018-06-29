package com.topaiebiz.promotion.mgmt.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.basic.api.ConfigApi;
import com.topaiebiz.member.api.MemberApi;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.promotion.constants.PromotionConstants;
import com.topaiebiz.promotion.mgmt.aop.MemberLockOperation;
import com.topaiebiz.promotion.mgmt.dao.*;
import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.dto.coupon.*;
import com.topaiebiz.promotion.mgmt.entity.*;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import com.topaiebiz.promotion.mgmt.service.CouponActivityService;
import com.topaiebiz.promotion.mgmt.service.PromotionService;
import com.topaiebiz.promotion.mgmt.util.PromotionUtils;
import com.topaiebiz.promotion.promotionEnum.PromotionGradeEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionStateEnum;
import com.topaiebiz.system.util.SecurityContextUtils;
import com.topaiebiz.trade.api.order.OrderPayServiceApi;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.dto.order.OrderPayDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.topaiebiz.promotion.constants.PromotionConstants.CacheKey.*;
import static com.topaiebiz.promotion.constants.PromotionConstants.ReceiveState.*;
import static com.topaiebiz.promotion.constants.PromotionConstants.SeparatorChar.SEPARATOR_COLON;
import static com.topaiebiz.promotion.constants.PromotionConstants.UserType.NEW_USER;
import static com.topaiebiz.promotion.promotionEnum.PromotionStateEnum.*;
import static com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum.PROMOTION_TYPE_COUPON_ACTIVE;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 14:13 2018/5/10
 * @Modified by:
 */
@Slf4j
@Service
public class CouponActivityServiceImpl implements CouponActivityService {

    @Autowired
    private PromotionLogDao promotionLogDao;

    @Autowired
    private PromotionDao promotionDao;

    @Autowired
    private PromotionCouponConfigDao promotionCouponConfigDao;

    @Autowired
    private ConfigApi configApi;

    @Autowired
    private PromotionShareRrceiveDao promotionShareRrceiveDao;

    @Autowired
    private PromotionShareDao promotionShareDao;

    @Autowired
    private ShareCouponDao shareCouponDao;

    @Autowired
    private MemberCouponDao memberCouponDao;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private MemberApi memberApi;

    @Autowired
    private StoreApi storeApi;

    @Autowired
    private OrderPayServiceApi orderPayServiceApi;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private DistLockSservice distLockSservice;

    /**
     * @param * @param null
     * @Author: tangx.w
     * @Description: 添加优惠券活动
     * @Date: 2018/5/10 14:23
     */
    @Override
    public Long addCouponActivity(PromotionDto promotionDto) throws ParseException {
        Date startDate = PromotionUtils.pareTime(promotionDto.getPromotionStart());
        Date endDate = PromotionUtils.pareTime(promotionDto.getPromotionEnd());
        promotionDto.setStartTime(startDate);
        promotionDto.setEndTime(endDate);
        PromotionEntity promotion = new PromotionEntity();
        if (promotionDto.getId() != null) {
            promotion = promotionDao.selectById(promotionDto.getId());
            if (promotion != null) {
                //如果存在此活动则为编辑
                return editPromotion(promotionDto, promotion);
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
        operationType = PromotionConstants.OperationType.TYPE_NEWLY_ADDED;
        String memo = "";
        String activeConfig = PromotionUtils.packageActiveConfigDto(promotionDto);
        if (PromotionConstants.SubType.COUPON_SHARE.equals(promotionDto.getSubType())) {
            promotion.setAmount(promotionDto.getNumberOfCopies());
        }
        promotion.setActiveConfig(activeConfig);
        promotion.setSponsorType(storeId);
        promotion.setCreatorId(creatorId);
        promotion.setUsedAmount(0);
        promotion.setCreatedTime(new Date());
        promotion.setGradeId(PromotionGradeEnum.PROMOTION_GRADE_PLATFORM.getCode());
        promotion.setTypeId(PROMOTION_TYPE_COUPON_ACTIVE.getCode());
        promotion.setDiscountType(PromotionConstants.DiscountType.THE_SALE);
        promotionDao.insert(promotion);
        PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(userName, operationType, memo, promotion.getId());
        promotionLogDao.insert(promotionLogEntity);
        return promotion.getId();
    }

    public Long editPromotion(PromotionDto promotionDto, PromotionEntity promotion) {
        if (promotionDto.getNumberOfCopies() != null && promotionDto.getShareAddNum() != null) {
            promotionDto.setNumberOfCopies(promotionDto.getNumberOfCopies() + promotionDto.getShareAddNum());
            promotionDto.setAmount(promotionDto.getAmount() + promotionDto.getShareAddNum());
        }
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        String userName = SecurityContextUtils.getCurrentUserDto().getUsername();
        Integer operationType = PromotionConstants.OperationType.TYPE_EDIT;
        String memo = "";
        PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(userName, operationType, memo, promotionDto.getId());
        promotionLogDao.insert(promotionLogEntity);
        if (PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode().equals(promotion.getMarketState())) {
            promotionDto.setMarketState(promotion.getMarketState());
        }
        //改变优惠券类型清空圈中优惠券
        if (!promotionDto.getSubType().equals(promotion.getSubType())) {
            cleaUupCoupons(promotion.getId());
        }
        if (!PromotionStateEnum.PROMOTION_STATE_NOT_RELEASE.getCode().equals(promotion.getMarketState())) {
            promotionDto.setMarketState(promotion.getMarketState());
        }
        BeanCopyUtil.copy(promotionDto, promotion);
        String activeConfig = PromotionUtils.packageActiveConfigDto(promotionDto);
        promotion.setActiveConfig(activeConfig);
        promotion.setLastModifierId(creatorId);
        promotion.setLastModifiedTime(new Date());
        promotionDao.updateById(promotion);
        return promotion.getId();
    }

    public void cleaUupCoupons(Long promotionId) {
        EntityWrapper<PromotionCouponConfigEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("promotionId", promotionId);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionCouponConfigEntity> promotionCouponConfigList = promotionCouponConfigDao.selectList(entityWrapper);
        for (PromotionCouponConfigEntity promotionCouponConfigEntity : promotionCouponConfigList) {
            promotionCouponConfigEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
            promotionCouponConfigDao.updateById(promotionCouponConfigEntity);
        }
    }

    /**
     * @param promotionDto
     * @Author: tangx.w
     * @Description: 选择优惠券
     * @Date: 2018/5/10 16:36
     */
    @Override
    public PageInfo<PromotionDto> getCouponList(PagePO pagePO, PromotionDto promotionDto) {
        Page<PromotionDto> page = PageDataUtil.buildPageParam(pagePO);
        //获取之前被选中的优惠券，一会选择的时候排除用
        List<Long> promotionIds = getpromotionIds(promotionDto.getId());
        if (CollectionUtils.isEmpty(promotionIds)) {
            promotionIds = null;
        }
        promotionDto.setPromotionIdList(promotionIds);
        List<PromotionDto> Promotionlist = promotionDao.getPromotionCouponsList(page, promotionDto);
        page.setRecords(Promotionlist);
        return PageDataUtil.copyPageInfo(page);
    }

    public List<Long> getpromotionIds(Long promotionId) {
        EntityWrapper<PromotionCouponConfigEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("promotionId", promotionId);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionCouponConfigEntity> promotionCouponConfigList = promotionCouponConfigDao.selectList(entityWrapper);
        return promotionCouponConfigList.stream().map(e -> e.getCouponPromotionId()).collect(Collectors.toList());
    }

    /**
     * @param promotionId,promotionIds
     * @Author: tangx.w
     * @Description: 保存选择优惠券
     * @Date: 2018/5/11 16:30
     */
    @Override
    public void saveSelectedCoupons(Long promotionId, String promotionIds) {
        List<Long> couponIds = PromotionUtils.stringToList(promotionIds);
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        //根据优惠券查询各个优惠券的名称属性
        List<PromotionEntity> couponList = getCouponInfos(couponIds);
        for (PromotionEntity promotion : couponList) {
            //插入配置表数据
            PromotionCouponConfigEntity promotionCouponConfig = new PromotionCouponConfigEntity();
            promotionCouponConfig.setPromotionId(promotionId);
            promotionCouponConfig.setCouponPromotionId(promotion.getId());
            promotionCouponConfig.setCreatorId(creatorId);
            promotionCouponConfig.setCreatedTime(new Date());
            promotionCouponConfigDao.insert(promotionCouponConfig);

        }
    }


    public List<PromotionEntity> getCouponInfos(List<Long> couponIds) {
        EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", couponIds);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionEntity> promotionList = promotionDao.selectList(entityWrapper);
        return promotionList;
    }

    /**
     * @param promotionDto
     * @Author: tangx.w
     * @Description: 获取选中的优惠券列表
     * @Date: 2018/5/12 9:18
     */
    @Override
    public PageInfo<PromotionDto> getSelectedCoupons(PagePO pagePO, PromotionDto promotionDto) {
        Page<PromotionDto> page = PageDataUtil.buildPageParam(pagePO);
        List<PromotionDto> promotionList = new ArrayList<>();
        List<PromotionDto> promotionDtoList = promotionCouponConfigDao.getSelectedCoupons(page, promotionDto);
        for (PromotionDto promotion : promotionDtoList) {
            if (promotion != null) {
                promotionList.add(promotion);
            }
        }
        page.setRecords(promotionList);
        return PageDataUtil.copyPageInfo(page);
    }

    /**
     * @param promotionId,couponId
     * @Author: tangx.w
     * @Description: 取消选择
     * @Date: 2018/5/11 11:03
     */
    @Override
    public void cancelCoupon(Long promotionId, Long couponId) {
        EntityWrapper<PromotionCouponConfigEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("couponPromotionId", couponId);
        entityWrapper.eq("promotionId", promotionId);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionCouponConfigEntity> promotionCouponConfigList = promotionCouponConfigDao.selectList(entityWrapper);
        for (PromotionCouponConfigEntity promotionCouponConfigEntity : promotionCouponConfigList) {
            promotionCouponConfigEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
            promotionCouponConfigDao.updateById(promotionCouponConfigEntity);
        }
    }

    /**
     * @param promotionId,couponIdList
     * @Author: tangx.w
     * @Description: 保存
     * @Date: 2018/5/11 15:59
     */
    @Override
    public void saveAcitivity(List<CouponDto> couponList, Long promotionId, Integer isRelease, Integer subType) throws ParseException {
        EntityWrapper<PromotionEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.eq("id", promotionId);
        entityEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        PromotionEntity promotion = promotionDao.selectList(entityEntityWrapper).get(0);
        Map<Long, CouponDto> couponMap = PromotionUtils.couponListToMap(couponList);
        List<Long> couponIdList = couponList.stream().map(e -> e.getCouponId()).collect(Collectors.toList());
        EntityWrapper<PromotionCouponConfigEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("couponPromotionId", couponIdList);
        entityWrapper.eq("promotionId", promotionId);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionCouponConfigEntity> promotionCouponConfigList = promotionCouponConfigDao.selectList(entityWrapper);
        Integer sum = 0;
        for (PromotionCouponConfigEntity promotionCouponConfig : promotionCouponConfigList) {
            CouponDto coupon = couponMap.get(promotionCouponConfig.getCouponPromotionId());
            if (coupon == null) {
                throw new GlobalException(PromotionExceptionEnum.SHARE_COUPONS_WITHOUT_A_NUMBER_OF_COUPONS);
            }
            if (PromotionConstants.IsRslease.TYPE_RELEASE.equals(isRelease)) {
                promotionCouponConfig.setIsReleaseData(PromotionConstants.IsRsleaseDate.TYPE_YES);
            }
            if (coupon.getAddNum() > 0) {
                promotionCouponConfig.setTotalNum(promotionCouponConfig.getTotalNum() + coupon.getAddNum());
                promotionCouponConfig.setRemainderNum(promotionCouponConfig.getRemainderNum() + coupon.getAddNum());
                if (PromotionConstants.SubType.COUPON_SHARE.equals(promotion.getSubType())) {
                    promotionCouponConfig.setAmount(promotionCouponConfig.getAmount() + promotionCouponConfig.getTotalNum() * coupon.getAddNum());
                    promotionCouponConfig.setRemainderAmount(promotionCouponConfig.getRemainderNum() + promotionCouponConfig.getTotalNum() * coupon.getAddNum());
                }
            } else {
                promotionCouponConfig.setTotalNum(coupon.getCouponNum());
                promotionCouponConfig.setRemainderNum(coupon.getCouponNum());
            }
            if (PromotionConstants.SubType.COUPON_SHARE.equals(promotion.getSubType())) {
                promotionCouponConfig.setAmount(promotionCouponConfig.getTotalNum() * promotion.getAmount());
                promotionCouponConfig.setRemainderAmount(promotionCouponConfig.getTotalNum() * promotion.getAmount());
            }
            if (PromotionConstants.SubType.ORDINARY_COUPONS.equals(subType)) {
                sum = sum + promotionCouponConfig.getTotalNum();
            }
            promotionCouponConfigDao.updateById(promotionCouponConfig);
        }
        if (PromotionConstants.SubType.ORDINARY_COUPONS.equals(subType)) {
            insertAmount(sum, promotionId);
        }
        //如果是发布则更新活动状态
        if (PromotionConstants.IsRslease.TYPE_RELEASE.equals(isRelease)) {
            EntityWrapper<PromotionEntity> entityEntity = new EntityWrapper<>();
            entityEntity.eq("id", promotionId);
            entityEntity.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
            PromotionEntity promotionEntity = promotionDao.selectList(entityEntityWrapper).get(0);
            Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
            String userName = SecurityContextUtils.getCurrentUserDto().getUsername();
            Integer operationType = PromotionConstants.OperationType.TYPE_RELEASE;
            String memo = "";
            PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(userName, operationType, memo, promotionId);
            promotionLogEntity.setCreatorId(creatorId);
            promotionLogEntity.setCreatedTime(new Date());
            promotionLogDao.insert(promotionLogEntity);
            if (PromotionConstants.SubType.COUPON_SHARE.equals(subType)) {
                List<PromotionEntity> releasePromotionList = promotionDao.selectReleaseList(PromotionUtils.formatTime(promotion.getStartTime()), PromotionUtils.formatTime(promotion.getEndTime()));
                if (CollectionUtils.isEmpty(releasePromotionList)) {
                    promotionEntity.setMarketState(PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode());
                } else {
                    throw new GlobalException(PromotionExceptionEnum.COUPON_SHARINCG_ACTIVITIES_HAVE_BEEN_RELEASED_IN_THIS_TIME_PERIOD);
                }
            } else {
                promotionEntity.setMarketState(PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode());
            }
            promotionDao.updateById(promotionEntity);
        }
    }

    public void insertAmount(Integer sum, Long promotionId) {
        EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", promotionId);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionEntity> promotionList = promotionDao.selectList(entityWrapper);
        PromotionEntity promotion = promotionList.get(0);
        promotion.setAmount(sum);
        promotionDao.updateById(promotion);
    }


    /**
     * @param id
     * @Author: tangx.w
     * @Description: 取消按钮-新增/复制的时候调用
     * @Date: 2018/5/12 13:46
     */
    @Override
    public void cancel(Long id) {
        //删除活动记录
        delActiveRecord(id);
        //删除活动优惠券配置记录
        delCouponConfigRecord(id);
    }

    public void delActiveRecord(Long id) {
        EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionEntity> promotionList = promotionDao.selectList(entityWrapper);
        for (PromotionEntity promotion : promotionList) {
            promotion.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
            promotionDao.updateById(promotion);
        }
    }

    public void delCouponConfigRecord(Long id) {
        EntityWrapper<PromotionCouponConfigEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionCouponConfigEntity> promotionCouponConfigList = promotionCouponConfigDao.selectList(entityWrapper);
        for (PromotionCouponConfigEntity promotionCouponConfig : promotionCouponConfigList) {
            promotionCouponConfig.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
            promotionCouponConfigDao.updateById(promotionCouponConfig);
        }
    }

    /**
     * @param id
     * @Author: tangx.w
     * @Description: 复制优惠券活动
     * @Date: 2018/5/12 14:13
     */
    @Override
    public Long copyCouponActive(Long id) {
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        String userName = SecurityContextUtils.getCurrentUserDto().getUsername();
        Integer operationType = PromotionConstants.OperationType.TYPE_NEWLY_ADDED;
        String memo = "";
        //复制活动记录
        Long newPromotionId = copyActiveRecord(id, creatorId);
        //复制活动优惠券配置记录
        copyCouponConfigRecord(newPromotionId, id, creatorId);
        PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(userName, operationType, memo, newPromotionId);
        promotionLogEntity.setCreatorId(creatorId);
        promotionLogEntity.setCreatedTime(new Date());
        promotionLogDao.insert(promotionLogEntity);
        return newPromotionId;
    }

    public Long copyActiveRecord(Long id, Long creatorId) {
        EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionEntity> promotionList = promotionDao.selectList(entityWrapper);
        PromotionEntity promotion = promotionList.get(0);
        promotion.setCreatorId(creatorId);
        promotion.setCreatedTime(new Date());
        promotion.setMarketState(PromotionStateEnum.PROMOTION_STATE_NOT_RELEASE.getCode());
        promotion.setId(null);
        promotionDao.insert(promotion);
        return promotion.getId();
    }

    public void copyCouponConfigRecord(Long newPromotionId, Long id, Long creatorId) {
        EntityWrapper<PromotionCouponConfigEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("promotionId", id);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionCouponConfigEntity> promotionCouponConfigList = promotionCouponConfigDao.selectList(entityWrapper);
        for (PromotionCouponConfigEntity promotionCouponConfig : promotionCouponConfigList) {
            promotionCouponConfig.setPromotionId(newPromotionId);
            promotionCouponConfig.setCreatorId(creatorId);
            promotionCouponConfig.setCreatedTime(new Date());
            promotionCouponConfig.setId(null);
            promotionCouponConfigDao.insert(promotionCouponConfig);
        }
    }

    /**
     * @param promotionDto
     * @Author: tangx.w
     * @Description: 停止优惠券活动
     * @Description: 停止优惠券活动
     * @Date: 2018/5/12 14:44
     */
    @Override
    public void stopCouponActive(PromotionDto promotionDto) {
        String userName = SecurityContextUtils.getCurrentUserDto().getUsername();
        Integer operationType = PromotionConstants.OperationType.TYPE_STOP;
        EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", promotionDto.getId());
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionEntity> promotionList = promotionDao.selectList(entityWrapper);
        PromotionEntity promotion = promotionList.get(0);
        promotion.setMarketState(PromotionStateEnum.PROMOTION_STATE_TERMINATED.getCode());
        PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(userName, operationType, promotionDto.getMemo(), promotion.getId());
        promotionLogDao.insert(promotionLogEntity);
        promotionDao.updateById(promotion);
    }

    /**
     * @param promotionDto
     * @Author: tangx.w
     * @Description: 获取优惠券活动列表
     * @Date: 2018/5/14 10:34
     */
    @Override
    public PageInfo<PromotionDto> getCouponActives(PromotionDto promotionDto) throws ParseException {
        int pageNo = promotionDto.getPageNo();
        int pageSize = promotionDto.getPageSize();
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(pageNo);
        pagePO.setPageSize(pageSize);
        Page<PromotionDto> page = PageDataUtil.buildPageParam(pagePO);
        List<PromotionDto> promotionDtoList = promotionDao.getCouponActives(page, promotionDto);
        List<PromotionDto> promotionList = judgingActiveState(promotionDtoList);
        page.setRecords(promotionList);
        return PageDataUtil.copyPageInfo(page);
    }

    public List<PromotionDto> judgingActiveState(List<PromotionDto> promotionDtoList) throws ParseException {
        List<PromotionDto> promotionList = new ArrayList<>();
        for (PromotionDto promotion : promotionDtoList) {
            if (PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode().equals(promotion.getMarketState())) {
                String startTime = PromotionUtils.formatTime(promotion.getStartTime());
                String endTime = PromotionUtils.formatTime(promotion.getEndTime());
                Integer state = PromotionUtils.judgmentStoreTime(startTime, endTime);
                promotion.setMarketState(state);
            }
            promotionList.add(promotion);
        }
        return promotionList;
    }

    /**
     * @param * @param null
     * @Author: tangx.w
     * @Description: 获取优惠券分享的属性
     * @Date: 2018/5/16 13:53
     */
    @Override
    public CouponSharePropertyDto getPaymentPageBannerProperty(BannerDto bannerDto) throws ParseException {
        if (StringUtils.isEmpty(bannerDto.getPayId())) {
            log.warn("支付单号不能为空");
            throw new GlobalException(PromotionExceptionEnum.PAYMENT_OF_SINGLE_NUMBER_DOES_NOT_EXIST);
        }
        List<Long> payIdList = new ArrayList<>();
        Long payId = Long.valueOf(bannerDto.getPayId());
        payIdList.add(payId);
        String entryName = bannerDto.getEntryName();
        BannerDto bannerProperty = JSONObject.parseObject(configApi.getConfig(entryName), BannerDto.class);
        CouponSharePropertyDto couponSharePropertyDto = new CouponSharePropertyDto();
        if (bannerProperty != null) {
            if (PromotionConstants.BannerActiveType.TYPE_COUPON_SHARE_ACTIVE.equals(bannerProperty.getType())) {
                couponSharePropertyDto = JSONObject.parseObject(bannerProperty.getProperty(), CouponSharePropertyDto.class);
            } else {
                log.warn("暂时没有该类型活动，请先配置");
                throw new GlobalException(PromotionExceptionEnum.THERE_IS_NO_SUCH_TYPE_OF_ACTIVITY_FOR_THE_TIME_BEING);
            }
            Map<Long, OrderPayDTO> orderPayDTOMap = orderPayServiceApi.queryPayInfos(payIdList);
            if (orderPayDTOMap != null) {
                OrderPayDTO orderPayDTO = orderPayDTOMap.get(payId);
                Date now = new Date();
                Date startime = PromotionUtils.pareTime(couponSharePropertyDto.getShareCouponStartTime());
                Date endTime = PromotionUtils.pareTime(couponSharePropertyDto.getShareCouponEndTime());
                if (now.getTime() < endTime.getTime() && now.getTime() > startime.getTime()) {
                    if (OrderConstants.PayStatus.SUCCESS.equals(orderPayDTO.getPayState()) && (orderPayDTO.getPayTime().getTime() > startime.getTime() && orderPayDTO.getPayTime().getTime() < endTime.getTime())) {
                        couponSharePropertyDto.setOpen(isOpen(couponSharePropertyDto.getPromotionId()));
                        couponSharePropertyDto.setShareKey(getShareKey(couponSharePropertyDto.getPromotionId(), bannerDto.getPayId()));
                    } else {
                        /** 订单支付不在活动时间，入口关闭 **/
                        couponSharePropertyDto.setOpen(false);
                    }
                } else {
                    /** 当前时间过了活动时间，入口关闭 **/
                    couponSharePropertyDto.setOpen(false);
                }
            } else {
                /** payId不存在，入口关闭 **/
                couponSharePropertyDto.setOpen(false);
            }
        } else {
            log.warn("请先配置优惠券活动入口");

        }
        return couponSharePropertyDto;
    }

    public String getShareKey(Long promotionId, String payId) {
        // 登录会员
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        EntityWrapper<PromotionShareEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.eq("promotionId", promotionId);
        entityEntityWrapper.eq("payId", payId);
        entityEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionShareEntity> promotionShareList = promotionShareDao.selectList(entityEntityWrapper);
        if (CollectionUtils.isEmpty(promotionShareList)) {
            PromotionShareEntity promotionShareEntity = new PromotionShareEntity();
            promotionShareEntity.setPromotionId(promotionId);
            promotionShareEntity.setMemberId(memberId);
            promotionShareEntity.setCreatedTime(new Date());
            promotionShareEntity.setPayId(payId);
            promotionShareDao.insert(promotionShareEntity);
            return PromotionUtils.getDesEncrypt(promotionShareEntity.getId().toString());
        } else {
            return PromotionUtils.getDesEncrypt(promotionShareList.get(0).getId().toString());
        }


    }

    public boolean isOpen(Long promotionId) {
        EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", promotionId);
        entityWrapper.eq("deletedFLag", PromotionConstants.DeletedFlag.DELETED_NO);
        PromotionEntity promotionEntity = promotionDao.selectList(entityWrapper).get(0);
        Date now = new Date();
        if (PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode().equals(promotionEntity.getMarketState())) {
            if (now.getTime() > promotionEntity.getStartTime().getTime() && now.getTime() < promotionEntity.getEndTime().getTime()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    /**
     * @param shareCouponDto
     * @Author: tangx.w
     * @Description: 领取分享优惠券
     * @Date: 2018/5/16 14:35
     */
    @Override
    public ShareCouponDto getShareCoupon(ShareCouponDto shareCouponDto) {
        ShareCouponDto shareCouponResult = new ShareCouponDto();
        String key = shareCouponDto.getShareKey();
        Long promotionShareId = Long.valueOf(PromotionUtils.getDesDecrypt(key));
        //判断分享礼包是否已经被领取完
        Long promotionId = shareCouponDto.getPromotionId();
        //获取promitionId活动信息
        PromotionEntity promotion = queryPromotionInfo(promotionId);
        //获取活动配置
        ActiveConfigDto activeConfigDto = getActiveConfig(promotion);
        // 登录会员
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        log.info("优惠券分享-领取优惠券的会员id", memberId);
        Integer userType = activeConfigDto.getUserType();
        Integer receiveNum = getReceiveNum(promotionShareId, memberId);
        log.info("优惠券分享-该会员该活动领取张数{}张", receiveNum);
        //判断活动期间用户已领取的优惠券分数
        boolean isNew = promotionService.isNewUser(memberId);
        if (isNew && PromotionConstants.UserType.NEW_USER.equals(userType)) {
            shareCouponResult.setReselut(PromotionConstants.ShareCouponResult.TYPE_NEW_LIMIT);
        } else if (!isNew && PromotionConstants.UserType.OLD_USER.equals(userType)) {
            shareCouponResult.setReselut(PromotionConstants.ShareCouponResult.TYPE_OLD_LIMIT);
        } else {
            Integer confineAmount = promotion.getConfineAmount();
            Integer RrceiveCouponShareNum = promotionShareRrceiveDao.countRrceiveCouponShareNum(promotion.getId(), memberId);
            if (RrceiveCouponShareNum >= confineAmount && receiveNum == 0) {
                shareCouponResult.setReselut(PromotionConstants.ShareCouponResult.TYPE_ACTIVE_LIMIT);
            } else {
                Integer dayConfineAmount = activeConfigDto.getDayConfineAmount();
                Integer dayRrceiveCouponShareNum = promotionShareRrceiveDao.countDayRrceiveCouponShareNum(promotion.getId(), memberId);
                log.info("优惠券分享-该会员该活动当天领取张数{}张", dayRrceiveCouponShareNum);
                if (dayConfineAmount != null && dayRrceiveCouponShareNum >= dayConfineAmount && receiveNum == 0) {
                    shareCouponResult.setReselut(PromotionConstants.ShareCouponResult.TYPE_DAY_LIMIT);
                } else {
                    Integer countPeopleRrceiveNum = promotionShareRrceiveDao.countPeopleRrceiveNum(promotionShareId);
                    shareCouponResult.setReselut(PromotionConstants.ShareCouponResult.TYPE_NORMAL);
                    if (countPeopleRrceiveNum != null && countPeopleRrceiveNum < activeConfigDto.getShareConfinePeopleAmount() && receiveNum == 0) {
                        //领取优惠券
                        shareCouponResult = receiveShareCoupons(memberId, promotion, activeConfigDto, shareCouponResult, promotionShareId, key, countPeopleRrceiveNum);
                    } else if (countPeopleRrceiveNum != null && countPeopleRrceiveNum >= activeConfigDto.getShareConfinePeopleAmount() && receiveNum == 0) {
                        shareCouponResult.setReselut(PromotionConstants.ShareCouponResult.TYPE_BAG_LIMIT);
                    } else if (countPeopleRrceiveNum == null && promotion.getUsedAmount() >= promotion.getAmount() && receiveNum == 0) {
                        shareCouponResult.setReselut(PromotionConstants.ShareCouponResult.TYPE_GIFT_BAG_LIMIT);
                    }
                    shareCouponResult.setShareKey(shareCouponDto.getShareKey());
                    shareCouponResult = packageShareCoupon(shareCouponResult, memberId);
                    log.info("优惠券分享-查找返回数据{}", shareCouponResult);
                }
            }
        }
        return shareCouponResult;
    }

    public Integer getReceiveNum(Long promotionShareId, Long memberId) {
        EntityWrapper<PromotionShareRrceiveEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.eq("shareId", promotionShareId);
        entityEntityWrapper.eq("memberId", memberId);
        return promotionShareRrceiveDao.selectList(entityEntityWrapper).size();
    }

    public ShareCouponDto packageShareCoupon(ShareCouponDto shareCouponDto, Long memberId) {
        if (StringUtils.isEmpty(shareCouponDto.getShareKey())) {
            return shareCouponDto;
        } else {
            Long shareId = Long.valueOf(PromotionUtils.getDesDecrypt(shareCouponDto.getShareKey()));
            //获取优惠券该会员获取的优惠券列表
            if (!PromotionConstants.ShareCouponResult.TYPE_BAG_LIMIT.equals(shareCouponDto.getReselut())) {
                shareCouponDto.setPromotionList(getpromotionList(shareId, memberId));
                //获取会员领取详情
                shareCouponDto.setCouponDtoList(getCouponDtoList(memberId, shareId));
            }
            //获取每个会员领取记录
            shareCouponDto = getReceiveList(shareCouponDto, shareId, memberId);
            return shareCouponDto;
        }
    }

    public List<CouponDetilDto> getCouponDtoList(Long memberId, Long shareId) {
        EntityWrapper<PromotionShareRrceiveEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("memberId", memberId);
        entityWrapper.eq("shareId", shareId);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        ReceiveDetailDto receiveDetailDto = JSONObject.parseObject(promotionShareRrceiveDao.selectList(entityWrapper).get(0).getReceiveDetail(), ReceiveDetailDto.class);
        return receiveDetailDto.getCouponDetilList();
    }

    public ShareCouponDto getReceiveList(ShareCouponDto shareCouponDto, Long shareId, Long memberId) {
        List<ShareCouponReceiveDetailDto> shareCouponReceiveDetailList = new ArrayList<>();
        EntityWrapper<PromotionShareRrceiveEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.eq("shareId", shareId);
        entityEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionShareRrceiveEntity> promotionShareRrceiveList = promotionShareRrceiveDao.selectList(entityEntityWrapper);
        List<Long> memberIdList = promotionShareRrceiveList.stream().map(e -> e.getMemberId()).collect(Collectors.toList());
        Map<Long, MemberDto> memberDtoMap = memberApi.getMemberMap(memberIdList);
        for (PromotionShareRrceiveEntity promotionShareRrceiveEntity : promotionShareRrceiveList) {
            ShareCouponReceiveDetailDto shareCouponReceiveDetail = new ShareCouponReceiveDetailDto();
            shareCouponReceiveDetail.setMemberId(promotionShareRrceiveEntity.getMemberId());
            /** 隐藏手机号中间四位 **/
            shareCouponReceiveDetail.setPhone(PromotionUtils.hiddenPhoneNum(memberDtoMap.get(promotionShareRrceiveEntity.getMemberId()).getTelephone()));
            shareCouponReceiveDetail.setSmallIcon(memberDtoMap.get(promotionShareRrceiveEntity.getMemberId()).getSmallIcon());
            shareCouponReceiveDetail.setTotalValue(JSONObject.parseObject(promotionShareRrceiveEntity.getReceiveDetail(), ReceiveDetailDto.class).getTotalVelue());
            shareCouponReceiveDetailList.add(shareCouponReceiveDetail);
        }
        shareCouponDto.setShareRrceiveList(shareCouponReceiveDetailList);
        if (memberDtoMap.get(memberId) != null) {
            shareCouponDto.setPhone(memberDtoMap.get(memberId).getTelephone());
        }
        return shareCouponDto;
    }


    public List<PromotionEntity> getpromotionList(Long shareId, Long memberId) {
        EntityWrapper<PromotionShareRrceiveEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.eq("shareId", shareId);
        entityEntityWrapper.eq("memberId", memberId);
        PromotionShareRrceiveEntity promotionShareRrceiveEntity = promotionShareRrceiveDao.selectList(entityEntityWrapper).get(0);
        ReceiveDetailDto receiveDetailDto = JSONObject.parseObject(promotionShareRrceiveEntity.getReceiveDetail(), ReceiveDetailDto.class);
        List<Long> couponIds = receiveDetailDto.getCouponDetilList().stream().map(e -> e.getCouponId()).collect(Collectors.toList());
        EntityWrapper<PromotionEntity> promotionEntityEntityWrapper = new EntityWrapper<>();
        promotionEntityEntityWrapper.in("id", couponIds);
        promotionEntityEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        return promotionDao.selectList(promotionEntityEntityWrapper);
    }


    public ActiveConfigDto getActiveConfig(PromotionEntity promotion) {
        ActiveConfigDto activeConfigDto = JSONObject.parseObject(promotion.getActiveConfig(), ActiveConfigDto.class);
        if (activeConfigDto != null) {
            if (activeConfigDto.getReceiveConfineAmount() != null && activeConfigDto.getShareConfinePeopleAmount() != null) {
                return activeConfigDto;
            } else {
                throw new GlobalException(PromotionExceptionEnum.ACTIVITY_CONFIGURATION_PROBLEM_PLEASE_CONTACT_THE_ADMINISTRATOR);
            }
        } else {
            throw new GlobalException(PromotionExceptionEnum.ACTIVITY_CONFIGURATION_PROBLEM_PLEASE_CONTACT_THE_ADMINISTRATOR);
        }
    }


    public PromotionEntity queryPromotionInfo(Long promitionId) {
        EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", promitionId);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionEntity> promotionList = promotionDao.selectList(entityWrapper);
        return promotionList.get(0);
    }

    @MemberLockOperation
    public ShareCouponDto receiveShareCoupons(Long memberId, PromotionEntity promotion, ActiveConfigDto activeConfigDto, ShareCouponDto shareCouponResult, Long promotionShareId, String key, Integer countPeopleRrceiveNum) {
        ShareCouponDto shareCoupon = new ShareCouponDto();

        Integer shareConfinePeopleAmoumt = activeConfigDto.getShareConfinePeopleAmount();

        if (shareConfinePeopleAmoumt > countPeopleRrceiveNum) {

            shareCoupon.setShareKey(key);
            if (CollectionUtils.isEmpty(getShareConfigList(promotionShareId))) {
                //新建一个分享礼包
                newShareBag(promotion, promotionShareId);
            }
            //获取礼包
            List<ShareCouponEntity> shareCouponList = queryShareCoupon(promotionShareId);
            //随机获取优惠券
            randomGetCoupons(shareCouponList, activeConfigDto, memberId);
            return shareCouponResult;
        } else {
            shareCouponResult.setReselut(PromotionConstants.ShareCouponResult.TYPE_BAG_LIMIT);
            return shareCouponResult;
        }
    }

    public List<ShareCouponEntity> getShareConfigList(Long promotionShareId) {
        EntityWrapper<ShareCouponEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.eq("shareId", promotionShareId);
        entityEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        return shareCouponDao.selectList(entityEntityWrapper);
    }

    public void newShareBag(PromotionEntity promotion, Long promotionShareId) {
        EntityWrapper<PromotionCouponConfigEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("promotionId", promotion.getId());
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionCouponConfigEntity> promotionCouponConfigList = promotionCouponConfigDao.selectList(entityWrapper);
        for (PromotionCouponConfigEntity promotionCouponConfig : promotionCouponConfigList) {
            ShareCouponEntity shareCoupon = new ShareCouponEntity();
            shareCoupon.setPromotionId(promotion.getId());
            shareCoupon.setShareId(promotionShareId);
            shareCoupon.setCouponPromotionId(promotionCouponConfig.getCouponPromotionId());
            shareCoupon.setTotalNum(promotionCouponConfig.getTotalNum());
            shareCoupon.setRemainderNum(promotionCouponConfig.getRemainderNum());
            shareCoupon.setCreatedTime(new Date());
            log.info("优惠券分享-新建一个分享礼包{}", shareCoupon);
            shareCouponDao.insert(shareCoupon);
        }
        EntityWrapper<PromotionEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.eq("id", promotion.getId());
        entityEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<PromotionEntity> promotionList = promotionDao.selectList(entityEntityWrapper);
        PromotionEntity promotionEntity = promotionList.get(0);
        promotionEntity.setUsedAmount(promotionEntity.getUsedAmount() + 1);
        promotionDao.updateById(promotionEntity);
        log.info("优惠券分享-新建分享礼包完毕，礼包领取数量加1,领取礼包数量为{}", promotionEntity.getUsedAmount());
    }

    public List<ShareCouponEntity> queryShareCoupon(Long promotionShareId) {
        EntityWrapper<ShareCouponEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("shareId", promotionShareId);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        return shareCouponDao.selectList(entityWrapper);
    }

    public void randomGetCoupons(List<ShareCouponEntity> shareCouponList, ActiveConfigDto activeConfigDto, Long memberId) {
        Integer receiveAmount = activeConfigDto.getReceiveConfineAmount();
        log.info("每个会员领取优惠券数量{}", receiveAmount);
        //获取所有的分享优惠券id
        for (Integer i = receiveAmount; i > 0; i--) {
            Iterator itr = shareCouponList.iterator();
            while (itr.hasNext()) {
                ShareCouponEntity couponEntity = (ShareCouponEntity) itr.next();
                if (couponEntity.getRemainderNum() == 0) {
                    itr.remove();
                }
            }
            Integer index = Integer.valueOf((int) (Math.random() * (shareCouponList.size())));
            ShareCouponEntity shareCoupon = shareCouponList.get(index);
            //插入领取记录表
            log.info("优惠券分享-随机领取优惠券{}", shareCoupon);
            saveMemberCoupon(shareCoupon, memberId);
            //礼包总数量剩余数减一
            updateConfig(shareCoupon);
            //插入分享领取记录表
            savePromotionShareReceive(shareCoupon, memberId);
            shareCoupon.setRemainderNum(shareCoupon.getRemainderNum() - 1);
            shareCouponDao.updateById(shareCoupon);
            shareCouponList = queryShareCoupon(shareCoupon.getShareId());
        }
    }

    public void updateConfig(ShareCouponEntity shareCoupon) {
        EntityWrapper<PromotionCouponConfigEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("promotionId", shareCoupon.getPromotionId());
        entityWrapper.eq("couponPromotionId", shareCoupon.getCouponPromotionId());
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        PromotionCouponConfigEntity promotionCouponConfigEntity = promotionCouponConfigDao.selectList(entityWrapper).get(0);
        if (promotionCouponConfigEntity != null) {
            promotionCouponConfigEntity.setRemainderAmount(promotionCouponConfigEntity.getRemainderAmount() - 1);
            promotionCouponConfigDao.updateById(promotionCouponConfigEntity);
        } else {
            throw new GlobalException(PromotionExceptionEnum.COUPON_ID_NOT_NULL);
        }
        EntityWrapper<PromotionEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.eq("id", shareCoupon.getCouponPromotionId());
        entityEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        PromotionEntity promotion = promotionDao.selectList(entityEntityWrapper).get(0);
        promotion.setUsedAmount(promotion.getUsedAmount() + 1);
        promotionDao.updateById(promotion);
    }

    public void saveMemberCoupon(ShareCouponEntity sharCoupon, Long memberId) {
        MemberCouponEntity memberCouponEntity = new MemberCouponEntity();
        memberCouponEntity.setMemberId(memberId);
        memberCouponEntity.setUsageState(PromotionConstants.UsageState.USAGE_NO);
        memberCouponEntity.setCouponId(sharCoupon.getCouponPromotionId());
        memberCouponEntity.setReceiverTime(new Date());
        memberCouponEntity.setCreatedTime(new Date());
        memberCouponDao.insert(memberCouponEntity);

    }

    /**
     * @param * @param null
     * @Author: tangx.w
     * @Description: 插入当前礼包优惠券分享领取记录
     * @Date: 2018/5/17 23:37
     */
    public void savePromotionShareReceive(ShareCouponEntity shareCoupon, Long memberId) {
        EntityWrapper<PromotionShareRrceiveEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.eq("memberId", memberId);
        entityEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        entityEntityWrapper.eq("shareId", shareCoupon.getShareId());
        List<PromotionShareRrceiveEntity> promotionShareRrceiveList = promotionShareRrceiveDao.selectList(entityEntityWrapper);
        if (CollectionUtils.isNotEmpty(promotionShareRrceiveList)) {
            PromotionShareRrceiveEntity promotionShareRrceiveEntity = promotionShareRrceiveList.get(0);
            ReceiveDetailDto receiveDetailDto = JSONObject.parseObject(promotionShareRrceiveEntity.getReceiveDetail(), ReceiveDetailDto.class);
            BigDecimal totalValue = receiveDetailDto.getTotalVelue();
            List<CouponDetilDto> couponDetilList = receiveDetailDto.getCouponDetilList();
            CouponDetilDto couponDetilDto = new CouponDetilDto();
            couponDetilDto.setCouponId(shareCoupon.getCouponPromotionId());
            couponDetilDto.setDiscountValue(getDiscountValue(shareCoupon.getCouponPromotionId()));
            couponDetilDto.setNum(1);
            totalValue = totalValue.add(couponDetilDto.getDiscountValue());
            couponDetilList.add(couponDetilDto);
            receiveDetailDto.setTotalVelue(totalValue);
            receiveDetailDto.setCouponDetilList(couponDetilList);
            promotionShareRrceiveEntity.setReceiveDetail(JSON.toJSONString(receiveDetailDto));
            promotionShareRrceiveDao.updateById(promotionShareRrceiveEntity);
        } else {
            PromotionShareRrceiveEntity promotionShareRrceiveEntity = new PromotionShareRrceiveEntity();
            promotionShareRrceiveEntity.setMemberId(memberId);
            promotionShareRrceiveEntity.setPromotionId(shareCoupon.getPromotionId());
            promotionShareRrceiveEntity.setShareId(shareCoupon.getShareId());
            ReceiveDetailDto receiveDetailDto = new ReceiveDetailDto();
            CouponDetilDto couponDetilDto = new CouponDetilDto();
            List<CouponDetilDto> couponDetilList = new ArrayList<>();
            couponDetilDto.setCouponId(shareCoupon.getCouponPromotionId());
            couponDetilDto.setDiscountValue(getDiscountValue(shareCoupon.getCouponPromotionId()));
            couponDetilDto.setNum(1);
            couponDetilList.add(couponDetilDto);
            receiveDetailDto.setTotalVelue(couponDetilDto.getDiscountValue());
            receiveDetailDto.setCouponDetilList(couponDetilList);
            promotionShareRrceiveEntity.setReceiveDetail(JSON.toJSONString(receiveDetailDto));
            promotionShareRrceiveEntity.setCreatedTime(new Date());
            promotionShareRrceiveDao.insert(promotionShareRrceiveEntity);
        }
    }

    public BigDecimal getDiscountValue(Long promotionId) {
        EntityWrapper<PromotionEntity> promotion = new EntityWrapper<>();
        promotion.eq("id", promotionId);
        promotion.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        return promotionDao.selectList(promotion).get(0).getDiscountValue();
    }

    @Override
    public PromotionCouponDTO getActivityCouponsByCode(String promotionCode, Long memberId) {
        String value = configApi.getConfig(promotionCode);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        //活动ID
        Long promotionId = Long.valueOf(value);

        PromotionEntity promotion = promotionDao.selectById(promotionId);
        if (promotion == null) {
            /** 活动不存在*/
            log.warn("活动不存在，活动ID {}", promotionId);
            throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
        }

        //活动优惠券领取列表
        PromotionCouponDTO pc = new PromotionCouponDTO();
        pc.setPromotionId(promotionId);

        //本次活动的优惠券配置列表
        List<PromotionCouponConfigEntity> pcConfigList = getPromotionCouponConfigList(promotionId);

        //查询优惠券Map
        Map<Long, PromotionEntity> couponMap = getCouponMap(promotionId, pcConfigList);
        List<CouponReceiveDTO> couponReceiveList = new ArrayList<>();
        for (PromotionCouponConfigEntity pcConfig : pcConfigList) {
            PromotionEntity coupon = couponMap.get(pcConfig.getCouponPromotionId());
            //该优惠券已失效
            if (coupon == null) {
                /** 优惠券不存在*/
                log.warn("优惠券不存在，活动ID {} 优惠券ID {}", promotionId, pcConfig.getCouponPromotionId());
                continue;
            }
            //该优惠券已失效
            if (PromotionConstants.DeletedFlag.DELETED_YES.equals(coupon.getDeletedFlag()) ||
                    (!PROMOTION_STATE_NOT_START.getCode().equals(coupon.getMarketState())
                            && !PROMOTION_STATE_ONGOING.getCode().equals(coupon.getMarketState())
                            && !PROMOTION_STATE_RELEASE.getCode().equals(coupon.getMarketState()))) {
                log.warn("无效的优惠券，活动ID {} 优惠券ID {}", promotionId, pcConfig.getCouponPromotionId());
                continue;
            }

            //无效的优惠券不展示
            CouponReceiveDTO couponReceive = new CouponReceiveDTO();
            BeanCopyUtil.copy(coupon, couponReceive);
            couponReceive.setCouponId(coupon.getId());
            couponReceive.setStoreId(coupon.getSponsorType());
            //店铺优惠券
            if (coupon.getSponsorType() != null) {
                //店铺名称
                StoreInfoDetailDTO storeInfo = storeApi.getStore(coupon.getSponsorType());
                if (storeInfo != null) {
                    couponReceive.setStoreName(storeInfo.getName());
                }
            }

            //获取领取状态
            Integer received = getReceivedState(memberId, promotion, coupon, pcConfig);
            couponReceive.setReceived(received);

            couponReceiveList.add(couponReceive);
        }

        //升序排序
        Collections.sort(couponReceiveList);
        pc.setCoupons(couponReceiveList);
        return pc;
    }

    /**
     * 活动下的所有优惠券配置列表
     *
     * @param promotionId 活动ID
     * @return
     */
    private List<PromotionCouponConfigEntity> getPromotionCouponConfigList(Long promotionId) {
        String redisCode = StringUtils.join(COUPON_CONFIGS_PREFIX, promotionId);
        List<PromotionCouponConfigEntity> pcConfigList = redisCache.getListValue(redisCode, PromotionCouponConfigEntity.class);
        //缓存中未存储配置
        if (CollectionUtils.isEmpty(pcConfigList)) {
            //本次活动的优惠券配置列表
            PromotionCouponConfigEntity pcConfigCond = new PromotionCouponConfigEntity();
            pcConfigCond.cleanInit();
            pcConfigCond.setPromotionId(promotionId);
            pcConfigCond.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
            pcConfigList = promotionCouponConfigDao.selectList(new EntityWrapper<>(pcConfigCond));
            if (CollectionUtils.isEmpty(pcConfigList)) {
                /** 活动未配置优惠券*/
                log.warn("活动未配置优惠券，活动ID {}", promotionId);
                throw new GlobalException(PromotionExceptionEnum.ACTIVITY_NOT_CONFIG_COUPON_YET);
            }

            redisCache.set(redisCode, pcConfigList, 300);
        }

        return pcConfigList;
    }

    /**
     * 活动中可用的优惠券Map（couponId-coupon）
     *
     * @param promotionId  活动ID
     * @param pcConfigList 配置列表
     * @return
     */
    private Map<Long, PromotionEntity> getCouponMap(Long promotionId, List<PromotionCouponConfigEntity> pcConfigList) {
        String redisCode = StringUtils.join(ACTIVITY_COUPONS_PREFIX, promotionId);
        List<PromotionEntity> coupons = redisCache.getListValue(redisCode, PromotionEntity.class);
        //从缓存中取数据
        if (CollectionUtils.isEmpty(coupons)) {
            //查询优惠券列表
            List<Long> couponIds = pcConfigList.stream().map(PromotionCouponConfigEntity::getCouponPromotionId).distinct().collect(Collectors.toList());
            EntityWrapper<PromotionEntity> couponsCond = new EntityWrapper<>();
            couponsCond.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
            couponsCond.in("id", couponIds);
            //状态：即将开始，进行中，已发布
            List<Integer> marketStates = new ArrayList<>();
            marketStates.add(PROMOTION_STATE_NOT_START.getCode());
            marketStates.add(PROMOTION_STATE_ONGOING.getCode());
            marketStates.add(PROMOTION_STATE_RELEASE.getCode());
            couponsCond.in("marketState", marketStates);
            coupons = promotionDao.selectList(couponsCond);
            if (CollectionUtils.isEmpty(coupons)) {
                /** 优惠券*/
                log.warn("活动未配置可用优惠券，活动ID {}", promotionId);
                throw new GlobalException(PromotionExceptionEnum.ACTIVITY_NOT_AVAIL_CONFIG_COUPON_YET);
            }

            redisCache.set(redisCode, coupons, 300);
        }

        return coupons.stream().collect(Collectors.toMap(PromotionEntity::getId, coupon -> coupon));
    }

    /**
     * 获取活动优惠券领取状态 0-未领取 1-已领取 2-已领完
     *
     * @param memberId  会员ID
     * @param promotion 活动
     * @param coupon    优惠券
     * @return
     */
    private Integer getReceivedState(Long memberId, PromotionEntity promotion, PromotionEntity coupon, PromotionCouponConfigEntity pcConfig) {
        ActiveConfigDto activeConfig = JSONObject.parseObject(promotion.getActiveConfig(), ActiveConfigDto.class);
        if (activeConfig != null) {
            //用户未登录
            if (memberId == null) {
                return RECEIVE_NO;
            }
            if (activeConfig.getUserType() != null) {
                //判断该会员是否符合优惠券限制新老用户的条件
                Boolean isNew = promotionService.isNewUser(memberId);
                Boolean limitNew = NEW_USER.equals(activeConfig.getUserType()) ? true : false;
                //限制用户不匹配
                if (isNew != limitNew) {
                    return RECEIVE_NO;
                }
            }

            Integer dayGetCouponNum = getDayBindCouponNum(coupon.getId(), memberId);
            //判断是否达到个人每日限制领取数量
            if (activeConfig.getDayConfineAmount() != null
                    && activeConfig.getDayConfineAmount() != 0
                    && dayGetCouponNum >= activeConfig.getDayConfineAmount()) {
                return RECEIVE_YES;
            }
        }

        Integer bindCouponNum = getBindCouponNum(coupon.getId(), memberId);
        //判断是否达到活动中，优惠券的个人领取限制
        if (promotion.getConfineAmount() != null
                && promotion.getConfineAmount() != 0
                && bindCouponNum >= promotion.getConfineAmount()) {
            return RECEIVE_YES;
        }
        //判断活动中的所有优惠券是否已被领完
        if (promotion.getAmount() != null
                && promotion.getUsedAmount() != null
                && promotion.getAmount() <= promotion.getUsedAmount()) {
            return RECEIVE_OVER;
        }

        //判断是否达到活动优惠券限制
        if (pcConfig.getRemainderNum() == null
                || pcConfig.getRemainderNum() <= 0) {
            return RECEIVE_OVER;
        }

        return RECEIVE_NO;
    }

    /**
     * 获取领取优惠券数量
     *
     * @param couponId 优惠券ID
     * @param memberId 会员ID
     * @return
     */
    private Integer getBindCouponNum(Long couponId, Long memberId) {
        String redisCode = StringUtils.join(COUPON_BIND_NUM_PREFIX, couponId, SEPARATOR_COLON, memberId);
        Integer num = redisCache.getInt(redisCode);
        if (num == null) {
            //查询该会员领取本次活动中优惠券的数量
            EntityWrapper<MemberCouponEntity> memberCouponCond = new EntityWrapper<>();
            memberCouponCond.eq("couponId", couponId);
            memberCouponCond.eq("memberId", memberId);
            memberCouponCond.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
            num = memberCouponDao.selectCount(memberCouponCond);
            redisCache.set(redisCode, num, 60);
        }
        return num;
    }

    /**
     * 获取日领取优惠券数量
     *
     * @param couponId 优惠券ID
     * @param memberId 会员ID
     * @return
     */
    private Integer getDayBindCouponNum(Long couponId, Long memberId) {
        String redisCode = StringUtils.join(COUPON_DAY_BIND_NUM_PREFIX, couponId, SEPARATOR_COLON, memberId);
        Integer dayNum = redisCache.getInt(redisCode);
        if (dayNum == null) {
            //查询该会员领取本次活动中当天优惠券的数量
            EntityWrapper<MemberCouponEntity> memberCouponCond = new EntityWrapper<>();
            memberCouponCond.eq("couponId", couponId);
            memberCouponCond.eq("memberId", memberId);
            memberCouponCond.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
            memberCouponCond.gt("createdTime", LocalDate.now());
            dayNum = memberCouponDao.selectCount(memberCouponCond);
            redisCache.set(redisCode, dayNum, 60);
        }
        return dayNum;
    }

    @Override
    @Transactional
    @MemberLockOperation
    public Boolean bindActivityCoupon(Long promotionId, Long couponId, Long memberId) {
        PromotionEntity promotion = queryPromotion(promotionId);

        //被领取的优惠券ID集合
        if (couponId == null) {
            throw new GlobalException(PromotionExceptionEnum.COUPON_ID_NOT_NULL);
        }

        PromotionEntity coupon = promotionDao.selectById(couponId);
        if (promotion == null) {
            /** 活动不存在*/
            log.warn("优惠券不存在，活动ID {} 优惠券ID {}", promotionId, couponId);
            throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
        }

        //本次活动的优惠券配置
        PromotionCouponConfigEntity pcConfig;
        PromotionCouponConfigEntity pcConfigCond = new PromotionCouponConfigEntity();
        pcConfigCond.cleanInit();
        pcConfigCond.setPromotionId(promotionId);
        pcConfigCond.setCouponPromotionId(couponId);
        pcConfigCond.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
        try {
            pcConfig = promotionCouponConfigDao.selectOne(pcConfigCond);
        } catch (Exception e) {
            log.error("优惠券活动配置有多条记录，活动ID {} 优惠券ID {} ", promotionId, couponId);
            return false;
        }

        if (pcConfig == null) {
            /** 活动未配置优惠券*/
            log.warn("活动未配置优惠券，活动ID {}", promotion.getId());
            throw new GlobalException(PromotionExceptionEnum.ACTIVITY_NOT_CONFIG_COUPON_YET);
        }

        //核对优惠券活动领取限制
        checkLimitAmount(memberId, couponId, promotion);
        //核对活动中优惠券的数量
        checkCouponAmount(promotion, pcConfig);

        //保存会员领取优惠券数据
        saveMemberCoupon(memberId, coupon);

        //更新活动中所有优惠券领取数量
        Integer count = promotionDao.updateUsedAmountById(promotionId, 1);
        if (count == 0) {
            log.warn("活动中的优惠券已被领完，活动ID {},  ", promotionId);
            throw new GlobalException(PromotionExceptionEnum.COUPONS_ARE_NOT_TO_BE_OBTAINED);
        }

        //更新优惠券已领数量
        PromotionEntity updateCoupon = new PromotionEntity();
        updateCoupon.clearInit();
        //优惠券使用数量
        Integer usedAmount = 1;
        if (coupon.getUsedAmount() != null) {
            usedAmount = coupon.getUsedAmount() + 1;
        }
        updateCoupon.setId(couponId);
        updateCoupon.setUsedAmount(usedAmount);
        promotionDao.updateById(updateCoupon);

        //更新优惠券配置剩余数量
        count = promotionCouponConfigDao.updateRemainderNumById(pcConfig.getId(), 1);
        if (count == 0) {
            log.warn("优惠券已被领完，配置ID {},  ", pcConfig.getId());
            throw new GlobalException(PromotionExceptionEnum.COUPONS_ARE_NOT_TO_BE_OBTAINED);
        }

        //活动下的所有优惠券配置列表缓存删除
        redisCache.delete(StringUtils.join(COUPON_CONFIGS_PREFIX, promotionId));
        //活动下的所有优惠券列表缓存删除
        redisCache.delete(StringUtils.join(ACTIVITY_COUPONS_PREFIX, promotionId));
        return true;
    }

    /**
     * 查询正在进行的优惠券活动
     *
     * @return
     */
    private PromotionEntity queryPromotion(Long promotionId) {
        //查询活动是否开始
        PromotionEntity promotionCond = new PromotionEntity();
        promotionCond.clearInit();
        //活动类型（11-优惠券活动）
        promotionCond.setTypeId(PROMOTION_TYPE_COUPON_ACTIVE.getCode());
        //活动状态（6-进行中）
        promotionCond.setMarketState(PROMOTION_STATE_RELEASE.getCode());
        promotionCond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        promotionCond.setId(promotionId);
        PromotionEntity promotion = promotionDao.selectOne(promotionCond);
        if (promotion == null) {
            log.warn("queryPromotion-优惠券活动不存在，活动ID {}", promotionId);
            throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
        }
        if (promotion.getStartTime().after(new Date())) {
            log.warn("queryPromotion-优惠券活动尚未开始，活动ID {}", promotionId);
            throw new GlobalException(PromotionExceptionEnum.COUPON_ACTIVITY_NOT_STARTED_YET);
        }
        if (promotion.getEndTime().before(new Date())) {
            log.warn("queryPromotion-优惠券活动已结束，活动ID {}", promotionId);
            throw new GlobalException(PromotionExceptionEnum.COUPON_ACTIVITY_OVER);
        }

        return promotion;
    }

    /**
     * 核对优惠券活动限制
     *
     * @param memberId  会员ID
     * @param couponId  优惠券ID
     * @param promotion 活动
     */
    private void checkLimitAmount(Long memberId, Long couponId, PromotionEntity promotion) {
        ActiveConfigDto activeConfig = JSONObject.parseObject(promotion.getActiveConfig(), ActiveConfigDto.class);
        if (activeConfig != null) {
            //判断该会员是否符合优惠券限制新老用户的条件
            if (activeConfig.getUserType() != null) {
                Boolean isNew = promotionService.isNewUser(memberId);
                Boolean limitNew = NEW_USER.equals(activeConfig.getUserType()) ? true : false;
                //用户限制不匹配
                if (isNew != limitNew) {
                    if (limitNew) {
                        //限新用户可领
                        log.warn("优惠券限新用户可领，活动ID {} 优惠券ID {} ", promotion.getId(), couponId);
                        throw new GlobalException(PromotionExceptionEnum.COUPON_LIMITED_FOR_NEW_USER);
                    } else {
                        //限老用户可领
                        log.warn("优惠券限老用户可领，活动ID {} 优惠券ID {} ", promotion.getId(), couponId);
                        throw new GlobalException(PromotionExceptionEnum.COUPON_LIMITED_FOR_OLD_USER);
                    }
                }
            }

            //绑定前删除缓存
            redisCache.delete(COUPON_DAY_BIND_NUM_PREFIX);
            //查询该会员本次活动中日领取优惠券的数量
            Integer dayGetCouponNum = getDayBindCouponNum(couponId, memberId);
            //判断是否达到个人每日限制领取数量
            if (activeConfig.getDayConfineAmount() != null
                    && activeConfig.getDayConfineAmount() != 0
                    && dayGetCouponNum >= activeConfig.getDayConfineAmount()) {
                log.warn("优惠券已达到个人每日领取限制，活动ID {} 优惠券ID {} ", promotion.getId(), couponId);
                throw new GlobalException(PromotionExceptionEnum.THE_COUPONS_REACHED_THE_UPPER_LIMIT_ON_THE_DAY);
            }
        }

        //绑定前删除缓存
        redisCache.delete(COUPON_BIND_NUM_PREFIX);
        //查询该会员领取本次活动中优惠券的数量
        Integer bindCouponNum = getBindCouponNum(couponId, memberId);
        //判断是否达到活动中优惠券个人领取限制
        if (promotion.getConfineAmount() != null
                && promotion.getConfineAmount() != 0
                && bindCouponNum >= promotion.getConfineAmount()) {
            log.warn("活动中的优惠券已达到个人领取限制，活动ID {} 优惠券ID {} ", promotion.getId(), couponId);
            throw new GlobalException(PromotionExceptionEnum.THE_COUPONS_HAVE_BEEN_TAKEN);
        }
    }

    /**
     * 检验优惠券数量
     *
     * @param pcConfig
     */
    private void checkCouponAmount(PromotionEntity promotion, PromotionCouponConfigEntity pcConfig) {
        if (promotion.getAmount() <= promotion.getUsedAmount()) {
            log.warn("活动中的所有优惠券已被领完，活动ID {} ", promotion.getId());
            throw new GlobalException(PromotionExceptionEnum.COUPONS_ARE_NOT_TO_BE_OBTAINED);
        }

        if (pcConfig.getRemainderNum() == null
                || pcConfig.getRemainderNum() <= 0) {
            log.warn("优惠券已被领完，配置ID {} ", pcConfig.getId());
            throw new GlobalException(PromotionExceptionEnum.COUPONS_ARE_NOT_TO_BE_OBTAINED);
        }
    }

    /**
     * 保存会员领取优惠券数据
     *
     * @param memberId
     * @param promotion
     */
    private void saveMemberCoupon(Long memberId, PromotionEntity promotion) {
        //保存会员保定优惠券记录
        MemberCouponEntity coupon = new MemberCouponEntity();
        coupon.setMemberId(memberId);
        coupon.setCouponId(promotion.getId());
        coupon.setUsageState(PromotionConstants.UsageState.USAGE_NO);
        coupon.setStoreId(promotion.getSponsorType());
        coupon.setReceiverTime(new Date());
        coupon.setCreatorId(memberId);
        coupon.setCreatedTime(new Date());
        memberCouponDao.insert(coupon);
    }
}


