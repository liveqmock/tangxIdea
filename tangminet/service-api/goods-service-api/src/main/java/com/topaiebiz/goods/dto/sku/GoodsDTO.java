package com.topaiebiz.goods.dto.sku;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by hecaifeng on 2018/2/3.
 */
@Data
public class GoodsDTO implements Comparable<GoodsDTO>{

    /** 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。 */
    private Long id;

    private Long titleId;

    /** 商品id。*/
    private Long goodsId;

    /** 商品名称(标题显示的名称)。 */
    private String name;

    /** 市场价。*/
    private BigDecimal marketPrice;

    /** 默认价格（页面刚打开的价格）。 */
    private BigDecimal defaultPrice;

    /** 商品销量。*/
    private Long salesVolome;

    /** 商品图片。*/
    private String pictureName;

    /** 排序号。*/
    private Long sortNo;

    @Override
    public int compareTo(GoodsDTO o) {
        if (o.sortNo!= null) {
            if (this.sortNo > o.sortNo) {
                return 1;
            }
        }
        return 0;
    }
}
