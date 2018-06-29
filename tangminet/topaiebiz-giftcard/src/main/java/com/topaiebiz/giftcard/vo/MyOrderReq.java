package com.topaiebiz.giftcard.vo;

import com.nebulapaas.base.po.PagePO;

/**
 * @description: 我的礼卡订单请求参数
 * @author: Jeff Chen
 * @date: created in 下午2:55 2018/1/24
 */
public class MyOrderReq extends PagePO {

    /**
     * 订单状态
     */
    private Integer orderStatus;

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }
}
