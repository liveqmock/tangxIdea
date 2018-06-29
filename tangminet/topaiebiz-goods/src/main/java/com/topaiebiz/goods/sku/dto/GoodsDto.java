package com.topaiebiz.goods.sku.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by dell on 2018/1/26.
 */
@Data
public class GoodsDto implements Serializable{

    /**
     * 商品id。
     */
    private Long id;

    /**
     * 商品编码。
     */
    private String itemCode;

    /**
     * 店铺id。
     */
    private Long belongStore;

    /**
     * 店铺名称。
     */
    private String storeName;

    /**
     * 商品名称。
     */
    private String name;

    /**
     * 商品图片。
     */
    private String pictureName;

    /**
     * 商品价格。
     */
    private BigDecimal defaultPrice;

}

