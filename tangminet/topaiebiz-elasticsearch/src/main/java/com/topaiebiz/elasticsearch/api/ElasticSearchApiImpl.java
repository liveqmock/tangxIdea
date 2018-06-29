package com.topaiebiz.elasticsearch.api;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.topaiebiz.elasticsearch.dto.ItemDto;
import com.topaiebiz.elasticsearch.dto.SyncResultDTO;
import com.topaiebiz.goods.api.BackendCategoryApi;
import com.topaiebiz.goods.api.BrandApi;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.api.GoodsSkuApi;
import com.topaiebiz.goods.constants.ItemConstants;
import com.topaiebiz.goods.constants.ItemStatusEnum;
import com.topaiebiz.goods.dto.Brand.BrandDTO;
import com.topaiebiz.goods.dto.category.backend.BackendCategoryDTO;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 13:30 2018/6/27
 * @Modified by:
 */
@Service
@Slf4j
public class ElasticSearchApiImpl implements ElasticSearchApi {

    @Autowired
    private GoodsApi goodsApi;

    @Autowired
    private GoodsSkuApi goodsSkuApi;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private BackendCategoryApi backendCategoryApi;

    @Autowired
    private BrandApi brandApi;

    @Autowired
    private RestClient restClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${elasticSearch.index}")
    private String index;


    /**
     * @param * @param null
     * @Author: tangx.w
     * @Description: 全量同步数据到搜索引擎
     * @Date: 2018/6/27 13:36
     */
    @Override
    public void syncAllItems() {
        Long startItemId = 0L;
        Integer step = 100;
        SyncResultDTO syncResultDTO = new SyncResultDTO();
        do {
            List<Long> itemIds = goodsApi.getItemEs(startItemId, step).stream().map(e -> e.getId()).collect(Collectors.toList());
            syncResultDTO = doSync(itemIds, startItemId);
            startItemId = syncResultDTO.getLastRecordId();
        } while (syncResultDTO.getResultSize() != 0);

    }


    /**
     * @param itemIds,startItemId
     * @Author: tangx.w
     * @Description: 同步数据
     * @Date: 2018/6/27 14:36
     */
    private SyncResultDTO doSync(List<Long> itemIds, Long startItemId) {
        SyncResultDTO syncResultDTO = new SyncResultDTO();
        if (CollectionUtils.isEmpty(itemIds)) {
            syncResultDTO.setResultSize(0);
            syncResultDTO.setLastRecordId(startItemId);
            return syncResultDTO;
        }
        long snycStart = System.currentTimeMillis();

        /** 批量同步数据到es **/
        syncItems(itemIds);

        syncResultDTO.setLastRecordId(itemIds.get(itemIds.size() - 1));
        syncResultDTO.setResultSize(itemIds.size());
        log.info("==>向搜素引擎同步{}条数据，用时{}秒", itemIds.size(), (System.currentTimeMillis() - snycStart) / 1000);
        return syncResultDTO;
    }


    /**
     * @param itemIds
     * @Author: tangx.w
     * @Description: 批量同步数据到es
     * @Date: 2018/6/29 14:05
     */
    @Override
    public void syncItems(List<Long> itemIds) {
        doSyncItem(itemIds);
    }

    /**
     * @param itemId
     * @Author: tangx.w
     * @Description: 同步单条数据到es
     * @Date: 2018/6/29 14:05
     */
    @Override
    public void syncItem(Long itemId) {
        doSyncItem(Lists.newArrayList(itemId));
    }

    private void doSyncItem(List<Long> itemIds) {
        if (CollectionUtils.isEmpty(itemIds)) {
            log.error("参数错误，itemIds参数为空！");
            return;
        }
        List<GoodsSkuDTO> goodsSkuDTOS = goodsSkuApi.getGoodsSkuList(itemIds);
        Map<Long, List<GoodsSkuDTO>> itemSkusMap = goodsSkuDTOS.stream().collect(Collectors.groupingBy(GoodsSkuDTO::getItemId));
        EntityWrapper<ItemEntity> wrapper = new EntityWrapper<>();
        wrapper.in("id", itemIds);
        List<ItemEntity> itemEsList = itemDao.selectList(wrapper);

        List<Long> syncIds = new ArrayList<>();
        List<Long> deleteIds = new ArrayList<>();
        for (ItemEntity itemEs : itemEsList) {
            List<GoodsSkuDTO> skus = itemSkusMap.get(itemEs.getId());
            if (shouldDelete(skus, itemEs)) {
                deleteIds.add(itemEs.getId());
                continue;
            }
            /** 向搜索引擎插入数据 **/
            syncIds.add(itemEs.getId());
        }
        /**    同步数据到es **/
        updateItem(syncIds);
        /** 把不上架、冻结、删除的item商品/库存为0的商品从搜索引擎删除 **/
        deleteItem(deleteIds);
    }

