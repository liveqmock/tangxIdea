package com.topaiebiz.trade.order.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.trade.api.OrderTaskServiceApi;
import com.topaiebiz.trade.order.dto.page.OrderPageParamDto;
import com.topaiebiz.trade.order.service.CustomerOrderServie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description 用户订单控制层
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/19 17:25
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@MemberLogin
@RestController
@RequestMapping(value = "/trade/customer", method = RequestMethod.POST)
public class CustomerOrderController {

    @Autowired
    private CustomerOrderServie customerOrderServie;

    @Autowired
    private OrderTaskServiceApi orderTaskServiceApi;

    /**
     * Description: 查询用户订单列表
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/19
     *
     * @param:
     **/
    @RequestMapping(value = "/queryMyOrders", method = RequestMethod.POST)
    public ResponseInfo queryMyOrders(@RequestBody OrderPageParamDto orderPageParamDto) {
        return new ResponseInfo(customerOrderServie.queryCustomerOrders(orderPageParamDto));
    }

    /**
     * Description: 查询订单详情
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/19
     *
     * @param:
     **/
    @RequestMapping(value = "/queryOrderDetail/{orderId}", method = RequestMethod.POST)
    public ResponseInfo queryOrderDetail(@PathVariable Long orderId) {
        return new ResponseInfo(customerOrderServie.queryCustomerOrderDetail(orderId));
    }

    /**
     * Description: 取消订单
     **/
    @RequestMapping(value = "/cancel/{payId}")
    public ResponseInfo cancelOrder(@PathVariable Long payId) {
        return new ResponseInfo(customerOrderServie.cancelOrder(payId));
    }

    /**
     * Description: 延长收货
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/19
     *
     * @param:
     **/
    @RequestMapping(value = "/extendShip/{orderId}")
    public ResponseInfo extendShip(@PathVariable Long orderId) {
        return new ResponseInfo(customerOrderServie.extendShip(orderId));
    }

    /**
     * Description: 确认收货
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/19
     *
     * @param:
     **/
    @RequestMapping(value = "/confirmReceipt/{orderId}")
    public ResponseInfo confirmReceipt(@PathVariable Long orderId) {
        return new ResponseInfo(customerOrderServie.confirmReceipt(orderId));
    }

    /**
     * Description: 删除订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/19
     *
     * @param:
     **/
    @RequestMapping(value = "/deleteOrder/{orderId}")
    public ResponseInfo deleteOrder(@PathVariable Long orderId) {
        return new ResponseInfo(customerOrderServie.deleteOrder(orderId));
    }


    /**
     * Description: 去评价 获取 待评价的商品
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/7
     *
     * @param:
     **/
    @RequestMapping(value = "/evaluateGoods/{orderId}")
    public ResponseInfo queryEvaluateGoods(@PathVariable Long orderId) {
        return new ResponseInfo(customerOrderServie.evaluateOrder(orderId));
    }


    @RequestMapping(value = "/autoComplete")
    public void autoComplete() {
        orderTaskServiceApi.completeOrders();
    }

    @RequestMapping(value = "/autoCancel")
    public void autoCancel() {
        orderTaskServiceApi.cancelUnPayOrder();
    }

    @RequestMapping(value = "/autoReceipt")
    public void autoReceipt() {
        orderTaskServiceApi.receivingOrder();
    }

}
