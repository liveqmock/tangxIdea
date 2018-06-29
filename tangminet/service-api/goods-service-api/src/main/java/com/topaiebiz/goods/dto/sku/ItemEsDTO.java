package com.topaiebiz.goods.dto.sku;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by hecaifeng on 2018/6/27.
 */
@Data
public class ItemEsDTO {

    private Long id;

    /**
     * 商品名称(标题显示的名称)。
     */
    private String name;

    /**
     * 积分比例。小数形式。
     */
    private BigDecimal integralRatio;

    /**
     * 销售数量。
     */
    private Long salesVolume;

    /**
     * 商品主图。
     */
    private String pictureName;

    /**
     * 市场价。
     */
    private BigDecimal marketPrice;

    /**
     * 默认价格（页面刚打开的价格）。
     */
    private BigDecimal defaultPrice;

    /**
     * 所属店铺。
     */
    private Long belongStore;

    /**
     * 所属品牌。
     */
    private Long belongBrand;

    /**
     * 所属类目。
     */
    private Long belongCategory;

    /**
     * 商品状态（1 新录入 2 已上架 3 下架 4 违规下架）。
     */
    private Integer status;

    /**
     * 商品是否冻结（0为正常，1为冻结）
     */
    private Integer frozenFlag;

    /**
     * 逻辑删除标识
     */
    private Integer deletedFlag;

    /**
     * 评价条数。
     */
    private Integer commentCount;

}
