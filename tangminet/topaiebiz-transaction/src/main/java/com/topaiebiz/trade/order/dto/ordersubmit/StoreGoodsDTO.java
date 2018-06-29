package com.topaiebiz.trade.order.dto.ordersubmit;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-09 11:19
 */
@Data
public class StoreGoodsDTO {
    private Long cartId;
    private Long itemId;
    private Long goodsId;
    private String goodsName;
    private Long goodsNum;
    private String goodsImg;
    private String saleFieldValue;
    /**
     * 原价
     */
    private BigDecimal originPrice;

    /**
     * 默认单品营销活动
     */
    private List<PromotionInfoDTO> promotions;
}