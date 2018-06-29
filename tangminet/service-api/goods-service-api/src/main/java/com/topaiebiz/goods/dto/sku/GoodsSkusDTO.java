package com.topaiebiz.goods.dto.sku;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by hecaifeng on 2018/5/14.
 */
@Data
public class GoodsSkusDTO {

    /**
     * 商品skuId。
     */
    private Long id;

    /**
     * 商品itemId。
     */
    private Long itemId;

    /**
     * 规格外部商家编码（货号）。
     */
    private String articleNumber;

    /**
     * 销售属性集合以键值对形式存放  (key:value,key1:value1)。
     */
    private String saleFieldValue;

    /**
     * 销售价格,最多两位小数。
     */
    private BigDecimal price;

    /**
     * SKU库存。
     */
    private Long stockNumber;

}
