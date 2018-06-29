package com.topaiebiz.promotion.mgmt.dto.floor;

import lombok.Data;

import java.util.List;

/**
 * 楼层信息（C端）
 */
@Data
public class FloorDTO {
    /**
     * 楼层分类名称
     */
    private String typeName;
    /**
     * 商品列表
     */
    List<FloorGoodsDTO> goodsList;
}
