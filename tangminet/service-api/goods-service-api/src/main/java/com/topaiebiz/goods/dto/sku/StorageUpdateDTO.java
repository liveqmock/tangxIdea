package com.topaiebiz.goods.dto.sku;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by hecaifeng on 2018/1/4.
 */
@Data
public class StorageUpdateDTO implements Serializable{

    /** skuId。*/
    private Long skuId;

    /** 变更数量，必须为正数。*/
    private Integer num;

    /** 商品id。*/
    private Long itemId;
}