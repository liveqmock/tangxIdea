package com.topaiebiz.merchant.api;

import com.topaiebiz.merchant.dto.merchantReturn.MerchantReturnDTO;

/**
 * 商家店铺退货api
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/2/8 0008 下午 7:32
 */
public interface MerchantReturnApi {

    /**
     * 根据店铺id查询
     * @param storeId
     * @return
     */
    MerchantReturnDTO getMerchantReturnInfo(Long storeId);
}
