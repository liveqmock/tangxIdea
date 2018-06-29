package com.topaiebiz.goods.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by hecaifeng on 2018/5/21.
 */
@Data
public class CategoryBrandAddDTO {

    /**
     * 类目ID。
     */
    @NotNull(message = "{validation.attrGroup.categoryId}")
    private Long categoryId;

    /**
     * 新增品牌ID。
     */
    private Long[] addBrandId;

    /**
     * 更新品牌ID。
     */
    private Long[] updateBrandId;



}
