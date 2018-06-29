package com.topaiebiz.openapi.service;

import com.topaiebiz.trade.dto.order.OrderDetailDTO;

/**
 * Created by ward on 2018-03-01.
 */
public interface OrderService {

    /**
     * Description: 订单详情
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/2
     *
     * @param:
     **/
    OrderDetailDTO queryOrderDetail(Long orderId);

    /**
     * 手动报关
     *
     * @param orderId
     * @return
     */
    Boolean pushPaymentToCustom(Long orderId);

}
