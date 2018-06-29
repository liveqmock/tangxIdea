package com.topaiebiz.promotion.mgmt.dto.floor;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 楼层商品列表
 */
@Data
public class FloorCardDTO {
    /**
     * 商品楼层
     */
    private String floorCode;
    /**
     * 礼卡发行ID
     */
    private Long batchId;
    /**
     * 礼卡名称
     */
    private String cardName;
    /**
     * 礼卡封面
     */
    private String cover;
    /**
     * 礼卡面值
     */
    private BigDecimal faceValue;
    /**
     * 礼卡售价
     */
    private BigDecimal salePrice;
}
