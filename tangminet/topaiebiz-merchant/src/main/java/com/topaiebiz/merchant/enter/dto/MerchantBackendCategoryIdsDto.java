package com.topaiebiz.merchant.enter.dto;

import lombok.Data;

/**
 * 商家后台类目DTO
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/5 0025 上午 2:26
 */
@Data
public class MerchantBackendCategoryIdsDto {

    /** 全局唯一标识符 */
    private Long id;

    /**类目ids*/
    private Long[] ids;

    /**商家id*/
    private Long merchantId;

}
