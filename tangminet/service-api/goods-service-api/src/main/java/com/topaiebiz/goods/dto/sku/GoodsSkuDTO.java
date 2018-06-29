package com.topaiebiz.goods.dto.sku;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Description 商品属性表，一条数据对应一个SKU。
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年8月23日 下午5:24:37
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class GoodsSkuDTO implements Serializable {

    /**
     * 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。
     */
    private Long id;

    /**
     * 所属商品。
     */
    private Long itemId;

    /**
     * 所属商品SPU。
     */
    private Long spuId;

    /**
     * 属性集合以键值对形式存放 (key:value,key1:value1)。
     */
    private String baseFieldValue;

    /**
     * 销售属性集合以键值对形式存放  (key:value,key1:value1)。
     */
    private String saleFieldValue;

    /**
     * 销售属性
     */
    private List<SaleAttributeDTO> saleAttributes;

    /**
     * SKU商品图片。
     */
    private String saleImage;

    /**
     * 市场价。
     */
    private BigDecimal marketPrice;

    /**
     * 销售价格,最多两位小数。
     */
    private BigDecimal price;

    /**
     * 库存数量。
     */
    private Long stockNumber;

    /**
     * 预占用库存。
     */
    private Double lockedNumber;

    /**
     * 销售数量。
     */
    private Long salesVolume;

    /**
     * 货号。
     */
    private String articleNumber;

    /**
     * 商品条形码。
     */
    private String barCode;

    /** 积分支付比例。*/
    private BigDecimal scoreRate;

    /**
     * 所属商品。
     */
    private ItemDTO item;


}
