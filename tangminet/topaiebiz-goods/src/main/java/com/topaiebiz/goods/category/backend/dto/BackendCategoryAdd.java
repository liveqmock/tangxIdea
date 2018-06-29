package com.topaiebiz.goods.category.backend.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by hecaifeng on 2018/3/24.
 */
@Data
public class BackendCategoryAdd {

    /**
     * 商家类目审核状态。
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

    /**
     * 状态集合
     */
    private List<Integer> statuses;
}
