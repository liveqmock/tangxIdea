package com.topaiebiz.trade.order.facade;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.goods.api.GoodsSkuApi;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.goods.dto.sku.StorageUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-09 20:56
 */
@Component
@Slf4j
public class GoodsSkuServiceFacade {

    @Autowired
    private GoodsSkuApi skuApi;

    public GoodsSkuDTO getGoodsSku(Long skuId) {
        GoodsSkuDTO sku = skuApi.getGoodsSku(skuId);
        log.info("skuApi.getGoodsSku({}) return:{}", skuId, JSON.toJSONString(sku));
        return sku;
    }

    public Map<Long, GoodsSkuDTO> getGoodsSkuMap(List<Long> skuIds) {
        log.info("skuApi.getGoodsSkuMap({}) request ...", skuIds);
        Map<Long, GoodsSkuDTO> resMap = skuApi.getGoodsSkuMap(skuIds);
        log.info("skuApi.getGoodsSkuMap({}) response:{}", skuIds, JSON.toJSONString(resMap));
        loadSaleAttributes(resMap);
        return resMap;
    }

    public boolean descreaseStorages(Long orderId, List<StorageUpdateDTO> updates) {
        Integer val = skuApi.descreaseStorages(orderId, updates);
        log.info("skuApi.descreaseStorages({},{}) return:{}", orderId, JSON.toJSONString(updates), val);
        return val > 0;
    }

    public boolean inscreaseStorages(Long orderId, List<StorageUpdateDTO> updates) {
        Integer val = skuApi.inscreaseStorages(orderId, updates);
        log.info("skuApi.inscreaseStorages({},{}) return:{}", orderId, JSON.toJSONString(updates), val);
        return val > 0;
    }

    public void loadSaleAttributes(Map<Long, GoodsSkuDTO> skuMap) {
        if (MapUtils.isEmpty(skuMap)) {
            return;
        }
        skuApi.loadSaleAttributes(new ArrayList<>(skuMap.values()));
    }

    public void loadSaleAttributes(List<GoodsSkuDTO> skus) {
        if (CollectionUtils.isEmpty(skus)) {
            return;
        }
        skuApi.loadSaleAttributes(skus);
    }

}