package com.topaiebiz.trade.order.po.ordersubmit;

import lombok.Data;

import java.io.Serializable;

/***
 * @author yfeng
 * @date 2018-01-09 15:26
 */
@Data
public class OrderRequestGoods implements Serializable {
    private static final long serialVersionUID = 1L;

    /*** 购物车ID集合  ***/
    private Long cartId;

    /*** 商品ID ***/
    private Long goodsId;

    /**** 商品数量 ****/
    private Long goodsNum;

    /**** 营销活动ID ****/
    private Long promotionId;
}