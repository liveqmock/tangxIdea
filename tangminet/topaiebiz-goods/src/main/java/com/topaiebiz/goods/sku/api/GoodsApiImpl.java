package com.topaiebiz.goods.sku.api;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.brand.dao.BrandDao;
import com.topaiebiz.goods.brand.entity.BrandEntity;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryAttrDao;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryDao;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryAttrEntity;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryEntity;
import com.topaiebiz.goods.constants.GoodsConstants;
import com.topaiebiz.goods.constants.ItemConstants;
import com.topaiebiz.goods.dto.sku.*;
import com.topaiebiz.goods.goodsenum.ItemStatusEnum;
import com.topaiebiz.goods.sku.dao.GoodsSkuDao;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.dao.ItemPictureDao;
import com.topaiebiz.goods.sku.entity.GoodsSkuEntity;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import com.topaiebiz.goods.sku.entity.ItemPictureEntity;
import com.topaiebiz.goods.sku.exception.GoodsSkuExceptionEnum;
import com.topaiebiz.message.api.TemplateApi;
import com.topaiebiz.promotion.api.PromotionApi;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.dto.PromotionGoodsDTO;
import com.topaiebiz.system.util.SecurityContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hecaifeng on 2018/1/4.
 */
@Service
@Slf4j
class GoodsApiImpl implements GoodsApi {

    private static final String IMAGEPATH = "https://shopnc-oss.oss-cn-hangzhou.aliyuncs.com/";
    private static final int w = 100;
    private static final int h = 100;
    private static final Integer COUNT_ITEM = 3;
    private static String DEFAULT_PREVFIX = "update_";


    @Autowired
    private ItemDao itemDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private BackendCategoryDao backendCategoryDao;

    @Autowired
    private PromotionApi promotionApi;

    @Autowired
    private GoodsSkuDao goodsSkuDao;

    @Autowired
    private TemplateApi templateApi;

    @Autowired
    private ItemPictureDao itemPictureDao;

    @Autowired
    private BackendCategoryAttrDao backendCategoryAttrDao;

    @Override
    public ItemDTO getItem(Long itemId) {
        if (null == itemId) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        ItemEntity item = itemDao.selectById(itemId);
        if (null == item) {
            return null;
        }
        ItemDTO itemDTO = new ItemDTO();
        BeanCopyUtil.copy(item, itemDTO);
        itemDTO.setSalesVolome(item.getSalesVolume());
        return itemDTO;
    }

    @Override
    public ItemAppDTO getGoods(Long goodsId) {
        if (null == goodsId) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        ItemEntity item = itemDao.selectById(goodsId);
        if (null == item) {
            return null;
        }
        ItemAppDTO itemAppDTO = new ItemAppDTO();
        BeanCopyUtil.copy(item, itemAppDTO);
        if (item.getBelongBrand() != null) {
            BrandEntity brandEntity = brandDao.selectById(item.getBelongBrand());
            if (null != brandEntity) {
                itemAppDTO.setBrandName(brandEntity.getName());
            }
        }
        BackendCategoryEntity backendCategoryEntity =
                backendCategoryDao.selectById(item.getBelongCategory());
        if (backendCategoryEntity != null) {
            itemAppDTO.setCategoryName(backendCategoryEntity.getName());
        }
        return itemAppDTO;
    }

    @Override
    public Long getAllSkusStorage(Long itemId) {
        Long stockNumberts = itemDao.selecStockNumbertById(itemId);
        return stockNumberts;
    }

