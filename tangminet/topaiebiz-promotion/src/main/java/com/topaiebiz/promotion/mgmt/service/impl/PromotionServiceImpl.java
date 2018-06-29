package com.topaiebiz.promotion.mgmt.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.util.common.math.MathCountUtils;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.api.GoodsSkuApi;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.goods.dto.sku.OutGoodsDTO;
import com.topaiebiz.merchant.api.MerchantGradeApi;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.dto.grade.MerchantGradeDTO;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.promotion.constants.PromotionConstants;
import com.topaiebiz.promotion.mgmt.aop.MemberLockOperation;
import com.topaiebiz.promotion.mgmt.dao.*;
import com.topaiebiz.promotion.mgmt.dto.*;
import com.topaiebiz.promotion.mgmt.dto.coupon.ActiveConfigDto;
import com.topaiebiz.promotion.mgmt.dto.coupon.CouponReceiveDTO;
import com.topaiebiz.promotion.mgmt.dto.coupon.SkuStateDto;
import com.topaiebiz.promotion.mgmt.entity.*;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEntry;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import com.topaiebiz.promotion.mgmt.manager.SinglePromotionManager;
import com.topaiebiz.promotion.mgmt.service.PromotionService;
import com.topaiebiz.promotion.mgmt.util.PromotionUtils;
import com.topaiebiz.promotion.promotionEnum.PromotionGradeEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionPlatformCouponStateEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionStateEnum;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import com.topaiebiz.system.dto.CurrentUserDto;
import com.topaiebiz.system.util.SecurityContextUtils;
import com.topaiebiz.trade.api.OrderStaticsApi;
import com.topaiebiz.trade.api.order.OrderServiceApi;
import com.topaiebiz.trade.dto.statics.PromotionStaticsDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.topaiebiz.promotion.constants.PromotionConstants.ReceiveState.RECEIVE_NO;
import static com.topaiebiz.promotion.constants.PromotionConstants.ReceiveState.RECEIVE_YES;
import static com.topaiebiz.promotion.constants.PromotionConstants.VariableExceptionCode.RELEASE_PROMOTION;

