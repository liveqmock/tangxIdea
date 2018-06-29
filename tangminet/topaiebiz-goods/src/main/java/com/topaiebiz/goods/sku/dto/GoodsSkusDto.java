package com.topaiebiz.goods.sku.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by hecaifeng on 2018/5/3.
 */
@Data
public class GoodsSkusDto {

    /**
     * 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。
     */
    private Long id;

    /**
     * 所属商品。
     */
    private Long itemId;

    /**
     * 销售属性集合以键值对形式存放  (key:value,key1:value1)。
     */
    private String saleFieldValue;


    /**
     * 销售价格,最多两位小数。
     */
    private BigDecimal price;

    /**
     * 库存数量。
     */
    private Long stockNumber;
}
