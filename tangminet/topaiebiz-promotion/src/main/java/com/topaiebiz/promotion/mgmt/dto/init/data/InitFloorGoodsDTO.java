package com.topaiebiz.promotion.mgmt.dto.init.data;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 楼层配置
 */
@Data
public class InitFloorGoodsDTO {
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品楼层，比如辣妈育儿宝典
     */
    private String floorCode;
    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * 折扣价
     */
    private BigDecimal discountPrice;
    /**
     * 排序
     */
    private Integer sort;
}
