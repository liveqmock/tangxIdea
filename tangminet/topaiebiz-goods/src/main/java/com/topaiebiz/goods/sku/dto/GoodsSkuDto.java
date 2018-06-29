package com.topaiebiz.goods.sku.dto;

import lombok.Data;

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
public class GoodsSkuDto {

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
     * SKU商品图片。
     */
    private String saleImage;

    /**
     * 销售价格,最多两位小数。
     */
    private BigDecimal price;

    /**
     * 库存数量。
     */
    private Long stockNumber;
    /**
     * 销量
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

    /**
     * 商品状态（1 新录入 2 已上架 3 下架 4 违规下架）。
     */
    private Integer status;

    /**
     * 原有库存。
     */
    private Long repertoryNum;

    /**
     * 活动库存数量。
     */
    private Long promotionNum;

    /**
     * 活动价格。
     */
    private BigDecimal promotionPrice;

    /**
     * 营销活动id。
     */
    private Long promotionId;

    /**
     * 营销商品id。
     */
    private Long promotionGoodsId;

    /**
     * ID限购。
     */
    private Integer confineNum;

    /**
     * 营销状态。
     */
    private Integer state;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 订单商品数量属性-----仅创建订单调用查询店铺营销活动时使用
     */
    private Long orderGoodsNum;

    private List<GoodsSkuBaseDto> goodsSkuBaseDto;

    private List<GoodsSkuSaleDto> goodsSkuSaleDto;

    private List<GoodsSkuSaleValueDto> goodsSkuSaleValueDto;

    private List<GoodsSkuSaleKeyDto> goodsSkuSaleKeyDto;

    private List<GoodsSkuSaleKeyAndValueDto> goodsSkuSaleKeyAndValueDtos;

}