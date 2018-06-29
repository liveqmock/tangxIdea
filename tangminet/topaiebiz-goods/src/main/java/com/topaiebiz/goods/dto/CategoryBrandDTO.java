package com.topaiebiz.goods.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by hecaifeng on 2018/5/21.
 */
@Data
public class CategoryBrandDTO implements Serializable {

    /**
     * 品牌ID。
     */
    private Long id;

    /**
     * 品牌名称。
     */
    private String name;

    /**
     * 英文名称。
     */
    private String englishName;

    /**
     * 品牌首字母。
     */
    private String brandInitials;

    /**
     * 品牌图片。
     */
    private String brandImage;


    /**
     * 品牌展示排序。
     */
    private Integer sortNo;

}
