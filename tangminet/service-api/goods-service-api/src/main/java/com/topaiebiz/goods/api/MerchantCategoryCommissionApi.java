package com.topaiebiz.goods.api;

import com.topaiebiz.goods.dto.category.backend.MerchantCategoryCommissionDTO;

import java.util.Map;

/***
 * @author yfeng
 * @date 2018-03-26 14:29
 */
public interface MerchantCategoryCommissionApi {
    Map<Long, MerchantCategoryCommissionDTO> queryMerchantCategoryMap(Long merchantId);
}