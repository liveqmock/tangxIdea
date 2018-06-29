package com.topaiebiz.goods.dto.category.backend;

import lombok.Data;

/**
 * Created by hecaifeng on 2018/3/24.
 */
@Data
public class BackendCategoryStatusDTO {

    /**
     * 商家类目审核是否通过，1为审核通过。0为待审核。
     */
    private Integer status;

    /**
     * 类目id。
     */
    private Long[] ids;

    /**
     * 商家id
     */
    private Long merchantId;
}
