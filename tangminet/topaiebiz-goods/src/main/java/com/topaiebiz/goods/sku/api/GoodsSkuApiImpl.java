package com.topaiebiz.goods.sku.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.api.GoodsSkuApi;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryAttrDao;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryAttrEntity;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.goods.dto.sku.SaleAttributeDTO;
import com.topaiebiz.goods.dto.sku.StorageUpdateDTO;
import com.topaiebiz.goods.sku.dao.GoodsSkuDao;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.entity.GoodsSkuEntity;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import com.topaiebiz.goods.sku.exception.GoodsSkuExceptionEnum;
import com.topaiebiz.goods.sku.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by dell on 2018/1/6.
 */
@Slf4j
@Service
public class GoodsSkuApiImpl implements GoodsSkuApi {

    @Autowired
    private GoodsSkuDao goodsSkuDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private BackendCategoryAttrDao backendCategoryAttrDao;

    @Autowired
    MessageSender messageSender;

    @Autowired
    private MessageUtil messageUtil;

    @Override
    public GoodsSkuDTO getGoodsSku(Long skuId) {
        /** 判断商品skuId是否为空 */
        if (skuId == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODS_SKU_ID_NOT_EXISTS);
        }
        /** 判断商品skuId是否存在 */
        GoodsSkuEntity condition = new GoodsSkuEntity();
        condition.clearInit();
        condition.setId(skuId);
        condition.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        GoodsSkuEntity goodsSkuEntity = goodsSkuDao.selectOne(condition);
        if (goodsSkuEntity == null) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
        }
        ItemEntity condition1 = new ItemEntity();
        condition1.clearInit();
        condition1.setId(goodsSkuEntity.getItemId());
        condition1.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        ItemEntity itemEntity = itemDao.selectOne(condition1);
        GoodsSkuDTO goodsSkuDTO = new GoodsSkuDTO();
        ItemDTO itemDTO = new ItemDTO();
        BeanCopyUtil.copy(itemEntity, itemDTO);
        BeanCopyUtil.copy(goodsSkuEntity, goodsSkuDTO);
        goodsSkuDTO.setItem(itemDTO);
        return goodsSkuDTO;
    }

    @Override
    public List<GoodsSkuDTO> getGoodsSkuList(Long itemId) {
        if (null == itemId) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        ItemEntity item = itemDao.selectById(itemId);
        if (null == item) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_EXIST);
        }
        List<GoodsSkuDTO> goodsSkuDTOS = goodsSkuDao.selectGoodsSkuDTO(itemId);
        return goodsSkuDTOS;
    }

    @Override
    public Map<Long, GoodsSkuDTO> getGoodsSkuMap(List<Long> skuIds) {
        if (CollectionUtils.isEmpty(skuIds)) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        Map<Long, GoodsSkuDTO> itemDtoMap = new HashMap<Long, GoodsSkuDTO>();

        EntityWrapper<GoodsSkuEntity> condition = new EntityWrapper<>();
        condition.in("id", skuIds);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<GoodsSkuEntity> goodsSkus = goodsSkuDao.selectList(condition);
        if (CollectionUtils.isEmpty(goodsSkus)) {
            return Maps.newHashMap();
        }

        List<Long> itemIds = goodsSkus.stream().map(goodsSku -> goodsSku.getItemId()).collect(Collectors.toList());

        EntityWrapper<ItemEntity> itemCondition = new EntityWrapper<>();
        itemCondition.in("id", itemIds);
        itemCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<ItemEntity> itemList = itemDao.selectList(itemCondition);


        Map<Long, ItemEntity> itemMap = itemList.stream().collect(Collectors.toMap(ItemEntity::getId, item -> item));

        for (GoodsSkuEntity goodsSku : goodsSkus) {
            Long skuId = goodsSku.getId();
            GoodsSkuDTO goodsSkuDTO = new GoodsSkuDTO();
            BeanCopyUtil.copy(goodsSku, goodsSkuDTO);

            //根据itemId查询商品信息
            ItemEntity itemEntity = itemMap.get(goodsSku.getItemId());
            if (itemEntity == null) {
                itemDtoMap.put(skuId, null);
                continue;
            }
            ItemDTO itemDTO = new ItemDTO();
            BeanCopyUtil.copy(itemEntity, itemDTO);
            goodsSkuDTO.setItem(itemDTO);
            itemDtoMap.put(skuId, goodsSkuDTO);
        }
        return itemDtoMap;
    }

    @Override
    public List<GoodsSkuDTO> getGoodsSkuList(List<Long> itemIds) {
        if (CollectionUtils.isEmpty(itemIds)) {
            throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_ID_NOT_NULL);
        }
        EntityWrapper<GoodsSkuEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.in("itemId", itemIds);
        List<GoodsSkuEntity> goodsSkuEntityList = goodsSkuDao.selectList(cond);
        if (CollectionUtils.isEmpty(goodsSkuEntityList)) {
            return Collections.EMPTY_LIST;
        }
        return BeanCopyUtil.copyList(goodsSkuEntityList, GoodsSkuDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer descreaseStorages(Long orderId, List<StorageUpdateDTO> updates) {
        Integer updateCount = 0;
        if (!CollectionUtils.isEmpty(updates)) {
            for (StorageUpdateDTO storageUpdateDTO : updates) {
                int update = goodsSkuDao.reduceStock(storageUpdateDTO.getSkuId(), storageUpdateDTO.getNum());
                Long salesVolume = itemDao.selectSalesVolomeById(storageUpdateDTO.getItemId());
                int integer = itemDao.updateSales(salesVolume, storageUpdateDTO.getItemId());
                if (update == 0) {
                    throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_STOCKNUMBER_DEFICIENCY);
                }
                if (integer == 0) {
                    throw new GlobalException(GoodsSkuExceptionEnum.SALES_OF_SYNCHRONIZATION);
                }

                Long stockNumber = findStockNumber(storageUpdateDTO.getItemId());
                if (stockNumber == 0) {
                    messageUtil.outItem(storageUpdateDTO.getItemId());
                } else {
                    messageUtil.changItem(storageUpdateDTO.getItemId());
                }
                updateCount += update;
            }
        }
        return updateCount;
    }

    /**
     * 查询商品库存是否为0
     *
     * @param itemId
     */
    private Long findStockNumber(Long itemId) {
        // 查询每件商品的库存
        EntityWrapper<GoodsSkuEntity> cond = new EntityWrapper<>();
        cond.eq("itemId", itemId);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<GoodsSkuEntity> goodsSkuEntityList = goodsSkuDao.selectList(cond);
        return goodsSkuEntityList.stream().mapToLong(GoodsSkuEntity::getStockNumber).sum();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer inscreaseStorages(Long orderId, List<StorageUpdateDTO> updates) {
        if (null == orderId || orderId <= 0) {
            throw new GlobalException(GoodsSkuExceptionEnum.ORDER_NUMBER_ERRO);
        }
        log.info("订单取消库存回退！orderid={}, updates={}", orderId, JSON.toJSONString(updates));
        Integer updateCount = 0;
        if (!CollectionUtils.isEmpty(updates)) {
            for (StorageUpdateDTO storageUpdateDTO : updates) {
                Long stockNumber = findStockNumber(storageUpdateDTO.getItemId());
                int update = goodsSkuDao.backStock(storageUpdateDTO.getSkuId(), storageUpdateDTO.getNum());
                Long salesVolume = itemDao.selectSalesVolomeById(storageUpdateDTO.getItemId());
                int integer = itemDao.updateSales(salesVolume, storageUpdateDTO.getItemId());
                if (update == 0) {
                    log.error("商品库存回退失败！orderid={}, skuId={}", orderId, storageUpdateDTO.getSkuId());
                    throw new GlobalException(GoodsSkuExceptionEnum.GOODSITEM_STOCKBACK_FAIL);
                }
                if (integer == 0) {
                    log.error("同步商品销量失败！orderid={}, itemId={}", orderId, storageUpdateDTO.getItemId());
                    throw new GlobalException(GoodsSkuExceptionEnum.SALES_OF_SYNCHRONIZATION);
                }
                messageUtil.changItem(storageUpdateDTO.getItemId());
                if (stockNumber == 0) {
                    messageUtil.putItem(storageUpdateDTO.getItemId());
                }
                updateCount += update;
            }
        }

        return updateCount;
    }

    @Override
    public void loadSaleAttributes(GoodsSkuDTO skuDTO) {
        if (skuDTO == null) {
            return;
        }

        //step 1 : saleFields -> saleAttribute
        String skuDTOSaleFieldValue = skuDTO.getSaleFieldValue();
        List<SaleAttributeDTO> saleAttributeDTOS = parseSaleFieldValue(skuDTOSaleFieldValue);
        skuDTO.setSaleAttributes(saleAttributeDTOS);

        //step 2 : load categories
        Set<Long> categoryIdSet = new HashSet<>();
        for (SaleAttributeDTO attr : skuDTO.getSaleAttributes()) {
            categoryIdSet.add(attr.getId());
        }
        List<Long> categoryIds = new ArrayList<>(categoryIdSet);
        EntityWrapper<BackendCategoryAttrEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.in("id", categoryIds);
        List<BackendCategoryAttrEntity> categoryList = backendCategoryAttrDao.selectList(cond);
        Map<Long, BackendCategoryAttrEntity> categoryMap = categoryList.stream().collect(Collectors.toMap(BackendCategoryAttrEntity::getId, item -> item));

        //step 3 : saleAttribute -> salesFaields
        renderSaleAttributeValue(skuDTO, categoryMap);

    }

    @Override
    public void loadSaleAttributes(List<GoodsSkuDTO> skuDTOs) {
        if (CollectionUtils.isEmpty(skuDTOs)) {
            return;
        }

        //step 1 : saleFields -> saleAttribute
        for (GoodsSkuDTO sku : skuDTOs) {
            String saleFieldValue = sku.getSaleFieldValue();
            List<SaleAttributeDTO> saleAttributes = parseSaleFieldValue(saleFieldValue);
            sku.setSaleAttributes(saleAttributes);
        }

        //step 2 : load categories
        Set<Long> categoryIdSet = new HashSet<>();
        for (GoodsSkuDTO sku : skuDTOs) {
            for (SaleAttributeDTO attr : sku.getSaleAttributes()) {
                categoryIdSet.add(attr.getId());
            }
        }

        Map<Long, BackendCategoryAttrEntity> categoryMap = new HashMap();
        if (CollectionUtils.isEmpty(categoryIdSet)) {
            log.warn("SKU {} 没有销售属性", JSON.toJSONString(skuDTOs));
        } else {
            EntityWrapper<BackendCategoryAttrEntity> cond = new EntityWrapper<>();
            cond.in("id", Lists.newArrayList(categoryIdSet));
            List<BackendCategoryAttrEntity> categoryList = backendCategoryAttrDao.selectList(cond);
            categoryMap = categoryList.stream().collect(Collectors.toMap(BackendCategoryAttrEntity::getId, item -> item));
        }
        //step 3 : saleAttribute -> salesFaields
        for (GoodsSkuDTO sku : skuDTOs) {
            renderSaleAttributeValue(sku, categoryMap);
        }
    }

    @Override
    public Boolean inventoryIsZero(Long skuId, Long storeId, Long num) {
        Boolean update = false;
        GoodsSkuEntity goodsSku = new GoodsSkuEntity();
        goodsSku.clearInit();
        goodsSku.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        goodsSku.setId(skuId);
        GoodsSkuEntity goodsSkuEntity = goodsSkuDao.selectOne(goodsSku);
        if (goodsSkuEntity != null) {
            ItemEntity item = new ItemEntity();
            item.clearInit();
            item.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            item.setId(goodsSkuEntity.getItemId());
            ItemEntity itemEntity = itemDao.selectOne(item);
            if (!itemEntity.getBelongStore().equals(storeId)) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODS_NOT_SHOP);
            }
            goodsSkuEntity.setStockNumber(num);
            update = goodsSkuDao.updateById(goodsSkuEntity) > 0;
        }
        return update;
    }

    @Override
    public Boolean stockNumberToZero(String articleNumber, Long storeId, Long num) {
        log.info("根据商品货号和商品店铺Id修改商品库存！articleNumber={}, storeId={},num = {}", articleNumber, storeId, num);
        if (StringUtils.isBlank(articleNumber)) {
            throw new GlobalException(GoodsSkuExceptionEnum.ARTICLE_NUMBER_DOES_NOT_EXIST);
        }
        EntityWrapper<GoodsSkuEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("articleNumber", articleNumber);
        List<GoodsSkuEntity> goodsSkuEntities = goodsSkuDao.selectList(cond);
        if (CollectionUtils.isEmpty(goodsSkuEntities)) {
            //throw new GlobalException(GoodsSkuExceptionEnum.NUMBER_NOT_FIND_GOODSSKU);
            log.warn("无此货号:{}", articleNumber);
            return false;
        }
        if (goodsSkuEntities.size() > 1) {
            log.warn("stockNumberToZero 根据商品货号和商品店铺Id修改商品库存!  " +
                            "articleNumber={},<货号> storeId={}，<店铺id> goodsSkuSize= {} <sku条数>",
                    articleNumber, storeId, goodsSkuEntities.size());
        }
        Integer update = 0;
        for (GoodsSkuEntity goodsSkuEntity : goodsSkuEntities) {
            ItemEntity item = new ItemEntity();
            item.clearInit();
            item.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            item.setId(goodsSkuEntity.getItemId());
            ItemEntity itemEntity = itemDao.selectOne(item);
            if (!itemEntity.getBelongStore().equals(storeId)) {
                throw new GlobalException(GoodsSkuExceptionEnum.GOODS_NOT_SHOP);
            }
            goodsSkuEntity.setStockNumber(num);
            Integer i = goodsSkuDao.updateById(goodsSkuEntity);
            update += i;
        }
        return update > 0;
    }

    private void renderSaleAttributeValue(GoodsSkuDTO sku, Map<Long, BackendCategoryAttrEntity> categoryMap) {
        if (CollectionUtils.isEmpty(sku.getSaleAttributes())) {
            sku.setSaleFieldValue(" : ");
            return;
        }

        for (SaleAttributeDTO attr : sku.getSaleAttributes()) {
            BackendCategoryAttrEntity backCategoryAttr = categoryMap.get(attr.getId());
            attr.setName(backCategoryAttr.getName());
        }

        List<String> attributeItems = new ArrayList<>();
        for (SaleAttributeDTO attr : sku.getSaleAttributes()) {
            attributeItems.add(StringUtils.join(attr.getName(), ":", attr.getValue()));
        }
        sku.setSaleFieldValue(StringUtils.join(attributeItems, ","));
    }

    private List<SaleAttributeDTO> parseSaleFieldValue(String saleFieldValue) {
        if (StringUtils.isBlank(saleFieldValue)) {
            return Lists.newArrayList();
        }
        String[] split = saleFieldValue.split(",");
        List<SaleAttributeDTO> attributeDTOS = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            String[] strs = split[i].split(":");
            Long id = Long.parseLong(strs[0]);
            SaleAttributeDTO dto = new SaleAttributeDTO();
            dto.setId(id);
            dto.setValue(strs[1]);
            attributeDTOS.add(dto);
        }
        return attributeDTOS;
    }


}
