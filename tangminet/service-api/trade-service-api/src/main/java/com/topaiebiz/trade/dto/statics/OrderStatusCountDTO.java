package com.topaiebiz.trade.dto.statics;

import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * @author yfeng
 * @date 2018-01-15 15:32
 */
@Data
@NoArgsConstructor
public class OrderStatusCountDTO {

    /**
     * 未支付订单数量
     */
    private Long unpay = 0L;
    /**
     * 待发货订单数量
     */
    private Long unshipCount = 0L;
    /**
     * 待收货订单数量
     */
    private Long unreceiveCount = 0L;
    /**
     * 待评价订单数量
     */
    private Long unevaluateCount = 0L;

    public OrderStatusCountDTO(Long unpay, Long unshipCount, Long unreceiveCount, Long unevaluateCount) {
        this.unpay = unpay;
        this.unshipCount = unshipCount;
        this.unreceiveCount = unreceiveCount;
        this.unevaluateCount = unevaluateCount;
    }
}