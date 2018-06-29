package com.topaiebiz.giftcard.vo;

import java.math.BigDecimal;

/**
 * @description: 圈定的商品信息
 * @author: Jeff Chen
 * @date: created in 下午1:40 2018/4/20
 */
public class GiftcardGoodsVO {

    /**
     * 商品id
     */
    private Long goodsId;

    /** 商品名称(标题显示的名称)。 */
    private String name;
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
     * 销售数量。
     */
    private Long salesVolume;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getSaleImage() {
        return saleImage;
    }

    public void setSaleImage(String saleImage) {
        this.saleImage = saleImage;
    }

    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getSalesVolume() {
        return salesVolume;
    }

    public void setSalesVolume(Long salesVolume) {
        this.salesVolume = salesVolume;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
