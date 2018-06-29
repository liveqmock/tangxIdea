package com.topaiebiz.goods.sku.dto;

import com.topaiebiz.goods.comment.dto.GoodsSkuCommentDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by hecaifeng on 2018/6/11.
 */
@Data
public class ItemCommentsDTO {

    /**
     * 评价条数。
     */
    private Integer commentCount;

    /**
     * 好评度。
     */
    private BigDecimal praise;

    /**
     * 评价列表。
     */
    private List<GoodsSkuCommentDto> goodsSkuCommentDtos;
}
