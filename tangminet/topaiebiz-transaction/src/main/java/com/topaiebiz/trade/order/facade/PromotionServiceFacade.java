package com.topaiebiz.trade.order.facade;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.topaiebiz.promotion.api.PromotionApi;
import com.topaiebiz.promotion.dto.PromotionConsumeDTO;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/***
 * @author yfeng
 * @date 2018-01-09 12:51
 */
@Component
@Slf4j
public class PromotionServiceFacade {

    @Autowired
    private PromotionApi promotionApi;

    public List<PromotionDTO> querySkuPromotions(Long goodsSkuId) {
        List<Long> goodsSkuIds = Lists.newArrayList(goodsSkuId);
        Map<Long, List<PromotionDTO>> skuPromotionMap = querySkuPromotionMap(goodsSkuIds);
        List<PromotionDTO> promotions = skuPromotionMap.get(goodsSkuId);
        if (promotions == null) {
            return Collections.emptyList();
        }
        return promotions;
    }

    public Map<Long, List<PromotionDTO>> querySkuPromotionMap(List<Long> goodsSkuIds) {
        if (CollectionUtils.isEmpty(goodsSkuIds)) {
            return Collections.emptyMap();
        }
        Map<Long, List<PromotionDTO>> skuPromotionMap = promotionApi.getSkuPromotions(goodsSkuIds);
        log.info("promotionApi.getSkuPromotions({}) return:{}", JSON.toJSONString(goodsSkuIds), JSON.toJSONString(skuPromotionMap));
        return skuPromotionMap;
    }

    public Map<Long, List<PromotionDTO>> queryStorePromotionMap(List<Long> storeIds) {
        if (CollectionUtils.isEmpty(storeIds)) {
            return Collections.emptyMap();
        }
        Map<Long, List<PromotionDTO>> storePromotionMap = promotionApi.getStorePromotions(storeIds);
        log.info("promotionApi.getStorePromotions({}) return:{}", JSON.toJSONString(storeIds), JSON.toJSONString(storePromotionMap));
        return storePromotionMap;
    }

    public List<PromotionDTO> queryStorePromotions(Long storeId) {
        List<PromotionDTO> storePromotions = promotionApi.getStorePromotionList(storeId, PromotionTypeEnum.PROMOTION_TYPE_REDUCE_PRICE.getCode());
        log.info("promotionApi.getStorePromotionList({},{}) return:{}", storeId, PromotionTypeEnum.PROMOTION_TYPE_REDUCE_PRICE.getCode(), JSON.toJSONString(storePromotions));
        if (CollectionUtils.isEmpty(storePromotions)) {
            return Lists.newArrayList();
        }
        return storePromotions;
    }

    public List<PromotionDTO> queryStoreCoupons(Long memberId, Long storeId) {
        List<PromotionDTO> storePromotions = promotionApi.getStoreCoupons(memberId, storeId);
        log.info("promotionApi.getStoreCoupons({},{}) return:{}", memberId, storeId, JSON.toJSONString(storePromotions));
        if (CollectionUtils.isEmpty(storePromotions)) {
            return Lists.newArrayList();
        }
        return filterDuplicatePromotions(storePromotions);
    }

    private List<PromotionDTO> filterDuplicatePromotions(List<PromotionDTO> promotionDTOS) {
        if (CollectionUtils.isEmpty(promotionDTOS)) {
            return Lists.newArrayList();
        }
        Set<Long> idSet = new HashSet<>();
        List<PromotionDTO> results = new ArrayList<>();
        for (PromotionDTO promotionDTO : promotionDTOS) {
            if (idSet.contains(promotionDTO.getId())) {
                continue;
            }
            results.add(promotionDTO);
            idSet.add(promotionDTO.getId());
        }
        return results;
    }

    public Map<Long, PromotionDTO> queryPromotionMap(List<Long> promotionIds) {
        if (CollectionUtils.isEmpty(promotionIds)) {
            return Maps.newHashMap();
        }
        Map<Long, PromotionDTO> promotionDTOMap = promotionApi.getPromotionByIds(promotionIds);
        log.info("promotionApi.getPromotionByIds({}) return:{}", JSON.toJSONString(promotionIds), JSON.toJSONString(promotionDTOMap));
        return promotionDTOMap;
    }

    public List<PromotionDTO> queryFreightPromotions(Long storeId) {
        List<PromotionDTO> promotions = promotionApi.getStorePromotionList(storeId, PromotionTypeEnum.PROMOTION_TYPE_FREE_SHIPPING.getCode());
        log.info("promotionApi.getStorePromotionList({},{}) return:{}", storeId, PromotionTypeEnum.PROMOTION_TYPE_FREE_SHIPPING.getCode(), JSON.toJSONString(promotions));
        if (CollectionUtils.isEmpty(promotions)) {
            return Lists.newArrayList();
        }
        return promotions;
    }

    public List<PromotionDTO> queryPlatformPromotions(Long memberId) {
        List<PromotionDTO> promotions = promotionApi.getPlatformPromotions(memberId);
        log.info("promotionApi.queryPlatformPromotions({}) return:{}", memberId, JSON.toJSONString(promotions));
        if (CollectionUtils.isEmpty(promotions)) {
            return Lists.newArrayList();
        }
        return filterDuplicatePromotions(promotions);
    }

    public Boolean checkHoldStatus(Long memberId, List<Long> couponPromIds) {
        Boolean checkResult = promotionApi.checkHoldStatus(memberId, couponPromIds);
        log.info("promotionApi.checkHoldStatus({},{}) return:{}", memberId, JSON.toJSONString(couponPromIds), checkResult);
        return checkResult;
    }

    public Boolean usePromotions(Long memberId, PromotionConsumeDTO promotionConsumeDTO) {
        log.info("promotionApi.usePromotions({},{})", memberId, JSON.toJSONString(promotionConsumeDTO));
        Boolean result = promotionApi.usePromotions(memberId, promotionConsumeDTO);
        log.info("promotionApi.usePromotions() return:{}", result);
        return result;
    }

    public Boolean backPromotions(Long memberId, PromotionConsumeDTO promotionConsumeDTO) {
        log.info("promotionApi.backPromotions({},{})", memberId, JSON.toJSONString(promotionConsumeDTO));
        Boolean result = promotionApi.backPromotions(memberId, promotionConsumeDTO);
        log.info("promotionApi.backPromotions() return:{}", result);
        return result;
    }
}