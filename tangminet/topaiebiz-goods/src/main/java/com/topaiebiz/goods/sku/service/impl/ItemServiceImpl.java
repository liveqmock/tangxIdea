package com.topaiebiz.goods.sku.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.brand.dao.BrandDao;
import com.topaiebiz.goods.brand.dao.SuitableAgeDao;
import com.topaiebiz.goods.brand.entity.BrandEntity;
import com.topaiebiz.goods.brand.entity.SuitableAgeEntity;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryAttrDao;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryDao;
import com.topaiebiz.goods.category.backend.dao.BackendMerchantCategoryDao;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryDto;
import com.topaiebiz.goods.category.backend.dto.BackendCategorysDto;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryAttrEntity;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryEntity;
import com.topaiebiz.goods.category.backend.entity.BackendMerchantCategoryEntity;
import com.topaiebiz.goods.category.backend.exception.BackendCategoryExceptionEnum;
import com.topaiebiz.goods.category.frontend.dao.FrontBackCategoryDao;
import com.topaiebiz.goods.category.frontend.dao.FrontendCategoryDao;
import com.topaiebiz.goods.category.frontend.entity.FrontBackCategoryEntity;
import com.topaiebiz.goods.comment.dao.GoodsSkuCommentDao;
import com.topaiebiz.goods.comment.dao.GoodsSkuCommentPictureDao;
import com.topaiebiz.goods.comment.dto.GoodsSkuCommentDto;
import com.topaiebiz.goods.comment.dto.GoodsSkuCommentPictureDto;
import com.topaiebiz.goods.comment.entity.GoodsSkuCommentEntity;
import com.topaiebiz.goods.constants.GoodsConstants;
import com.topaiebiz.goods.constants.ItemConstants;
import com.topaiebiz.goods.goodsenum.BackendCategoryLevelEnum;
import com.topaiebiz.goods.goodsenum.GoodsRedisKey;
import com.topaiebiz.goods.goodsenum.ItemStatusEnum;
import com.topaiebiz.goods.sku.dao.GoodsSkuDao;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.dao.ItemPictureDao;
import com.topaiebiz.goods.sku.dao.ItemUpdownLogDao;
import com.topaiebiz.goods.sku.dto.*;
import com.topaiebiz.goods.sku.dto.app.*;
import com.topaiebiz.goods.sku.entity.GoodsSkuEntity;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import com.topaiebiz.goods.sku.entity.ItemPictureEntity;
import com.topaiebiz.goods.sku.entity.ItemUpdownLogEntity;
import com.topaiebiz.goods.sku.exception.GoodsSkuExceptionEnum;
import com.topaiebiz.goods.sku.service.ItemService;
import com.topaiebiz.goods.sku.util.ItemDetailWatcher;
import com.topaiebiz.goods.sku.util.MessageUtil;
import com.topaiebiz.member.api.MemberApi;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.merchant.api.FreightTemplateApi;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.constants.MerchantConstants;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.merchant.dto.template.FreightTemplateDTO;
import com.topaiebiz.merchant.dto.template.FreightTemplateDetailDTO;
import com.topaiebiz.message.api.TemplateApi;
import com.topaiebiz.promotion.api.PromotionApi;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.dto.PromotionGoodsDTO;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import com.topaiebiz.promotion.util.SaleRateUtil;
import com.topaiebiz.system.util.SecurityContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.topaiebiz.goods.constants.GoodsConstants.GoodsCommentLevel.*;
import static com.topaiebiz.goods.constants.GoodsConstants.GoodsCommentType.FOUR_TYPE;
import static com.topaiebiz.goods.constants.GoodsConstants.GoodsCommentType.ZERO_TYPE;
import static com.topaiebiz.goods.constants.GoodsConstants.GoodsPraiseRatio.*;
import static com.topaiebiz.goods.constants.GoodsConstants.IsImage.YES_IMAGE;

