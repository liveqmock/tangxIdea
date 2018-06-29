package com.topaiebiz.goods.brand.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

/**
 * Created by hecaifeng on 2018/5/18.
 */
@Data
public class BrandQueryDto extends PagePO {

    /**
     * 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。
     */
    private Long id;

    /**
     * 类目名称。
     */
    private String categoryName;

    /**
     * 类目ID。
     */
    private String categoryId;

    /**
     * 品牌名称。
     */
    private String name;

    /**
     * 英文名称。
     */
    private String englishName;
}
