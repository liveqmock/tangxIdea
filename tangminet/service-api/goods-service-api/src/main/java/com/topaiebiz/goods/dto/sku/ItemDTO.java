package com.topaiebiz.goods.dto.sku;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.topaiebiz.goods.constants.GoodsConstants.SkuStatus.SALING;

/**
 * Created by hecaifeng on 2018/1/4.
 */
@Data
public class ItemDTO extends PagePO implements Serializable {

    /**
     * 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。
     */
    private Long id;

    /**
     * 唯一编码 (本字段是从业务角度考虑的，相当于全局的唯一业务主键)。
     */
    private String itemCode;

    /** 商品名称(标题显示的名称)。 */
    private String name;

    /**
     * 佣金比例。小数形式。平台收取商家的佣金。
     */
    private Double brokerageRatio;

    /**
     * 引用SPU商品。
     */
    private Long spuId;

    /**
     * 市场价。
     */
    private BigDecimal marketPrice;

    /**
     * 默认价格（页面刚打开的价格）。
     */
    private BigDecimal defaultPrice;

    /** 累计销量。 */
    private Long salesVolome;

    /**
     * 商品图片。
     */
    private String pictureName;

    /** 所属店铺。 */
    private Long belongStore;

    /** 所属品牌。 */
    private Long belongBrand;

    /**
     * 适用年龄段。
     */
    private Long ageId;

    /**
     * 所属类目。
     */
    private Long belongCategory;

    /**
     * 图片所属类目属性。
     */
    private Long imageField;

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
     * 营销id。
     */
    private Long promotionId;

    /**
     * 逻辑删除标识
     */
    private Integer deletedFlag;

    /**
     * 税率。
     */
    private BigDecimal taxRate;

    /**
     * 是否可以购买此SKU
     *
     * @return 是否可以购买此SKU
     */
    public boolean canPurchase() {
        return SALING.equals(status);
    }
}
