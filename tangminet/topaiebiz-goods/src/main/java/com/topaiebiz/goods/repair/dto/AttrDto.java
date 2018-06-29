package com.topaiebiz.goods.repair.dto;

import lombok.Data;

/**
 * Created by hecaifeng on 2018/2/8.
 */
@Data
public class AttrDto {

    /** 属性id。*/
    private Long id;

    /** 第三级类目id。*/
    private Long categoryId;

    /** 属性名称。*/
    private String name;

    /** 属性值。*/
    private String value;


}
