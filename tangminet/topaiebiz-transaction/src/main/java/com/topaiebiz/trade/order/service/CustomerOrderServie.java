package com.topaiebiz.trade.order.service;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.trade.order.dto.customer.CmOrderDetailDTO;
import com.topaiebiz.trade.order.dto.customer.CmOrderPageDTO;
import com.topaiebiz.trade.order.dto.customer.EvaluateGoodDTO;
import com.topaiebiz.trade.order.dto.page.OrderPageParamDto;

import java.util.List;

/**
 * Description 用户订单service
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/20 13:37
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface CustomerOrderServie {

    /**
     *
     * Description: 用户--订单分页
     *
     * Author: hxpeng
     * createTime: 2018/1/12
     *
     * @param:
     **/
    PageInfo<CmOrderPageDTO> queryCustomerOrders(OrderPageParamDto paramDto);

    /**
     *
     * Description: 用户查询订单详情
     *
     * Author: hxpeng
     * createTime: 2018/1/12
     *
     * @param:
     **/
    CmOrderDetailDTO queryCustomerOrderDetail(Long orderId);

    /**
     *
     * Description: 取消订单
     *
     * Author: hxpeng
     * createTime: 2018/1/13
     *
     * @param:
     **/
    Boolean cancelOrder(Long orderId);

    /**
     *
     * Description: 删除订单
     *
     * Author: hxpeng
     * createTime: 2018/1/13
     *
     * @param:
     **/
    Boolean deleteOrder(Long orderId);

    /**
     *
     * Description: 确认收货
     *
     * Author: hxpeng
     * createTime: 2018/1/13
     *
     * @param:
     **/
    Boolean confirmReceipt(Long orderId);


    /**
     *
     * Description: 延长收货
     *
     * Author: hxpeng
     * createTime: 2018/1/18
     *
     * @param:
     **/
    Boolean extendShip(Long orderId);

    /**
    *
    * Description: 是否可以评价
    *
    * Author: hxpeng
    * createTime: 2018/2/7
    *
    * @param:
    **/
    List<EvaluateGoodDTO> evaluateOrder(Long orderId);

    /**
    *
    * Description: 查询用户有效的订单数
    *
    * Author: hxpeng
    * createTime: 2018/4/24
    *
    * @param:
    **/
    Integer queryMemberVaildOrderCounts(Long memberId);
}