/**
 * Description： 营销活动
 * <p>
 * <p>
 * Author Joe
 * <p>
 * Date 2017年9月22日 下午1:56:29
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Service
public class PromotionServiceImpl implements PromotionService {

    // 营销活动
    @Autowired
    private PromotionDao promotionDao;

    // 平台优惠码
    @Autowired
    private PromotionCodeDao promotionCodeDao;

    // 营销活动商品
    @Autowired
    private PromotionGoodsDao promotionGoodsDao;

    // 营销活动商家报名
    @Autowired
    private PromotionEntryDao promotionEntryDao;

    // item商品
    @Autowired
    private GoodsApi goodsApi;

    // sku商品
    @Autowired
    private GoodsSkuApi goodsSkuApi;

    // 店铺
    @Autowired
    private StoreApi storeApi;

    // 商家等级
    @Autowired
    private MerchantGradeApi merchantGradeApi;

    // 会员优惠券
    @Autowired
    private MemberCouponDao memberCouponDao;

    // 交易统计
    @Autowired
    private OrderStaticsApi orderStaticsApi;

    // 店铺活动适用记录
    @Autowired
    private PromotionStoreUsageLogDao promotionStoreUsageLogDao;

    // 平台活动使用记录
    @Autowired
    private PromotionPlatformUsageLogDao promotionPlatformUsageLogDao;

    @Autowired
    private PromotionLogDao promotionLogDao;

    @Autowired
    private OrderServiceApi orderServiceApi;

    @Autowired
    private SinglePromotionManager singlePromotionManager;

    /**
     * Description 查询所有活动
     * <p>
     * Author Joe
     *
     * @return
     */
    @Override
    public PageInfo<PromotionDto> getPromotionList(PromotionDto promotionDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageSize(promotionDto.getPageSize());
        pagePO.setPageNo(promotionDto.getPageNo());
        Page<PromotionDto> page = PageDataUtil.buildPageParam(pagePO);
        // 获取当前登陆用户所属店铺
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        if (null != currentUserDto) {
            Long storeId = currentUserDto.getStoreId();
            if (null != storeId) {
                promotionDto.setSponsorType(storeId);
            }
        }
        List<PromotionDto> selectPromotionList = promotionDao.selectPromotionList(page, promotionDto);
        page.setRecords(selectPromotionList);
        return PageDataUtil.copyPageInfo(page);
    }

    /**
     * 显示标题验证
     *
     * @param promotionEntity
     * @return
     */
    private PromotionEntity validationShowTitle(PromotionEntity promotionEntity) {
        if (null == promotionEntity.getShowType()) {
            throw new GlobalException(PromotionExceptionEnum.SHOW_TYPE_NOT_NULL);
        }
        SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss ");
        if (promotionEntity.getShowType() == PromotionConstants.ShowType.SHOW_TITLE) {
            if (StringUtils.isBlank(promotionEntity.getShowTitle())) {
                // 当显示类型要求显示标题时，显示标题不可为空
                throw new GlobalException(PromotionExceptionEnum.SHOW_TITLE_NOT_NULL);
            } else if (promotionEntity.getShowTitle().length() > PromotionConstants.ShowTitleLength.SHOW_TITLE_LENGTH) {
                // 限制字数不可超过五个字
                throw new GlobalException(PromotionExceptionEnum.PLEASE_ADJUST_TITLE_LENGTH);
            }
        } else {
            promotionEntity.setShowTitle(sdf.format(promotionEntity.getStartTime()));
        }
        return promotionEntity;
    }

    /**
     * Description 添加单品折扣
     * <p>
     * Author Joe
     *
     * @param promotionSingleDto
     * @return
     */
    @Override
    public Long addPromotionSingle(PromotionSingleDto promotionSingleDto) throws ParseException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        // 获取所属店铺
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        // 开始时间与结束时间
        promotionSingleDto.setStartTime(PromotionUtils.pareTime(promotionSingleDto.getPromotionStart()));
        promotionSingleDto.setEndTime(PromotionUtils.pareTime(promotionSingleDto.getPromotionEnd()));
        PromotionEntity promotion = new PromotionEntity();
        BeanCopyUtil.copy(promotionSingleDto, promotion);
        // 时间限制
        timeLimit(promotion);
        promotion.setSponsorType(storeId);
        promotion.setCreatorId(creatorId);
        promotion.setCreatedTime(new Date());
        promotion.setGradeId(PromotionGradeEnum.PROMOTION_GRADE_SINGLE.getCode());
        promotion.setTypeId(PromotionTypeEnum.PROMOTION_TYPE_SINGLE.getCode());
        promotion.setDiscountType(PromotionConstants.DiscountType.DISCOUNT);
        promotion.setIsGoodsArea(PromotionConstants.IsGoodsArea.NOT_ALL);
        promotionDao.insert(promotion);
        return promotion.getId();
    }

    /**
     * Description 添加/修改单品折扣商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    @Override
    @Transactional
    public void modifyPromotionSingleGoods(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        Long promotionId = null;
        if (CollectionUtils.isNotEmpty(promotionGoodsList)) {
            for (PromotionGoodsDto promotionGoodsDto : promotionGoodsList) {
                promotionId = promotionGoodsDto.getPromotionId();
                PromotionGoodsException(promotionGoodsDto);
                if (promotionGoodsDto.getPromotionNum() != null) {
                    // 验证活动数量是否大于原库存
                    if (promotionGoodsDto.getPromotionNum() > promotionGoodsDto.getRepertoryNum()) {
                        throw new GlobalException(PromotionExceptionEnum.ACTIVITY_NUMBER_GREATER_THAN_STOCK);
                    }
                }
                PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
                promotionGoodsEntity.clearInit();
                // 查询条件
                promotionGoodsEntity.setPromotionId(promotionId);
                promotionGoodsEntity.setGoodsSkuId(promotionGoodsDto.getGoodsSkuId());
                promotionGoodsEntity.setDeleteFlag(null);
                //查询该活动是否选中该sku商品
                PromotionGoodsEntity promotionGoods = promotionGoodsDao.selectOne(promotionGoodsEntity);
                if (promotionGoods != null) {
                    if (promotionGoodsDto.getDiscountValue() == null || promotionGoodsDto.getConfineNum() == null || promotionGoodsDto.getPromotionNum() == null) {
                        promotionGoods.setRepertoryNum(promotionGoodsDto.getRepertoryNum());
                        modifySingleGoods(promotionGoods);
                    } else {
                        // 优惠折扣
                        BigDecimal discountValue = promotionGoodsDto.getDiscountValue();
                        Double discount = discountValue.doubleValue() / 10;
                        // 活动价格
                        Double promotionPrice = MathCountUtils.multiply(discount, promotionGoodsDto.getGoodsPrice().doubleValue());
                        promotionGoods.setPromotionNum(promotionGoodsDto.getPromotionNum());
                        promotionGoods.setConfineNum(promotionGoodsDto.getConfineNum());
                        promotionGoods.setPromotionPrice(BigDecimal.valueOf(promotionPrice));
                        promotionGoods.setDiscountValue(discountValue);
                        promotionGoods.setDiscountType(PromotionConstants.DiscountType.DISCOUNT);
                        promotionGoods.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
                        promotionGoods.setLastModifierId(creatorId);
                        promotionGoods.setLastModifiedTime(new Date());
                        promotionGoodsDao.updateById(promotionGoods);
                    }

                }

            }

        }
        PromotionEntity promotion = promotionDao.selectById(promotionId);
        // 是否指定商品可用
        promotion.setIsGoodsArea(PromotionConstants.IsGoodsArea.NOT_ALL);
        promotion.setLastModifierId(creatorId);
        promotion.setLastModifiedTime(new Date());
        promotionDao.updateById(promotion);
    }

    /**
     * 删除单品活动商品重建
     *
     * @param promotionGoods
     * @param promotionGoods
     */
    private void modifySingleGoods(PromotionGoodsEntity promotionGoods) {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        promotionGoodsDao.deleteById(promotionGoods);
        PromotionGoodsEntity goodsEntity = new PromotionGoodsEntity();
        goodsEntity.setPromotionId(promotionGoods.getPromotionId());
        goodsEntity.setStoreId(promotionGoods.getStoreId());
        goodsEntity.setItemId(promotionGoods.getItemId());
        goodsEntity.setGoodsSkuId(promotionGoods.getGoodsSkuId());
        goodsEntity.setRepertoryNum(promotionGoods.getRepertoryNum());
        goodsEntity.setCreatorId(creatorId);
        goodsEntity.setCreatedTime(new Date());
        goodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
        promotionGoodsDao.insert(goodsEntity);
    }

    /**
     * Description 保存/发布单品折扣活动
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePromotionSingle(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        /** 活动ID */
        Long promotionId = promotionGoodsList.get(0).getPromotionId();
        /** 活动状态 */
        Integer marketState = promotionGoodsList.get(0).getMarketState();
        for (PromotionGoodsDto promotionGoodsDto : promotionGoodsList) {
            /** 商品item */
            Long itemId = promotionGoodsDto.getItemId();
            if (null != itemId) {
                /** 活动中该item商品下所有的sku商品 */
                EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("promotionId", promotionId);
                entityWrapper.eq("itemId", itemId);
                List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(entityWrapper);
                // 获取sku商品信息，copy
                List<PromotionGoodsDto> promotionGoodsDtoList = copyPromotionGoods(promotionGoodsEntities, promotionGoodsDto);
                if (!CollectionUtils.isEmpty(promotionGoodsDtoList)) {
                    modifyPromotionSingleGoods(promotionGoodsDtoList);
                }
            }
        }
        // 保存/发布
        this.saveOrRelease(promotionId, marketState);

    }

    /**
     * 保存/发布，单品折扣与一口价保存/发布调用
     */
    private void saveOrRelease(Long promotionId, Integer marketState) {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        PromotionEntity promotion = promotionDao.selectById(promotionId);
        if (null != promotion) {
            if (PromotionStateEnum.PROMOTION_STATE_NOT_START.getCode().equals(marketState)) {
                // 调用发布活动接口
                doReleasePromotion(promotion.getId(), promotion.getTypeId());
            } else {
                if (PromotionStateEnum.PROMOTION_STATE_ABNORMAL.getCode().equals(marketState)) {
                    promotion.setMarketState(PromotionStateEnum.PROMOTION_STATE_NOT_RELEASE.getCode());
                }
                promotion.setLastModifierId(creatorId);
                promotion.setLastModifiedTime(new Date());
                promotionDao.updateById(promotion);
            }
        } else {
            throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
        }
    }


    private List<PromotionGoodsDto> copyPromotionGoods(List<PromotionGoodsEntity> promotionGoodsEntities, PromotionGoodsDto promotionGoodsDto) {
        List<PromotionGoodsDto> promotionGoodsDtos = new ArrayList<>();
        for (PromotionGoodsEntity promotionGoodsEntity : promotionGoodsEntities) {
            PromotionGoodsDto promotionGoodsDTO = new PromotionGoodsDto();
            BeanCopyUtil.copy(promotionGoodsEntity, promotionGoodsDTO);
            promotionGoodsDtos.add(promotionGoodsDTO);
        }
        for (PromotionGoodsDto promotionGoods : promotionGoodsDtos) {
            GoodsSkuDTO goodsSku = goodsSkuApi.getGoodsSku(promotionGoods.getGoodsSkuId());
            promotionGoodsDto.setGoodsPrice(goodsSku.getPrice());
            promotionGoodsDto.setGoodsSkuId(promotionGoods.getGoodsSkuId());
            promotionGoodsDto.setRepertoryNum(promotionGoods.getRepertoryNum());
            BeanCopyUtil.copy(promotionGoodsDto, promotionGoods);
        }
        return promotionGoodsDtos;
    }

    /**
     * Description 添加一口价
     * <p>
     * Author Joe
     *
     * @param promotionSingleDto
     * @return
     */
    @Override
    public Long savePromotionPrice(PromotionSingleDto promotionSingleDto) throws ParseException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        // 根据电话查到会员，获取所属店铺
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        // 开始时间与结束时间
        promotionSingleDto.setStartTime(PromotionUtils.pareTime(promotionSingleDto.getPromotionStart()));
        promotionSingleDto.setEndTime(PromotionUtils.pareTime(promotionSingleDto.getPromotionEnd()));
        PromotionEntity promotion = new PromotionEntity();
        BeanCopyUtil.copy(promotionSingleDto, promotion);
        // 时间限制
        timeLimit(promotion);
        promotion.setSponsorType(storeId);
        promotion.setCreatorId(creatorId);
        promotion.setCreatedTime(new Date());
        promotion.setGradeId(PromotionGradeEnum.PROMOTION_GRADE_SINGLE.getCode());
        promotion.setTypeId(PromotionTypeEnum.PROMOTION_TYPE_PRICE.getCode());
        promotion.setDiscountType(PromotionConstants.DiscountType.THE_SALE);
        promotion.setIsGoodsArea(PromotionConstants.IsGoodsArea.NOT_ALL);
        promotionDao.insert(promotion);
        return promotion.getId();
    }

    /**
     * Description 添加/修改一口价商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    @Override
    @Transactional
    public void modifyPromotionPriceGoods(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        Long promotionId = null;
        if (CollectionUtils.isNotEmpty(promotionGoodsList)) {
            for (PromotionGoodsDto promotionGoodsDto : promotionGoodsList) {
                promotionId = promotionGoodsDto.getPromotionId();
                PromotionGoodsException(promotionGoodsDto);
                if (promotionGoodsDto.getPromotionNum() != null) {
                    // 验证活动数量是否大于原库存
                    if (promotionGoodsDto.getPromotionNum() > promotionGoodsDto.getRepertoryNum()) {
                        throw new GlobalException(PromotionExceptionEnum.ACTIVITY_NUMBER_GREATER_THAN_STOCK);
                    }
                }
                PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
                promotionGoodsEntity.clearInit();
                // 查询条件
                promotionGoodsEntity.setPromotionId(promotionId);
                promotionGoodsEntity.setGoodsSkuId(promotionGoodsDto.getGoodsSkuId());
                promotionGoodsEntity.setDeleteFlag(null);
                //查询该活动是否选中该sku商品
                PromotionGoodsEntity promotionGoods = promotionGoodsDao.selectOne(promotionGoodsEntity);
                if (promotionGoods != null) {
                    if (promotionGoodsDto.getPromotionPrice() == null || promotionGoodsDto.getConfineNum() == null || promotionGoodsDto.getPromotionNum() == null) {
                        modifySingleGoods(promotionGoods);
                    } else {
                        // 优惠值
                        Double discountValue = MathCountUtils.subtract(promotionGoodsDto.getGoodsPrice().doubleValue(), promotionGoodsDto.getPromotionPrice().doubleValue());
                        promotionGoods.setPromotionNum(promotionGoodsDto.getPromotionNum());
                        promotionGoods.setConfineNum(promotionGoodsDto.getConfineNum());
                        promotionGoods.setPromotionPrice(promotionGoodsDto.getPromotionPrice());
                        promotionGoods.setDiscountValue(BigDecimal.valueOf(discountValue));
                        promotionGoods.setDiscountType(PromotionConstants.DiscountType.THE_SALE);
                        promotionGoods.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
                        promotionGoods.setLastModifierId(creatorId);
                        promotionGoods.setLastModifiedTime(new Date());
                        promotionGoodsDao.updateById(promotionGoods);
                    }
                }

            }

        }
        PromotionEntity promotion = promotionDao.selectById(promotionId);
        // 是否指定商品可用
        promotion.setIsGoodsArea(PromotionConstants.IsGoodsArea.NOT_ALL);
        promotion.setLastModifierId(creatorId);
        promotion.setLastModifiedTime(new Date());
        promotionDao.updateById(promotion);
    }

    /**
     * Description 发布/保存一口价活动
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    @Override
    @Transactional
    public void savePromotionPriceGoods(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        Long promotionId = promotionGoodsList.get(0).getPromotionId();
        Integer marketState = promotionGoodsList.get(0).getMarketState();
        for (PromotionGoodsDto promotionGoodsDto : promotionGoodsList) {
            Long itemId = promotionGoodsDto.getItemId();
            if (itemId != null) {
                EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("promotionId", promotionId);
                entityWrapper.eq("itemId", itemId);
                List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(entityWrapper);
                // 获取sku商品信息，copy
                List<PromotionGoodsDto> promotionGoodsDtoList = copyPromotionGoods(promotionGoodsEntities, promotionGoodsDto);
                if (!CollectionUtils.isEmpty(promotionGoodsDtoList)) {
                    modifyPromotionPriceGoods(promotionGoodsDtoList);
                }
            }
        }
        // 保存/发布
        this.saveOrRelease(promotionId, marketState);
    }

    /**
     * Description 添加秒杀活动
     * <p>
     * Author Joe
     *
     * @param promotionSingleDto
     * @return
     */
    @Override
    public Long savePromotionSeckill(PromotionSingleDto promotionSingleDto) throws ParseException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        /** 所属店铺*/
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        // 开始时间与结束时间
        promotionSingleDto.setStartTime(PromotionUtils.pareTime(promotionSingleDto.getPromotionStart()));
        promotionSingleDto.setEndTime(PromotionUtils.pareTime(promotionSingleDto.getPromotionEnd()));
        PromotionEntity promotion = new PromotionEntity();
        BeanCopyUtil.copy(promotionSingleDto, promotion);
        // 时间限制
        timeLimit(promotion);
        // 显示标题验证
        promotion = validationShowTitle(promotion);
        promotion.setSponsorType(storeId);
        promotion.setCreatorId(creatorId);
        promotion.setCreatedTime(new Date());
        promotion.setGradeId(PromotionGradeEnum.PROMOTION_GRADE_SINGLE.getCode());
        promotion.setTypeId(PromotionTypeEnum.PROMOTION_TYPE_SECKILL.getCode());
        promotion.setDiscountType(PromotionConstants.DiscountType.THE_SALE);
        // 是否指定商品可用
        promotion.setIsGoodsArea(PromotionConstants.IsGoodsArea.NOT_ALL);
        promotionDao.insert(promotion);
        return promotion.getId();
    }

    /**
     * 活动时间限制
     *
     * @param promotion
     */
    private void timeLimit(PromotionEntity promotion) {
        /** 当前时间 */
        Date nowDate = new Date();
        if (nowDate.getTime() > promotion.getStartTime().getTime()) {
            /** 当开始时间在当前时间之前 */
            throw new GlobalException(PromotionExceptionEnum.PLEASE_ADJUST_THE_START_TIME);
        }
        // 判断开始时间是否在整点
        SimpleDateFormat myFmt = new SimpleDateFormat("mmss");
        String mmssStart = myFmt.format(promotion.getStartTime());
        if (!("0000".equals(mmssStart) || "3000".equals(mmssStart))) {
            throw new GlobalException(PromotionExceptionEnum.PLEASE_ADJUST_THE_START_TIME);
        }
        // 设置开始时间要在当前时间一小时以后
//		Calendar c = Calendar.getInstance();
//		c.setTime(new Date()); // 设置当前日期
//		c.add(Calendar.HOUR, 1);
//		Date date = c.getTime(); // 结果
//		if (date.getTime() > promotion.getStartTime().getTime()) {
//			//限制活动时间
//			throw new GlobalException(PromotionExceptionEnum.PLEASE_ADJUST_THE_START_TIME);
//		}
    }

    /**
     * Description 添加/修改秒杀活动商品
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    @Override
    @Transactional
    public void modifyPromotionSeckillGoods(List<PromotionGoodsDto> promotionGoodsDtoList) throws GlobalException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        /** 所属店铺*/
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        Long promotionId = null;
        // 插入营销活动商品信息数据
        if (promotionGoodsDtoList.size() != 0) {
            for (PromotionGoodsDto promotionGoodsDto : promotionGoodsDtoList) {
                promotionId = promotionGoodsDto.getPromotionId();
                PromotionGoodsException(promotionGoodsDto);
                // 验证活动价格
                if (promotionGoodsDto.getPromotionNum() != null) {
                    // 验证活动数量是否大于原库存
                    if (promotionGoodsDto.getPromotionNum() > promotionGoodsDto.getRepertoryNum()) {
                        throw new GlobalException(PromotionExceptionEnum.ACTIVITY_NUMBER_GREATER_THAN_STOCK);
                    }
                }
                PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
                promotionGoodsEntity.clearInit();
                // 查询条件
                promotionGoodsEntity.setDeleteFlag(null);
                promotionGoodsEntity.setPromotionId(promotionId);
                promotionGoodsEntity.setGoodsSkuId(promotionGoodsDto.getGoodsSkuId());
                //查询该活动是否选中该sku商品
                PromotionGoodsEntity promotionGoods = promotionGoodsDao.selectOne(promotionGoodsEntity);
                if (promotionGoods != null) {
                    if (promotionGoodsDto.getPromotionPrice() == null || promotionGoodsDto.getConfineNum() == null || promotionGoodsDto.getPromotionNum() == null) {
                        modifySingleGoods(promotionGoods);
                    } else {
                        // 优惠值
                        Double discountValue = MathCountUtils.subtract(promotionGoodsDto.getGoodsPrice().doubleValue(), promotionGoodsDto.getPromotionPrice().doubleValue());
                        if (storeId != null) {
                            promotionGoods.setState(PromotionConstants.AuditState.NO_AUDIT);
                        }
                        promotionGoods.setPromotionNum(promotionGoodsDto.getPromotionNum());
                        promotionGoods.setConfineNum(promotionGoodsDto.getConfineNum());
                        promotionGoods.setPromotionPrice(promotionGoodsDto.getPromotionPrice());
                        promotionGoods.setDiscountType(PromotionConstants.DiscountType.THE_SALE);
                        promotionGoods.setDiscountValue(BigDecimal.valueOf(discountValue));
                        promotionGoods.setPromotionId(promotionId);
                        promotionGoods.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
                        promotionGoods.setLastModifierId(creatorId);
                        promotionGoods.setLastModifiedTime(new Date());
                        promotionGoodsDao.updateById(promotionGoods);
                    }
                }
            }
        }
        PromotionEntity promotion = promotionDao.selectById(promotionId);
        // 是否指定商品可用
        promotion.setIsGoodsArea(PromotionConstants.IsGoodsArea.NOT_ALL);
        promotion.setLastModifierId(creatorId);
        promotion.setLastModifiedTime(new Date());
        promotionDao.updateById(promotion);
    }

    /**
     * Description 保存/发布秒杀活动
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    @Override
    @Transactional
    public void savePromotionSeckillGoods(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        /** 所属店铺*/
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        Long promotionId = promotionGoodsList.get(0).getPromotionId();
        Integer marketState = promotionGoodsList.get(0).getMarketState();
        for (PromotionGoodsDto promotionGoodsDto : promotionGoodsList) {
            Long itemId = promotionGoodsDto.getItemId();
            if (itemId != null) {
                EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("promotionId", promotionId);
                entityWrapper.eq("itemId", itemId);
                List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(entityWrapper);
                // 获取sku商品信息，copy
                List<PromotionGoodsDto> promotionGoodsDtoList = copyPromotionGoods(promotionGoodsEntities, promotionGoodsDto);
                if (!CollectionUtils.isEmpty(promotionGoodsDtoList)) {
                    modifyPromotionSeckillGoods(promotionGoodsDtoList);
                }
            }
        }
        PromotionEntity promotion = promotionDao.selectById(promotionId);
        if (marketState != null) {
            /** 不点击批量设置 */
            if (!(PromotionStateEnum.PROMOTION_STATE_ABNORMAL.getCode().equals(marketState))) {
                if (PromotionStateEnum.PROMOTION_STATE_NOT_START.getCode().equals(marketState)) {
                    // 调用发布活动接口
                    releasePromotion(promotion.getId(), promotion.getTypeId());
                }
            }
            if (promotion != null) {
                if (PromotionStateEnum.PROMOTION_STATE_ABNORMAL.getCode().equals(marketState)) {
                    promotion.setMarketState(PromotionStateEnum.PROMOTION_STATE_NOT_RELEASE.getCode());
                } else {
                    promotion.setMarketState(marketState);
                }
                promotion.setLastModifierId(creatorId);
                promotion.setLastModifiedTime(new Date());
                promotionDao.updateById(promotion);
            } else {
                throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
            }
        } else {
            // 创建商家报名活动数据
            savePromotionEntry(promotionId, storeId);
            // 调用发布活动接口
            releasePromotion(promotion.getId(), promotion.getTypeId());
        }
    }

    /**
     * Description 修改单品级活动(单品折扣,一口价,秒杀)
     * <p>
     * Author Joe
     */
    @Override
    @Transactional
    public Long modifyPromotionSingle(PromotionSingleDto promotionSingleDto) throws ParseException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        // 开始时间与结束时间
        promotionSingleDto.setStartTime(PromotionUtils.pareTime(promotionSingleDto.getPromotionStart()));
        promotionSingleDto.setEndTime(PromotionUtils.pareTime(promotionSingleDto.getPromotionEnd()));
        PromotionEntity promotionEntity = promotionDao.selectById(promotionSingleDto.getId());
        BeanUtils.copyProperties(promotionSingleDto, promotionEntity);
        // 时间限制
        timeLimit(promotionEntity);
        if (promotionEntity.getTypeId().equals(PromotionTypeEnum.PROMOTION_TYPE_SECKILL.getCode())) {
            // 显示标题验证
            promotionEntity = validationShowTitle(promotionEntity);
        }
        promotionEntity.setLastModifierId(creatorId);
        promotionEntity.setLastModifiedTime(new Date());
        promotionEntity.setIsGoodsArea(PromotionConstants.IsGoodsArea.NOT_ALL);
        promotionDao.updateById(promotionEntity);
        return promotionEntity.getId();
    }

    /**
     * Description 添加满减活动
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    @Override
    public Long savePromotionReducePrice(PromotionDto promotionDto) throws ParseException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        // 根据电话查到会员，获取所属店铺
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        // 开始时间与结束时间
        promotionDto.setStartTime(PromotionUtils.pareTime(promotionDto.getPromotionStart()));
        promotionDto.setEndTime(PromotionUtils.pareTime(promotionDto.getPromotionEnd()));
        // 判断优惠条件是否大于条件值
        if (promotionDto.getDiscountValue().doubleValue() > promotionDto.getCondValue().doubleValue()) {
            throw new GlobalException(PromotionExceptionEnum.PREFERENTIAL_VALUE_SHALL_NOT_EXCEED_CONDITION_VALUE);
        }
        PromotionEntity promotion = new PromotionEntity();
        BeanCopyUtil.copy(promotionDto, promotion);
        // 时间限制
        timeLimit(promotion);
        promotion.setSponsorType(storeId);
        promotion.setCreatorId(creatorId);
        promotion.setCreatedTime(new Date());
        promotion.setGradeId(PromotionGradeEnum.PROMOTION_GRADE_STORE.getCode());
        promotion.setTypeId(PromotionTypeEnum.PROMOTION_TYPE_REDUCE_PRICE.getCode());
        promotion.setDiscountType(PromotionConstants.DiscountType.THE_SALE);
        promotion.setIsGoodsArea(PromotionConstants.IsGoodsArea.ALL);
        promotionDao.insert(promotion);
        return promotion.getId();
    }

    /**
     * Description 修改满减活动
     * <p>
     * Author Joe
     */
    @Override
    @Transactional
    public Long modifyPromotionReducePrice(PromotionDto promotionDto) throws ParseException {
        return editPromotion(promotionDto);
    }

    /**
     * Description 添加店铺优惠券
     * <p>
     * Author Joe
     *
     * @param promotionDto
     */
    @Override
    public Long savePromotionStoreCoupon(PromotionDto promotionDto) throws ParseException {
        // 开始时间与结束时间
        promotionDto.setStartTime(PromotionUtils.pareTime(promotionDto.getPromotionStart()));
        promotionDto.setEndTime(PromotionUtils.pareTime(promotionDto.getPromotionEnd()));
        PromotionEntity promotion = new PromotionEntity();
        //判断是新增还是修改
        if (promotionDto.getId() != null) {
            promotion = promotionDao.selectById(promotionDto);
            if (promotion != null) {
                return updateById(promotion, promotionDto);
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
            promotion.setMarketState(PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode());
            operationType = PromotionConstants.OperationType.TYPE_RELEASE;
        } else {
            operationType = PromotionConstants.OperationType.TYPE_NEWLY_ADDED;
        }
        String memo = "";
        promotion.setActiveConfig(getPromotionActiveConfig(promotionDto));
        promotion.setSponsorType(storeId);
        promotion.setCreatorId(creatorId);
        promotion.setUsedAmount(0);
        promotion.setCreatedTime(new Date());
        promotion.setGradeId(PromotionGradeEnum.PROMOTION_GRADE_STORE.getCode());
        promotion.setTypeId(PromotionTypeEnum.PROMOTION_TYPE_STORE_COUPON.getCode());
        promotion.setDiscountType(PromotionConstants.DiscountType.THE_SALE);
        promotionDao.insert(promotion);
        PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(userName, operationType, memo, promotion.getId());
        promotionLogDao.insert(promotionLogEntity);
        return promotion.getId();
    }

    /**
     * @param promotion,promotionDto
     * @Author: tangx.w
     * @Description: 修改商家优惠券活动
     * @Date: 2018/5/2 9:47
     */
    public Long updateById(PromotionEntity promotion, PromotionDto promotionDto) throws ParseException {
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        String userName = SecurityContextUtils.getCurrentUserDto().getUsername();
        Integer operationType = PromotionConstants.OperationType.TYPE_EDIT;

        //判断商品范围有没有改变，商品范围改变商品清空
        if (!promotionDto.getIsGoodsArea().equals(promotion.getIsGoodsArea())) {
            EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("promotionId", promotion.getId());
            entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
            List<PromotionGoodsEntity> promotionGoodsList = promotionGoodsDao.selectList(entityWrapper);
            for (PromotionGoodsEntity promotionGoods : promotionGoodsList) {
                promotionGoods.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
                promotionGoodsDao.updateById(promotionGoods);
            }
        }
        BeanCopyUtil.copy(promotionDto, promotion);
        if (PromotionConstants.IsRslease.TYPE_RELEASE.equals(promotionDto.getIsRelease())) {
            if (PromotionStateEnum.PROMOTION_STATE_NOT_RELEASE.getCode().equals(promotion.getMarketState())) {
                operationType = PromotionConstants.OperationType.TYPE_RELEASE;
            }
            promotion.setMarketState(PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode());
        }
        promotion.setActiveConfig(getPromotionActiveConfig(promotionDto));
        promotion.setLastModifiedTime(new Date());
        promotion.setLastModifierId(creatorId);
        String memo = "";
        PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(userName, operationType, memo, promotionDto.getId());
        promotionLogDao.insert(promotionLogEntity);
        promotionDao.updateById(promotion);
        return promotion.getId();
    }


    public String getPromotionActiveConfig(PromotionDto promotionDto) {
        ActiveConfigDto activeConfigDto = new ActiveConfigDto();
        activeConfigDto.setDayConfineAmount(promotionDto.getDayConfineAmount());
        activeConfigDto.setReleaseEndTime(promotionDto.getReleaseEndTime());
        activeConfigDto.setReleaseStartTime(promotionDto.getReleaseStartTime());
        return JSONObject.toJSON(activeConfigDto).toString();
    }

    /**
     * Description 修改店铺优惠券
     * <p>
     * Author Joe
     */
    @Override
    @Transactional
    public Long modifyPromotionStoreCoupon(PromotionDto promotionDto) throws ParseException {
        return editPromotion(promotionDto);
    }

    /**
     * Description 添加/修改活动商品(满减,包邮,店铺优惠券,平台优惠券,平台优惠码)
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @return
     * @throws GlobalException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyPromotionGoods(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        /** 所属店铺*/
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        Long promotionId = null;
        if (CollectionUtils.isEmpty(promotionGoodsList)) {
            return;
        }
        Long itemId = promotionGoodsList.get(0).getItemId();
        promotionId = promotionGoodsList.get(0).getPromotionId();
        PromotionEntity promotionEntity = promotionDao.selectById(promotionId);

        PromotionGoodsEntity promotionGoodsEntity1 = new PromotionGoodsEntity();
        promotionGoodsEntity1.clearInit();
        // 修改内容
        promotionGoodsEntity1.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
        EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("promotionId", promotionId);
        entityWrapper.eq("itemId", itemId);
        promotionGoodsDao.update(promotionGoodsEntity1, entityWrapper);
        for (PromotionGoodsDto promotionGoodsDto : promotionGoodsList) {
            if (promotionGoodsDto.getGoodsSkuId() == null) {
                continue;
            }
            promotionId = promotionGoodsDto.getPromotionId();
            // 验证活动商品
            PromotionGoodsException(promotionGoodsDto);
            PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
            promotionGoodsEntity.clearInit();
            // 查询条件
            promotionGoodsEntity.setDeleteFlag(null);
            promotionGoodsEntity.setPromotionId(promotionId);
            promotionGoodsEntity.setGoodsSkuId(promotionGoodsDto.getGoodsSkuId());
            PromotionGoodsEntity promotionGoods = promotionGoodsDao.selectOne(promotionGoodsEntity);
            promotionGoods.setDiscountType(PromotionConstants.DiscountType.THE_SALE);
            promotionGoods.setCreatorId(creatorId);
            promotionGoods.setCreatedTime(new Date());
            if (promotionEntity.getSponsorType() == null) {
                if (storeId == null) {
                    promotionGoods.setState(PromotionConstants.AuditState.APPROVED_AUDIT);
                } else {
                    promotionGoods.setState(PromotionConstants.AuditState.NO_AUDIT);
                }
            }
            promotionGoods.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
            promotionGoodsDao.updateById(promotionGoods);
        }

        PromotionEntity promotion = promotionDao.selectById(promotionId);
        promotion.setIsGoodsArea(PromotionConstants.IsGoodsArea.NOT_ALL);
        promotion.setLastModifierId(creatorId);
        promotion.setLastModifiedTime(new Date());
        promotionDao.updateById(promotion);
    }

    /**
     * Description 发布/保存活动(满减,包邮,店铺优惠券,平台优惠券)
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    @Override
    @Transactional
    public void savePromotion(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        /** 所属店铺*/
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        Long promotionId = promotionGoodsList.get(0).getPromotionId();
        Integer marketState = promotionGoodsList.get(0).getMarketState();
        for (PromotionGoodsDto promotionGoodsDto : promotionGoodsList) {
            List<PromotionGoodsDto> promotionGoodsDtoList = new ArrayList<>();
            Long itemId = promotionGoodsDto.getItemId();
            EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("promotionId", promotionId);
            entityWrapper.eq("itemId", itemId);
            // 查询该活动下所属itemId的sku商品
            List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(entityWrapper);
            // copy数据
            for (PromotionGoodsEntity promotionGoodsEntity : promotionGoodsEntities) {
                PromotionGoodsDto promotionGoods = new PromotionGoodsDto();
                BeanCopyUtil.copy(promotionGoodsEntity, promotionGoods);
                promotionGoodsDtoList.add(promotionGoods);
            }
            if (CollectionUtils.isNotEmpty(promotionGoodsDtoList)) {
                modifyPromotionGoods(promotionGoodsDtoList);
            }
        }
        if (marketState != null) {
            PromotionEntity promotion = promotionDao.selectById(promotionId);
            if (PromotionStateEnum.PROMOTION_STATE_NOT_START.getCode().equals(marketState)) {
                // 调用发布活动接口
                releasePromotion(promotion.getId(), promotion.getTypeId());
            }
            if (promotion != null) {
                if (PromotionStateEnum.PROMOTION_STATE_ABNORMAL.getCode().equals(marketState)) {
                    promotion.setMarketState(PromotionStateEnum.PROMOTION_STATE_NOT_RELEASE.getCode());
                } else {
                    promotion.setMarketState(marketState);
                }
                promotion.setLastModifierId(creatorId);
                promotion.setLastModifiedTime(new Date());
                promotionDao.updateById(promotion);
            } else {
                throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
            }
        } else {
            // 创建商家报名活动数据
            savePromotionEntry(promotionId, storeId);
        }
    }

    private void savePromotionEntry(Long promotionId, Long storeId) {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        // 查询该店铺报名情况
        EntityWrapper<PromotionEntryEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        entityWrapper.eq("promotionId", promotionId);
        entityWrapper.eq("storeId", storeId);
        List<PromotionEntryEntity> promotionEntryEntities = promotionEntryDao.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(promotionEntryEntities)) {
            PromotionEntryEntity promotionEntryEntity = new PromotionEntryEntity();
            promotionEntryEntity.setStoreId(storeId);
            promotionEntryEntity.setPromotionId(promotionId);
            promotionEntryEntity.setState(PromotionConstants.AuditState.NO_AUDIT);
            promotionEntryEntity.setCreatorId(creatorId);
            promotionEntryEntity.setCreatedTime(new Date());
            promotionEntryDao.insert(promotionEntryEntity);
        }
    }

    /**
     * Description 添加包邮活动
     * <p>
     * Author Joe
     *
     * @throws GlobalException
     */
    @Override
    public Long savePromotionFreeShipping(PromotionSingleDto promotionSingleDto) throws ParseException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        // 获取所属店铺
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        // 开始时间与结束时间
        promotionSingleDto.setStartTime(PromotionUtils.pareTime(promotionSingleDto.getPromotionStart()));
        promotionSingleDto.setEndTime(PromotionUtils.pareTime(promotionSingleDto.getPromotionEnd()));
        PromotionEntity promotion = new PromotionEntity();
        BeanCopyUtil.copy(promotionSingleDto, promotion);
        // 时间限制
        timeLimit(promotion);
        promotion.setSponsorType(storeId);
        promotion.setCreatorId(creatorId);
        promotion.setCreatedTime(new Date());
        promotion.setGradeId(PromotionGradeEnum.PROMOTION_GRADE_STORE.getCode());
        promotion.setTypeId(PromotionTypeEnum.PROMOTION_TYPE_FREE_SHIPPING.getCode());
        promotion.setCondType(1);
        promotion.setDiscountType(PromotionConstants.DiscountType.FREE_SHIPPING);
        promotion.setIsGoodsArea(PromotionConstants.IsGoodsArea.ALL);
        promotionDao.insert(promotion);
        return promotion.getId();
    }

    /**
     * Description 修改包邮活动
     * <p>
     * Author Joe
     *
     * @throws GlobalException
     */
    @Override
    @Transactional
    public Long modifyPromotionFreeShipping(PromotionSingleDto promotionSingleDto) throws ParseException {
        PromotionDto promotionDto = new PromotionDto();
        BeanCopyUtil.copy(promotionSingleDto, promotionDto);
        return editPromotion(promotionDto);
    }

    /**
     * Description 添加平台优惠码
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    @Override
    public Long savePromotionCouponCode(PromotionDto promotionDto) throws ParseException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        // 获取所属店铺
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        // 开始时间与结束时间
        promotionDto.setStartTime(PromotionUtils.pareTime(promotionDto.getPromotionStart()));
        promotionDto.setEndTime(PromotionUtils.pareTime(promotionDto.getPromotionEnd()));
        PromotionEntity promotion = new PromotionEntity();
        BeanCopyUtil.copy(promotionDto, promotion);
        promotion.setSponsorType(storeId);
        promotion.setCreatorId(creatorId);
        promotion.setGradeId(PromotionGradeEnum.PROMOTION_GRADE_PLATFORM.getCode());
        promotion.setTypeId(PromotionTypeEnum.PROMOTION_TYPE_COUPON_CODE.getCode());
        promotion.setCreatedTime(new Date());
        promotion.setDiscountType(PromotionConstants.DiscountType.THE_SALE);
        promotion.setIsGoodsArea(PromotionConstants.IsGoodsArea.ALL);
        promotionDao.insert(promotion);
        Long promotionId = promotion.getId();
        return promotionId;
    }

    /**
     * Description 修改平台优惠码活动
     * <p>
     * Author Joe
     *
     * @throws GlobalException
     */
    @Override
    @Transactional
    public Long modifyPromotionCouponCode(PromotionDto promotionDto) throws GlobalException, ParseException {
        return editPromotion(promotionDto);
    }

    /**
     * Description： 添加平台优惠券
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    @Override
    public Long savePromotionCoupon(PromotionDto promotion) throws ParseException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        /** 所属店铺*/
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        // 开始时间与结束时间
        promotion.setStartTime(PromotionUtils.pareTime(promotion.getPromotionStart()));
        promotion.setEndTime(PromotionUtils.pareTime(promotion.getPromotionEnd()));
        // 判断优惠条件是否大于条件值
        if (promotion.getDiscountValue().doubleValue() > promotion.getCondValue().doubleValue()) {
            throw new GlobalException(PromotionExceptionEnum.PREFERENTIAL_VALUE_SHALL_NOT_EXCEED_CONDITION_VALUE);
        }
        // 限制数额不得为空
        if (promotion.getConfineAmount() == null) {
            throw new GlobalException(PromotionExceptionEnum.FILL_IN_THE_LIMIT_CORRECTLY);
        } else if (promotion.getConfineAmount() < 0) {
            throw new GlobalException(PromotionExceptionEnum.FILL_IN_THE_LIMIT_CORRECTLY);
        }
        PromotionEntity promotionEntity = new PromotionEntity();
        BeanCopyUtil.copy(promotion, promotionEntity);
        // 时间限制
        timeLimit(promotionEntity);
        promotionEntity.setSponsorType(storeId);
        promotionEntity.setCreatorId(creatorId);
        promotionEntity.setCreatedTime(new Date());
        promotionEntity.setGradeId(PromotionGradeEnum.PROMOTION_GRADE_PLATFORM.getCode());
        promotionEntity.setTypeId(PromotionTypeEnum.PROMOTION_TYPE_COUPON.getCode());
        promotionEntity.setDiscountType(PromotionConstants.DiscountType.THE_SALE);
        promotionEntity.setIsGoodsArea(PromotionConstants.IsGoodsArea.ALL);
        promotionDao.insert(promotionEntity);
        return promotionEntity.getId();
    }

    /**
     * Description 修改平台优惠券
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    @Override
    @Transactional
    public Long modifyPromotionCoupon(PromotionDto promotionDto) throws ParseException {
        return editPromotion(promotionDto);
    }

    /**
     * Description 保存/发布平台优惠码活动
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     * @throws GlobalException
     */
    @Override
    @Transactional
    public void savePromotionCouponCodeGoods(List<PromotionGoodsDto> promotionGoodsList) throws GlobalException {
        // 当前用户ID
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        /** 所属店铺*/
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        // 活动id
        Long promotionId = promotionGoodsList.get(0).getPromotionId();
        Integer marketState = promotionGoodsList.get(0).getMarketState();
        // 插入活动商品
        for (PromotionGoodsDto promotionGoodsDto : promotionGoodsList) {
            Long itemId = promotionGoodsDto.getItemId();
            List<PromotionGoodsDto> promotionGoodsDtoList = new ArrayList<>();
            EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("promotionId", promotionId);
            entityWrapper.eq("itemId", itemId);
            // 查询该活动下所属itemId的sku商品
            List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(entityWrapper);
            // copy数据
            for (PromotionGoodsEntity promotionGoodsEntity : promotionGoodsEntities) {
                PromotionGoodsDto promotionGoods = new PromotionGoodsDto();
                BeanCopyUtil.copy(promotionGoodsEntity, promotionGoods);
                promotionGoodsDtoList.add(promotionGoods);
            }
            if (CollectionUtils.isNotEmpty(promotionGoodsDtoList)) {
                modifyPromotionGoods(promotionGoodsDtoList);
            }
        }
        if (marketState != null) {
            PromotionEntity promotion = promotionDao.selectById(promotionId);
            /** 当前时间 */
            Date nowDate = new Date();
            if (PromotionStateEnum.PROMOTION_STATE_NOT_START.getCode().equals(marketState)) {
                if (nowDate.getTime() > promotion.getEndTime().getTime()) {
                    /** 发布时间过期 */
                    throw new GlobalException(PromotionExceptionEnum.TIME_EXPIRED);
                }
            }
            if (promotion == null) {
                throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
            }
            // 生成优惠码
            if (PromotionStateEnum.PROMOTION_STATE_NOT_START.getCode().equals(promotion.getMarketState())) {
                // 发放数量
                Integer amount = promotion.getAmount();
                for (int i = 0; i < amount; i++) {
                    // 活动优惠码对象
                    PromotionCodeEntity promotionCode = new PromotionCodeEntity();
                    promotionCodeDao.insert(promotionCode);
                    // 获取刚刚插入的活动优惠码ID
                    Long promotionCodeId = promotionCode.getId();
                    PromotionCodeEntity couponCodeEntity = promotionCodeDao.selectById(promotionCodeId);
                    // 优惠码
                    String couponCode = "PROMOTION" + promotionCode.getId();
                    couponCodeEntity.setPromotionId(promotion.getId());
                    couponCodeEntity.setPromotionCode(couponCode);
                    couponCodeEntity.setCreatorId(creatorId);
                    promotionCodeDao.updateById(couponCodeEntity);
                }
            }
            if (marketState == 5) {
                promotion.setMarketState(0);
            } else {
                promotion.setMarketState(marketState);
            }
            promotion.setLastModifierId(creatorId);
            promotion.setLastModifiedTime(new Date());
            promotionDao.updateById(promotion);
        } else {
            // 创建商家报名活动数据
            savePromotionEntry(promotionId, storeId);
        }
    }


    /**
     * Description 修改营销活动回显
     * <p>
     * Author Joe
     *
     * @return
     */
    @Override
    public PromotionDto findPromotionById(Long id) {
        /** 所属店铺*/
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        PromotionEntity promotion = promotionDao.selectById(id);
        PromotionDto promotionDto = new PromotionDto();
        BeanCopyUtil.copy(promotion, promotionDto);
        promotionDto.setReleaseEndTime(formatTime(promotion.getEndTime()));
        promotionDto.setReleaseStartTime(formatTime(promotion.getStartTime()));
        ActiveConfigDto activeConfig = JSONObject.parseObject(promotion.getActiveConfig(), ActiveConfigDto.class);
        if (activeConfig != null) {
            promotionDto.setReleaseStartTime(activeConfig.getReleaseStartTime());
            promotionDto.setReleaseEndTime(activeConfig.getReleaseEndTime());
            promotionDto.setDayConfineAmount(activeConfig.getDayConfineAmount());
            promotionDto.setNumberOfCopies(activeConfig.getNumberOfCopies());
            promotionDto.setUserType(activeConfig.getUserType());
            promotionDto.setShareConfinePeopleAmount(activeConfig.getShareConfinePeopleAmount());
            promotionDto.setReceiveType(activeConfig.getReceiveType());
            promotionDto.setReceiveConfineAmount(activeConfig.getReceiveConfineAmount());

        }
        if (promotionDto.getStoreGrade() != null) {
            promotionDto.setStoreGradeName(merchantGradeApi.getMerhantGradeInfoBy(promotionDto.getStoreGrade()).getName());
        }
        if (storeId != null) {
            // 商家审核
            PromotionEntryEntity promotionEntryEntity = new PromotionEntryEntity();
            promotionEntryEntity.clearInit();
            promotionEntryEntity.setPromotionId(promotionDto.getId());
            promotionEntryEntity.setStoreId(storeId);
            PromotionEntryEntity entryEntity = promotionEntryDao.selectOne(promotionEntryEntity);
            if (entryEntity != null) {
                // 商家审核状态
                promotionDto.setAuditState(entryEntity.getState());
            }
        }
        return promotionDto;
    }

    /**
     * @param date
     * @Author: tangx.w
     * @Description: 时间格式转换
     * @Date: 2018/5/2 17:16
     */
    public String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);

    }

    /**
     * Description 停止平台优惠活动
     * <p>
     * Author Joe
     *
     * @return
     */
    @Override
    @Transactional
    public Integer modifyStopPromotion(Long id) {
        // 当前用户ID
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        PromotionEntity promotion = promotionDao.selectById(id);
        promotion.setMarketState(PromotionStateEnum.PROMOTION_STATE_TERMINATED.getCode());
        promotion.setLastModifierId(creatorId);
        promotion.setLastModifiedTime(new Date());
        return promotionDao.updateById(promotion);
    }

    /**
     * Description 删除营销活动
     * <p>
     * Author Joe
     *
     * @return
     */
    @Override
    @Transactional
    public Integer removePromotion(String ids) {
        // 当前用户ID
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        String[] arrStr = ids.split(",");
        for (String string : arrStr) {
            Long id = Long.parseLong(string);
            PromotionEntity promotion = promotionDao.selectById(id);
            promotion.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
            promotion.setLastModifierId(creatorId);
            promotion.setLastModifiedTime(new Date());
            promotionDao.updateById(promotion);
        }
        return null;
    }

    /**
     * Description 发布活动
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releasePromotion(Long id, Integer typeId) {
        doReleasePromotion(id, typeId);
    }

    private void doReleasePromotion(Long id, Integer typeId) {
        /** 所属店铺*/
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        PromotionEntity promotion = promotionDao.selectById(id);
        // 校验活动是否存在
        if (null == promotion) {
            throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
        }
        /** 当前时间 */
        Date nowDate = new Date();
        if (nowDate.getTime() > promotion.getEndTime().getTime()) {
            /** 发布时间过期 */
            throw new GlobalException(PromotionExceptionEnum.TIME_EXPIRED);
        }
        /** 校验商品是否配置完全 */
        EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("promotionId", id);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        // 该活动下被选中的所有商品
        List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(entityWrapper);

        singlePromotionManager.checkAddSinglePromotionGoods(promotion, promotionGoodsEntities);

        if (PromotionConstants.IsGoodsArea.NOT_ALL.equals(promotion.getIsGoodsArea())) {
            if (CollectionUtils.isEmpty(promotionGoodsEntities)) {
                throw new GlobalException(PromotionExceptionEnum.PROMOTIONGOODS_NOT_NULL);
            }

            //秒杀活动-判断是否含有未上架商品
            if (PromotionTypeEnum.PROMOTION_TYPE_SECKILL.getCode().equals(promotion.getTypeId())) {
                List<Long> itemIds = promotionGoodsEntities.stream().map(promotionGoods -> promotionGoods.getItemId()).distinct().collect(Collectors.toList());
                //无效商品列表
                List<OutGoodsDTO> invalidGoodsList = goodsApi.getOutGoods(itemIds);
                if (!CollectionUtils.isEmpty(invalidGoodsList)) {
                    log.warn("活动含有未上架的商品，商品详情 {}", JSON.toJSONString(invalidGoodsList));
                    String invalidGoodsIds = StringUtils.join(invalidGoodsList.stream().map(invalidGoods -> invalidGoods.getId()).collect(Collectors.toList()), ",");
                    throw new GlobalException(new PromotionExceptionEntry(RELEASE_PROMOTION, StringUtils.join("活动含有已下架的商品，商品ID:", invalidGoodsIds)));
                }
            }

            for (PromotionGoodsEntity promotionGoodsEntity : promotionGoodsEntities) {
                if (PromotionGradeEnum.PROMOTION_GRADE_SINGLE.getCode().equals(promotion.getGradeId())) {
                    // 秒杀活动
                    if (promotion.getTypeId().equals(PromotionTypeEnum.PROMOTION_TYPE_SECKILL.getCode())) {
                        if (null == storeId) {
                            // 发布活动，默认平台所选商品全部通过
                            promotionGoodsEntity.setState(PromotionConstants.AuditState.APPROVED_AUDIT);
                        }
                    }
                    // 单品活动未配置完全的商品，默认不参加活动
                    if (promotionGoodsEntity.getConfineNum() == null || promotionGoodsEntity.getPromotionPrice() == null || promotionGoodsEntity.getPromotionNum() == null) {
                        promotionGoodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
                    }
                    promotionGoodsDao.updateById(promotionGoodsEntity);
                } else if (PromotionGradeEnum.PROMOTION_GRADE_PLATFORM.getCode().equals(promotion.getGradeId())) {
                    // 发布活动，默认平台所选商品全部通过
                    promotionGoodsEntity.setState(PromotionConstants.AuditState.APPROVED_AUDIT);
                    promotionGoodsDao.updateById(promotionGoodsEntity);
                }
            }
        }
        /** 二次校验 */
        EntityWrapper<PromotionGoodsEntity> entityWrapper2 = new EntityWrapper<>();
        entityWrapper2.eq("promotionId", id);
        if (null == promotion.getSponsorType()) {
            // 平台活动商品需通过审核
            entityWrapper2.eq("state", PromotionConstants.AuditState.APPROVED_AUDIT);
        }
        entityWrapper2.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        // 该活动下被选中的所有商品
        List<PromotionGoodsEntity> promotionGoodsEntities2 = promotionGoodsDao.selectList(entityWrapper2);
        if (PromotionConstants.IsGoodsArea.NOT_ALL.equals(promotion.getIsGoodsArea())) {
            if (CollectionUtils.isEmpty(promotionGoodsEntities2)) {
                throw new GlobalException(PromotionExceptionEnum.PROMOTIONGOODS_NOT_NULL);
            }
        }
        // 批量删除不参加活动的商品
        EntityWrapper<PromotionGoodsEntity> delEntityWrapper = new EntityWrapper<>();
        delEntityWrapper.eq("promotionId", id);
        delEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_YES);
        promotionGoodsDao.delete(delEntityWrapper);
        promotion.setMarketState(PromotionStateEnum.PROMOTION_STATE_NOT_START.getCode());
        promotion.setLastModifierId(creatorId);
        promotion.setLastModifiedTime(new Date());
        promotionDao.updateById(promotion);
    }

    /**
     * Description 活动选择保存商品
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    @Override
    public void savePromotionGoods(List<PromotionGoodsDto> promotionGoodsList) {
        // 当前用户ID
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        /** 所属店铺*/
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        if (CollectionUtils.isEmpty(promotionGoodsList)) {
            return;
        }
        PromotionEntity promotionEntity = promotionDao.selectById(promotionGoodsList.get(0).getPromotionId());
        if (promotionEntity.getMaxProductNum() != null) {
            if (promotionGoodsList.size() > promotionEntity.getMaxProductNum()) {
                /** 报名商品数量超过了最大报名商品数*/
                throw new GlobalException(PromotionExceptionEnum.GOODS_NUMBER_SHALL_NOT_BE_GREATER_THAN_MAXIMUM);
            }
        }
        if (promotionEntity.getMixProductNum() != null) {
            if (promotionGoodsList.size() < promotionEntity.getMixProductNum()) {
                /** 报名商品数量低于最少报名商品数*/
                throw new GlobalException(PromotionExceptionEnum.GOODS_NUMBER_SHALL_NOT_BE_LESS_THAN_MINIMUM);
            }
        }
        // 将选中的商品放入数据库
        for (PromotionGoodsDto promotionGoodsDTO : promotionGoodsList) {

            List<GoodsSkuDTO> goodsSkuList = goodsSkuApi.getGoodsSkuList(promotionGoodsDTO.getItemId());
            for (GoodsSkuDTO goodsSkuDTO : goodsSkuList) {
                PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
                promotionGoodsEntity.clearInit();
                // 查询条件
                promotionGoodsEntity.setDeleteFlag(null);
                promotionGoodsEntity.setPromotionId(promotionGoodsDTO.getPromotionId());
                promotionGoodsEntity.setGoodsSkuId(goodsSkuDTO.getId());
                //查询该活动是否选中该sku商品
                PromotionGoodsEntity promotionGoods = promotionGoodsDao.selectOne(promotionGoodsEntity);
                // 该活动没有选过此商品
                if (promotionGoods == null) {
                    ItemDTO item = goodsApi.getItem(promotionGoodsDTO.getItemId());
                    PromotionGoodsEntity goodsEntity = new PromotionGoodsEntity();
                    BeanCopyUtil.copy(promotionGoodsDTO, goodsEntity);
                    if (storeId == null) {
                        goodsEntity.setState(PromotionConstants.AuditState.APPROVED_AUDIT);
                    }
                    goodsEntity.setGoodsSkuId(goodsSkuDTO.getId());
                    goodsEntity.setStoreId(item.getBelongStore());
                    goodsEntity.setRepertoryNum(goodsSkuDTO.getStockNumber().intValue());
//                        if (promotionEntity.getGradeId() != PromotionGradeEnum.PROMOTION_GRADE_SINGLE.getCode()) {
//                            goodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
//                        }
                    goodsEntity.setCreatorId(creatorId);
                    goodsEntity.setCreatedTime(new Date());
                    promotionGoodsDao.insert(goodsEntity);
                }
            }

            if (!(PromotionTypeEnum.PROMOTION_TYPE_COUPON.getCode().equals(promotionEntity.getTypeId()) || PromotionTypeEnum.PROMOTION_TYPE_STORE_COUPON.getCode().equals(promotionEntity.getTypeId()))) {
                promotionEntity.setIsGoodsArea(PromotionConstants.IsGoodsArea.NOT_ALL);
                promotionDao.updateById(promotionEntity);
            }
        }
    }

    /**
     * Description 活动选择保存商品
     * <p>
     * Author Joe
     *
     * @return
     * @throws GlobalException
     */
    @Override
    public void saveGoods(String itemIds, Long promotionId) {
        // 当前用户ID
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        List<Long> itemIdlist = new ArrayList<>();
        String[] itemId = itemIds.split(",");
        for (int i = 0; i < itemId.length; i++) {
            itemIdlist.add(Long.valueOf(itemId[i]));
        }
        List<ItemDTO> itemDTOList = goodsApi.getItemMap(itemIdlist);
        Map<Long, String> itemCodeMap = new HashMap<>();
        for (ItemDTO itemDTO : itemDTOList) {
            Long id = itemDTO.getId();
            String itemCode = itemDTO.getItemCode();
            if (StringUtils.isBlank(itemCode)) {
                itemCode = "";
            }
            itemCodeMap.put(id, itemCode);
        }
        PromotionEntity promotionEntity = promotionDao.selectById(promotionId);
        List<GoodsSkuDTO> goodsSkuList = goodsSkuApi.getGoodsSkuList(itemIdlist);
        for (GoodsSkuDTO goodsSku : goodsSkuList) {
            //判断这个数据是不是之前被选择过
            List<PromotionGoodsEntity> promotionGoodsList = isExistPromotionGoods(goodsSku, promotionId);
            if (CollectionUtils.isEmpty(promotionGoodsList)) {
                PromotionGoodsEntity goodsEntity = new PromotionGoodsEntity();
                goodsEntity = packagePromotionGoods(goodsSku, promotionEntity, goodsEntity, itemCodeMap);
                goodsEntity.setCreatorId(creatorId);
                promotionGoodsDao.insert(goodsEntity);
            } else {
                for (PromotionGoodsEntity promotionGoods : promotionGoodsList) {
                    promotionGoods.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
                    promotionGoodsDao.updateById(promotionGoods);
                }

            }
        }
    }

    public List<PromotionGoodsEntity> isExistPromotionGoods(GoodsSkuDTO goodsSku, Long promotionId) {
        EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("goodsSkuId", goodsSku.getId());
        entityWrapper.eq("promotionId", promotionId);
        return promotionGoodsDao.selectList(entityWrapper);

    }

    public PromotionGoodsEntity packagePromotionGoods(GoodsSkuDTO goodsSku, PromotionEntity promotionEntity, PromotionGoodsEntity goodsEntity, Map<Long, String> map) {
        String itemCode = map.get(goodsSku.getItemId());
        goodsEntity.setIsReleaseData(PromotionConstants.IsRsleaseDate.TYPE_NO);
        goodsEntity.setItemCode(itemCode);
        goodsEntity.setPromotionId(promotionEntity.getId());
        goodsEntity.setStoreId(promotionEntity.getSponsorType());
        goodsEntity.setItemId(goodsSku.getItemId());
        goodsEntity.setGoodsSkuId(goodsSku.getId());
        goodsEntity.setRepertoryNum(Integer.valueOf(goodsSku.getStockNumber().toString()));
        goodsEntity.setDiscountType(PromotionConstants.DiscountType.THE_SALE);
        goodsEntity.setDiscountValue(promotionEntity.getDiscountValue());
        goodsEntity.setCreatedTime(new Date());
        goodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
        return goodsEntity;
    }

    /**
     * Description 获取营销活动所选商品
     * <p>
     * Author Joe
     *
     * @return
     */
    @Override
    public PageInfo<PromotionGoodsDto> getPromotionGoods(ItemDTO itemDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageSize(itemDto.getPageSize());
        pagePO.setPageNo(itemDto.getPageNo());
        Page<PromotionGoodsDto> page = PageDataUtil.buildPageParam(pagePO);
        PromotionEntity promotionEntity = promotionDao.selectById(itemDto.getPromotionId());
        /** 所属店铺*/
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        if (storeId != null) {
            itemDto.setBelongStore(storeId);
        }
        /** 根据营销活动查询所选商品(根据所属item去重) */
        List<PromotionGoodsDto> promotionGoodsList = promotionGoodsDao.findPromotionGoodsByPromotionId(page, itemDto);
        if (CollectionUtils.isEmpty(promotionGoodsList)) {
            page.setRecords(new ArrayList<>());
            return PageDataUtil.copyPageInfo(page);
        }
        List<Long> itemIds = promotionGoodsList.stream().map(promotion -> promotion.getItemId()).collect(Collectors.toList());
        // 批量查询item库存
        Map<Long, Long> stockNumberMap = goodsApi.getStockNumberMap(itemIds);
        // 查询活动商品
        EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("promotionId", promotionEntity.getId());
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        entityWrapper.in("itemId", itemIds);
        List<PromotionGoodsEntity> promotionGoodsEntityList = promotionGoodsDao.selectList(entityWrapper);
        HashMap<Long, List<PromotionGoodsEntity>> skuByItemIdMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(promotionGoodsEntityList)) {
            for (PromotionGoodsEntity promotionGoods : promotionGoodsEntityList) {
                Long itemId = promotionGoods.getItemId();
                List<PromotionGoodsEntity> promotionGoodsEntities = skuByItemIdMap.get(itemId);
                if (null == promotionGoodsEntities) {
                    promotionGoodsEntities = new ArrayList<>();
                    skuByItemIdMap.put(itemId, promotionGoodsEntities);
                }
                promotionGoodsEntities.add(promotionGoods);
            }
        }
        for (PromotionGoodsDto promotionGoods : promotionGoodsList) {
            /** 判断item商品下参与活动的sku商品的数值是否一直*/
            boolean equal = false;
            Long allSkusStorage = stockNumberMap.get(promotionGoods.getItemId());
            if (null == allSkusStorage) {
                promotionGoods.setRepertoryNum(0);
            } else {
                promotionGoods.setRepertoryNum(allSkusStorage.intValue());
            }

            List<PromotionGoodsEntity> promotionGoodsEntities = skuByItemIdMap.get(promotionGoods.getItemId());
            if (CollectionUtils.isEmpty(promotionGoodsEntities)) {
                continue;
            }
            // ID限购（去重）
            List<Integer> confineNums = promotionGoodsEntities.stream().map(promotionGoodsEntity -> promotionGoodsEntity.getConfineNum()).distinct().collect(Collectors.toList());

            // 活动数量（去重）
            List<Integer> promotionNums = promotionGoodsEntities.stream().map(promotionGoodsEntity -> promotionGoodsEntity.getPromotionNum()).distinct().collect(Collectors.toList());

            // 活动价格（去重）
            List<BigDecimal> promotionPrices = promotionGoodsEntities.stream().map(promotionGoodsEntity -> promotionGoodsEntity.getPromotionPrice()).distinct().collect(Collectors.toList());

            // 优惠值（去重）
            List<BigDecimal> discountValues = promotionGoodsEntities.stream().map(promotionGoodsEntity -> promotionGoodsEntity.getDiscountValue()).distinct().collect(Collectors.toList());
            if (promotionEntity.getTypeId() == PromotionTypeEnum.PROMOTION_TYPE_SINGLE.getCode()) {
                if (discountValues.size() == 1) {
                    if (promotionNums.size() == 1) {
                        if (confineNums.size() == 1) {
                            equal = true;
                        }
                    }
                }
            } else {
                if (promotionPrices.size() == 1) {
                    if (promotionNums.size() == 1) {
                        if (confineNums.size() == 1) {
                            equal = true;
                        }
                    }
                }
            }
            if (equal) {
                promotionGoods.setDiscountValue(promotionGoodsEntities.get(0).getDiscountValue());
                promotionGoods.setPromotionPrice(promotionGoodsEntities.get(0).getPromotionPrice());
                promotionGoods.setPromotionNum(promotionGoodsEntities.get(0).getPromotionNum());
                promotionGoods.setConfineNum(promotionGoodsEntities.get(0).getConfineNum());
            }

        }
        page.setRecords(promotionGoodsList);
        return PageDataUtil.copyPageInfo(page);
    }

    /**
     * Description 取消活动商品
     * <p>
     * Author Joe
     * s
     *
     * @param promotionId
     * @param itemId
     */
    @Override
    @Transactional
    public void removeGoods(Long promotionId, Long itemId) {
        PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
        promotionGoodsEntity.clearInit();
        // 条件
        promotionGoodsEntity.setPromotionId(promotionId);
        promotionGoodsEntity.setItemId(itemId);
        promotionGoodsEntity.setDeleteFlag(null);
        promotionGoodsDao.delete(new EntityWrapper<>(promotionGoodsEntity));
    }

    /**
     * Description 定时任务：活动统计
     * <p>
     * Author Joe
     */
    @Override
    @Transactional
    public void promotionStatistical() {
        EntityWrapper<PromotionEntity> entityWrapperStatistical = new EntityWrapper<>();
        entityWrapperStatistical.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        entityWrapperStatistical.eq("marketState", PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
        List<PromotionEntity> promotionStatisticalEntities = promotionDao.selectList(entityWrapperStatistical);
        List<Long> promotionIds = promotionStatisticalEntities.stream().map(promotion -> promotion.getId()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(promotionStatisticalEntities)) {
            Map<Long, PromotionStaticsDTO> map = orderStaticsApi.promotionStatics(promotionIds);
            for (PromotionEntity promotionEntity : promotionStatisticalEntities) {
                if (promotionEntity.getGradeId() == PromotionGradeEnum.PROMOTION_GRADE_SINGLE.getCode()) {
                    // 单品活动查询下单商品件数，支付买家数，总金额
                    PromotionStaticsDTO promotionStaticsDTO = map.get(promotionEntity.getId());
                    if (promotionStaticsDTO == null) {
                        continue;
                    }
                    promotionEntity.setOrderGoodsNum(promotionStaticsDTO.getGoodsCount());
                    promotionEntity.setPayUserNum(promotionStaticsDTO.getMemberCount());
                    promotionEntity.setOrderTotalPrice(promotionStaticsDTO.getOrderTotalPrice());
                } else if (promotionEntity.getGradeId() == PromotionGradeEnum.PROMOTION_GRADE_STORE.getCode()) {
                    // 店铺活动查询下单量
                    PromotionStoreUsageLogEntity promotionStoreUsageLogEntity = new PromotionStoreUsageLogEntity();
                    promotionStoreUsageLogEntity.setPromotionId(promotionEntity.getId());
                    Integer storePromotionCount = promotionStoreUsageLogDao.selectCount(new EntityWrapper<>(promotionStoreUsageLogEntity));
                    promotionEntity.setOrderNum(storePromotionCount);
                } else if (promotionEntity.getGradeId() == PromotionGradeEnum.PROMOTION_GRADE_PLATFORM.getCode()) {
                    // 支付级活动查询下单量
                    PromotionPlatformUsageLogEntity promotionPlatformUsageLogEntity = new PromotionPlatformUsageLogEntity();
                    promotionPlatformUsageLogEntity.setPromotionId(promotionEntity.getId());
                    Integer platformPromotionCount = promotionPlatformUsageLogDao.selectCount(new EntityWrapper<>(promotionPlatformUsageLogEntity));
                    promotionEntity.setOrderNum(platformPromotionCount);
                }
                promotionDao.updateById(promotionEntity);
            }
        }
    }

    /**
     * Description 定时任务：活动报名
     * <p>
     * Author Joe
     */
    @Override
    @Transactional
    public void promotionApply() {
        /** 报名开始*/
        List<PromotionDto> promotionDtoApplyStartList = promotionDao.selectPromotionApplyStart();
        if (CollectionUtils.isNotEmpty(promotionDtoApplyStartList)) {
            for (PromotionDto promotionDto : promotionDtoApplyStartList) {
                PromotionEntity promotion = promotionDao.selectById(promotionDto.getId());
                promotion.setState(1);
                promotion.setLastModifiedTime(new Date());
                promotionDao.updateById(promotion);
            }
        }
        /** 报名结束,并且所有未审核商品全部为未通过*/
        List<PromotionDto> promotionDtoApplyEndList = promotionDao.selectPromotionApplyEnd();
        for (PromotionDto promotionDto : promotionDtoApplyEndList) {
            PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
            promotionGoodsEntity.clearInit();
            promotionGoodsEntity.setPromotionId(promotionDto.getId());
            promotionGoodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
            List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(new EntityWrapper<>(promotionGoodsEntity));
            for (PromotionGoodsEntity promotionGoods : promotionGoodsEntities) {
                if (promotionGoods.getState() == null || promotionGoods.getState() == 0) {
                    promotionGoods.setState(2);
                    promotionGoodsDao.updateById(promotionGoods);
                }
            }
            PromotionEntity promotion = promotionDao.selectById(promotionDto.getId());
            promotion.setState(2);
            promotion.setLastModifiedTime(new Date());
            promotionDao.updateById(promotion);
        }

    }


    /**
     * Description 平台报名活动列表
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     * @throws ParseException
     */
    @Override
    public PageInfo<PromotionDto> getPlatformEnrolPromotionList(PromotionDto promotionDto) throws ParseException {
        PagePO pagePO = new PagePO();
        pagePO.setPageSize(promotionDto.getPageSize());
        pagePO.setPageNo(promotionDto.getPageNo());
        Page<PromotionDto> page = PageDataUtil.buildPageParam(pagePO);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!(promotionDto.getPromotionStart() == null || "".equals(promotionDto.getPromotionStart()))) {
            promotionDto.setStartTime(PromotionUtils.pareTime(promotionDto.getPromotionStart()));
        }
        if (!(promotionDto.getPromotionEnd() == null || "".equals(promotionDto.getPromotionEnd()))) {
            promotionDto.setEndTime(PromotionUtils.pareTime(promotionDto.getPromotionEnd()));
        }
        /** 平台报名商家列表*/
        List<PromotionDto> promotionDtoList = promotionDao.selectPlatformEnrolPromotionList(page, promotionDto);
        for (PromotionDto promotion : promotionDtoList) {
            promotion.setStoreGradeName(merchantGradeApi.getMerhantGradeInfoBy(promotion.getStoreGrade()).getName());
        }
        page.setRecords(promotionDtoList);
        return PageDataUtil.copyPageInfo(page);
    }

    /**
     * Description 发起报名
     * <p>
     * Author Joe
     *
     * @param promotionEnrolDto
     * @throws ParseException
     */
    @Override
    @Transactional
    public void modifyInitiateEnrol(PromotionEnrolDto promotionEnrolDto) throws ParseException {
        Date endDate = PromotionUtils.pareTime(promotionEnrolDto.getPromotionEnrolStart());
        PromotionEntity promotion = promotionDao.selectById(promotionEnrolDto.getId());
        /** 比较报名截止时间和活动开始时间*/
        if (endDate.getTime() > promotion.getStartTime().getTime()) {
            throw new GlobalException(PromotionExceptionEnum.PLEASE_ADJUST_THE_ENROL_END_TIME);
        }
        promotion.setApplyTitle(promotionEnrolDto.getApplyTitle());
        // 开始时间与结束时间
        promotion.setApplyStartTime(PromotionUtils.pareTime(promotionEnrolDto.getPromotionEnrolStart()));
        promotion.setApplyEndTime(endDate);
        promotion.setApplyDesc(promotionEnrolDto.getApplyDesc());
        promotion.setStoreGrade(promotionEnrolDto.getStoreGrade());
        promotion.setMaxProductNum(promotionEnrolDto.getMaxProductNum());
        promotion.setMixProductNum(promotionEnrolDto.getMixProductNum());
        promotion.setState(PromotionConstants.ApplyState.APPLY_ALLOW);
        promotionDao.updateById(promotion);
    }

    /**
     * Description 发起报名获取活动列表
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    @Override
    public PageInfo<PromotionDto> getEnrolPromotionList(PromotionDto promotionDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageSize(promotionDto.getPageSize());
        pagePO.setPageNo(promotionDto.getPageNo());
        Page<PromotionDto> page = PageDataUtil.buildPageParam(pagePO);
        List<PromotionDto> promotionList = promotionDao.selectEnrolPromotionList(page, promotionDto);
        page.setRecords(promotionList);
        return PageDataUtil.copyPageInfo(page);
    }

    /**
     * Description 营销活动报名商家列表
     * <p>
     * Author Joe
     *
     * @param promotionEntryDto
     * @return
     * @throws ParseException
     */
    @Override
    public PageInfo<PromotionEntryDto> getPromotionEnrolStoreList(PromotionEntryDto promotionEntryDto) throws ParseException {
        PagePO pagePO = new PagePO();
        pagePO.setPageSize(promotionEntryDto.getPageSize());
        pagePO.setPageNo(promotionEntryDto.getPageNo());
        Page<PromotionEntryDto> page = PageDataUtil.buildPageParam(pagePO);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!(promotionEntryDto.getPromotionEnrolStart() == null || "".equals(promotionEntryDto.getPromotionEnrolStart()))) {
            Date startDate = sdf.parse(promotionEntryDto.getPromotionEnrolStart());
            promotionEntryDto.setApplyStartTime(startDate);
        }
        if (!(promotionEntryDto.getPromotionEnrolEnd() == null || "".equals(promotionEntryDto.getPromotionEnrolEnd()))) {
            Date endDate = sdf.parse(promotionEntryDto.getPromotionEnrolEnd());
            promotionEntryDto.setApplyEndTime(endDate);
        }
        List<PromotionEntryDto> promotionEntryDtoList = promotionEntryDao.selectPromotionEnrolStoreList(page, promotionEntryDto);
        for (PromotionEntryDto promotionEntry : promotionEntryDtoList) {
            if (promotionEntry.getPromotionId() != null && promotionEntry.getStoreId() != null) {
                /** 根据活动id和店铺id查询商品信息*/
                PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
                promotionGoodsEntity.clearInit();
                // 查询条件
                promotionGoodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
                promotionGoodsEntity.setPromotionId(promotionEntry.getPromotionId());
                promotionGoodsEntity.setStoreId(promotionEntry.getStoreId());
                List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(new EntityWrapper<>(promotionGoodsEntity));
                /** 商家报名商品数量*/
                if (CollectionUtils.isEmpty(promotionGoodsEntities)) {
                    promotionEntry.setEnrolGoodsNum(0);
                } else {
                    promotionEntry.setEnrolGoodsNum(promotionGoodsEntities.size());
                }
            } else {
                promotionEntry.setEnrolGoodsNum(0);
            }
        }
        page.setRecords(promotionEntryDtoList);
        return PageDataUtil.copyPageInfo(page);
    }

    /**
     * Description 报名活动商家不通过
     * <p>
     * Author Joe
     *
     * @param id
     */
    @Override
    @Transactional
    public void modifyStoreAuditNonconformity(Long id) {
        PromotionEntryEntity promotionEntry = promotionEntryDao.selectById(id);
        PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
        promotionGoodsEntity.clearInit();
        // 查询条件
        promotionGoodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
        promotionGoodsEntity.setPromotionId(promotionEntry.getPromotionId());
        promotionGoodsEntity.setStoreId(promotionEntry.getStoreId());
        List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(new EntityWrapper<>(promotionGoodsEntity));
        for (PromotionGoodsEntity promotionGoods : promotionGoodsEntities) {
            promotionGoods.setState(PromotionConstants.AuditState.FAILURE_AUDIT);
            promotionGoodsDao.updateById(promotionGoods);
        }
        promotionEntry.setState(PromotionConstants.AuditState.FAILURE_AUDIT);
        promotionEntryDao.updateById(promotionEntry);
    }

    /**
     * Description 查看店铺报名详情
     * <p>
     * Author Joe
     *
     * @param id
     * @return
     */
    @Override
    public PromotionEntryDto getPromotionEnrolStore(Long id) {
        PromotionEntryDto promotionEntryDto = promotionEntryDao.selectPromotionEnrolStore(id);
        PromotionEntity promotionEntity = promotionDao.selectById(promotionEntryDto.getPromotionId());
        /** 根据活动id和店铺id查询商品信息*/
        PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
        promotionGoodsEntity.clearInit();
        // 查询条件
        promotionGoodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
        promotionGoodsEntity.setPromotionId(promotionEntryDto.getPromotionId());
        promotionGoodsEntity.setStoreId(promotionEntryDto.getStoreId());
        List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(new EntityWrapper<>(promotionGoodsEntity));
        promotionEntryDto.setEnrolGoodsNum(promotionGoodsEntities.size());
        promotionEntryDto.setTypeId(promotionEntity.getTypeId());
        return promotionEntryDto;
    }

    /**
     * Description 商家营销活动报名列表(全部活动)
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    @Override
    public PageInfo<PromotionDto> selectStoreAllPromotionList(PromotionDto promotionDto) throws ParseException {
        // 获取当前登陆用户所属店铺
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        Long storeId = 0L;
        if (null != currentUserDto) {
            storeId = currentUserDto.getStoreId();
        }
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(promotionDto.getPageNo());
        pagePO.setPageSize(promotionDto.getPageSize());
        Page<PromotionDto> page = PageDataUtil.buildPageParam(pagePO);
        promotionDto.setStoreId(storeId);
        List<PromotionDto> promotionDtoList = promotionDao.selectStoreAllPromotionList(page, promotionDto);
        // 店铺信息
        StoreInfoDetailDTO store = storeApi.getStore(storeId);
        for (PromotionDto promotion : promotionDtoList) {
            // 报名等级要求名称
            promotion.setStoreGradeName(merchantGradeApi.getMerhantGradeInfoBy(promotion.getStoreGrade()).getName());
            // 报名等级
            MerchantGradeDTO merhantGradeInfoBy = merchantGradeApi.getMerhantGradeInfoBy(promotion.getStoreGrade());
            // 当前店铺等级
            MerchantGradeDTO gradeInfoBy = merchantGradeApi.getMerhantGradeInfoBy(store.getMerchantGradeId());
            //是否可报名
            if (merhantGradeInfoBy.getIntegralValue() > gradeInfoBy.getIntegralValue()) {
                // 不可报名
                promotion.setWhetherEnrol(1);
            } else {
                // 可报名
                promotion.setWhetherEnrol(0);
            }
        }
        return PageDataUtil.copyPageInfo(page.setRecords(promotionDtoList));
    }

    /**
     * Description 商家营销活动报名列表(已报名活动)
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    @Override
    public PageInfo<PromotionDto> getStoreEnrolPromotionList(PromotionDto promotionDto) throws ParseException {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(promotionDto.getPageNo());
        pagePO.setPageSize(promotionDto.getPageSize());
        Page<PromotionDto> page = PageDataUtil.buildPageParam(pagePO);
        /** 所属店铺*/
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        promotionDto.setStoreId(storeId);
        List<PromotionDto> promotionDtoList = promotionDao.selectStoreEnrolPromotionList(page, promotionDto);
        List<PromotionDto> promotionList = new ArrayList<PromotionDto>();
        Integer del = 0;
        for (PromotionDto promotion : promotionDtoList) {
            if (storeId != null) {
                // 查询该店铺报名情况
                EntityWrapper<PromotionEntryEntity> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
                entityWrapper.eq("promotionId", promotion.getId());
                entityWrapper.eq("storeId", storeId);
                List<PromotionEntryEntity> promotionEntryEntities = promotionEntryDao.selectList(entityWrapper);
                if (!CollectionUtils.isEmpty(promotionEntryEntities)) {
                    promotionList.add(promotion);
                } else {
                    del++;
                }
            }

        }
        for (PromotionDto promotionDto2 : promotionList) {
            /** 该店铺在该活动中所报名的商品*/
            PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
            promotionGoodsEntity.clearInit();
            // 查询条件
            promotionGoodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
            promotionGoodsEntity.setPromotionId(promotionDto2.getId());
            promotionGoodsEntity.setStoreId(storeId);
            List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(new EntityWrapper<>(promotionGoodsEntity));
            if (!CollectionUtils.isEmpty(promotionGoodsEntities)) {
                // 查询该店铺报名情况
                PromotionEntryEntity promotionEntryEntity = new PromotionEntryEntity();
                promotionEntryEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
                promotionEntryEntity.setPromotionId(promotionDto2.getId());
                promotionEntryEntity.setStoreId(storeId);
                PromotionEntryEntity promotionEntry = promotionEntryDao.selectOne(promotionEntryEntity);
                if (promotionEntry != null) {
                    if (promotionEntry.getState() == 1) {
                        for (PromotionGoodsEntity promotionGoods : promotionGoodsEntities) {
                            if (promotionGoods.getState() != null) {
                                if (promotionGoods.getState() == 2) {
                                    promotionDto2.setAuditState(3);
                                }
                            }
                        }
                    }
                }
            }
        }
        page.setRecords(promotionList);
        return PageDataUtil.copyPageInfo(page);
    }

    /**
     * C端获取店铺优惠券
     *
     * @param storeId
     * @return
     */
    @Override
    public List<PromotionDto> getStoreCoupons(Long storeId) throws ParseException {
        List<PromotionDto> promotionDtos = new ArrayList<>();
        EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("sponsorType", storeId);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        entityWrapper.eq("typeId", PromotionTypeEnum.PROMOTION_TYPE_STORE_COUPON.getCode());
        entityWrapper.eq("marketState", PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode());
        List<PromotionEntity> promotionList = promotionDao.selectList(entityWrapper);
        for (PromotionEntity promotion : promotionList) {
            PromotionDto promotionDto = new PromotionDto();
            ActiveConfigDto activeConfigDto = JSONObject.parseObject(promotion.getActiveConfig(), ActiveConfigDto.class);
            Date startTime = PromotionUtils.pareTime(activeConfigDto.getReleaseStartTime());
            Date endTime = PromotionUtils.pareTime(activeConfigDto.getReleaseEndTime());
            Date now = new Date();
            if (startTime.getTime() < now.getTime() && endTime.getTime() > now.getTime()) {
                BeanCopyUtil.copy(promotion, promotionDto);
                promotionDtos.add(promotionDto);
            }
        }
        List<PromotionEntity> promotionEntities = promotionDao.selectCouponsList(storeId);
        if (CollectionUtils.isNotEmpty(promotionEntities)) {
            for (PromotionEntity promotion : promotionEntities) {
                PromotionDto promotionDto = new PromotionDto();
                BeanCopyUtil.copy(promotion, promotionDto);
                if (promotionDto.getUsedAmount() != null) {
                    if (promotionDto.getAmount() != null && promotionDto.getUsedAmount() >= promotionDto.getAmount()) {
                        continue;
                    }
                }
                promotionDtos.add(promotionDto);
            }
        }
        return promotionDtos;
    }

    /**
     * C端领取优惠券
     *
     * @param memberId
     * @param promotionId
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @MemberLockOperation
    public void getCoupon(Long memberId, Long promotionId) {
        PromotionEntity promotionEntity = promotionDao.selectById(promotionId);
        if (promotionEntity == null) {
            /** 活动不存在*/
            throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
        }
        MemberCouponEntity memberCouponEntity = new MemberCouponEntity();
        memberCouponEntity.clearInit();
        // 查询条件
        memberCouponEntity.setCouponId(promotionId);
        memberCouponEntity.setMemberId(memberId);
        // 已领取优惠券数量
        Integer getCouponNum = memberCouponDao.selectCount(new EntityWrapper<>(memberCouponEntity));
        // 发放数量不做限制时
        if (promotionEntity.getAmount() != null) {
            // 领取数量不为空
            if (promotionEntity.getUsedAmount() != null) {
                // 领取数量大于或等于发放数量，不可以继续领取
                if (promotionEntity.getUsedAmount() >= promotionEntity.getAmount()) {
                    // 优惠券不可领取
                    throw new GlobalException(PromotionExceptionEnum.COUPONS_ARE_NOT_TO_BE_OBTAINED);
                }
            }
        }
        Integer dayConfineAmount = null;
        Integer dayGetCouponNum = getDayBindCouponNum(promotionId, memberId);
        ActiveConfigDto activeConfigDto = JSONObject.parseObject(promotionEntity.getActiveConfig(), ActiveConfigDto.class);
        if (activeConfigDto != null) {
            dayConfineAmount = activeConfigDto.getDayConfineAmount();
        }
        // 个人领取限制不做限制时
        if (promotionEntity.getConfineAmount() == null) {
            saveMemberCoupon(memberId, promotionEntity);
        } else if (promotionEntity.getConfineAmount() == 0) {
            saveMemberCoupon(memberId, promotionEntity);
        } else if (getCouponNum < promotionEntity.getConfineAmount()) {
            if ((dayConfineAmount != null && dayConfineAmount > dayGetCouponNum) || dayConfineAmount == null) {
                saveMemberCoupon(memberId, promotionEntity);
            } else {
                throw new GlobalException(PromotionExceptionEnum.THE_COUPONS_REACHED_THE_UPPER_LIMIT_ON_THE_DAY);
            }
        } else if (getCouponNum >= promotionEntity.getConfineAmount()) {
            // 优惠券已领取
            throw new GlobalException(PromotionExceptionEnum.THE_COUPONS_HAVE_BEEN_TAKEN);
        }

    }

    /**
     * 获取日领取优惠券数量
     *
     * @param couponId 优惠券ID
     * @param memberId 会员ID
     * @return
     */
    private Integer getDayBindCouponNum(Long couponId, Long memberId) {
        //查询该会员领取本次活动中优惠券的数量
        EntityWrapper<MemberCouponEntity> memberCouponCond = new EntityWrapper<>();
        memberCouponCond.eq("couponId", couponId);
        memberCouponCond.eq("memberId", memberId);
        memberCouponCond.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        memberCouponCond.gt("createdTime", LocalDate.now());
        return memberCouponDao.selectCount(memberCouponCond);
    }

    /**
     * 会员优惠券
     *
     * @param memberCouponDto
     * @return
     */
    @Override
    public PageInfo<PromotionDto> getMemberCoupon(MemberCouponDto memberCouponDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageSize(memberCouponDto.getPageSize());
        pagePO.setPageNo(memberCouponDto.getPageNo());
        Page<PromotionDto> page = PageDataUtil.buildPageParam(pagePO);
        List<PromotionDto> promotionDtos = promotionDao.selectMemberCoupon(page, memberCouponDto);
        page.setRecords(promotionDtos);
        return PageDataUtil.copyPageInfo(page);
    }

    /**
     * 已过期优惠券
     *
     * @param memberCouponDto
     * @return
     */
    @Override
    public PageInfo<PromotionDto> getOverdueCoupons(MemberCouponDto memberCouponDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageSize(memberCouponDto.getPageSize());
        pagePO.setPageNo(memberCouponDto.getPageNo());
        Page<PromotionDto> page = PageDataUtil.buildPageParam(pagePO);
        List<PromotionDto> promotionDtos = promotionDao.selectOverdueCoupons(page, memberCouponDto);
        page.setRecords(promotionDtos);
        return PageDataUtil.copyPageInfo(page);
    }

    /**
     * C端批量获取店铺优惠券
     *
     * @param storeIds
     * @return
     */
    @Override
    public Map<Long, List<PromotionDto>> getStoreCouponsList(List<Long> storeIds) {
        Map<Long, List<PromotionDto>> map = new HashMap<>();
        // 当前时间
        Date now = new Date();
        // 针对店铺优惠券增加活动状态查询
        List<Integer> marketStates = new ArrayList<>();
        marketStates.add(PromotionStateEnum.PROMOTION_STATE_ONGOING.getCode());
        marketStates.add(PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode());
        //查询条件
        EntityWrapper<PromotionEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        entityWrapper.in("marketState", marketStates);
        entityWrapper.eq("typeId", PromotionTypeEnum.PROMOTION_TYPE_STORE_COUPON.getCode());
        entityWrapper.in("sponsorType", storeIds);
        entityWrapper.gt("endTime", now);
        entityWrapper.lt("startTime", now);
        if (CollectionUtils.isEmpty(storeIds)) {
            return map;
        }
        List<PromotionEntity> promotionEntities = promotionDao.selectList(entityWrapper);
        for (PromotionEntity promotion : promotionEntities) {
            if (promotion.getUsedAmount() != null) {
                if (promotion.getAmount() != null && promotion.getUsedAmount() >= promotion.getAmount()) {
                    continue;
                }
            }
            Long storeId = promotion.getSponsorType();
            // 根据店铺id取出相应的活动集合
            List<PromotionDto> promotionDtos = map.get(storeId);
            // 将店铺id与活动集合绑定
            if (CollectionUtils.isEmpty(promotionDtos)) {
                promotionDtos = new ArrayList<>();
                map.put(storeId, promotionDtos);
            }
            PromotionDto promotionDto = new PromotionDto();
            BeanCopyUtil.copy(promotion, promotionDto);
            promotionDtos.add(promotionDto);
        }
        return map;
    }

    @Override
    public Boolean isNewUser(Long memberId) {
        Integer count = orderServiceApi.queryMemberValidOrderCounts(memberId);
        return count.equals(0);
    }

    @Override
    public List<CouponReceiveDTO> getCouponListByCode(List<Long> couponIds, Long memberId) {
        //优惠券列表
        List<PromotionEntity> couponList = promotionDao.selectBatchIds(couponIds);
        if (CollectionUtils.isEmpty(couponList)) {
            return null;
        }

        List<CouponReceiveDTO> couponReceiveList = new ArrayList<>();
        for (PromotionEntity coupon : couponList) {
            //封装DTO
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

            if (memberId == null) {
                couponReceive.setReceived(RECEIVE_NO);
                couponReceiveList.add(couponReceive);
                continue;
            }

            EntityWrapper<MemberCouponEntity> couponWrapper = new EntityWrapper<>();
            couponWrapper.eq("memberId", memberId);
            couponWrapper.eq("couponId", coupon.getId());
            couponWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
            List<MemberCouponEntity> mcList = memberCouponDao.selectList(couponWrapper);
            //是否被领取
            if (CollectionUtils.isEmpty(mcList)) {
                couponReceive.setReceived(RECEIVE_NO);
            } else {
                couponReceive.setReceived(RECEIVE_YES);
            }

            couponReceiveList.add(couponReceive);
        }
        //升序排序
        Collections.sort(couponReceiveList);

        return couponReceiveList;
    }

    /**
     * 保存会员领取优惠券数据
     *
     * @param memberId
     * @param promotionEntity
     */
    private void saveMemberCoupon(Long memberId, PromotionEntity promotionEntity) {
        MemberCouponEntity couponEntity = new MemberCouponEntity();
        couponEntity.setMemberId(memberId);
        couponEntity.setCouponId(promotionEntity.getId());
        couponEntity.setUsageState(PromotionConstants.UsageState.USAGE_NO);
        couponEntity.setStoreId(promotionEntity.getSponsorType());
        couponEntity.setReceiverTime(new Date());
        couponEntity.setCreatorId(memberId);
        couponEntity.setCreatedTime(new Date());
        memberCouponDao.insert(couponEntity);
        if (promotionEntity.getUsedAmount() == null || promotionEntity.getUsedAmount() == 0) {
            promotionEntity.setUsedAmount(1);
        } else {
            promotionEntity.setUsedAmount(promotionEntity.getUsedAmount() + 1);
        }
        promotionDao.updateById(promotionEntity);
    }

    // ***********************************************************************************************************************//

    /**
     * Description 活动商品验证
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     * @throws GlobalException
     */
    @Override
    public void PromotionGoodsException(PromotionGoodsDto promotionGoodsDto) throws GlobalException {
        if (null == promotionGoodsDto.getPromotionId()) {// 营销活动ID
            throw new GlobalException(PromotionExceptionEnum.PROMOTION_ID_NOT_NULL);
        } else if (null == promotionGoodsDto.getRepertoryNum()) {// 商品原库存
            throw new GlobalException(PromotionExceptionEnum.ORIGINAL_STOCK_MUST_NOT_NULL);
        }
    }

    private Long editPromotion(PromotionDto promotionDto) throws ParseException {
        // 当前用户ID(创建人编号)
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        // 开始时间与结束时间
        promotionDto.setStartTime(PromotionUtils.pareTime(promotionDto.getPromotionStart()));
        promotionDto.setEndTime(PromotionUtils.pareTime(promotionDto.getPromotionEnd()));
        PromotionEntity promotion = promotionDao.selectById(promotionDto.getId());
        // 判断优惠条件是否大于条件值
        if (promotion.getTypeId() != PromotionTypeEnum.PROMOTION_TYPE_FREE_SHIPPING.getCode()) {
            if (promotionDto.getDiscountValue().doubleValue() > promotionDto.getCondValue().doubleValue()) {
                throw new GlobalException(PromotionExceptionEnum.PREFERENTIAL_VALUE_SHALL_NOT_EXCEED_CONDITION_VALUE);
            }
        }
        if (promotion.getTypeId().equals(PromotionTypeEnum.PROMOTION_TYPE_COUPON.getCode()) || promotion.getTypeId().equals(PromotionTypeEnum.PROMOTION_TYPE_STORE_COUPON.getCode())) {
            // 正确填写限制数额
            if (promotionDto.getConfineAmount() == null) {
                throw new GlobalException(PromotionExceptionEnum.FILL_IN_THE_LIMIT_CORRECTLY);
            } else if (promotionDto.getConfineAmount() < 0) {
                throw new GlobalException(PromotionExceptionEnum.FILL_IN_THE_LIMIT_CORRECTLY);
            }
        }
        BeanCopyUtil.copy(promotionDto, promotion);
        // 时间限制
        timeLimit(promotion);
        promotion.setLastModifierId(creatorId);
        promotion.setLastModifiedTime(new Date());
        promotionDao.updateById(promotion);
        return promotion.getId();
    }

    /**
     * @param id
     * @Author: tangx.w
     * @Description: 发布店铺优惠券
     * @Date: 2018/4/27 10:07
     */
    @Override
    public void editPromotionStatus(Long id) {
        PromotionEntity promotionEntity = promotionDao.selectById(id);
        if (promotionEntity == null) {
            throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
        }
        String userName = SecurityContextUtils.getCurrentUserDto().getUsername();
        Integer operationType = PromotionConstants.OperationType.TYPE_RELEASE;
        String memo = "";
        PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(userName, operationType, memo, id);
        promotionLogDao.insert(promotionLogEntity);
        //更新商品数据状态
        updateCouponGoodsState(id);
        promotionEntity.setMarketState(PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode());
        promotionDao.updateById(promotionEntity);
    }

    public void updateCouponGoodsState(Long id) {
        EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("promotionId", id);
        entityWrapper.eq("isReleaseData", PromotionConstants.IsRsleaseDate.TYPE_NO);
        List<PromotionGoodsEntity> promotionGoodsList = promotionGoodsDao.selectList(entityWrapper);
        for (PromotionGoodsEntity promotionGoods : promotionGoodsList) {
            promotionGoods.setIsReleaseData(PromotionConstants.IsRsleaseDate.TYPE_YES);
            promotionGoodsDao.updateById(promotionGoods);
        }
    }

    /**
     * @param id
     * @Author: tangx.w
     * @Description: 复制优惠券
     * @Date: 2018/5/2 10:15
     */
    @Override
    public Long copyOfCoupons(Long id) {
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        String userName = SecurityContextUtils.getCurrentUserDto().getUsername();
        Integer operationType = PromotionConstants.OperationType.TYPE_NEWLY_ADDED;
        String memo = "";
        //优惠券复制
        Long promotionId = copyPromotionOfCoupons(id);
        //店铺优惠券商品复制
        copyGoodsOfCoupons(promotionId, id);
        PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(userName, operationType, memo, promotionId);
        promotionLogEntity.setCreatorId(creatorId);
        promotionLogEntity.setCreatedTime(new Date());
        promotionLogDao.insert(promotionLogEntity);
        return promotionId;
    }

    /**
     * @param id
     * @Author: tangx.w
     * @Description: //优惠券复制
     * @Date: 2018/5/2 10:58
     */
    public Long copyPromotionOfCoupons(Long id) {
        PromotionEntity promotion = promotionDao.selectById(id);
        if (promotion != null) {
            Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
            promotion.setCreatorId(creatorId);
            promotion.setCreatedTime(new Date());
            promotion.setMarketState(PromotionStateEnum.PROMOTION_STATE_NOT_RELEASE.getCode());
            promotion.setUsedAmount(0);
            promotion.setId(null);
            promotionDao.insert(promotion);
            return promotion.getId();
        } else {
            throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
        }
    }

    /**
     * @param promotionId id
     * @Author: tangx.w
     * @Description: 店铺优惠券商品复制
     * @Date: 2018/5/2 11:06
     */
    public void copyGoodsOfCoupons(Long promotionId, Long id) {
        EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("promotionId", id);
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        Long creatorId = SecurityContextUtils.getCurrentUserDto().getId();
        List<PromotionGoodsEntity> promotionEntities = promotionGoodsDao.selectList(entityWrapper);
        String username = SecurityContextUtils.getCurrentUserDto().getUsername();
        Integer operationType = PromotionConstants.OperationType.TYPE_NEWLY_ADDED;
        String memo = "";
        PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(username, operationType, memo, promotionId);
        promotionLogDao.insert(promotionLogEntity);
        for (PromotionGoodsEntity promotionGoods : promotionEntities) {
            promotionGoods.setCreatedTime(new Date());
            promotionGoods.setCreatorId(creatorId);
            promotionGoods.setIsReleaseData(PromotionConstants.IsRsleaseDate.TYPE_NO);
            promotionGoods.setPromotionId(promotionId);
            promotionGoods.setId(null);
            promotionGoodsDao.insert(promotionGoods);
        }
    }

    /**
     * @param promotionId,memo
     * @Author: tangx.w
     * @Description: 停止店铺优惠券
     * @Date: 2018/5/2 15:57
     */
    @Override
    public void stopCoupon(Long promotionId, String memo) {
        String username = SecurityContextUtils.getCurrentUserDto().getUsername();
        Integer operationType = PromotionConstants.OperationType.TYPE_STOP;
        PromotionLogEntity promotionLogEntity = PromotionUtils.packagePromotionLog(username, operationType, memo, promotionId);
        promotionLogDao.insert(promotionLogEntity);
        PromotionEntity promotion = promotionDao.selectById(promotionId);
        promotion.setMarketState(PromotionStateEnum.PROMOTION_STATE_TERMINATED.getCode());
        promotionDao.updateById(promotion);
    }

    /**
     * @param promotionId
     * @Author: tangx.w
     * @Description: 查看优惠券操作日志
     * @Date: 2018/5/2 19:53
     */
    @Override
    public List<PromotionLogEntity> getPromotionLogLists(Long promotionId) {
        EntityWrapper<PromotionLogEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.eq("promotionId", promotionId);
        entityEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        return promotionLogDao.selectList(entityEntityWrapper);
    }

    /**
     * @param skuIds,promotionId
     * @Author: tangx.w
     * @Description: 获取圈中商品item的sku
     * @Date: 2018/5/3 14:35
     */
    @Override
    public List<PromotionGoodsEntity> getItemSkuList(String skuIds, Long promotionId) {
        List<Long> skuIdList = getSkuIdList(skuIds);
        EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("goodsSkuId", skuIdList);
        entityWrapper.eq("promotionId", promotionId);
        return promotionGoodsDao.selectList(entityWrapper);

    }

    public List<Long> getSkuIdList(String skuIds) {
        String[] skuId = skuIds.split(",");
        List<Long> skuIdList = new ArrayList<>();
        for (int i = 0; i < skuId.length; i++) {
            skuIdList.add(Long.valueOf(skuId[i]));
        }
        return skuIdList;
    }

    /**
     * @param skuId,promotionId
     * @Author: tangx.w
     * @Description: sku选择/取消选择
     * @Date: 2018/5/4 16:25
     */
    @Override
    public void changeCouponState(Long skuId, Long promotionId) {
        EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("goodsSkuId", skuId);
        entityWrapper.eq("promotionId", promotionId);
        List<PromotionGoodsEntity> PromotionGoodsList = promotionGoodsDao.selectList(entityWrapper);
        PromotionGoodsEntity promotionGoodsEntity = PromotionGoodsList.get(0);
        if (PromotionConstants.DeletedFlag.DELETED_NO.equals(promotionGoodsEntity.getDeletedFlag())) {
            promotionGoodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
            promotionGoodsDao.updateById(promotionGoodsEntity);
        } else {
            promotionGoodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
            promotionGoodsDao.updateById(promotionGoodsEntity);
        }
    }

    /**
     * @param skuIds,promotionId
     * @Author: tangx.w
     * @Description: 获取sku状态
     * @Date: 2018/5/4 16:25
     */
    @Override
    public List<SkuStateDto> getSkuState(String skuIds, Long promotionId) {
        EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("promotionId", promotionId);
        List<PromotionGoodsEntity> promotionGoodsList = promotionGoodsDao.selectList(entityWrapper);
        Map<Long, Byte> map = new HashMap<>();
        Map<Long, Byte> isReleaseMap = new HashMap<>();
        for (PromotionGoodsEntity promotionGoods : promotionGoodsList) {
            map.put(promotionGoods.getGoodsSkuId(), promotionGoods.getDeletedFlag());
            isReleaseMap.put(promotionGoods.getGoodsSkuId(), promotionGoods.getIsReleaseData());
        }
        String[] skuId = skuIds.split(",");
        List<SkuStateDto> list = new ArrayList<>();
        for (int i = 0; i < skuId.length; i++) {
            SkuStateDto skuStateDto = new SkuStateDto();
            skuStateDto.setSkuId(Long.valueOf(skuId[i]));
            skuStateDto.setState(map.get(Long.valueOf(skuId[i])));
            skuStateDto.setIsReleaseData(isReleaseMap.get(Long.valueOf(skuId[i])));
            list.add(skuStateDto);
        }
        return list;
    }

    /**
     * @param id
     * @Author: tangx.w
     * @Description: 发布中的平台优惠券，修改完商品之后的保存
     * @Date: 2018/5/7 19:25
     */
    @Override
    public void save(Long id) {
        EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("promotionId", id);
        entityWrapper.eq("isReleaseData", PromotionConstants.IsRsleaseDate.TYPE_NO);
        List<PromotionGoodsEntity> promotionGoodsList = promotionGoodsDao.selectList(entityWrapper);
        for (PromotionGoodsEntity PromotionGoods : promotionGoodsList) {
            PromotionGoods.setIsReleaseData(PromotionConstants.IsRsleaseDate.TYPE_YES);
            promotionGoodsDao.updateById(PromotionGoods);
        }
    }


    /**
     * @param promotionDto
     * @Author: tangx.w
     * @Description: 获取优惠券列表 商家端typeId=7  平台端typeId=11
     * @Date: 2018/5/7 19:25
     */
    @Override
    public PageInfo<PromotionDto> getCouponsList(PromotionDto promotionDto) throws ParseException {
        PagePO pagePO = new PagePO();
        pagePO.setPageSize(promotionDto.getPageSize());
        pagePO.setPageNo(promotionDto.getPageNo());
        Page<PromotionDto> page = PageDataUtil.buildPageParam(pagePO);
        // 获取当前登陆用户所属店铺
        CurrentUserDto currentUserDto = SecurityContextUtils.getCurrentUserDto();
        if (null != currentUserDto) {
            Long storeId = currentUserDto.getStoreId();
            if (null != storeId) {
                promotionDto.setSponsorType(storeId);
            }
        }
        List<PromotionDto> PromotionList = new ArrayList<>();
        List<PromotionDto> selectPromotionList = promotionDao.selectPromotionList(page, promotionDto);
        for (PromotionDto promotion : selectPromotionList) {
            if (PromotionTypeEnum.PROMOTION_TYPE_STORE_COUPON.getCode().equals(promotionDto.getTypeId())) {
                //获取当前店铺优惠券状态
                promotion = getStoreCouponstate(promotion);
            } else if (PromotionTypeEnum.PROMOTION_TYPE_COUPON.getCode().equals(promotionDto.getTypeId())) {
                //获取平台优惠券的状态
                promotion = getPlatformCouponstate(promotion);
            }
            PromotionList.add(promotion);
        }
        page.setRecords(PromotionList);
        return PageDataUtil.copyPageInfo(page);
    }


    public PromotionDto getStoreCouponstate(PromotionDto promotion) throws ParseException {
        if (PromotionStateEnum.PROMOTION_STATE_RELEASE.getCode().equals(promotion.getMarketState())) {
            ActiveConfigDto activeConfigDto = JSONObject.parseObject(promotion.getActiveConfig(), ActiveConfigDto.class);
            Integer state = PromotionUtils.judgmentStoreTime(activeConfigDto.getReleaseStartTime(), activeConfigDto.getReleaseEndTime());
            promotion.setMarketState(state);
        }
        return promotion;
    }

    public PromotionDto getPlatformCouponstate(PromotionDto promotion) {
        if (PromotionPlatformCouponStateEnum.PROMOTION_STATE_START.getCode().equals(promotion.getMarketState())) {
            Integer state = PromotionUtils.judgmentPlatformTime(promotion.getEndTime());
            promotion.setMarketState(state);
        }
        return promotion;
    }
}