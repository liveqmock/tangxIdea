package com.topaiebiz.goods.sku.dto.app;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by hecaifeng on 2018/3/20.
 */
@Data
public class GoodsPraiseDto {

    /**
     * 好评星级。
     * 好评度80%及以上=5颗星
     * 好评度60%及以上=4颗星
     * 好评度40%及以上=3颗星
     * 好评度20%及以上=2颗星
     * 好评度20%以下=1颗星
     */
    private Integer praiseLevel;

    /**
     * 好评度。
     */
    private BigDecimal praiseRatio;

    /**
     * 评价等级对应的条数。
     */
    private List<GoodsSkuCommentCountDto> goodsSkuCommentCounts;
}
