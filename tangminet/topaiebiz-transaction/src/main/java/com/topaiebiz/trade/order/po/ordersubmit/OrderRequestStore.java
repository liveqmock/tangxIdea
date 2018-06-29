package com.topaiebiz.trade.order.po.ordersubmit;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 下单接口请求模型中商品模型<br/>
 *
 * @author yangfeng
 * @date 2018-01-09 12:26
 */
@Data
public class OrderRequestStore implements Serializable {
    private static final long serialVersionUID = 141640684604864684L;

    /*** 店铺ID ***/
    private Long storeId;

    /*** 订单商品 ***/
    private List<OrderRequestGoods> goodsList;

    /*** 优惠活动ID ***/
    private Long promotionId;

    /*** 优惠券活动ID ***/
    private Long couponId;

    /***** 包邮活动ID ****/
    private Long freightPromotionId;

    /*** 留言信息，文本 ***/
    private String orderMessage;

    public static OrderRequestStore build(Long storeId, List<OrderRequestGoods> goodsList) {
        OrderRequestStore ori = new OrderRequestStore();
        ori.setStoreId(storeId);
        ori.setGoodsList(goodsList);
        return ori;
    }
}