/**
 * Description 商品sku实现类
 * <p>
 * <p>Author Hedda
 * <p>
 * <p>Date 2017年10月3日 下午7:05:58
 * <p>
 * <p>Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * <p>Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Service
public class ItemServiceImpl extends ServiceImpl<ItemDao, ItemEntity> implements ItemService {

    @Autowired
    private ItemDao itemDao;

    /**
     * 商品item图片。
     */
    @Autowired
    private ItemPictureDao itemPictureDao;

    /**
     * 商品item记录上下架。
     */
    @Autowired
    private ItemUpdownLogDao itemUpdownLogDao;

    /**
     * 商品item属性，sku。
     */
    @Autowired
    private GoodsSkuDao goodsSkuDao;

    /**
     * 品牌。
     */
    @Autowired
    private BrandDao brandDao;

    /**
     * 年龄段。
     */
    @Autowired
    private SuitableAgeDao suitableAgeDao;

    /**
     * 后台类目。
     */
    @Autowired
    private BackendCategoryDao backendCategoryDao;

    /**
     * 类目属性。
     */
    @Autowired
    private BackendCategoryAttrDao backendCategoryAttrDao;

    /**
     * 前后台绑定。
     */
    @Autowired
    private FrontBackCategoryDao frontBackCategoryDao;

    /**
     * 前台类目。
     */
    @Autowired
    private FrontendCategoryDao frontendCategoryDao;

    /**
     * 评价。
     */
    @Autowired
    private GoodsSkuCommentDao goodsSkuCommentDao;

    @Autowired
    private PromotionApi promotionApi;

    @Autowired
    private StoreApi storeApi;

    @Autowired
    private GoodsSkuCommentPictureDao goodsSkuCommentPictureDao;

    @Autowired
    private FreightTemplateApi freightTemplateApi;

    @Autowired
    private BackendMerchantCategoryDao backendMerchantCategoryDao;

    @Autowired
    private MemberApi memberApi;

    @Autowired
    private TemplateApi templateApi;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    MessageSender messageSender;

    @Autowired
    private MessageUtil messageUtil;

    @Override
    public PageInfo<ItemDto> getMerchantListItemDto(ItemDto itemDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(itemDto.getPageNo());
        pagePO.setPageSize(itemDto.getPageSize());
        Page<ItemDto> page = PageDataUtil.buildPageParam(pagePO);
        // 获得到店铺的id
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        itemDto.setBelongStore(storeId);
        List<ItemDto> listItemDto = itemDao.selectListItemDto(page, itemDto);
        if (listItemDto != null) {
            for (ItemDto itemDto2 : listItemDto) {
                // 查询每件商品的库存
                Long stockNumber = itemDao.selecStockNumbertById(itemDto2.getId());
                if (stockNumber != null) {
                    itemDto2.setStockNumber(stockNumber);
                }
            }
        }
        page.setRecords(listItemDto);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public PageInfo<ItemDto> getMerchantListStoreItemDto(ItemDto itemDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(itemDto.getPageNo());
        pagePO.setPageSize(itemDto.getPageSize());
        Page<ItemDto> page = PageDataUtil.buildPageParam(pagePO);
        // 获得到店铺的id
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        itemDto.setBelongStore(storeId);
        List<ItemDto> selectListStoreItemDto = itemDao.selectListStoreItemDto(page, itemDto);
        if (selectListStoreItemDto != null) {
            for (ItemDto itemDto2 : selectListStoreItemDto) {
                Long id = itemDto2.getId();
                // 查询每件商品的库存
                Long stockNumber = itemDao.selecStockNumbertById(id);
                if (stockNumber != null) {
                    itemDto2.setStockNumber(stockNumber);
                }
            }
        }
        page.setRecords(selectListStoreItemDto);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public Integer removeItems(Long[] id) throws GlobalException {
        Integer i = 0;
        /** 判断id是否为空 */
        if (id == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        promotionApi.goodsSuspendSales(id);
        /** 判断id是否存在 */
        for (Long long1 : id) {
            ItemEntity itemEntity = new ItemEntity();
            itemEntity.clearInit();
            itemEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            itemEntity.setId(long1);
            ItemEntity item = itemDao.selectOne(itemEntity);
            if (item == null) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
            }
            item.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            item.setLastModifiedTime(new Date());
            item.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
            i = itemDao.updateById(item);
            List<ItemPictureEntity> itemPictures = itemPictureDao.selectItemPicture(long1);
            if (!(itemPictures == null || itemPictures.size() == 0)) {
                for (ItemPictureEntity itemPicture : itemPictures) {
                    itemPicture.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                    itemPicture.setLastModifiedTime(new Date());
                    itemPicture.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
                    i = itemPictureDao.updateById(itemPicture);
                }
            }
            List<GoodsSkuEntity> goodsSkus = goodsSkuDao.selectGoodsSku(long1);
            if (!(goodsSkus == null || goodsSkus.size() == 0)) {
                for (GoodsSkuEntity goodsSku : goodsSkus) {
                    goodsSku.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                    goodsSku.setLastModifiedTime(new Date());
                    goodsSku.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
                    i = goodsSkuDao.updateById(goodsSku);
                }
            }
        }
        // 删除模板内容
        templateApi.removeItem(id);
        i = itemDao.deleteItem(id);
        return i;
    }

    @Override
    public Integer putItems(Long[] id) throws GlobalException {
        Integer i = 0;
        /** 判断id是否为空 */
        if (id == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        promotionApi.goodsSuspendSales(id);
        /** 判断id是否存在 */
        for (Long itemId : id) {
            ItemEntity itemEntity = new ItemEntity();
            itemEntity.clearInit();
            itemEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            itemEntity.setId(itemId);
            ItemEntity item = itemDao.selectOne(itemEntity);
            if (item == null) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
            }
            if (item.getFrozenFlag().equals(ItemConstants.FrozenFlag.YES_FROZEN)) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOOD_FROZEN);
            }
            if (item.getBrokerageRatio() == null) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODS_BROKERAGERATIO_IS_NULL);
            }
            item.setStatus(ItemStatusEnum.ITEM_STATUS_PUTAWAY.getCode());
            item.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            item.setLastModifiedTime(new Date());
            i = itemDao.updateById(item);
            /** 添加上架到商品上下架记录表 */
            ItemUpdownLogEntity itemUpdownLog = new ItemUpdownLogEntity();
            itemUpdownLog.setItemId(itemId);
            itemUpdownLog.setStatus(item.getStatus());
            itemUpdownLog.setCreatedTime(new Date());
            itemUpdownLog.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
            i = itemUpdownLogDao.insert(itemUpdownLog);
            messageUtil.putItem(itemId);
        }
        return i;
    }

    @Override
    public Integer outItems(Long[] id) throws GlobalException {
        Integer i = 0;
        /** 判断id是否为空 */
        if (id == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        List<Long> ids = Arrays.asList(id);
        Boolean b = promotionApi.hasSecKill(ids);
        if (b) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODS_SECKILL_EXIST);
        }
        promotionApi.goodsSuspendSales(id);
        /** 判断id是否存在 */
        for (Long itemId : id) {
            ItemEntity item = new ItemEntity();
            item.clearInit();
            item.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            item.setId(itemId);
            ItemEntity itemEntity = itemDao.selectOne(item);
            if (itemEntity == null) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
            }
            itemEntity.setStatus(ItemStatusEnum.ITEM_STATUS_REMOVE.getCode());
            itemEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            itemEntity.setLastModifiedTime(new Date());
            i = itemDao.updateById(itemEntity);
            /** 添加下架到商品上下架记录表 */
            ItemUpdownLogEntity itemUpdownLog = new ItemUpdownLogEntity();
            itemUpdownLog.setItemId(itemId);
            itemUpdownLog.setStatus(itemEntity.getStatus());
            itemUpdownLog.setCreatedTime(new Date());
            itemUpdownLog.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
            i = itemUpdownLogDao.insert(itemUpdownLog);
            messageUtil.outItem(itemId);

        }
        // 删除模板内容
        templateApi.removeItem(id);
        return i;
    }

    @Override
    public Integer saveMerchantItem(
            ItemDto itemDto, List<ItemPictureDto> itemPictureDtos, List<GoodsSkuDto> goodsSkuDtos)
            throws GlobalException {
        ItemEntity item = new ItemEntity();
        BeanCopyUtil.copy(itemDto, item);
        List<ItemPictureEntity> itemPictureEntities = new ArrayList<ItemPictureEntity>();
        /** 复制商品图片信息 */
        if (!(itemPictureDtos == null || itemPictureDtos.size() == 0)) {
            if (itemPictureDtos.size() < 1) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODSPICTURE_NAME_NOT_NULL);
            }
            for (ItemPictureDto itemPictureDto : itemPictureDtos) {
                ItemPictureEntity itemPicture = new ItemPictureEntity();
                BeanCopyUtil.copy(itemPictureDto, itemPicture);
                itemPictureEntities.add(itemPicture);
            }
        }
        List<GoodsSkuEntity> goodsSkuEntities = new ArrayList<GoodsSkuEntity>();
        /** 复制商品sku信息 */
        if (!(goodsSkuDtos == null || goodsSkuDtos.size() == 0)) {
            for (GoodsSkuDto goodsSkuDto : goodsSkuDtos) {
                if (goodsSkuDto.getPrice() == null) {
                    throw new GlobalException(GoodsSkuExceptionEnum.GOODSSKU_PRICE_NOT_NULL);
                }
                if (goodsSkuDto.getSaleImage() == null) {
                    throw new GlobalException(GoodsSkuExceptionEnum.SKU_IMAGE);
                }
                GoodsSkuEntity goodsSku = new GoodsSkuEntity();
                BeanCopyUtil.copy(goodsSkuDto, goodsSku);
                goodsSkuEntities.add(goodsSku);
            }
        }
        Integer i = 0;
        // 获得到店铺的id
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        item.setBelongStore(storeId);
        item.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        item.setCreatedTime(new Date());
        item.setFrozenFlag(ItemConstants.FrozenFlag.NO_FROZEN);
        getBrokerageRatio(item, merchantId);
        i = itemDao.insert(item);
        // 自定义类目属性id
        List<Long> attrIds = itemDto.getAttrIds();
        if (CollectionUtils.isNotEmpty(attrIds)) {
            EntityWrapper<BackendCategoryAttrEntity> backendCategoryAttrs = new EntityWrapper<>();
            backendCategoryAttrs.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            backendCategoryAttrs.in("id", attrIds);
            List<BackendCategoryAttrEntity> backendCategoryAttrEntities =
                    backendCategoryAttrDao.selectList(backendCategoryAttrs);
            if (CollectionUtils.isNotEmpty(backendCategoryAttrEntities)) {
                for (BackendCategoryAttrEntity backendCategoryAttrEntity : backendCategoryAttrEntities) {
                    backendCategoryAttrEntity.setStoreCustom(item.getId());
                    backendCategoryAttrDao.updateById(backendCategoryAttrEntity);
                }
            }
        }

        Long itemId = item.getId();
        /** 添加商品上下架记录表 */
        ItemUpdownLogEntity itemUpdownLog = new ItemUpdownLogEntity();
        itemUpdownLog.setItemId(itemId);
        itemUpdownLog.setStatus(item.getStatus());
        itemUpdownLog.setCreatedTime(new Date());
        itemUpdownLog.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        itemUpdownLogDao.insert(itemUpdownLog);
        StoreInfoDetailDTO store = storeApi.getStore(storeId);
        if (!(goodsSkuEntities == null || goodsSkuEntities.size() == 0)) {
            for (GoodsSkuEntity goodsSkuEntity : goodsSkuEntities) {
                /** 添加积分支付比例 */
                if (store.getPtRate() != null) {
                    goodsSkuEntity.setScoreRate(store.getPtRate());
                }
                goodsSkuEntity.setItemId(itemId);
                goodsSkuEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
                goodsSkuEntity.setCreatedTime(new Date());
                i = goodsSkuDao.insert(goodsSkuEntity);
            }
        }
        String mainPic = null;
        if (!(itemPictureEntities == null || itemPictureEntities.size() == 0)) {
            for (ItemPictureEntity itemPictureEntity : itemPictureEntities) {
                if (itemPictureEntity.isMainPic() && StringUtils.isBlank(mainPic)) {
                    mainPic = itemPictureEntity.getName();
                }
                /** 添加商品图片信息 */
                itemPictureEntity.setItemId(itemId);
                itemPictureEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
                itemPictureEntity.setCreatedTime(new Date());
                i = itemPictureDao.insert(itemPictureEntity);
            }
        }
        ItemEntity itemUpdate = new ItemEntity();
        itemUpdate.cleanInit();
        itemUpdate.setId(item.getId());
        ItemEntity itemEntity = itemDao.selectOne(itemUpdate);
        if (mainPic != null) {
            itemEntity.setPictureName(mainPic);
        }
        itemDao.updateById(itemEntity);
        return i;
    }

    private void getBrokerageRatio(ItemEntity item, Long merchantId) {
        BackendMerchantCategoryEntity backendMerchantCategoryEntity = new BackendMerchantCategoryEntity();
        backendMerchantCategoryEntity.cleanInit();
        backendMerchantCategoryEntity.setMerchantId(merchantId);
        backendMerchantCategoryEntity.setCategoryId(item.getBelongCategory());
        backendMerchantCategoryEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        BackendMerchantCategoryEntity backendMerchantCategory =
                backendMerchantCategoryDao.selectOne(backendMerchantCategoryEntity);
        if (backendMerchantCategory != null) {
            if (backendMerchantCategory.getBrokerageRatio() == null && item.getStatus().equals(ItemStatusEnum.ITEM_STATUS_PUTAWAY.getCode())) {
                throw new GlobalException(GoodsSkuExceptionEnum.CATEGORY_BROKERAGERATIO_IS_NULL);
            }
            item.setBrokerageRatio(backendMerchantCategory.getBrokerageRatio());
        }
    }

    @Override
    public Integer modifyItem(
            ItemDto itemDto, List<ItemPictureDto> itemPictureDtos, List<GoodsSkuDto> goodsSkuDtos)
            throws GlobalException {
        Integer i = 0;
        ItemEntity item = itemDao.selectById(itemDto.getId());
        itemDto.setStatus(item.getStatus());
        if (StringUtils.isBlank(itemDto.getItemCode())) {
            itemDto.setItemCode(" ");
        }
        BeanCopyUtil.copy(itemDto, item);
        item.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        item.setLastModifiedTime(new Date());
        i = itemDao.updateById(item);
        // 自定义类目属性id
        List<Long> attrIds = itemDto.getAttrIds();
        if (CollectionUtils.isNotEmpty(attrIds)) {
            EntityWrapper<BackendCategoryAttrEntity> backendCategoryAttrs = new EntityWrapper<>();
            backendCategoryAttrs.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            backendCategoryAttrs.in("id", attrIds);
            List<BackendCategoryAttrEntity> backendCategoryAttrEntities =
                    backendCategoryAttrDao.selectList(backendCategoryAttrs);
            if (CollectionUtils.isNotEmpty(backendCategoryAttrEntities)) {
                for (BackendCategoryAttrEntity backendCategoryAttrEntity : backendCategoryAttrEntities) {
                    backendCategoryAttrEntity.setStoreCustom(item.getId());
                    backendCategoryAttrDao.updateById(backendCategoryAttrEntity);
                }
            }
        }
        Long itemId = item.getId();
        /** 添加商品上下架记录表 */
        ItemUpdownLogEntity itemUpdownLog = new ItemUpdownLogEntity();
        itemUpdownLog.setItemId(item.getId());
        itemUpdownLog.setStatus(item.getStatus());
        itemUpdownLog.setCreatedTime(new Date());
        itemUpdownLog.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        itemUpdownLogDao.insert(itemUpdownLog);
        /** 删除商品图片信息 */
        List<ItemPictureDto> itemPictureById = itemPictureDao.selectItemPictureById(item.getId());
        if (!(itemPictureById == null || itemPictureById.size() == 0)) {
            for (ItemPictureDto itemPictureDto : itemPictureById) {
                itemPictureDao.deleteItemPicture(itemPictureDto.getId());
            }
        }
        String mainPic = null;
        if (!(itemPictureDtos == null || itemPictureDtos.size() == 0)) {
            if (itemPictureDtos.size() < 1) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODSPICTURE_NAME_NOT_NULL);
            }
            for (ItemPictureDto itemPictureDto : itemPictureDtos) {
                /** 修改商品图片信息 */
                ItemPictureEntity itemPictureEntity = new ItemPictureEntity();
                BeanCopyUtil.copy(itemPictureDto, itemPictureEntity);
                if (itemPictureEntity.isMainPic() && StringUtils.isBlank(mainPic)) {
                    mainPic = itemPictureEntity.getName();
                }
                itemPictureEntity.setItemId(itemId);
                itemPictureEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
                itemPictureEntity.setCreatedTime(new Date());
                i = itemPictureDao.insert(itemPictureEntity);
            }
        }
        EntityWrapper<GoodsSkuEntity> cond = new EntityWrapper<>();
        cond.eq("itemId", item.getId());
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<GoodsSkuEntity> goodsSkuEntityList = goodsSkuDao.selectList(cond);
        if (CollectionUtils.isNotEmpty(goodsSkuEntityList)) {
            for (GoodsSkuEntity goodsSkuEntity : goodsSkuEntityList) {
                if (goodsSkuEntity.getSaleFieldValue() == null) {
                    goodsSkuEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                    goodsSkuDao.updateById(goodsSkuEntity);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(goodsSkuDtos)) {
            for (GoodsSkuDto goodsSkuDto : goodsSkuDtos) {
                if (goodsSkuDto.getPrice() == null) {
                    throw new GlobalException(GoodsSkuExceptionEnum.GOODSSKU_PRICE_NOT_NULL);
                }
                /** 修改商品sku信息 */
                GoodsSkuEntity goodsSkuEntity = new GoodsSkuEntity();
                BeanCopyUtil.copy(goodsSkuDto, goodsSkuEntity);
                if (goodsSkuEntity.getId() == null) {
                    goodsSkuEntity.setItemId(itemId);
                    goodsSkuEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
                    goodsSkuEntity.setCreatedTime(new Date());
                    i = goodsSkuDao.insert(goodsSkuEntity);
                } else {
                    goodsSkuEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
                    goodsSkuEntity.setLastModifiedTime(new Date());
                    goodsSkuEntity.setVersion(null);
                    i = goodsSkuDao.updateById(goodsSkuEntity);
                }
            }
        }
        ItemEntity itemUpdate = new ItemEntity();
        itemUpdate.cleanInit();
        itemUpdate.setId(item.getId());
        ItemEntity itemEntity = itemDao.selectOne(itemUpdate);
        if (mainPic != null) {
            itemEntity.setPictureName(mainPic);
        }
        itemDao.updateById(itemEntity);
        return i;
    }

    @Override
    public ItemDto findItemById(Long id) throws GlobalException {
        /** 判断id是否为空 */
        if (id == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        /** 判断id是否存在 */
        ItemEntity item = itemDao.selectById(id);
        if (item == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
        }
        ItemDto itemDto = itemDao.selectItemById(id);
        BrandEntity brandEntity = brandDao.selectById(itemDto.getBelongBrand());
        if (brandEntity != null) {
            itemDto.setBrandName(brandEntity.getName());
        }
        SuitableAgeEntity suitableAgeEntity = suitableAgeDao.selectById(itemDto.getAgeId());
        if (suitableAgeEntity != null) {
            itemDto.setAgeGroup(suitableAgeEntity.getAgeGroup());
        }

        BackendCategoryEntity backendCategoryEntity =
                backendCategoryDao.selectById(itemDto.getBelongCategory());
        itemDto.setCategoryName(backendCategoryEntity.getName());
        FreightTemplateDTO freightTemplateDTO =
                freightTemplateApi.getFreighTemplateDTO(itemDto.getLogisticsId());
        itemDto.setFreightTemplateName(freightTemplateDTO.getFreightName());
        List<BackendCategoryDto> backendCategoryDtos = new ArrayList<BackendCategoryDto>();
        // 返回三级类目
        if (itemDto.getBelongCategory() != null) {

            BackendCategoryDto backendCategory3 =
                    backendCategoryDao.selectBackendCategoryById(itemDto.getBelongCategory());
            if (backendCategory3 == null) {
                throw new GlobalException(GoodsSkuExceptionEnum.NONEXISTENCE);
            }
            BackendCategoryDto backendCategory2 =
                    backendCategoryDao.selectBackendCategoryById(backendCategory3.getParentId());
            BackendCategoryDto backendCategory1 =
                    backendCategoryDao.selectBackendCategoryById(backendCategory2.getParentId());
            backendCategoryDtos.add(backendCategory1);
            backendCategoryDtos.add(backendCategory2);
            backendCategoryDtos.add(backendCategory3);
        }
        itemDto.setBackendCategoryDtos(backendCategoryDtos);
        return itemDto;
    }

    @Override
    public List<GoodsSkuDto> findGoodsSkuById(Long id) throws GlobalException {
        /** 判断id是否为空 */
        if (id == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        /** 判断id是否存在 */
        ItemEntity item = itemDao.selectById(id);
        if (item == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
        }
        ItemDto itemDto = itemDao.selectItemById(id);
        List<GoodsSkuDto> goodsSkuDtos = goodsSkuDao.selectGoodsSkuById(id);
        if (!(goodsSkuDtos == null || goodsSkuDtos.size() == 0)) {
            for (GoodsSkuDto goodsSkuDto : goodsSkuDtos) {
                // 取出每个销售属性
                String saleFieldValue = goodsSkuDto.getSaleFieldValue();
                if (saleFieldValue != null) {
                    goodsSkuDto.setSaleFieldValue(loadSaleFieldValue(saleFieldValue));
                    String[] strs = saleFieldValue.split(",");
                    // 创建销售属性集合
                    List<GoodsSkuSaleDto> goodsSkuSaleDtos = new ArrayList<GoodsSkuSaleDto>();
                    // 创建销售属性key集合
                    List<GoodsSkuSaleKeyDto> goodsSkuSaleKeyDtos = new ArrayList<GoodsSkuSaleKeyDto>();
                    // 创建销售属性value集合
                    List<GoodsSkuSaleValueDto> goodsSkuSaleValueDtos = new ArrayList<GoodsSkuSaleValueDto>();
                    // 创建销售属性中的键与值得集合
                    List<GoodsSkuSaleKeyAndValueDto> goodsSkuSaleKeyAndValueDtos =
                            new ArrayList<GoodsSkuSaleKeyAndValueDto>();
                    for (int i = 0; i < strs.length; i++) {
                        String[] strs1 = strs[i].split(":");
                        // 创建销售属性对象
                        GoodsSkuSaleDto goodsSkuSaleDto = new GoodsSkuSaleDto();
                        // 创建销售属性key对象
                        GoodsSkuSaleKeyDto goodsSkuSaleKeyDto = new GoodsSkuSaleKeyDto();
                        // 创建销售属性value对象
                        GoodsSkuSaleValueDto goodsSkuSaleValueDto = new GoodsSkuSaleValueDto();
                        // 创建销售属性中的键与值得对象
                        GoodsSkuSaleKeyAndValueDto goodsSkuSaleKeyAndValueDto =
                                new GoodsSkuSaleKeyAndValueDto();
                        // 创建销售属性中的值得集合
                        List<GoodsSkuSaleValueDto> goodsSkuSaleValueDtos2 =
                                new ArrayList<GoodsSkuSaleValueDto>();
                        // 创建销售属性中的值得对象
                        GoodsSkuSaleValueDto goodsSkuSaleValueDto2 = new GoodsSkuSaleValueDto();
                        // 保存key值
                        BackendCategoryAttrEntity backendCategoryAttrEntity =
                                backendCategoryAttrDao.selectById(strs1[0]);
                        goodsSkuSaleDto.setId(strs1[0]);
                        goodsSkuSaleDto.setKeyName(backendCategoryAttrEntity.getName());
                        goodsSkuSaleKeyDto.setKeyName(backendCategoryAttrEntity.getName());
                        goodsSkuSaleKeyAndValueDto.setSaleId(strs1[0]);
                        Long imageField = itemDto.getImageField();
                        // 判断是否上传sku图片
                        if (imageField != null) {
                            String imageFileld = Long.toString(imageField);
                            if (imageFileld.equals(goodsSkuSaleKeyAndValueDto.getSaleId())) {
                                goodsSkuSaleValueDto2.setImageurl(goodsSkuDto.getSaleImage());
                                goodsSkuSaleKeyAndValueDto.setImageField(imageField);
                            }
                        }
                        // 保存value值
                        goodsSkuSaleDto.setSaleName(strs1[1]);
                        goodsSkuSaleValueDto.setValueName(strs1[1]);
                        goodsSkuSaleValueDto2.setValueName(strs1[1]);
                        // 保存对象
                        goodsSkuSaleDtos.add(goodsSkuSaleDto);
                        goodsSkuSaleKeyDtos.add(goodsSkuSaleKeyDto);
                        goodsSkuSaleValueDtos.add(goodsSkuSaleValueDto);
                        goodsSkuSaleValueDtos2.add(goodsSkuSaleValueDto2);
                        goodsSkuSaleKeyAndValueDto.setGoodsSkuSaleValueDto(goodsSkuSaleValueDtos2);
                        goodsSkuSaleKeyAndValueDtos.add(goodsSkuSaleKeyAndValueDto);
                        // 保存goodsSkuDto集合
                        goodsSkuDto.setGoodsSkuSaleDto(goodsSkuSaleDtos);
                        goodsSkuDto.setGoodsSkuSaleKeyDto(goodsSkuSaleKeyDtos);
                        goodsSkuDto.setGoodsSkuSaleValueDto(goodsSkuSaleValueDtos);
                        goodsSkuDto.setGoodsSkuSaleKeyAndValueDtos(goodsSkuSaleKeyAndValueDtos);
                    }
                }
                // 取出每个基本属性
                String baseFieldValue = goodsSkuDto.getBaseFieldValue();
                if (baseFieldValue != null) {
                    String[] strss = baseFieldValue.split(",");
                    // 创建基本属性集合
                    List<GoodsSkuBaseDto> goodsSkuBaseDtos = new ArrayList<GoodsSkuBaseDto>();
                    for (int i = 0; i < strss.length; i++) {
                        String[] strss1 = strss[i].split(":");
                        GoodsSkuBaseDto goodsSkuBaseDto = new GoodsSkuBaseDto();
                        goodsSkuBaseDto.setId(strss1[0]);
                        goodsSkuBaseDto.setBaseName(strss1[1]);
                        goodsSkuBaseDtos.add(goodsSkuBaseDto);
                        goodsSkuDto.setGoodsSkuBaseDto(goodsSkuBaseDtos);
                    }
                }
            }
        }
        return goodsSkuDtos;
    }

    @Override
    public List<ItemPictureDto> findItemPictureById(Long id) throws GlobalException {
        /** 判断id是否为空 */
        if (id == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        /** 判断id是否存在 */
        ItemEntity item = itemDao.selectById(id);
        if (item == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
        }
        List<ItemPictureDto> itemPictureDtos = itemPictureDao.selectItemPictureById(id);
        return itemPictureDtos;
    }

    @Override
    public PageInfo<ItemDto> getDecorateItem(ItemDto itemDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(itemDto.getPageNo());
        pagePO.setPageSize(itemDto.getPageSize());
        Page<ItemDto> page = PageDataUtil.buildPageParam(pagePO);
        // 获得到店铺的id
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        EntityWrapper<ItemEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("status", ItemStatusEnum.ITEM_STATUS_PUTAWAY.getCode());
        cond.eq("frozenFlag", ItemConstants.FrozenFlag.NO_FROZEN);
        cond.eq("belongStore", storeId);
        if (itemDto.getId() != null) {
            cond.eq("id", itemDto.getId());
        }
        if (itemDto.getName() != null) {
            cond.like("name", itemDto.getName());
        }
        List<ItemEntity> itemEntities = itemDao.selectPage(page, cond);
        if (CollectionUtils.isEmpty(itemEntities)) {
            return PageDataUtil.copyPageInfo(page);
        }
        List<ItemDto> listItemDto = new ArrayList<>();

        for (ItemEntity itemEntity : itemEntities) {
            ItemDto item = new ItemDto();
            BeanCopyUtil.copy(itemEntity, item);
            // 查询每件商品的库存
            Long stockNumber = itemDao.selecStockNumbertById(item.getId());
            if (stockNumber != null) {
                item.setStockNumber(stockNumber);
            }
            listItemDto.add(item);
        }
        page.setRecords(listItemDto);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public Integer freezeItem(Long[] id) throws GlobalException {
        Integer i = 0;
        /** 判断id是否为空 */
        if (id == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        promotionApi.goodsSuspendSales(id);
        for (Long itemId : id) {
            /** 判断id是否存在 */
            ItemEntity item = new ItemEntity();
            item.clearInit();
            item.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            item.setId(itemId);
            ItemEntity itemEntity = itemDao.selectOne(item);
            if (itemEntity == null) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
            }
            itemEntity.setFrozenFlag(ItemConstants.FrozenFlag.YES_FROZEN);
            itemEntity.setLastModifiedTime(new Date());
            itemEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            i = itemDao.updateById(itemEntity);
            messageUtil.outItem(itemId);
        }
        templateApi.removeItem(id);
        return i;
    }

    @Override
    public Integer unFreezeItem(Long[] id) throws GlobalException {
        Integer i = 0;
        /** 判断id是否为空 */
        if (id == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        for (Long itemId : id) {
            /** 判断id是否存在 */
            ItemEntity itemEntity = new ItemEntity();
            itemEntity.clearInit();
            itemEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            itemEntity.setId(itemId);
            ItemEntity item = itemDao.selectOne(itemEntity);
            if (item == null) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
            }
            item.setFrozenFlag(ItemConstants.FrozenFlag.NO_FROZEN);
            item.setLastModifiedTime(new Date());
            item.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            i = itemDao.updateById(item);
            messageUtil.putItem(itemId);
        }
        return i;
    }

    @Override
    public PageInfo<ItemDto> getListMerchantItemDto(ItemDto itemDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(itemDto.getPageNo());
        pagePO.setPageSize(itemDto.getPageSize());
        Page<ItemDto> page = PageDataUtil.buildPageParam(pagePO);
        if (StringUtils.isNotBlank(itemDto.getStoreName())) {
            List<StoreInfoDetailDTO> storeInfoList = storeApi.queryStores(itemDto.getStoreName());
            if (CollectionUtils.isEmpty(storeInfoList)) {
                return PageDataUtil.copyPageInfo(page);
            }
            List<Long> stroreIds = storeInfoList.stream().map(s -> s.getId()).collect(Collectors.toList());
            itemDto.setStoreIds(stroreIds);
        }
        List<ItemDto> storeGoodsList = itemDao.selectListMerchantItemDto(page, itemDto);
        if (CollectionUtils.isEmpty(storeGoodsList)) {
            return PageDataUtil.copyPageInfo(page);
        }
        storeGoodsList = getStoreName(storeGoodsList); // 获取店铺名称
        storeGoodsList = getCategoryName(storeGoodsList); // 获取类目名称
        // 获取库存
        for (ItemDto itemDto2 : storeGoodsList) {
            // 查询每件商品的库存
            Long stockNumber = itemDao.selecStockNumbertById(itemDto2.getId());
            if (stockNumber != null) {
                itemDto2.setStockNumber(stockNumber);
            }
        }
        page.setRecords(storeGoodsList);
        return PageDataUtil.copyPageInfo(page);
    }

    public List<ItemDto> getStoreName(List<ItemDto> itemDtos) {
        return itemDtos.stream().map(itemDto -> {
            StoreInfoDetailDTO storeInfoDetail = storeApi.getStore(itemDto.getBelongStore());
            if (storeInfoDetail != null) {
                itemDto.setStoreName(storeInfoDetail.getName());
            }
            return itemDto;
        }).collect(Collectors.toList());
    }

    public List<ItemDto> getCategoryName(List<ItemDto> itemDtos) {
        return itemDtos.stream().map(itemDto -> {
            BackendCategoryEntity backendCategory = backendCategoryDao.selectById(itemDto.getBelongCategory());
            if (backendCategory != null) {
                itemDto.setCategoryName(backendCategory.getName());
            }
            return itemDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<BackendCategorysDto> getItemRecentlyCategoryList() {
        List<BackendCategorysDto> backendCategorysListDto = new ArrayList<BackendCategorysDto>();
        /** 创建一个新list集合。 */
        List<BackendCategoryDto> resultList = new ArrayList<BackendCategoryDto>();
        /** 查询出最近商品使用后台类目。 */
        List<BackendCategoryDto> itemRecentlyCategoryList =
                backendCategoryDao.selectItemRecentlyCategoryList();
        if (!(itemRecentlyCategoryList == null || itemRecentlyCategoryList.size() == 0)) {
            for (BackendCategoryDto backendCategoryDto : itemRecentlyCategoryList) {
                /** 判断是否包含本后台类目。 */
                if (!isContain(resultList, backendCategoryDto)) {
                    resultList.add(backendCategoryDto);
                }
                /** 判断resultList长度是否为10 */
                if (resultList.size() == 10) {
                    break;
                }
            }
        }
        // 查询出第三级类目的前两级类目
        if (!(resultList == null || resultList.size() == 0)) {
            for (BackendCategoryDto backendCategoryDto : resultList) {
                // 创建一个BackendCategoryDto集合
                List<BackendCategoryDto> reaultMsg = new ArrayList<BackendCategoryDto>();
                // 第三级parentId查询第二级类目
                BackendCategoryDto twobackendCategoryDto =
                        backendCategoryDao.selectTwoBackendCategoryDtoByParentId(
                                backendCategoryDto.getParentId());
                // 第二级parentId查询第一级类目
                BackendCategoryDto onebackendCategoryDto =
                        backendCategoryDao.selectOneBackendCategoryDtoByParentId(
                                twobackendCategoryDto.getParentId());
                // 将一二三级类目添加到BackendCategoryDto集合
                reaultMsg.add(onebackendCategoryDto);
                reaultMsg.add(twobackendCategoryDto);
                reaultMsg.add(backendCategoryDto);
                // 创建backendCategorysDto对象
                BackendCategorysDto backendCategorysDto = new BackendCategorysDto();
                backendCategorysDto.setBackendCategoryDto(reaultMsg);
                backendCategorysListDto.add(backendCategorysDto);
            }
        }
        return backendCategorysListDto;
    }

    @Override
    public List<BackendCategorysDto> getMerchantItemRecentlyCategoryList() {
        // 根据商家id查询商家对应的类目
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        List<BackendCategoryDto> merchantBackendCategory = getMerchantBackendCategory(merchantId);
        // 查询出最近商家商品使用后台类目
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        List<BackendCategoryDto> resultList = new ArrayList<>();
        List<BackendCategoryDto> itemRecentlyCategoryList = backendCategoryDao.selectMerchantItemRecentlyCategoryList(storeId);
        if (CollectionUtils.isEmpty(itemRecentlyCategoryList)) {
            return Collections.EMPTY_LIST;
        }
        for (BackendCategoryDto backendCategoryDto : itemRecentlyCategoryList) {
            /** 判断是否包含本后台类目。 */
            if (!isContain(resultList, backendCategoryDto)) {
                resultList.add(backendCategoryDto);
            }
            /** 判断resultList长度是否为10 */
            if (resultList.size() == 10) {
                break;
            }
        }
        List<BackendCategoryDto> backendCategoryDtos = new ArrayList<>();
        for (BackendCategoryDto b : merchantBackendCategory) {
            if (isContain(resultList, b)) {
                backendCategoryDtos.add(b);
            }
        }
        List<BackendCategorysDto> backendCategorysListDto = new ArrayList<>();
        // 查询出第三级类目的前两级类目
        for (BackendCategoryDto backendCategoryDto : backendCategoryDtos) {
            // 创建一个BackendCategoryDto集合
            List<BackendCategoryDto> reaultMsg = new ArrayList<>();
            // 第三级parentId查询第二级类目
            BackendCategoryDto twobackendCategoryDto = getBackendCategory(backendCategoryDto);
            // 第二级parentId查询第一级类目
            BackendCategoryDto onebackendCategoryDto = getBackendCategory(twobackendCategoryDto);
            // 将一二三级类目添加到BackendCategoryDto集合
            reaultMsg.add(onebackendCategoryDto);
            reaultMsg.add(twobackendCategoryDto);
            reaultMsg.add(backendCategoryDto);
            // 创建backendCategorysDto对象
            BackendCategorysDto backendCategorysDto = new BackendCategorysDto();
            backendCategorysDto.setBackendCategoryDto(reaultMsg);
            backendCategorysListDto.add(backendCategorysDto);
        }
        return backendCategorysListDto;
    }

    public BackendCategoryDto getBackendCategory(BackendCategoryDto backendCategoryDto) {
        BackendCategoryEntity cond = new BackendCategoryEntity();
        cond.clearInit();
        cond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        cond.setId(backendCategoryDto.getParentId());
        BackendCategoryEntity backendCategoryEntity = backendCategoryDao.selectOne(cond);
        BackendCategoryDto backendCategory = new BackendCategoryDto();
        BeanCopyUtil.copy(backendCategoryEntity, backendCategory);
        return backendCategory;
    }

    public List<BackendCategoryDto> getMerchantBackendCategory(Long merchantId) {
        EntityWrapper<BackendMerchantCategoryEntity> cond = new EntityWrapper<>();
        cond.eq("merchantId", merchantId);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<BackendMerchantCategoryEntity> backendMerchantCategoryEntities = backendMerchantCategoryDao.selectList(cond);
        if (CollectionUtils.isEmpty(backendMerchantCategoryEntities)) {
            return Collections.EMPTY_LIST;
        }
        List<Long> categoryIds = backendMerchantCategoryEntities.stream().map(backendMerchantCategoryEntity ->
                backendMerchantCategoryEntity.getCategoryId()).collect(Collectors.toList());
        EntityWrapper<BackendCategoryEntity> condBackend = new EntityWrapper<>();
        condBackend.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condBackend.in("id", categoryIds);
        condBackend.eq("level", BackendCategoryLevelEnum.BACKEND_LEVEL_THERE.getCode());
        List<BackendCategoryEntity> backendCategoryEntities = backendCategoryDao.selectList(condBackend);
        List<BackendCategoryDto> categoryDtos = new ArrayList<>();
        for (BackendCategoryEntity backendCategoryEntity : backendCategoryEntities) {
            BackendCategoryDto backendCategoryDto = new BackendCategoryDto();
            BeanCopyUtil.copy(backendCategoryEntity, backendCategoryDto);
            categoryDtos.add(backendCategoryDto);
        }
        return categoryDtos;
    }

    @Override
    public Integer modifyGoodsSkuStockNumber(Long skuId, Long salesNumber) throws GlobalException {
        /** 判断商品skuId是否为空 */
        if (skuId == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        /** 判断商品skuId是否存在 */
        GoodsSkuEntity goodsSku = goodsSkuDao.selectGoodsSkuBySkuId(skuId);
        if (goodsSku == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
        }
        /** 判断库存是否充足 */
        if (goodsSku.getStockNumber() < salesNumber) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_STOCKNUMBER_DEFICIENCY);
        }
        /** 计算库存 */
        goodsSku.setStockNumber(goodsSku.getStockNumber() - salesNumber);
        goodsSku.setLastModifiedTime(new Date());
        goodsSku.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        return goodsSkuDao.updateById(goodsSku);
    }

    @Override
    public Integer addGoodsSkuStockNumber(Long memberId, Long skuId, Long salesNumber)
            throws GlobalException {
        /** 判断商品skuId是否为空 */
        if (skuId == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        /** 判断商品skuId是否存在 */
        GoodsSkuEntity goodsSku = goodsSkuDao.selectGoodsSkuBySkuId(skuId);
        if (goodsSku == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
        }
        /** 计算库存 */
        goodsSku.setStockNumber(goodsSku.getStockNumber() + salesNumber);
        goodsSku.setLastModifiedTime(new Date());
        goodsSku.setLastModifierId(memberId);
        return goodsSkuDao.updateById(goodsSku);
    }

    @Override
    public ItemDto findMerchantItemById(Long id) throws GlobalException {
        /** 判断id是否为空 */
        if (id == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        /** 判断id是否存在 */
        ItemEntity item = itemDao.selectById(id);
        if (item == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
        }
        ItemDto itemDto = itemDao.selectMerchantItemById(id);
        return itemDto;
    }

    @Override
    public PageInfo<ItemCustomerDto> getGoodsList(ItemCustomerDto itemCustomerDto)
            throws GlobalException {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(itemCustomerDto.getPageNo());
        pagePO.setPageSize(itemCustomerDto.getPageSize());
        Page<ItemCustomerDto> page = PageDataUtil.buildPageParam(pagePO);
        // 根据前台类目id查询出对应的后台类目
        if (itemCustomerDto.getFrontendCategory() != null) {
            EntityWrapper<FrontBackCategoryEntity> frontBackCategory = new EntityWrapper<>();
            frontBackCategory.eq("frontId", itemCustomerDto.getFrontendCategory());
            frontBackCategory.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            List<FrontBackCategoryEntity> frontBackCategoryEntities = frontBackCategoryDao.selectList(frontBackCategory);
            if (CollectionUtils.isNotEmpty(frontBackCategoryEntities)) {
                List<Long> backIds = frontBackCategoryEntities.stream().map(frontBackCategoryEntity -> frontBackCategoryEntity.getBackId()).collect(Collectors.toList());
                itemCustomerDto.setBelongCategoryIds(backIds);
            } else {
                return PageDataUtil.copyPageInfo(page);
            }
        }
        List<Long> frozenStores = storeApi.getFrozenStoreIds();
        if (CollectionUtils.isNotEmpty(frozenStores)) {
            itemCustomerDto.setStoreIds(frozenStores);
        }
        List<ItemCustomerDto> itemDtoList = itemDao.selectGoodsList(itemCustomerDto, page);
        if (CollectionUtils.isNotEmpty(itemDtoList)){
            for (ItemCustomerDto itemDto : itemDtoList){
                itemDto.setDefaultPrice(itemDto.getMinPrice());
            }
        }
        removeItemCustomer(itemDtoList);
        page.setRecords(itemDtoList);
        return PageDataUtil.copyPageInfo(page);
    }

    /**
     * 移除库存为0 的商品
     *
     * @param itemDtoList
     */
    private void removeItemCustomer(List<ItemCustomerDto> itemDtoList) {
        List<ItemCustomerDto> removeItemList = new ArrayList<>();
        // 库存为0不展示
        for (ItemCustomerDto itemCustomer : itemDtoList) {
            EntityWrapper<GoodsSkuEntity> cond = new EntityWrapper<>();
            cond.eq("itemId", itemCustomer.getId());
            cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            List<GoodsSkuEntity> goodsSkuEntityList = goodsSkuDao.selectList(cond);
            long stockNumbers = goodsSkuEntityList.stream().mapToLong(GoodsSkuEntity::getStockNumber).sum();
            if (stockNumbers == 0) {
                removeItemList.add(itemCustomer);
            }
        }
        itemDtoList.removeAll(removeItemList);
    }


    @Override
    public ItemAppDto findAppItemById(Long id) throws GlobalException {
        /** 判断id是否为空 */
        if (null == id) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        ItemDetailWatcher itemDetailWatcher = new ItemDetailWatcher(id);

        /** 判断id是否存在 */
        ItemEntity item = itemDao.selectById(id);
        if (null == item) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
        }
        ItemAppDto itemAppDto = new ItemAppDto();
        BeanCopyUtil.copy(item, itemAppDto);
        itemDetailWatcher.logOperation("加载Item");

        /** 加載商品sku数据 * */
        List<GoodsSkuDto> goodsSkuDtos = goodsSkuDao.selectGoodsSkuById(id);
        if (CollectionUtils.isEmpty(goodsSkuDtos)) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODS_SKU_IS_NULL);
        }
        itemAppDto.setGoodsSkuDtos(goodsSkuDtos);
        itemDetailWatcher.logOperation("加载SKU");

        // 加载商品店铺信息
        loadItemStoreInfo(itemAppDto);
        itemDetailWatcher.logOperation("加载店铺");

        // 计算销量和库存
        caculateSalesVolumeAndStorage(itemAppDto);
        itemDetailWatcher.logOperation("加载商品销量和库存");

        // 加载商品默认运费
        try {
            loadItemFreightPrice(itemAppDto);
            itemDetailWatcher.logOperation("加载商品默认运费");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        // 加載商品销售屬性名
        loadSaleAttributeKeys(itemAppDto);
        itemDetailWatcher.logOperation("加载商品销售属性键");

        // 加載商品销售屬性名
        loadSaleAttributes(itemAppDto);
        itemDetailWatcher.logOperation("加载商品销售属性值");

        // 加载营销活动
        try {
            loadItemPromotions(itemAppDto);
            itemDetailWatcher.logOperation("加载营销活动");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        // 加载商品评论
        try {
            loadItemComments(itemAppDto);
            itemDetailWatcher.logOperation("加载商品评论");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        // 商品图片
        loadItemImages(itemAppDto);
        itemDetailWatcher.logOperation("加载商品图片");

        itemDetailWatcher.logPerformance(log);
        return itemAppDto;
    }

    private void loadSaleAttributeKeys(ItemAppDto itemAppDto) {
        // 创建销售属性集合，渲染ID
        List<GoodsSkuSaleDto> saleKeyDTOs = new ArrayList<>();
        List<GoodsSkuDto> goodsSkuDtos = itemAppDto.getGoodsSkuDtos();
        // 取到第一条sku对象
        GoodsSkuDto goodsSkuDto = goodsSkuDtos.get(0);
        // 销售属性
        String saleFieldValue = goodsSkuDto.getSaleFieldValue();
        if (StringUtils.isBlank(saleFieldValue)) {
            return;
        }
        String[] saleFieldValueArray = saleFieldValue.split(",");
        for (int i = 0; i < saleFieldValueArray.length; i++) {
            String[] attrArray = saleFieldValueArray[i].split(":");
            Long id = Long.parseLong(attrArray[0]);
            GoodsSkuSaleDto goodsSkuSaleDto = new GoodsSkuSaleDto();
            goodsSkuSaleDto.setId(id.toString());
            saleKeyDTOs.add(goodsSkuSaleDto);
        }

        // 批量查询类目属性
        List<Long> attrIds =
                saleKeyDTOs.stream().map(item -> Long.parseLong(item.getId())).collect(Collectors.toList());
        EntityWrapper<BackendCategoryAttrEntity> cond = new EntityWrapper<>();
        cond.in("id", attrIds);
        List<BackendCategoryAttrEntity> backAttrs = backendCategoryAttrDao.selectList(cond);
        if (CollectionUtils.isEmpty(backAttrs)) {
            return;
        }

        // 数据渲染
        Map<Long, BackendCategoryAttrEntity> attrMap =
                backAttrs
                        .stream()
                        .collect(Collectors.toMap(BackendCategoryAttrEntity::getId, item -> item));
        for (GoodsSkuSaleDto saleDto : saleKeyDTOs) {
            BackendCategoryAttrEntity attr = attrMap.get(Long.parseLong(saleDto.getId()));
            if (attr != null) {
                saleDto.setKeyName(attr.getName());
            }
        }
        itemAppDto.setGoodsSkuSaleKeyDtos(saleKeyDTOs);
    }

    private void loadSaleAttributes(ItemAppDto itemAppDto) {
        // 创建销售属性集合
        List<GoodsSkuSaleDto> goodsSkuSaleDtos = new ArrayList<GoodsSkuSaleDto>();
        // 属性值，加图片
        List<GoodsSkuSaleValueDto> goodsSkuSaleValueDtos = new ArrayList<GoodsSkuSaleValueDto>();
        GoodsSkuSaleKeyAndValueDto goodsSkuSaleKeyAndValueDto = new GoodsSkuSaleKeyAndValueDto();
        List<GoodsSkuDto> goodsSkuDtos = itemAppDto.getGoodsSkuDtos();

        // 取出每一个销售属性
        for (GoodsSkuDto goodsSkuDto : goodsSkuDtos) {
            // 创建销售属性集合
            List<GoodsSkuSaleDto> goodsSkuSaleDtolist = new ArrayList<GoodsSkuSaleDto>();
            // 取出每个销售属性
            String saleFieldValue = goodsSkuDto.getSaleFieldValue();
            if (StringUtils.isBlank(saleFieldValue)) {
                continue;
            }
            String[] strs2 = saleFieldValue.split(",");
            for (int i = 0; i < strs2.length; i++) {
                String[] strs22 = strs2[i].split(":");
                // 创建销售属性对象
                GoodsSkuSaleDto goodsSkuSaleDto = new GoodsSkuSaleDto();
                GoodsSkuSaleValueDto goodsSkuSaleValueDto = new GoodsSkuSaleValueDto();
                // 创建销售属性对象
                GoodsSkuSaleDto goodsSkuSaleDtoss = new GoodsSkuSaleDto();
                // 保存key值
                goodsSkuSaleDto.setId(strs22[0]);
                goodsSkuSaleDtoss.setId(strs22[0]);
                goodsSkuSaleDto.setSaleName(strs22[1]);
                goodsSkuSaleDtoss.setSaleName(strs22[1]);
                goodsSkuSaleDtos.add(goodsSkuSaleDto);
                Long imageField = itemAppDto.getImageField();
                goodsSkuSaleDtolist.add(goodsSkuSaleDtoss);
                // 判断是否上传sku图片
                if (imageField != null) {
                    String imageFileld = Long.toString(imageField);
                    goodsSkuSaleKeyAndValueDto.setSaleId(imageFileld);
                    BackendCategoryAttrEntity selectById = backendCategoryAttrDao.selectById(imageFileld);
                    goodsSkuSaleKeyAndValueDto.setSaleName(selectById.getName());
                    if (imageFileld.equals(goodsSkuSaleDto.getId())) {
                        goodsSkuSaleValueDto.setImageurl(goodsSkuDto.getSaleImage());
                        goodsSkuSaleValueDto.setValueName(strs22[1]);
                        goodsSkuSaleValueDtos.add(goodsSkuSaleValueDto);
                    }
                }
                goodsSkuDto.setGoodsSkuSaleDto(goodsSkuSaleDtolist);
                goodsSkuSaleKeyAndValueDto.setGoodsSkuSaleValueDto(goodsSkuSaleValueDtos);
                itemAppDto.setGoodsSkuSaleDtos(goodsSkuSaleDtos);
                itemAppDto.setGoodsSkuSaleKeyAndValueDto(goodsSkuSaleKeyAndValueDto);
            }
        }
    }

    private void loadItemFreightPrice(ItemAppDto itemAppDto) {
        Long logisticsId = itemAppDto.getLogisticsId();
        FreightTemplateDetailDTO freightTempleteDetail =
                freightTemplateApi.getFreightTempleteDetail(logisticsId);
        if (freightTempleteDetail != null) {
            BigDecimal firstPrice = freightTempleteDetail.getFirstPrice();
            itemAppDto.setFirstPrice(firstPrice);
        }
    }

    private void loadItemImages(ItemAppDto itemAppDto) {
        List<ItemPictureDto> itemPictureDtos = itemPictureDao.selectItemPictureById(itemAppDto.getId());
        if (CollectionUtils.isNotEmpty(itemPictureDtos)) {
            itemAppDto.setItemPictureDtos(itemPictureDtos);
        }
    }

    /**
     * 统计销量和库存
     *
     * @param itemAppDto
     */
    private void caculateSalesVolumeAndStorage(ItemAppDto itemAppDto) {
        List<GoodsSkuDto> goodsSkuDtos = itemAppDto.getGoodsSkuDtos();
        Long salesVolume = 0L;
        Long stockNum = 0L;

        // 销量和库存求和
        for (GoodsSkuDto goodsSkuDto : goodsSkuDtos) {
            salesVolume += goodsSkuDto.getSalesVolume();
            stockNum += goodsSkuDto.getStockNumber();
        }

        itemAppDto.setSalesVolume(salesVolume);
        itemAppDto.setStockNumber(stockNum);
    }

    private void loadItemStoreInfo(ItemAppDto itemAppDto) {
        StoreInfoDetailDTO storeInfo = storeApi.getStore(itemAppDto.getBelongStore());
        itemAppDto.setStoreName(storeInfo.getName());
        itemAppDto.setStoreImage(storeInfo.getImages());
        itemAppDto.setChangeState(storeInfo.getChangeState());
        if (Objects.equals(storeInfo.getHaitao(), MerchantConstants.Haitao.CONFIRM_HAITAO)) {
            itemAppDto.setHaitao(true);
        } else {
            itemAppDto.setHaitao(false);
        }
    }

    private PromotionDTO searchSinglePromotion(Map<Long, List<PromotionDTO>> skuPromotionMap, Long skuId, PromotionTypeEnum type) {
        List<PromotionDTO> singlePromotions = skuPromotionMap.get(skuId);
        if (CollectionUtils.isEmpty(singlePromotions)) {
            return null;
        }
        for (PromotionDTO promotionDTO : singlePromotions) {
            if (type.equals(promotionDTO.getType())) {
                return promotionDTO;
            }
        }
        return null;
    }

    //单品折扣和一口价
    private List<PromotionDTO> searchSinglePromotions(Map<Long, List<PromotionDTO>> skuPromotionMap, Long skuId) {
        List<PromotionDTO> singlePromotions = skuPromotionMap.get(skuId);
        if (CollectionUtils.isEmpty(singlePromotions)) {
            return null;
        }
        List<PromotionDTO> results = new ArrayList<>();
        for (PromotionDTO promotionDTO : singlePromotions) {
            if (!PromotionTypeEnum.PROMOTION_TYPE_SECKILL.equals(promotionDTO.getType())) {
                results.add(promotionDTO);
            }
        }
        return results;
    }

    private void loadItemSeckill(ItemAppDto itemAppDto, Map<Long, List<PromotionDTO>> skuPromotionMap) throws ParseException {
        List<GoodsSkuDto> goodsSkuDtos = itemAppDto.getGoodsSkuDtos();
        // 所有SKU销售数量总和
        Integer skuQuantitySaleSum = 0;
        // 所有SKU活动库存总和
        Long skuPromotionStockSum = 0L;
        List<BigDecimal> promotionPrices = new ArrayList<>();
        for (GoodsSkuDto goodsSkuDto : goodsSkuDtos) {
            // 商品属性id
            Long skuId = goodsSkuDto.getId();
            // 秒杀
            PromotionDTO seckill = searchSinglePromotion(skuPromotionMap, skuId, PromotionTypeEnum.PROMOTION_TYPE_SECKILL);
            if (seckill == null) {
                continue;
            }
            List<PromotionGoodsDTO> limitGoods = seckill.getLimitGoods();
            if (CollectionUtils.isNotEmpty(limitGoods)) {
                PromotionGoodsDTO promotionGoodsDTO = findPromotionGoods(limitGoods, goodsSkuDto.getId());
                promotionPrices.add(promotionGoodsDTO.getPromotionPrice());

                //设置整个Item的秒杀状态
                itemAppDto.setEndTime(seckill.getEndTime());
                itemAppDto.setIsKill(1);
                goodsSkuDto.setPrice(promotionGoodsDTO.getPromotionPrice());

                // 销售数量
                Integer quantitySale = promotionGoodsDTO.getQuantitySales();
                skuQuantitySaleSum += (quantitySale == null) ? 0 : quantitySale;

                // 活动库存
                Long skuPromotionNum = promotionGoodsDTO.getPromotionNum();
                skuPromotionStockSum += (skuPromotionNum == null) ? 0 : skuPromotionNum;
            }

            //真实的进度
            Integer realProgress = 0;
            if (!skuPromotionStockSum.equals(0L)) {
                realProgress = skuQuantitySaleSum * 100 / skuPromotionStockSum.intValue();
            }
            //通过规则计算出的销售进度
            Integer ruleProgress = SaleRateUtil.rendRate(itemAppDto.getId(), seckill.getStartTime(), seckill.getEndTime());
            //进度条
            Integer progress = Integer.max(ruleProgress, realProgress);
            itemAppDto.setPercent(new BigDecimal(progress));
        }

        setDefaultPrice(itemAppDto, promotionPrices);
    }

    private PromotionGoodsDTO findPromotionGoods(List<PromotionGoodsDTO> limitGoods, Long skuId) {
        for (PromotionGoodsDTO promotionGoodsDTO : limitGoods) {
            if (promotionGoodsDTO.getGoodsSkuId().equals(skuId)) {
                return promotionGoodsDTO;
            }
        }
        return null;
    }

    private void loadItemPromotions(ItemAppDto itemAppDto) throws ParseException {
        List<GoodsSkuDto> goodsSkuDtos = itemAppDto.getGoodsSkuDtos();
        if (CollectionUtils.isEmpty(goodsSkuDtos)) {
            return;
        }
        List<Long> skuIds = goodsSkuDtos.stream().map(item -> item.getId()).collect(Collectors.toList());
        Map<Long, List<PromotionDTO>> skuPromotionMap = promotionApi.getSkuPromotions(skuIds);

        //加载秒杀信息
        loadItemSeckill(itemAppDto, skuPromotionMap);

        // 单品折扣和一口价
        List<PromotionDTO> singlePromotions = new ArrayList<>();
        List<BigDecimal> promotionPrices = new ArrayList<>();
        for (GoodsSkuDto goodsSkuDto : goodsSkuDtos) {
            // 商品属性id
            Long skuId = goodsSkuDto.getId();
            // 单品折扣和一口价
            List<PromotionDTO> singlePromotion = searchSinglePromotions(skuPromotionMap, skuId);
            if (CollectionUtils.isNotEmpty(singlePromotion)) {
                PromotionDTO promotionDTO = singlePromotion.get(0);
                PromotionGoodsDTO promotionGoodsDTO = findPromotionGoods(promotionDTO.getLimitGoods(), skuId);
                BigDecimal promotionPrice = promotionGoodsDTO.getPromotionPrice();
                promotionPrices.add(promotionPrice);
                goodsSkuDto.setPrice(promotionPrice);
                singlePromotions.addAll(singlePromotion);
            }
        }

        setDefaultPrice(itemAppDto, promotionPrices);

        List<PromotionDTO> promotionDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(singlePromotions)) {
            Collections.sort(singlePromotions, new Comparator<PromotionDTO>() {
                @Override
                public int compare(PromotionDTO o1, PromotionDTO o2) {
                    int i = o1.getLimitGoods().get(0).getPromotionPrice().compareTo(o2.getLimitGoods().get(0).getPromotionPrice());
                    return i;
                }
            });
            for (PromotionDTO promotionDTO : singlePromotions) {
                /** 判断是否包含。 */
                if (!isContainSingle(promotionDTOList, promotionDTO)) {
                    promotionDTOList.add(promotionDTO);
                }
            }
        }

        itemAppDto.setSinglePromotions(promotionDTOList);

        // 促销，满减
        List<PromotionDTO> moneyOffPromotion =
                promotionApi.getStorePromotionList(
                        itemAppDto.getBelongStore(), PromotionTypeEnum.PROMOTION_TYPE_REDUCE_PRICE.getCode());
        itemAppDto.setMoneyOffPromotions(moneyOffPromotion);

        // 包邮
        List<PromotionDTO> pinkagePromotion =
                promotionApi.getStorePromotionList(
                        itemAppDto.getBelongStore(), PromotionTypeEnum.PROMOTION_TYPE_FREE_SHIPPING.getCode());
        if (CollectionUtils.isNotEmpty(pinkagePromotion)) {
            itemAppDto.setPinkagePromotions(pinkagePromotion.get(0));
        }

        // 优惠券
        List<PromotionDTO> platFormCoupon =
                promotionApi.getPlatFormCouponBySku(itemAppDto.getId(), itemAppDto.getBelongStore());
        itemAppDto.setCouponPromotions(platFormCoupon);
    }

    private void setDefaultPrice(ItemAppDto itemAppDto, List<BigDecimal> bigDecimals) {
        if (CollectionUtils.isEmpty(bigDecimals)) {
            return;
        }
        bigDecimals.add(itemAppDto.getDefaultPrice());
        itemAppDto.setDefaultPrice(Collections.min(bigDecimals));
    }

    private boolean isContainSingle(List<PromotionDTO> promotionDTOS, PromotionDTO promotionDTO) {
        for (PromotionDTO promotion : promotionDTOS) {
            if (promotion.getId().equals(promotionDTO.getId())) {
                return true;
            }
        }
        return false;
    }

    private void loadItemComments(ItemAppDto itemAppDto) {
        ItemCommentsDTO commentsDTO = redisCache.get(GoodsRedisKey.GOODS_DETAILS_COMMENT + itemAppDto.getId(), ItemCommentsDTO.class);
        if (commentsDTO == null) {
            commentsDTO = loadItemCommentsDTOFromDB(itemAppDto.getId(), itemAppDto.getCommentCount());
            // put to redis
            redisCache.set(GoodsRedisKey.GOODS_DETAILS_COMMENT + itemAppDto.getId(), commentsDTO);
        }
        itemAppDto.setGoodsSkuCommentDtos(commentsDTO.getGoodsSkuCommentDtos());
        itemAppDto.setCommentCount(commentsDTO.getCommentCount());
        itemAppDto.setPraise(commentsDTO.getPraise());
    }

    private ItemCommentsDTO loadItemCommentsDTOFromDB(Long id, Integer commentCount) {
        ItemCommentsDTO commentsDTO = new ItemCommentsDTO();
        // 查询3条商品评价
        EntityWrapper<GoodsSkuCommentEntity> goodsSkuComments = new EntityWrapper<>();
        goodsSkuComments.eq("itemId", id);
        goodsSkuComments.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        goodsSkuComments.orderBy(true, "createdTime", false);
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(ZERO_LENGTH);
        pagePO.setPageSize(THREE_LENGTH);
        Page<GoodsSkuCommentEntity> page = PageDataUtil.buildPageParam(pagePO);
        List<GoodsSkuCommentEntity> goodsSkuCommentEntities =
                goodsSkuCommentDao.selectPage(page, goodsSkuComments);

        // 如果没有商品评价，返回商品评价条数为0，商品好评度为0
        if (CollectionUtils.isEmpty(goodsSkuCommentEntities)) {
            commentsDTO.setPraise(new BigDecimal(0));
            commentsDTO.setCommentCount(0);
            return commentsDTO;
        }
        List<GoodsSkuCommentDto> goodsSkuCommentDtos = getGoodsSkuComments(goodsSkuCommentEntities);
        Integer goodsReputationCount = loadGoodsReputation(id);
        commentsDTO.setGoodsSkuCommentDtos(goodsSkuCommentDtos);
        BigDecimal praiseRatio = getPraiseRatio(goodsReputationCount, commentCount);
        commentsDTO.setPraise(praiseRatio);
        commentsDTO.setCommentCount(commentCount);
        return commentsDTO;
    }

    private List<GoodsSkuCommentDto> getGoodsSkuComments(List<GoodsSkuCommentEntity> goodsSkuCommentEntities) {
        // 如果有商品评价 copy
        List<GoodsSkuCommentDto> goodsSkuCommentDtos = new ArrayList<>();
        for (GoodsSkuCommentEntity goodsSkuCommentEntity : goodsSkuCommentEntities) {
            GoodsSkuCommentDto goodsSkuComment = new GoodsSkuCommentDto();
            BeanCopyUtil.copy(goodsSkuCommentEntity, goodsSkuComment);
            if (goodsSkuCommentEntity.getUserName() != null) {
                goodsSkuComment.setMemberName(getMemberName(goodsSkuCommentEntity.getUserName()));
            }
            goodsSkuCommentDtos.add(goodsSkuComment);
        }
        // 评价的图片
        for (GoodsSkuCommentDto goodsSkuCommentDto : goodsSkuCommentDtos) {
            // 商品评价图片
            List<GoodsSkuCommentPictureDto> goodsSkuCommentPictureDto =
                    goodsSkuCommentPictureDao.selectGoodsSkuCommentPictureDto(goodsSkuCommentDto.getId());
            if (!(goodsSkuCommentPictureDto == null || goodsSkuCommentPictureDto.size() == 0)) {
                goodsSkuCommentDto.setGoodsSkuCommentPictureDtos(goodsSkuCommentPictureDto);
            }
        }
        // 会员头像
        List<Long> memberIds = goodsSkuCommentEntities.stream().map(goodsSkuCommentEntity -> goodsSkuCommentEntity.getMemberId()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(memberIds)) {
            List<MemberDto> memberList = memberApi.getMemberList(memberIds);
            loadMemberName(memberList, goodsSkuCommentDtos);
        }
        return goodsSkuCommentDtos;
    }

    /**
     * 根据商品itemId查询商品好评数量
     *
     * @param itemId 商品id
     * @return
     */
    private Integer loadGoodsReputation(Long itemId) {
        // 好评数量
        EntityWrapper<GoodsSkuCommentEntity> cond = new EntityWrapper<>();
        cond.eq("itemId", itemId);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.gt("goodsReputation", THREE_LENGTH);
        return goodsSkuCommentDao.selectCount(cond);
    }

    @Override
    public PageInfo<ItemSellDto> getSellGoodsList(ItemSellDto itemSellDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(itemSellDto.getPageNo());
        pagePO.setPageSize(itemSellDto.getPageSize());
        Page<ItemSellDto> page = PageDataUtil.buildPageParam(pagePO);
        List<ItemSellDto> sellGoodsList = itemDao.selectSellGoodsList(page, itemSellDto);
        if (!(sellGoodsList == null || sellGoodsList.size() == 0)) {
            for (ItemSellDto itemSellDto2 : sellGoodsList) {
                Long id = itemSellDto2.getId();
                List<GoodsSkuDto> selectGoodsSkuById = goodsSkuDao.selectGoodsSkuById(id);
                if (!(selectGoodsSkuById == null || selectGoodsSkuById.size() == 0)) {
                    // 总销售数量
                    Integer saleNumbers = 0;
                    // 总销售金额
                    BigDecimal salesAmounts = null;
                    // 付款人数
                    Integer paymentPeoples = 0;
                    for (GoodsSkuDto goodsSkuDto : selectGoodsSkuById) {
                        // 付款人数
                        Integer paymentPeople = itemDao.selectPaymentPeoples(goodsSkuDto.getId());
                        paymentPeoples = paymentPeoples + paymentPeople;
                        List<ItemSellDto> itemSellDto3 = itemDao.selectSaleNumber(goodsSkuDto.getId());
                        if (!(itemSellDto3.size() == 0 || itemSellDto3 == null)) {
                            for (ItemSellDto itemSellDto4 : itemSellDto3) {
                                Integer saleNumber = itemSellDto4.getSaleNumber();
                                BigDecimal salesAmount = itemSellDto4.getSalesAmount();
                                saleNumbers = saleNumbers + saleNumber;
                                salesAmounts = salesAmounts.add(salesAmount);
                            }
                        }
                    }
                    itemSellDto2.setSaleNumber(saleNumbers);
                    itemSellDto2.setSalesAmount(salesAmounts);
                    itemSellDto2.setPaymentPeople(paymentPeoples);
                }
            }
        }
        page.setRecords(sellGoodsList);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public PageInfo<ItemSellDto> getMerchantSellGoodsList(ItemSellDto itemSellDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(itemSellDto.getPageNo());
        pagePO.setPageSize(itemSellDto.getPageSize());
        Page<ItemSellDto> page = PageDataUtil.buildPageParam(pagePO);
        // 获得到店铺的id
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        itemSellDto.setStoreId(storeId);
        List<ItemSellDto> sellGoodsList = itemDao.selectMerchantSellGoodsList(page, itemSellDto);
        if (!(sellGoodsList == null || sellGoodsList.size() == 0)) {
            for (ItemSellDto itemSellDto2 : sellGoodsList) {
                Long id = itemSellDto2.getId();
                List<GoodsSkuDto> selectGoodsSkuById = goodsSkuDao.selectGoodsSkuById(id);
                if (!(selectGoodsSkuById.size() == 0 || selectGoodsSkuById == null)) {
                    // 总销售数量
                    Integer saleNumbers = 0;
                    // 总销售金额
                    BigDecimal salesAmounts = null;
                    // 付款人数
                    Integer paymentPeoples = 0;
                    for (GoodsSkuDto goodsSkuDto : selectGoodsSkuById) {
                        // 付款人数
                        Integer paymentPeople = itemDao.selectPaymentPeoples(goodsSkuDto.getId());
                        paymentPeoples = paymentPeoples + paymentPeople;
                        List<ItemSellDto> itemSellDto3 = itemDao.selectSaleNumber(goodsSkuDto.getId());
                        if (!(itemSellDto3.size() == 0 || itemSellDto3 == null)) {
                            for (ItemSellDto itemSellDto4 : itemSellDto3) {
                                Integer saleNumber = itemSellDto4.getSaleNumber();
                                BigDecimal salesAmount = itemSellDto4.getSalesAmount();
                                saleNumbers = saleNumbers + saleNumber;
                                salesAmounts = salesAmounts.add(salesAmount);
                            }
                        }
                    }
                    itemSellDto2.setSaleNumber(saleNumbers);
                    itemSellDto2.setSalesAmount(salesAmounts);
                    itemSellDto2.setPaymentPeople(paymentPeoples);
                }
            }
        }
        page.setRecords(sellGoodsList);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public PageInfo<ItemSellDto> getSellGoodsCategoryList(ItemSellDto itemSellDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(itemSellDto.getPageNo());
        pagePO.setPageSize(itemSellDto.getPageSize());
        Page<ItemSellDto> page = PageDataUtil.buildPageParam(pagePO);
        List<ItemSellDto> sellGoodsList = itemDao.selectSellGoodsCategoryList(page, itemSellDto);
        if (!(sellGoodsList == null || sellGoodsList.size() == 0)) {
            for (ItemSellDto itemSellDto2 : sellGoodsList) {
                // 第三级类目id
                Long categoryIdt = itemSellDto2.getCategoryIdt();
                BackendCategoryDto selectBackendCategoryById =
                        backendCategoryDao.selectBackendCategoryById(categoryIdt);
                // 通过第三级parentId查询第二级类目
                BackendCategoryDto twobackendCategoryDto =
                        backendCategoryDao.selectTwoBackendCategoryDtoByParentId(
                                selectBackendCategoryById.getParentId());
                // 通过第二级parentId查询第一级类目
                BackendCategoryDto onebackendCategoryDto =
                        backendCategoryDao.selectOneBackendCategoryDtoByParentId(
                                twobackendCategoryDto.getParentId());
                itemSellDto2.setCategoryId(onebackendCategoryDto.getId());
                itemSellDto2.setCategoryName(onebackendCategoryDto.getName());
            }
        }
        List<ItemSellDto> msgList = new ArrayList<ItemSellDto>();
        // 去重
        for (int i = 0; i < sellGoodsList.size() - 1; i++) {
            for (int j = sellGoodsList.size() - 1; j > i; j--) {
                if (sellGoodsList.get(j).getCategoryId().equals(sellGoodsList.get(i).getCategoryId())) {
                    Integer count = sellGoodsList.get(i).getCount();
                    Integer count2 = sellGoodsList.get(j).getCount();
                    sellGoodsList.get(i).setCount(count2 + count);
                    msgList.add(sellGoodsList.get(j));
                }
            }
        }
        sellGoodsList.removeAll(msgList);
        page.setRecords(sellGoodsList);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public PageInfo<ItemSellDto> getStoreSellGoodsCategoryList(ItemSellDto itemSellDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(itemSellDto.getPageNo());
        pagePO.setPageSize(itemSellDto.getPageSize());
        Page<ItemSellDto> page = PageDataUtil.buildPageParam(pagePO);
        // 获得到店铺的id
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        itemSellDto.setStoreId(storeId);
        List<ItemSellDto> sellGoodsList = itemDao.selectStoreSellGoodsCategoryList(page, itemSellDto);
        List<ItemSellDto> msgList = new ArrayList<ItemSellDto>();
        if (!(sellGoodsList == null || sellGoodsList.size() == 0)) {
            for (ItemSellDto itemSellDto2 : sellGoodsList) {
                // 第三级类目id
                Long categoryIdt = itemSellDto2.getCategoryIdt();
                BackendCategoryDto selectBackendCategoryById =
                        backendCategoryDao.selectBackendCategoryById(categoryIdt);
                // 通过第三级parentId查询第二级类目
                BackendCategoryDto twobackendCategoryDto =
                        backendCategoryDao.selectTwoBackendCategoryDtoByParentId(
                                selectBackendCategoryById.getParentId());
                // 通过第二级parentId查询第一级类目
                BackendCategoryDto onebackendCategoryDto =
                        backendCategoryDao.selectOneBackendCategoryDtoByParentId(
                                twobackendCategoryDto.getParentId());
                itemSellDto2.setCategoryId(onebackendCategoryDto.getId());
                itemSellDto2.setCategoryName(onebackendCategoryDto.getName());
            }
        }
        // 去重
        for (int i = 0; i < sellGoodsList.size() - 1; i++) {
            for (int j = sellGoodsList.size() - 1; j > i; j--) {
                if (sellGoodsList.get(j).getCategoryId().equals(sellGoodsList.get(i).getCategoryId())) {
                    Integer count = sellGoodsList.get(i).getCount();
                    Integer count2 = sellGoodsList.get(j).getCount();
                    sellGoodsList.get(i).setCount(count2 + count);
                    msgList.add(sellGoodsList.get(j));
                }
            }
        }
        sellGoodsList.removeAll(msgList);
        page.setRecords(sellGoodsList);
        return PageDataUtil.copyPageInfo(page);
    }

    /**
     * 判断是否包含。
     */
    private boolean isContain(List<BackendCategoryDto> list, BackendCategoryDto backendCategoryDto) {
        for (BackendCategoryDto backDto : list) {
            if (backendCategoryDto.getId().equals(backDto.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public PageInfo<GoodsSkuCommentDto> findAppSkuCommentById(
            GoodsSkuCommentAppDto goodsSkuCommentAppDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageSize(goodsSkuCommentAppDto.getPageSize());
        pagePO.setPageNo(goodsSkuCommentAppDto.getPageNo());
        Page<GoodsSkuCommentDto> page = PageDataUtil.buildPageParam(pagePO);
        EntityWrapper<GoodsSkuCommentEntity> goodsSkuComments = new EntityWrapper<>();
        goodsSkuComments.eq("itemId", goodsSkuCommentAppDto.getItemId());
        goodsSkuComments.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        goodsSkuComments.orderBy(true, "createdTime", false);
        if (goodsSkuCommentAppDto.getType() != null
                && !Objects.equals(goodsSkuCommentAppDto.getType(), FOUR_TYPE.getCode())
                && !Objects.equals(goodsSkuCommentAppDto.getType(), ZERO_TYPE.getCode())) {
            goodsSkuComments.eq("type", goodsSkuCommentAppDto.getType());
        }
        if (Objects.equals(goodsSkuCommentAppDto.getType(), FOUR_TYPE.getCode())) {
            goodsSkuComments.eq("isImage", 1);
        }
        // 分页评价
        List<GoodsSkuCommentEntity> goodsSkuCommentEntities =
                goodsSkuCommentDao.selectPage(page, goodsSkuComments);
        if (CollectionUtils.isEmpty(goodsSkuCommentEntities)) {
            return null;
        }
        List<GoodsSkuCommentDto> goodsSkuCommentDtos = getGoodsSkuComments(goodsSkuCommentEntities);
        page.setRecords(goodsSkuCommentDtos);
        return PageDataUtil.copyPageInfo(page);
    }

    private String loadSaleFieldValue(String saleFieldValue1) {
        String saleFieldValue = "";
        if (StringUtils.isBlank(saleFieldValue1)) {
            return "";
        }
        String[] strss = saleFieldValue1.split(",");
        for (int i = 0; i < strss.length; i++) {
            String[] attrArray = strss[i].split(":");
            if (attrArray.length != 2
                    || StringUtils.isBlank(attrArray[0])
                    || StringUtils.isBlank(attrArray[1])) {
                continue;
            }
            BackendCategoryAttrEntity selectById = backendCategoryAttrDao.selectById(attrArray[0]);
            String name = selectById.getName();
            String value = attrArray[1];
            saleFieldValue = name + ":" + value + "  " + saleFieldValue;
        }
        return saleFieldValue;
    }

    public static final Integer ONE_LENGTH = 1;
    public static final Integer TWO_LENGTH = 2;
    public static final Integer ZERO_LENGTH = 0;
    public static final Integer THREE_LENGTH = 3;
    public static final String SYMBOL = "***";
    public static final String MOTHERBUY_MEMBER = "妈妈购会员";
    public static final String ANOMI_USER = "匿名用户";

    private List<GoodsSkuCommentDto> loadMemberName(
            List<MemberDto> memberList, List<GoodsSkuCommentDto> goodsSkuCommentDtos) {
        if (CollectionUtils.isEmpty(memberList)) {
            return goodsSkuCommentDtos;
        }

        Map<Long, MemberDto> memberDtoMap = memberList.stream().collect(Collectors.toMap(MemberDto::getId, item -> item));
        for (GoodsSkuCommentDto goodsSkuCommentDto : goodsSkuCommentDtos) {
            MemberDto memberDto = memberDtoMap.get(goodsSkuCommentDto.getMemberId());
            if (memberDto != null) {
                goodsSkuCommentDto.setSmallIcon(memberDto.getSmallIcon());
            }
        }
        return goodsSkuCommentDtos;
    }

    private String getMemberName(String userName) {
        if (StringUtils.isNotBlank(userName)) {
            if (userName.length() == TWO_LENGTH) {
                return userName.substring(ZERO_LENGTH, ONE_LENGTH);
            }
            String first = userName.substring(ZERO_LENGTH, ONE_LENGTH);
            String last = userName.substring(userName.length() - ONE_LENGTH);
            return first + SYMBOL + last;
        }
        return MOTHERBUY_MEMBER;
    }

    private static Pair<String, String> getMemberInfo(MemberDto memberDto) {
        if (memberDto == null) {
            return new ImmutablePair<>(ANOMI_USER, "");
        }
        String userName = memberDto.getUserName();
        if (StringUtils.isNotBlank(userName)) {
            if (userName.length() == TWO_LENGTH) {
                String substring = userName.substring(ZERO_LENGTH, ONE_LENGTH);
                return new ImmutablePair<>(substring + SYMBOL, memberDto.getSmallIcon());
            }
            String first = userName.substring(ZERO_LENGTH, ONE_LENGTH);
            String last = userName.substring(userName.length() - ONE_LENGTH);
            String substring = userName.substring(ZERO_LENGTH, ONE_LENGTH);
            return new ImmutablePair<>(first + SYMBOL + last, memberDto.getSmallIcon());
        }
        return new ImmutablePair<>(MOTHERBUY_MEMBER, memberDto.getSmallIcon());
    }

    @Override
    public Integer saveGoodsCommissionRate(CommissionRateDto commissionRateDto) {
        Integer i = 0;
        // 类目id
        Long categoryId = commissionRateDto.getCategoryId();
        BackendMerchantCategoryEntity backendMerchantCategoryEntity = new BackendMerchantCategoryEntity();
        backendMerchantCategoryEntity.cleanInit();
        backendMerchantCategoryEntity.setCategoryId(categoryId);
        backendMerchantCategoryEntity.setMerchantId(commissionRateDto.getMerchantId());
        backendMerchantCategoryEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        BackendMerchantCategoryEntity backendMerchantCategory = backendMerchantCategoryDao.selectOne(backendMerchantCategoryEntity);
        if (backendMerchantCategory == null) {
            throw new GlobalException(BackendCategoryExceptionEnum.COMMISSION_FAILURE);
        }
        StoreInfoDetailDTO storeByMerchantId = storeApi.getStoreByMerchantId(commissionRateDto.getMerchantId());
        backendMerchantCategory.setBrokerageRatio(commissionRateDto.getBrokerageRatio());
        backendMerchantCategory.setStoreId(storeByMerchantId.getId());
        i = backendMerchantCategoryDao.updateById(backendMerchantCategory);
        // 根据店铺id和类目id查询商品
        List<ItemEntity> itemEntities = itemDao.selectItemByCategoryIdAndStoreId(categoryId, storeByMerchantId.getId());
        if (!CollectionUtils.isEmpty(itemEntities)) {
            for (ItemEntity itemEntity : itemEntities) {
                ItemEntity item = new ItemEntity();
                item.clearInit();
                item.setId(itemEntity.getId());
                item.setBrokerageRatio(commissionRateDto.getBrokerageRatio());
                i = itemDao.updateById(item);
            }
        }
        return i;
    }

    @Override
    public GoodsDto findGoodsById(Long id) throws GlobalException {
        /** 判断id是否为空 */
        if (id == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.clearInit();
        itemEntity.setId(id);
        itemEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        /** 判断id是否存在 */
        ItemEntity item = itemDao.selectOne(itemEntity);
        if (item == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
        }
        GoodsDto goodsDto = new GoodsDto();
        BeanCopyUtil.copy(item, goodsDto);
        return goodsDto;
    }

    @Override
    public List<GoodsDto> findItemsById(Long[] id) throws GlobalException {
        /** 判断id是否为空 */
        if (id == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        List<GoodsDto> goodsDtos = new ArrayList<>();
        for (Long itemId : id) {
            ItemEntity itemEntity = new ItemEntity();
            itemEntity.clearInit();
            itemEntity.setId(itemId);
            itemEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            ItemEntity itemEntity1 = itemDao.selectOne(itemEntity);
            if (itemEntity1 != null) {
                GoodsDto goodsDto = new GoodsDto();
                BeanCopyUtil.copy(itemEntity1, goodsDto);
                ItemPictureEntity itemPictureEntity = new ItemPictureEntity();
                itemPictureEntity.cleanInit();
                itemPictureEntity.setItemId(itemEntity.getId());
                itemPictureEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
                itemPictureEntity.setIsMain(GoodsConstants.PicType.MAIN_PIC);
                ItemPictureEntity itemPictureEntity1 = itemPictureDao.selectOne(itemPictureEntity);
                goodsDto.setPictureName(itemPictureEntity1.getName());
                goodsDtos.add(goodsDto);
            }
        }
        return goodsDtos;
    }

    @Override
    public Integer saveIntegralRatio(List<IntegralRatioDto> integralRatioDtos) {
        Integer i = 0;

        for (IntegralRatioDto integralRatioDto : integralRatioDtos) {
            ItemEntity item = new ItemEntity();
            item.clearInit();
            item.setId(integralRatioDto.getId());
            item.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            ItemEntity itemEntity = itemDao.selectOne(item);
            if (itemEntity != null) {
                itemEntity.setIntegralRatio(integralRatioDto.getIntegralRatio());
                i = itemDao.updateById(itemEntity);
            }
            EntityWrapper<GoodsSkuEntity> cond = new EntityWrapper<>();
            cond.eq("itemId", integralRatioDto.getId());
            cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            List<GoodsSkuEntity> goodsSkuEntityList = goodsSkuDao.selectList(cond);
            if (CollectionUtils.isNotEmpty(goodsSkuEntityList)) {
                for (GoodsSkuEntity goodsSkuEntity : goodsSkuEntityList) {
                    goodsSkuEntity.setScoreRate(integralRatioDto.getIntegralRatio());
                    i = goodsSkuDao.updateById(goodsSkuEntity);
                }
            }
        }
        return i;
    }

    @Override
    public IntegralRatioDto findIdIntegralRatio(Long id) throws GlobalException {
        /** 判断id是否为空 */
        if (null == id) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        /** 判断id是否存在 */
        ItemEntity item = new ItemEntity();
        item.clearInit();
        item.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        item.setId(id);
        ItemEntity itemEntity = itemDao.selectOne(item);
        if (null == itemEntity) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
        }
        IntegralRatioDto integralRatioDto = new IntegralRatioDto();
        BeanCopyUtil.copy(itemEntity, integralRatioDto);
        return integralRatioDto;
    }

    @Override
    public CommissionRateDto findCommissionRate(CommissionRateDto commissionRateDto) {
        if (null == commissionRateDto.getCategoryId()) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_ID_NOT_NULL);
        }
        BackendMerchantCategoryEntity cond = new BackendMerchantCategoryEntity();
        cond.cleanInit();
        cond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        cond.setMerchantId(commissionRateDto.getMerchantId());
        cond.setCategoryId(commissionRateDto.getCategoryId());
        CommissionRateDto commissionRate = new CommissionRateDto();
        BackendMerchantCategoryEntity backendMerchantCategoryEntity =
                backendMerchantCategoryDao.selectOne(cond);
        if (backendMerchantCategoryEntity == null) {
            return commissionRate;
        }
        BeanCopyUtil.copy(backendMerchantCategoryEntity, commissionRate);
        return commissionRate;
    }

    @Override
    public GoodsPraiseDto findAppGoodsPraiseById(Long itemId) {
        GoodsPraiseDto goodsPraiseDto = new GoodsPraiseDto();
        EntityWrapper<GoodsSkuCommentEntity> goodsSkuComments = new EntityWrapper<>();
        goodsSkuComments.eq("itemId", itemId);
        goodsSkuComments.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        // 商品评价总条数
        Integer totalCount = goodsSkuCommentDao.selectCount(goodsSkuComments);
        if (totalCount == 0) {
            goodsPraiseDto.setPraiseLevel(0);
            goodsPraiseDto.setPraiseRatio(new BigDecimal(0));
            return goodsPraiseDto;
        }
        // 商品好评条数
        Integer goodsReputationCount = loadGoodsReputation(itemId);
        BigDecimal praiseRatio = getPraiseRatio(goodsReputationCount, totalCount);
        goodsPraiseDto.setPraiseRatio(praiseRatio);
        if (praiseRatio.compareTo(FIVE_RATIO) >= 0) {
            goodsPraiseDto.setPraiseLevel(FIVE_LEVEL);
        } else if (praiseRatio.compareTo(FOUR_RATIO) >= 0) {
            goodsPraiseDto.setPraiseLevel(FOUR_LEVEL);
        } else if (praiseRatio.compareTo(THREE_RATIO) >= 0) {
            goodsPraiseDto.setPraiseLevel(THREE_LEVEL);
        } else if (praiseRatio.compareTo(TWO_RATIO) >= 0) {
            goodsPraiseDto.setPraiseLevel(TWO_LEVEL);
        } else {
            goodsPraiseDto.setPraiseLevel(ONE_LEVEL);
        }
        List<GoodsSkuCommentCountDto> goodsSkuCommentCount =
                getGoodsSkuCommentCount(totalCount, itemId);
        goodsPraiseDto.setGoodsSkuCommentCounts(goodsSkuCommentCount);
        return goodsPraiseDto;
    }

    @Override
    public List<GoodsDto> findGoodsList(Long[] id) {
        /** 判断id是否为空 */
        if (id.length == 0) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        EntityWrapper<ItemEntity> cond = new EntityWrapper<>();
        cond.in("id", id);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<ItemEntity> itemEntities = itemDao.selectList(cond);
        if (CollectionUtils.isEmpty(itemEntities)) {
            return Collections.EMPTY_LIST;
        }
        List<GoodsDto> goodsDtos = BeanCopyUtil.copyList(itemEntities, GoodsDto.class);
        getStoreNames(goodsDtos);
        return goodsDtos;
    }

    @Override
    public PageInfo<ItemDto> getPromotionItem(ItemDto itemDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(itemDto.getPageNo());
        pagePO.setPageSize(itemDto.getPageSize());
        Page<ItemDto> page = PageDataUtil.buildPageParam(pagePO);
        List<Long> itemIds = promotionApi.getSelectItemIds(itemDto.getPromotionId());
        // 获得到店铺的id
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        EntityWrapper<ItemEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("frozenFlag", ItemConstants.FrozenFlag.NO_FROZEN);
        cond.eq("belongStore", storeId);
        if (CollectionUtils.isNotEmpty(itemIds)) {
            cond.notIn("id", itemIds);
        }
        if (itemDto.getId() != null) {
            cond.eq("id", itemDto.getId());
        }
        if (itemDto.getItemCode() != null) {
            cond.like("itemCode", itemDto.getItemCode());
        }
        if (itemDto.getName() != null) {
            cond.like("name", itemDto.getName());
        }
        List<ItemEntity> itemEntities = itemDao.selectPage(page, cond);
        if (CollectionUtils.isEmpty(itemEntities)) {
            return PageDataUtil.copyPageInfo(page);
        }
        List<ItemDto> itemDtos = BeanCopyUtil.copyList(itemEntities, ItemDto.class);
        for (ItemDto item : itemDtos) {
            // 查询每件商品的库存
            Long stockNumber = itemDao.selecStockNumbertById(item.getId());
            if (stockNumber != null) {
                item.setStockNumber(stockNumber);
            }
        }
        page.setRecords(itemDtos);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public List<GoodsSkusDto> findGoodsSku(Long itemId) {
        /** 判断id是否为空 */
        if (itemId == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        /** 判断id是否存在 */
        ItemEntity item = itemDao.selectById(itemId);
        if (item == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
        }
        EntityWrapper<GoodsSkuEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("itemId", itemId);
        List<GoodsSkuEntity> goodsSkuEntityList = goodsSkuDao.selectList(cond);
        if (CollectionUtils.isEmpty(goodsSkuEntityList)) {
            return Collections.EMPTY_LIST;
        }
        List<GoodsSkusDto> goodsSkusDtos = BeanCopyUtil.copyList(goodsSkuEntityList, GoodsSkusDto.class);
        for (GoodsSkusDto goodsSkusDto : goodsSkusDtos) {
            String s = loadSaleFieldValue(goodsSkusDto.getSaleFieldValue());
            goodsSkusDto.setSaleFieldValue(s);
        }
        return goodsSkusDtos;
    }

    public List<GoodsDto> getStoreNames(List<GoodsDto> goodsDtos) {
        return goodsDtos
                .stream()
                .map(
                        goodsDto -> {
                            StoreInfoDetailDTO storeInfoDetail = storeApi.getStore(goodsDto.getBelongStore());
                            if (storeInfoDetail != null) {
                                goodsDto.setStoreName(storeInfoDetail.getName());
                            }
                            return goodsDto;
                        })
                .collect(Collectors.toList());
    }

    private BigDecimal getPraiseRatio(Integer goodsReputationCount, Integer totalCount) {
        BigDecimal num1 = new BigDecimal(goodsReputationCount);
        BigDecimal num2 = new BigDecimal(totalCount);
        BigDecimal multiply = num1.multiply(new BigDecimal(100));
        return multiply.divide(num2, 0, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 查询全部，有图，好中差评条数
     *
     * @param totalCount 全部总条数
     * @param itemId     商品itemId
     * @return
     */
    private List<GoodsSkuCommentCountDto> getGoodsSkuCommentCount(Integer totalCount, Long itemId) {
        List<GoodsSkuCommentCountDto> goodsSkuCommentCountDtos = new ArrayList<>();
        for (GoodsConstants.GoodsCommentType goodsCommentType :
                GoodsConstants.GoodsCommentType.values()) {
            GoodsSkuCommentCountDto goodsSkuCommentCountDto = new GoodsSkuCommentCountDto();
            goodsSkuCommentCountDto.setType(goodsCommentType.getCode());
            if (Objects.equals(goodsCommentType.getCode(), ZERO_TYPE.getCode())) {
                goodsSkuCommentCountDto.setCount(totalCount);
            } else if (Objects.equals(goodsCommentType.getCode(), FOUR_TYPE.getCode())) {
                goodsSkuCommentCountDto.setCount(getCommentPicBySkuIds(itemId));
            } else {
                goodsSkuCommentCountDto.setCount(getCountByType(goodsCommentType.getCode(), itemId));
            }
            goodsSkuCommentCountDtos.add(goodsSkuCommentCountDto);
        }
        return goodsSkuCommentCountDtos;
    }

    /**
     * 根据商品skuIds查询商品有图的评价
     *
     * @param itemId 商品skuIds集合
     * @return
     */
    private Integer getCommentPicBySkuIds(Long itemId) {
        EntityWrapper<GoodsSkuCommentEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("itemId", itemId);
        cond.eq("isImage", YES_IMAGE);
        List<GoodsSkuCommentEntity> goodsSkuCommentEntities = goodsSkuCommentDao.selectList(cond);
        if (CollectionUtils.isEmpty(goodsSkuCommentEntities)) {
            return 0;
        }
        return goodsSkuCommentEntities.size();
    }

    /**
     * 根据type查询对应条数
     *
     * @param type   1好评 2 中评 3 差评
     * @param itemId 商品itemId
     * @return
     */
    private Integer getCountByType(Integer type, Long itemId) {
        EntityWrapper<GoodsSkuCommentEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("itemId", itemId);
        cond.eq("type", type);
        return goodsSkuCommentDao.selectCount(cond);
    }
}
