package com.topaiebiz.goods.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by hecaifeng on 2018/5/23.
 */
@Data
public class AttrGroupDTO implements Serializable {

    private Long id;

    /**
     * 属性分组名称。
     */
    private String name;

    /**
     * 类目描述。
     */
    private String description;
    /**
     * 排序号
     */
    private Integer sortNo;
}
