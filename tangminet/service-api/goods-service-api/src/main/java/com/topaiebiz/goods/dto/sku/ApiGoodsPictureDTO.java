package com.topaiebiz.goods.dto.sku;

import lombok.Data;

/**
 * Created by hecaifeng on 2018/5/25.
 */
@Data
public class ApiGoodsPictureDTO {

    /**
     * 所属商品。
     */
    private Long itemId;

    /**
     * 图片名称。
     */
    private String name;

    /**
     * 图片类型（1为显示的5张主图，2 为详情图）。
     */
    private Integer type;

    /**
     * 是否为主图（1是 ,0否，空也为否）。
     */
    private Integer isMain;
}
