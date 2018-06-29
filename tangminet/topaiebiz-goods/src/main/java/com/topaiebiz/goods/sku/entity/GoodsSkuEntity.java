package com.topaiebiz.goods.sku.entity;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;

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
@TableName("t_goo_goods_sku")
@Data
public class GoodsSkuEntity extends BaseBizEntity<Long> {

    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = 4799084762992286907L;

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
     * 货号。
     */
    private String articleNumber;

    /**
     * 预占用库存。
     */
    private Long lockedNumber;

    /**
     * 销售数量。
     */
    private Long salesVolume;

    /**
     * 商品条形码。
     */
    private String barCode;

    /**
     * 三级分类ID -->用于数据迁移，后期无用
     */
    private Long gcId;
    /**
     * 规格值数据 -->用于数据迁移，后期无用
     */
    private String specName;
    private String goodsCustom;
    private String goodsAttr;
    /**
     * 商品固定运费 -->用于数据迁移，后期无用
     */
    private BigDecimal goodsfreight;

    /** 积分支付比例。*/
    private BigDecimal scoreRate;
    /**
     * 备注。用于备注其他信息。
     */
    private String memo;

    public void clearInit() {
        this.setVersion(null);
        this.setCreatedTime(null);
        this.setDeleteFlag(null);
    }
}
