package com.topaiebiz.merchant.info.service;

import com.topaiebiz.merchant.info.dto.MerchantReturnDto;

/** 商家退货地址
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/1/24 0024 下午 4:42
 */
public interface MerchantReturnService {


    /**
     * 添加商家退货信息
     * @param merchantReturnDto
     * @return
     */
    Integer insertMerchantReturnInfo(MerchantReturnDto merchantReturnDto);

    /**
     * 回显退货地址信息
     * @param merchantId
     * @return
     */
    MerchantReturnDto selectMerchantReturnByMerchantId(Long merchantId);
}
