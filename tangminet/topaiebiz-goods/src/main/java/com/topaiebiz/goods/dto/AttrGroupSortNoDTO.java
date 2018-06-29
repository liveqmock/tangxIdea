package com.topaiebiz.goods.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by hecaifeng on 2018/5/21.
 */
@Data
public class AttrGroupSortNoDTO implements Serializable {

    private Long id;

    /**
     * 排序号
     */
    private Integer sortNo;
}
