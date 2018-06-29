package com.topaiebiz.openapi.service;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/2 14:35
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface OpenApiOrderService {


    /**
     * Description: 保存订单创建发布的消息
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/2
     *
     * @param:
     **/
    void saveOrderCreateMessage(Long payId);

    /**
     * Description: 保存订单确认退款的消息
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/2
     *
     * @param:
     **/
    void saveOrderRefundMessage(Long storeId, Long orderId);


    /**
     * Description: 定时推单
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/25
     *
     * @param:
     **/
    void pushOrderDetail();


}