    private boolean shouldDelete(List<GoodsSkuDTO> goodsSkuDTOS, ItemEntity itemEs) {
        Long stockNum = 0L;
        for (GoodsSkuDTO goodsSku : goodsSkuDTOS) {
            stockNum = stockNum + goodsSku.getStockNumber();
        }
        /** 删除条件 **/
        return !ItemStatusEnum.ITEM_STATUS_PUTAWAY.getCode().equals(itemEs.getStatus())
                || ItemConstants.FrozenFlag.YES_FROZEN.equals(itemEs.getFrozenFlag())
                || ItemConstants.DeletedFlag.DELETEDFLAG_YES.equals(itemEs.getDeletedFlag())
                || stockNum == 0L;
    }


    private void updateItem(List<Long> itemIds) {
        List<ItemDto> itemDtoList = packageItemDto(itemIds);
        log.info("插入的数据为={}", itemDtoList);

        for (ItemDto itemDto : itemDtoList) {
            String url = StringUtils.join("/", index, "/doc/", itemDto.getId());
            HttpEntity entity;
            try {
                entity = new NStringEntity(objectMapper.writeValueAsString(itemDto), ContentType.APPLICATION_JSON);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                return;
            }
            restClient.performRequestAsync("PUT",
                    url,
                    Collections.emptyMap(),
                    entity,
                    new ResponseListener() {
                        @Override
                        public void onSuccess(Response response) {
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            log.error("插入数据到搜索引擎失败，exception={}", exception);
                        }
                    });

        }
    }


    private void deleteItem(List<Long> itemIds) {
        for (Long itemId : itemIds) {
            String url = StringUtils.join("/", index, "/doc/", itemId);

            try {
                restClient.performRequest("DELETE",
                        url);
            } catch (IOException e) {
                log.error("删除item失败={}", e);
            }
        }
    }

    public List<ItemDto> packageItemDto(List<Long> itemIds) {
        List<ItemDto> itemDtos = new ArrayList<>();
        EntityWrapper<ItemEntity> wrapper = new EntityWrapper<>();
        wrapper.in("id", itemIds);
        List<ItemEntity> itemEntityList = itemDao.selectList(wrapper);

        /** 批量获取  商品类目BackendCategoryDTO **/
        List<Long> belongCategoryIds = itemEntityList.stream().map(e -> e.getBelongStore()).collect(Collectors.toList());
        Map<Long, List<BackendCategoryDTO>> backendCategorymap = backendCategoryApi.getBackendCategorys(belongCategoryIds).stream().collect(Collectors.groupingBy(BackendCategoryDTO::getId));

        /** 批量获取  商品品牌belongBrand **/
        List<Long> belongBrandIds = itemEntityList.stream().map(e -> e.getBelongBrand()).collect(Collectors.toList());
        Map<Long, List<BrandDTO>> brandDTOMap = brandApi.getBrands(belongBrandIds).stream().collect(Collectors.groupingBy(BrandDTO::getId));


        for (ItemEntity itemEntity : itemEntityList) {
            ItemDto itemDto = new ItemDto();
            loadItemBrandName(brandDTOMap, itemEntity, itemDto);
            itemDto.setId(itemEntity.getId());
            itemDto.setName(itemEntity.getName());
            itemDto.setMarketPrice(itemEntity.getMarketPrice());
            itemDto.setDefaultPrice(itemEntity.getMinPrice());
            itemDto.setBelongStore(itemEntity.getBelongStore());
            loadBackName(backendCategorymap, itemEntity, itemDto);
            itemDto.setSalesVolume(itemEntity.getSalesVolume() == null ? 0L : itemEntity.getSalesVolume());
            itemDto.setPictureName(itemEntity.getPictureName());
            itemDto.setDeletedFlag(itemEntity.getDeletedFlag());
            itemDto.setFrozenFlag(itemEntity.getFrozenFlag());
            itemDto.setStatus(itemEntity.getStatus());
            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

	private void loadBackName(Map<Long, List<BackendCategoryDTO>> backendCategorymap, ItemEntity itemEntity, ItemDto itemDto) {
    	if(backendCategorymap.get(itemEntity.getBelongCategory()).get(0) == null
				|| StringUtils.isEmpty(backendCategorymap.get(itemEntity.getBelongCategory()).get(0).getName())){
			itemDto.setBackName("");
			return;
		}
		itemDto.setBackName(backendCategorymap.get(itemEntity.getBelongCategory()).get(0).getName());
    	return;
	}

	private void loadItemBrandName(Map<Long, List<BrandDTO>> brandDTOMap, ItemEntity itemEntity, ItemDto itemDto) {
        if (itemEntity.getBelongBrand() == null || itemEntity.getBelongBrand() == 0) {
            itemDto.setBrandName("");
            return;
        }
        BrandDTO brandDTO = brandDTOMap.get(itemEntity.getBelongBrand()).get(0);
        if (brandDTO == null) {
            itemDto.setBrandName("");
            return;
        }
        itemDto.setBrandName(StringUtils.isBlank(brandDTO.getName()) ? "" : brandDTO.getName());
    }
}