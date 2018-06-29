package com.topaiebiz.goods.dto.sku;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by hecaifeng on 2018/5/15.
 */
@Data
public class ApiGoodsSkuDTO {
    /**
     * 商品skuId。
     */
    private Long skuId;

    /**
     * 商品itemId。
     */
    private Long itemId;

    /**
     * 规格外部商家编码（货号）。
     */
    private String articleNumber;

    /**
     * 属性集合以键值对形式存放 (key:value,key1:value1)。
     */
    private String baseFieldValue;

    /**
     * 销售属性集合以键值对形式存放  (key:value,key1:value1)。
     */
    private String saleFieldValue;

    /**
     * SKU商品图片。
     */
    private String saleImage;

    /**
     * 销售价格,最多两位小数。
     */
    private BigDecimal price;

    /**
     * SKU库存。
     */
    private Long stockNumber;

    /**
     * 商品条形码。
     */
    private String barCode;


    /**
     * 积分支付比例。
     */
    private BigDecimal scoreRate;

}
