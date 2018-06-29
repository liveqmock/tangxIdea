package com.topaiebiz.goods.dto.sku;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by hecaifeng on 2018/5/15.
 */
@Data
public class ApiGoodsDTO {

    /**
     * 商品itemId
     */
    private Long itemId;

    /**
     * 唯一编码 (外部商品编码)。
     */
    private String itemCode;

    /**
     * 商品item名称(标题显示的名称)。
     */
    @NotNull(message = "{validation.item.name}")
    private String itemName;

    /**
     * 佣金比例。小数形式。平台收取商家的佣金。
     */
    private Double brokerageRatio;

    /**
     * 市场价。
     */
    private BigDecimal marketPrice;

    /**
     * 商品价格（售价）。
     */
    @NotNull(message = "{validation.item.defaultPrice}")
    private BigDecimal defaultPrice;

    /**
     * 所属店铺。
     */
    @NotNull(message = "{validation.item.belongStore}")
    private Long belongStore;

    /**
     * 所属品牌。
     */
    private Long belongBrand;

    /**
     * 所属类目。
     */
    @NotNull(message = "{validation.item.belongCategory}")
    private Long belongCategory;

    /**
     * 商品总库存。
     */
    private Long itemNum;
    /**
     * 商品图片。
     */
    private String itemPicture;

    /**
     * 商品状态（1 仓库中 2 已上架 3 下架 ）。
     */
    private Integer itemStatus;

    /**
     * 商品冻结状态（0正常 1冻结 ）。
     */
    private Integer frozenFlag;

    /**
     * 选用物流模版。
     */
    @NotNull(message = "{validation.item.logisticsId}")
    private Long logisticsId;

    /**
     * 物流模版的体积、重量（体积默认为m3，重量默认为kg）。
     */
    private Double weightBulk;

    /**
     * 商品描述。
     */
    private String description;

    /**
     * 商品sku集合。
     */
    private List<ApiGoodsSkuDTO> goodsSkus;

    /**
     * 商品图片集合。
     */
    private List<ApiGoodsPictureDTO> goodsPictures;
}
