package com.topaiebiz.goods.dto.sku;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by hecaifeng on 2018/5/10.
 */
@Data
public class ItemsDTO {

    /**
     * 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。
     */
    private Long id;

    /**
     * 唯一编码 (本字段是从业务角度考虑的，相当于全局的唯一业务主键)。
     */
    private String itemCode;

    /**
     * 商品名称(标题显示的名称)。
     */
    private String name;

    /**
     * 市场价。
     */
    private BigDecimal marketPrice;

    /**
     * 默认价格（页面刚打开的价格）。
     */
    private BigDecimal defaultPrice;

    /**
     * 积分比例。小数形式。
     */
    private BigDecimal integralRatio;

    /**
     * 佣金比例。小数形式。平台收取商家的佣金。
     */
    private BigDecimal brokerageRatio;

    /**
     * 商品图片。
     */
    private String pictureName;

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
     * 选用物流模版。
     */
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
     * 商品图片集合。
     */
    private List<ItemPictureDTO> itemPictureDTOList;

    /**
     * 商品SKU集合。
     */
    private List<GoodsSkuDTO> goodsSkuDTOS;


}