    @Override
    public List<ItemDTO> getItemMap(List<Long> itemIds) {
        if (CollectionUtils.isEmpty(itemIds)) {
            return Collections.emptyList();
        }
        EntityWrapper<ItemEntity> items = new EntityWrapper<>();
        items.in("id", itemIds);
        items.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<ItemEntity> itemEntities = itemDao.selectList(items);
        List<ItemDTO> itemDTOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(itemEntities)) {
            for (ItemEntity itemEntity : itemEntities) {
                ItemDTO itemDTO = new ItemDTO();
                BeanCopyUtil.copy(itemEntity, itemDTO);
                itemDTOS.add(itemDTO);
            }
        }
        return itemDTOS;
    }

    @Override
    public List<ItemDTO> getItems(Long belongStore, Page page) {
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setBelongStore(belongStore);
        List<ItemDTO> itemDTOS = itemDao.selectItems(page, itemDTO);
        return itemDTOS;
    }

    @Override
    public List<ItemDTO> getItemByLogisticsId(Long logisticsId) {
        EntityWrapper<ItemEntity> itemEntities = new EntityWrapper<>();
        itemEntities.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        itemEntities.eq("logisticsId", logisticsId);
        List<ItemEntity> itemList = itemDao.selectList(itemEntities);
        List<ItemDTO> itemDTOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(itemList)) {
            for (ItemEntity itemEntity : itemList) {
                ItemDTO itemDTO = new ItemDTO();
                BeanCopyUtil.copy(itemEntity, itemDTO);
                itemDTOS.add(itemDTO);
            }
        }
        return itemDTOS;
    }

    @Override
    public List<GoodsDTO> getGoodsSort(List<GoodsDTO> goodsDTOS) {
        if (CollectionUtils.isEmpty(goodsDTOS)) {
            return Collections.EMPTY_LIST;
        }
        List<Long> itemIds = goodsDTOS.stream().map(goodsDTO -> goodsDTO.getGoodsId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(itemIds)) {
            return Collections.EMPTY_LIST;
        }
        EntityWrapper<ItemEntity> item = new EntityWrapper<>();
        item.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        item.in("id", itemIds);
        List<ItemEntity> itemEntities = itemDao.selectList(item);
        Map<Long, ItemEntity> itemMap =
                itemEntities.stream().collect(Collectors.toMap(ItemEntity::getId, itemEntity -> itemEntity));
        List<GoodsDTO> goodsDTOList = new ArrayList<>();

        for (GoodsDTO dto : goodsDTOS) {
            GoodsDTO goodsDTO = new GoodsDTO();
            ItemEntity entity = itemMap.get(dto.getGoodsId());
            if (entity == null) {
                continue;
            }
            goodsDTO.setDefaultPrice(entity.getMinPrice());
            goodsDTO.setGoodsId(entity.getId());
            goodsDTO.setId(dto.getId());
            goodsDTO.setName(entity.getName());
            goodsDTO.setSortNo(dto.getSortNo());
            goodsDTO.setMarketPrice(entity.getMarketPrice());
            goodsDTO.setPictureName(entity.getPictureName());
            goodsDTO.setSalesVolome(entity.getSalesVolume());
            goodsDTOList.add(goodsDTO);
        }
        return goodsDTOList;
    }

    @Override
    public List<GoodsDecorateDTO> getGoodsDecorate(List<GoodsDecorateDTO> goodsDecorateDTOS) {
        if (CollectionUtils.isEmpty(goodsDecorateDTOS)) {
            return Collections.EMPTY_LIST;
        }
        List<Long> goodsIds = goodsDecorateDTOS.stream().map(goodsDTO -> goodsDTO.getGoodsId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(goodsIds)) {
            return Collections.EMPTY_LIST;
        }
        EntityWrapper<ItemEntity> item = new EntityWrapper<>();
        item.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        item.in("id", goodsIds);
        item.eq("status", ItemStatusEnum.ITEM_STATUS_PUTAWAY.getCode());
        item.eq("frozenFlag", ItemConstants.FrozenFlag.NO_FROZEN);
        List<ItemEntity> itemEntities = itemDao.selectList(item);

        if (CollectionUtils.isEmpty(itemEntities)) {
            return Collections.EMPTY_LIST;
        }
        List<GoodsDecorateDTO> goodsDecorateDTOList = new ArrayList<>();

        Map<Long, ItemEntity> itemMap =
                itemEntities.stream().collect(Collectors.toMap(ItemEntity::getId, itemEntity -> itemEntity));
        for (GoodsDecorateDTO dto : goodsDecorateDTOS) {
            ItemEntity entity = itemMap.get(dto.getGoodsId());
            if (entity != null) {
                GoodsDecorateDTO goodsDecorateDTO = new GoodsDecorateDTO();
                goodsDecorateDTO.setSortNo(dto.getSortNo());
                goodsDecorateDTO.setGoodsId(entity.getId());
                List<Long> goodsSkuId = getGoodsSkuId(entity.getId());
                if (CollectionUtils.isNotEmpty(goodsSkuId)) {
                    boolean b = loadItemPromotions(goodsDecorateDTO, goodsSkuId);
                }
                goodsDecorateDTO.setName(entity.getName());
                goodsDecorateDTO.setDefaultPrice(entity.getDefaultPrice());
                goodsDecorateDTO.setMarketPrice(entity.getMarketPrice());
                goodsDecorateDTO.setPictureName(entity.getPictureName());
                goodsDecorateDTO.setSalesVolome(entity.getSalesVolume());
                goodsDecorateDTO.setIntegralRatio(entity.getIntegralRatio());
                goodsDecorateDTO.setCommentCount(entity.getCommentCount());
                goodsDecorateDTOList.add(goodsDecorateDTO);
            }
        }
        return goodsDecorateDTOList;
    }

    private boolean loadItemPromotions(GoodsDecorateDTO goodsDecorateDTO, List<Long> goodsSkuIds) {
        List<BigDecimal> bigDecimals = new ArrayList<>();
        for (Long skuId : goodsSkuIds) {
            // 秒杀
            PromotionDTO seckill = promotionApi.getSeckill(skuId);
            if (seckill != null) {
                List<PromotionGoodsDTO> limitGoods = seckill.getLimitGoods();
                if (!CollectionUtils.isEmpty(limitGoods)) {
                    PromotionGoodsDTO promotionGoodsDTO = limitGoods.get(0);
                    bigDecimals.add(promotionGoodsDTO.getPromotionPrice());
                    goodsDecorateDTO.setStareTime(seckill.getStartTime());
                    goodsDecorateDTO.setEndTime(seckill.getEndTime());
                }
            }

            // 单品折扣和一口价
            List<PromotionDTO> singlePromotion = promotionApi.getSinglePromotions(skuId);
            if (CollectionUtils.isNotEmpty(singlePromotion)) {
                PromotionDTO promotionDTO = singlePromotion.get(0);
                List<PromotionGoodsDTO> limitGoods1 = promotionDTO.getLimitGoods();
                PromotionGoodsDTO promotionGoodsDTO = limitGoods1.get(0);
                BigDecimal promotionPrice = promotionGoodsDTO.getPromotionPrice();
                bigDecimals.add(promotionPrice);
                goodsDecorateDTO.setStareTime(promotionDTO.getStartTime());
                goodsDecorateDTO.setEndTime(promotionDTO.getEndTime());
            }
        }
        return min(goodsDecorateDTO, bigDecimals);
    }

    private boolean min(GoodsDecorateDTO goodsDecorateDTO, List<BigDecimal> bigDecimals) {
        if (CollectionUtils.isEmpty(bigDecimals)) {
            return false;
        }
        BigDecimal min = Collections.min(bigDecimals);
        goodsDecorateDTO.setActivityPrice(min);
        return true;
    }

    private List<Long> getGoodsSkuId(Long itemId) {
        EntityWrapper<GoodsSkuEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("itemId", itemId);
        List<GoodsSkuEntity> goodsSkus = goodsSkuDao.selectList(cond);
        List<Long> goodsSkuIds = goodsSkus.stream().map(goodsSku -> goodsSku.getId()).collect(Collectors.toList());
        return goodsSkuIds;
    }


    @Override
    public List<StoreGoodsDTO> getStoreGoods(Long storeId) {
        EntityWrapper<ItemEntity> item = new EntityWrapper<>();
        item.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        item.eq("status", ItemStatusEnum.ITEM_STATUS_PUTAWAY.getCode());
        item.eq("belongStore", storeId);
        List<ItemEntity> itemEntities = itemDao.selectList(item);

        List<StoreGoodsDTO> storeGoodsDTOS = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(itemEntities)) {
            for (ItemEntity itemEntity : itemEntities) {
                StoreGoodsDTO storeGoodsDTO = new StoreGoodsDTO();
                storeGoodsDTO.setPictureName(itemEntity.getPictureName());
                storeGoodsDTO.setId(itemEntity.getId());
                storeGoodsDTOS.add(storeGoodsDTO);
                if (storeGoodsDTOS.size() == COUNT_ITEM) {
                    break;
                }
            }
        }
        return storeGoodsDTOS;
    }

    @Override
    public boolean updateGoods(Long storeId, Integer frozenFlag) {
        Integer i = 0;
        EntityWrapper<ItemEntity> items = new EntityWrapper<>();
        items.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        items.eq("belongStore", storeId);
        List<ItemEntity> itemEntities = itemDao.selectList(items);
        if (CollectionUtils.isNotEmpty(itemEntities)) {
            List<Long> goodsIds = itemEntities.stream().map(itemEntity -> itemEntity.getId()).collect(Collectors.toList());
            Long[] ids = (Long[]) goodsIds.toArray(new Long[0]);
            for (ItemEntity itemEntity : itemEntities) {
                itemEntity.setFrozenFlag(frozenFlag);
                itemEntity.setLastModifiedTime(new Date());
                itemEntity.setLogisticsId(SecurityContextUtils.getCurrentUserDto().getId());
                i = itemDao.updateById(itemEntity);
            }
            templateApi.removeItem(ids);
        }
        return i > 0;
    }

    @Override
    @Transactional
    public boolean saveItems(List<ApiGoodsDTO> goodsListDTOS) {
        if (CollectionUtils.isEmpty(goodsListDTOS)) {
            return false;
        }
        for (ApiGoodsDTO goodsListDTO : goodsListDTOS) {
            ItemEntity itemEntity = new ItemEntity();
            boolean item = insertItem(itemEntity, goodsListDTO);
            if (item == false) {
                log.error("商品添加失败！商品名称为{}", goodsListDTO.getItemName());
                throw new GlobalException(GoodsSkuExceptionEnum.ITEM_INSERT_FIAL);
            }
            List<ApiGoodsPictureDTO> goodsPictures = goodsListDTO.getGoodsPictures();
            if (CollectionUtils.isEmpty(goodsPictures)) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODS_PICTURE);
            }
            boolean picture = insertItemPicture(goodsPictures, itemEntity);
            if (picture == false) {
                log.error("商品图片添加失败！商品名称为{},商品ID={}", goodsListDTO.getItemName(), itemEntity.getId());
                throw new GlobalException(GoodsSkuExceptionEnum.ITEM_INSERT_FIAL);
            }
            List<ApiGoodsSkuDTO> goodsSkus = goodsListDTO.getGoodsSkus();
            if (CollectionUtils.isEmpty(goodsSkus)) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODS_SKU);
            }
            boolean goodsSku = insertGoodsSku(itemEntity, goodsSkus);
            if (goodsSku == false) {
                log.error("商品SKU添加失败！商品名称为{},商品ID={}", goodsListDTO.getItemName(), itemEntity.getId());
                throw new GlobalException(GoodsSkuExceptionEnum.ITEM_INSERT_FIAL);
            }
        }
        return true;
    }

    /**
     * 添加商品sku信息
     *
     * @param itemEntity
     * @param goodsSkus
     * @return
     */
    private boolean insertGoodsSku(ItemEntity itemEntity, List<ApiGoodsSkuDTO> goodsSkus) {
        for (ApiGoodsSkuDTO goodsSku : goodsSkus) {
            GoodsSkuEntity goodsSkuEntity = new GoodsSkuEntity();
            goodsSkuEntity.setItemId(itemEntity.getId());
            goodsSkuEntity.setSaleImage(goodsSku.getSaleImage());
            goodsSkuEntity.setBaseFieldValue(goodsSku.getBaseFieldValue());
            goodsSkuEntity.setSaleFieldValue(goodsSku.getSaleFieldValue());
            goodsSkuEntity.setPrice(goodsSku.getPrice());
            goodsSkuEntity.setStockNumber(goodsSku.getStockNumber());
            goodsSkuEntity.setArticleNumber(goodsSku.getArticleNumber());
            goodsSkuEntity.setBarCode(goodsSku.getBarCode());
            boolean result = goodsSkuDao.insert(goodsSkuEntity) > 0;
            if (!result) {
                return false;
            }
        }
        return true;
    }

    /**
     * 添加商品信息
     *
     * @param itemEntity
     * @param goodsListDTO
     * @return
     */
    private boolean insertItem(ItemEntity itemEntity, ApiGoodsDTO goodsListDTO) {
        itemEntity.setItemCode(goodsListDTO.getItemCode());
        itemEntity.setName(goodsListDTO.getItemName());
        itemEntity.setBrokerageRatio(goodsListDTO.getBrokerageRatio());
        itemEntity.setPictureName(goodsListDTO.getItemPicture());
        itemEntity.setMarketPrice(goodsListDTO.getMarketPrice());
        itemEntity.setDefaultPrice(goodsListDTO.getDefaultPrice());
        itemEntity.setBelongStore(goodsListDTO.getBelongStore());
        itemEntity.setBelongBrand(goodsListDTO.getBelongBrand());
        itemEntity.setBelongCategory(goodsListDTO.getBelongCategory());
        itemEntity.setStatus(goodsListDTO.getItemStatus());
        itemEntity.setFrozenFlag(goodsListDTO.getFrozenFlag());
        itemEntity.setLogisticsId(goodsListDTO.getLogisticsId());
        itemEntity.setWeightBulk(goodsListDTO.getWeightBulk());
        itemEntity.setDescription(goodsListDTO.getDescription());
        return itemDao.insert(itemEntity) > 0;
    }

    /**
     * 添加商品图片
     *
     * @param goodsPictures
     * @return
     */
    private boolean insertItemPicture(List<ApiGoodsPictureDTO> goodsPictures, ItemEntity itemEntity) {
        String mainPic = null;
        for (ApiGoodsPictureDTO goodsPicture : goodsPictures) {
            ItemPictureEntity itemPictureEntity = new ItemPictureEntity();
            itemPictureEntity.setItemId(itemEntity.getId());
            itemPictureEntity.setName(goodsPicture.getName());
            itemPictureEntity.setIsMain(goodsPicture.getIsMain());
            itemPictureEntity.setType(goodsPicture.getType());
            if (itemPictureEntity.isMainPic() && StringUtils.isBlank(mainPic)) {
                mainPic = itemPictureEntity.getName();
            }
            // itemPictureEntity.setName(ImageUtil.thumbnailImage(itemPictureEntity.getName(), w, h, DEFAULT_PREVFIX, false));
            boolean result = itemPictureDao.insert(itemPictureEntity) > 0;
            if (!result) {
                return false;
            }
        }
        ItemEntity itemUpdate = new ItemEntity();
        itemUpdate.cleanInit();
        itemUpdate.setId(itemEntity.getId());
        ItemEntity item = itemDao.selectOne(itemUpdate);
        if (mainPic == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODS_PICTURE_MAIN);
        }
        itemEntity.setPictureName(mainPic);
        return itemDao.updateById(item) > 0;
    }

    @Override
    public PageInfo<ApiGoodsDTO> getGoodsList(ApiGoodsQueryDTO apiGoodsQueryDTO) {
        GoodsQueryDTO goodsQueryDTO = new GoodsQueryDTO();
        BeanCopyUtil.copy(apiGoodsQueryDTO, goodsQueryDTO);
        if (goodsQueryDTO.getStoreId() == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.STOREID);
        }
        EntityWrapper<ItemEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("belongStore", goodsQueryDTO.getStoreId());
        if (goodsQueryDTO.getItemId() != null) {
            cond.eq("id", goodsQueryDTO.getItemId());
        }
        if (goodsQueryDTO.getItemName() != null) {
            cond.like("name", goodsQueryDTO.getItemName());
        }
        if (goodsQueryDTO.getItemStatus() != null) {
            cond.eq("status", goodsQueryDTO.getItemStatus());
        }
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(goodsQueryDTO.getPageNo());
        pagePO.setPageSize(goodsQueryDTO.getPageSize());
        Page<ApiGoodsDTO> page = PageDataUtil.buildPageParam(pagePO);
        List<ItemEntity> itemEntities = itemDao.selectPage(page, cond);
        if (CollectionUtils.isEmpty(itemEntities)) {
            return PageDataUtil.copyPageInfo(null);
        }
        List<GoodsListDTO> goodsList = BeanCopyUtil.copyList(itemEntities, GoodsListDTO.class);

        List<ApiGoodsDTO> apiGoodsList = getGoodsList(goodsList);

        List<Long> itemIds = itemEntities.stream().map(itemEntity -> itemEntity.getId()).collect(Collectors.toList());
        EntityWrapper<GoodsSkuEntity> skuCond = new EntityWrapper<>();
        skuCond.in("itemId", itemIds);
        skuCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<GoodsSkuEntity> goodsSkuEntityList = goodsSkuDao.selectList(skuCond);
        List<GoodsSkusDTO> goodsSkus = BeanCopyUtil.copyList(goodsSkuEntityList, GoodsSkusDTO.class);

        List<ApiGoodsSkuDTO> apiGoodsSkus = getGoodsSkus(goodsSkus);

        getGoodsListDto(apiGoodsList, apiGoodsSkus);
        page.setRecords(apiGoodsList);
        return PageDataUtil.copyPageInfo(page);
    }

    private List<ApiGoodsSkuDTO> getGoodsSkus(List<GoodsSkusDTO> goodsSkus) {
        List<ApiGoodsSkuDTO> apiGoodsSkus = new ArrayList<>();
        for (GoodsSkusDTO goodsSku : goodsSkus) {
            ApiGoodsSkuDTO apiGoodsSku = new ApiGoodsSkuDTO();
            apiGoodsSku.setItemId(goodsSku.getItemId());
            apiGoodsSku.setSkuId(goodsSku.getId());
            apiGoodsSku.setSaleFieldValue(loadSaleFieldValue(goodsSku.getSaleFieldValue()));
            apiGoodsSku.setPrice(goodsSku.getPrice());
            apiGoodsSku.setArticleNumber(goodsSku.getArticleNumber());
            apiGoodsSku.setStockNumber(goodsSku.getStockNumber());
            apiGoodsSkus.add(apiGoodsSku);
        }
        return apiGoodsSkus;
    }

    private List<ApiGoodsDTO> getGoodsList(List<GoodsListDTO> goodsList) {
        List<ApiGoodsDTO> apiGoodsList = new ArrayList<>();
        for (GoodsListDTO goods : goodsList) {
            ApiGoodsDTO apiGoods = new ApiGoodsDTO();
            apiGoods.setItemId(goods.getId());
            apiGoods.setItemCode(goods.getItemCode());
            apiGoods.setItemName(goods.getName());
            apiGoods.setItemPicture(IMAGEPATH + goods.getPictureName());
            apiGoods.setItemStatus(goods.getStatus());
            apiGoods.setFrozenFlag(goods.getFrozenFlag());
            apiGoods.setDefaultPrice(goods.getDefaultPrice());
            // 查询每件商品的库存
            Long stockNumber = itemDao.selecStockNumbertById(goods.getId());
            if (stockNumber != null) {
                apiGoods.setItemNum(stockNumber);
            }
            apiGoodsList.add(apiGoods);
        }
        return apiGoodsList;
    }

    @Override
    public List<ItemDTO> getItemDTOs(List<Long> itemIds, Long storeId, Page page) {
        EntityWrapper<ItemEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("status", ItemStatusEnum.ITEM_STATUS_PUTAWAY.getCode());
        cond.eq("frozenFlag", ItemConstants.FrozenFlag.NO_FROZEN);
        cond.notIn("id", itemIds);
        cond.eq("belongStore", storeId);
        List<ItemEntity> itemEntities = itemDao.selectPage(page, cond);
        List<ItemDTO> itemDTOS = BeanCopyUtil.copyList(itemEntities, ItemDTO.class);
        return itemDTOS;
    }

    @Override
    public List<ItemDTO> getStoreItems(List<Long> storeIds, Page page, Integer status) {
        EntityWrapper<ItemEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("status", ItemStatusEnum.ITEM_STATUS_PUTAWAY.getCode());
        cond.eq("frozenFlag", ItemConstants.FrozenFlag.NO_FROZEN);
        if (GoodsConstants.BelongStore.YES_BELONGSTORE.equals(status)) {
            cond.in("belongStore", storeIds);
        }
        if (GoodsConstants.BelongStore.NO_BELONGSTORE.equals(status)) {
            cond.notIn("belongStore", storeIds);
        }
        List<ItemEntity> itemEntities = itemDao.selectPage(page, cond);
        List<ItemDTO> itemDTOS = BeanCopyUtil.copyList(itemEntities, ItemDTO.class);
        return itemDTOS;
    }

    @Override
    public Map<Long, Long> getStockNumberMap(List<Long> itemIds) {
        if (CollectionUtils.isEmpty(itemIds)) {
            return Collections.EMPTY_MAP;
        }
        EntityWrapper<GoodsSkuEntity> cond = new EntityWrapper<>();
        cond.in("itemId", itemIds);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<GoodsSkuEntity> goodsSkuEntityList = goodsSkuDao.selectList(cond);
        if (CollectionUtils.isEmpty(goodsSkuEntityList)) {
            return Collections.EMPTY_MAP;
        }
        Map<Long, Long> stockNumberMap = new HashMap<>();
        Map<Long, List<GoodsSkuEntity>> goodsSkuMap = goodsSkuEntityList.stream().collect(Collectors.groupingBy(GoodsSkuEntity::getItemId));
        for (Long itemId : itemIds) {
            List<GoodsSkuEntity> goodsSkuEntity = goodsSkuMap.get(itemId);
            long stockNumbers = goodsSkuEntity.stream().mapToLong(GoodsSkuEntity::getStockNumber).sum();
            stockNumberMap.put(itemId, stockNumbers);
        }
        return stockNumberMap;
    }

    @Override
    public List<OutGoodsDTO> getOutGoods(List<Long> itemIds) {
        if (CollectionUtils.isEmpty(itemIds)) {
            return Collections.emptyList();
        }
        EntityWrapper<ItemEntity> cond = new EntityWrapper<>();
        cond.in("id", itemIds);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.last("AND (status NOT IN (2) or frozenFlag = 1)");
        List<ItemEntity> itemEntities = itemDao.selectList(cond);
        List<OutGoodsDTO> outGoodsDTOS = BeanCopyUtil.copyList(itemEntities, OutGoodsDTO.class);
        return outGoodsDTOS;
    }

    @Override
    public List<ItemEsDTO> getItemEs(Long num, Integer limit) {
        if (limit == null) {
            return Collections.emptyList();
        }
        List<ItemEsDTO> items = itemDao.selectItemEs(num, limit);
        return items;
    }

    @Override
    public void updateMinPrice(Long itemId) {
        doUpdateMinPrice(Lists.newArrayList(itemId));
    }

    @Override
    public void updateMinPrice(List<Long> itemIds) {
        doUpdateMinPrice(itemIds);
    }

    private void doUpdateMinPrice(List<Long> itemIds) {
        if (CollectionUtils.isEmpty(itemIds)) {
            return;
        }
        EntityWrapper<GoodsSkuEntity> cond = new EntityWrapper<>();
        cond.in("itemId", itemIds);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.gt("stockNumber", 0);
        List<GoodsSkuEntity> skuLists = goodsSkuDao.selectList(cond);
        if (CollectionUtils.isEmpty(skuLists)) {
            return;
        }
        List<Long> skuIds = skuLists.stream().map(item -> item.getId()).collect(Collectors.toList());
        /** skuId -> promotions  **/
        Map<Long, PromotionDTO> promotionMap = promotionApi.getSkuPromotionMap(skuIds);

        /** itemId -> skuLists  **/
        Map<Long, List<GoodsSkuEntity>> itemSkusMap = skuLists.stream().collect(Collectors.groupingBy(GoodsSkuEntity::getItemId));
        for (Map.Entry<Long, List<GoodsSkuEntity>> entry : itemSkusMap.entrySet()) {
            Long itemId = entry.getKey();
            List<GoodsSkuEntity> skus = entry.getValue();
            updateItemMinPrice(itemId, skus, promotionMap);
        }
    }

    private void updateItemMinPrice(Long itemId, List<GoodsSkuEntity> itemSkus, Map<Long, PromotionDTO> skuPromotionMap) {
        List<BigDecimal> priceLists = new ArrayList<>();

        //统计sku的价格和所有单品营销的价格
        for (GoodsSkuEntity sku : itemSkus) {
            priceLists.add(sku.getPrice());
            PromotionDTO promotionDTO = skuPromotionMap.get(sku.getId());
            if (promotionDTO == null) {
                continue;
            }
            List<PromotionGoodsDTO> promotionGoodsList = promotionDTO.getLimitGoods();
            if (CollectionUtils.isEmpty(promotionGoodsList)) {
                continue;
            }
            for (PromotionGoodsDTO promotionGoodsDTO : promotionGoodsList) {
                priceLists.add(promotionGoodsDTO.getPromotionPrice());
            }
        }

        //取出所有sku和
        BigDecimal minPrice = Collections.min(priceLists);

        //更新item的最低价格
        ItemEntity update = new ItemEntity();
        update.setId(itemId);
        update.setLastModifiedTime(new Date());
        update.setMinPrice(minPrice);
        itemDao.updateById(update);
    }

    private void getGoodsListDto(List<ApiGoodsDTO> apiGoodsList, List<ApiGoodsSkuDTO> apiGoodsSkus) {
        for (ApiGoodsDTO goods : apiGoodsList) {
            if (CollectionUtils.isNotEmpty(apiGoodsSkus)) {
                List<ApiGoodsSkuDTO> goodsSkus = new ArrayList<>();
                for (ApiGoodsSkuDTO goodsSku : apiGoodsSkus) {
                    if (goods.getItemId().equals(goodsSku.getItemId())) {
                        goodsSkus.add(goodsSku);
                        goods.setGoodsSkus(goodsSkus);
                    }
                }

            }
        }
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
            if (selectById != null) {
                String name = selectById.getName();
                String value = attrArray[1];
                saleFieldValue = name + ":" + value + "  " + saleFieldValue;
            }
        }
        return saleFieldValue;
    }

}
