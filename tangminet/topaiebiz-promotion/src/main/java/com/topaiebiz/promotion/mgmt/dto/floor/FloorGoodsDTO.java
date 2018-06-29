package com.topaiebiz.promotion.mgmt.dto.floor;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 楼层商品列表
 */
@Data
public class FloorGoodsDTO {
    /**
     * 商品楼层
     */
    private String floorCode;
    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品图片路径
     */
    private String image;
    /**
     * 商品原价
     */
    private BigDecimal marketPrice;
    /**
     * 折扣价
     */
    private BigDecimal discountPrice;
}
