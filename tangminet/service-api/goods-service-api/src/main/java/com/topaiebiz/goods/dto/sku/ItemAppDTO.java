package com.topaiebiz.goods.dto.sku;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description 商品基本信息表
 *
 * <p>Author Hedda
 *
 * <p>Date 2017年8月23日 下午5:23:11
 *
 * <p>Copyright Cognieon technology group co.LTD. All rights reserved.
 *
 * <p>Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class ItemAppDTO implements Serializable {

    /**
     * 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。
     */
    private Long id;

    /**
     * 商品名称(标题显示的名称)。
     */
    private String name;

    /**
     * 商品图片。
     */
    private String pictureName;

    /**
     * 所属店铺。
     */
    private Long belongStore;

    /**
     * 默认价格（页面刚打开的价格）。
     */
    private BigDecimal defaultPrice;

    /**
     * 市场价。
     */
    private BigDecimal marketPrice;

    /**
     * 品牌名称。
     */
    private String brandName;

    /**
     * 类目名称。
     */
    private String categoryName;

    /**
     * 后台类目。
     */
    private Long belongCategory;

    /**
     * 商品状态（1 新录入 2 已上架 3 下架 4 违规下架）。
     */
    private Integer status;
}
