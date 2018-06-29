package com.topaiebiz.goods.sku.dto.app;

import lombok.Data;

/**
 * Created by hecaifeng on 2018/3/20.
 */
@Data
public class GoodsSkuCommentCountDto {

    /** 评价数。*/
    private  Integer count;

    /**0全部 1 好评，2中评。3差评。4有图  */
    private Integer type;
}
