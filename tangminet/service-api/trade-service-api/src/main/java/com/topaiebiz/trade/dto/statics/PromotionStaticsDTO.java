package com.topaiebiz.trade.dto.statics;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


/***
 * @author yfeng
 * @date 2018-01-06 11:16
 */
@Data
public class PromotionStaticsDTO implements Serializable {
    private static final long serialVersionUID = 2378574364285909770L;

    /**
     * 下单商品件数
     */
    private Integer goodsCount = 0;

    /**
     * 支付买家数
     */
    private Integer memberCount = 0;

    /**
     * 优惠总金额
     */
    private BigDecimal orderTotalDiscount = BigDecimal.ZERO;

    /**
     * 订单总金额
     */
    private BigDecimal orderTotalPrice = BigDecimal.ZERO;

    /**
     * 营销活动ID
     */
    private Long promotionId;

//    public PromotionStaticsDTO(Integer goodsCount, Integer memberCount, BigDecimal orderTotalAmount) {
//        this.goodsCount = goodsCount;
//        this.memberCount = memberCount;
//        this.orderTotalAmount = orderTotalAmount;
//    }
//
//    public void setGoodsCount(Integer goodsCount) {
//        this.goodsCount += goodsCount;
//    }
//
//    public void setMemberCount(Integer memberCount) {
//        this.memberCount += memberCount;
//    }
//
//    public void setOrderTotalAmount(BigDecimal orderTotalAmount) {
//        this.orderTotalAmount = this.orderTotalAmount.add(orderTotalAmount);
//    }
}