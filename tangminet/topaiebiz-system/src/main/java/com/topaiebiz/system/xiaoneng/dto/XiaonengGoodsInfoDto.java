package com.topaiebiz.system.xiaoneng.dto;

import lombok.Data;

/**
 * Created by ward on 2018-04-05.
 */
@Data
public class XiaonengGoodsInfoDto {
    /**
     * 商品id
     */
    private String id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 主图片url
     */
    private String imageurl;
    /**
     * 商品详情页地址
     */
    private String url;
    /**
     * 货币符号
     */
    private String currency;
    /**
     * 商品网站价格(和currency合并显示，显示效果￥：180.00)",
     */
    private String siteprice;
    /**
     * 商品市场价格
     */
    private String marketprice;
    /**
     * 商品分类名称
     */
    private String category;
    /**
     * 商品品牌名称
     */
    private String brand;
}
