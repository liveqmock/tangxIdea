package com.topaiebiz.promotion.mgmt.dto.box.content;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 优惠券宝箱
 */
@Data
public class CardBoxDTO {
    /**
     * 礼卡发行ID
     */
    private Long batchId;
    /**
     * 礼卡金额
     */
    private BigDecimal cardValue;
    /**
     * 礼卡封面
     */
    private String cardCover;
}
