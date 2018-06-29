package com.topaiebiz.goods.category.backend.dto;

import lombok.Data;

/**
 * Created by hecaifeng on 2018/2/8.
 */
@Data
public class ItemBackendCategoryAttrDto {

    /** 商品id。*/
    private Long itemId;

    /** 类目id。*/
    private Long belongCategory;
}
