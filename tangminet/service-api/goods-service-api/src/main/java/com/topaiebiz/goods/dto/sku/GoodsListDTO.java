package com.topaiebiz.goods.dto.sku;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by hecaifeng on 2018/5/14.
 */
@Data
public class GoodsListDTO {


    /**
     * 商品itemId
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
     * 商品价格（售价）。
     */
    private BigDecimal defaultPrice;

    /**
     * 商品总库存。
     */
    private Integer itemNum;
    /**
     * 商品图片。
     */
    private String pictureName;

    /**
     * 商品状态（1 仓库中 2 已上架 3 下架 ）。
     */
    private Integer status;

    /**
     * 商品冻结状态（0正常 1冻结 ）。
     */
    private Integer frozenFlag;

    /**
     * 商品sku集合。
     */
    private List<GoodsSkusDTO> goodsSkus;

}
