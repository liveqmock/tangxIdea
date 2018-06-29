package com.topaiebiz.goods.dto.sku;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hecaifeng on 2018/3/30.
 */
@Data
public class GoodsDecorateDTO {

    /**
     * 商品id。
     */
    private Long goodsId;

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
     * 活动开始时间。
     */
    private Date stareTime;

    /**
     * 活动结束时间。
     */
    private Date endTime;

    /**
     * 活动价。
     */
    private BigDecimal activityPrice;

    /**
     * 商品销量。
     */
    private Long salesVolome;

    /**
     * 商品图片。
     */
    private String pictureName;

    /**
     * 排序号。
     */
    private Long sortNo;

    /**
     * 商品评价条数。
     */
    private Integer commentCount;

    /**
     * 积分比例。小数形式。
     */
    private BigDecimal integralRatio;
}
