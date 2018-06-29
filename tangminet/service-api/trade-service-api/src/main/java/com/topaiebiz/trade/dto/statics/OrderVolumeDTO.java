package com.topaiebiz.trade.dto.statics;

import lombok.Data;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-01-15 15:29
 */
@Data
public class OrderVolumeDTO {
    private Long orderCount = 0L;
    private BigDecimal orderAmount = BigDecimal.ZERO;
}