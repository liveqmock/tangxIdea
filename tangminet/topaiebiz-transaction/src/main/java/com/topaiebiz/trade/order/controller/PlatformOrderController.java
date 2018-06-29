package com.topaiebiz.trade.order.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.trade.order.dto.page.OrderPageParamDto;
import com.topaiebiz.trade.order.service.PlatformOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * Description 平台订单控制层
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/12 11:57
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@RestController
@Slf4j
@RequestMapping(value = "/trade/platform", method = RequestMethod.POST)
public class PlatformOrderController {

    @Autowired
    private PlatformOrderService platformOrderService;

    @Autowired
    private HttpServletResponse response;

    /**
     * Description: 平台--订单列表
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/20
     *
     * @param:
     **/
    @RequestMapping(value = "/queryOrders")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台订单列表")
    public ResponseInfo queryOrders(@RequestBody OrderPageParamDto paramDto) {
        return new ResponseInfo(platformOrderService.queryPlatformOrders(paramDto));
    }


    /**
     * Description: 订单详情
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/1
     *
     * @param:
     **/
    @RequestMapping(value = "/orderdetail/{orderId}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台订单详情")
    public ResponseInfo orderdetail(@PathVariable Long orderId) {
        return new ResponseInfo(platformOrderService.queryCommonOrderDetail(orderId));
    }

    /**
    *
    * Description: 下载日常订单数据
    *
    * Author: hxpeng
    * createTime: 2018/4/24
    *
    * @param:
    **/
//    @RequestMapping(value = "/downloadDailyData", method = RequestMethod.GET)
//    public void downloadDailyData(Integer days, String exportKey) {
//        if (null == days){
//            days = 1;
//        }
//        platformOrderService.downloadDailyOrderData(days, response);
//    }
}
