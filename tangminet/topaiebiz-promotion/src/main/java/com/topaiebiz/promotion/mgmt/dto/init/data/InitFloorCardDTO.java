package com.topaiebiz.promotion.mgmt.dto.init.data;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InitFloorCardDTO {
    /**
     * 礼卡名称
     */
    private String cardName;
    /**
     * 礼卡楼层
     */
    private String floorCode;
    /**
     * 礼卡发行id
     */
    private Long batchId;
    /**
     * 礼卡售价
     */
    private BigDecimal salePrice;
    /**
     * 排序
     */
    private Integer sort;
}
