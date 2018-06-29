package com.topaiebiz.trade.order.controller;


import com.alibaba.fastjson.JSONObject;
import com.nebulapaas.common.BindResultUtil;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.trade.api.OrderTaskServiceApi;
import com.topaiebiz.trade.dto.order.OrderDeliveryDTO;
import com.topaiebiz.trade.order.dto.page.OrderPageParamDto;
import com.topaiebiz.trade.order.dto.store.StoreOrderPageParamsDTO;
import com.topaiebiz.trade.order.dto.store.UpdateFreightDTO;
import com.topaiebiz.trade.order.service.StoreOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/19 19:28
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@Slf4j
@RequestMapping(value = "/trade/store", method = RequestMethod.POST)
public class StoreOrderController {

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private OrderTaskServiceApi orderTaskServiceApi;


    @RequestMapping("/queryOrders")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家订单列表")
    public ResponseInfo queryOrders(@RequestBody StoreOrderPageParamsDTO pageParamsDTO) {
        return new ResponseInfo(storeOrderService.queryStoreOrders(pageParamsDTO));
    }

    @RequestMapping("/detail/{orderId}")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家订单详情")
    public ResponseInfo queryOrderDetail(@PathVariable Long orderId) {
        return new ResponseInfo(storeOrderService.queryOrderDetail(orderId));
    }

    @RequestMapping("/shipOrder")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家订单发货")
    public ResponseInfo shipOrder(@Valid @RequestBody OrderDeliveryDTO deliveryDTO, BindingResult bindingResult) {
        BindResultUtil.dealBindResult(bindingResult);
        return new ResponseInfo(storeOrderService.shipOrder(deliveryDTO));
    }

    @RequestMapping("/updateFreight")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家订单修改运费")
    public ResponseInfo updateFreight(@Valid @RequestBody UpdateFreightDTO updateFreightDTO, BindingResult bindingResult) {
        BindResultUtil.dealBindResult(bindingResult);
        return new ResponseInfo(storeOrderService.updateFreight(updateFreightDTO.getOrderId(), updateFreightDTO.getFreight()));
    }

    @RequestMapping("/orderAddress/{orderId}")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家查询订单收货地址")
    public ResponseInfo queryOrderAddress(@PathVariable Long orderId) {
        return new ResponseInfo(storeOrderService.queryOrderAddress(orderId));
    }


    @RequestMapping("/storeIndex")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家个人中心")
    public ResponseInfo storeIndex() {
//        storeOrderService.storeIndex();
        return new ResponseInfo();
    }

    @RequestMapping(value = "/export/order")
    @PermissionController(value = PermitType.MERCHANT, operationName = "创建订单导出参数")
    public ResponseInfo exportOrder(@RequestBody StoreOrderPageParamsDTO orderPageParamDto) throws IOException {
        String result = storeOrderService.buildOrderExportParas(orderPageParamDto);
        if (StringUtils.isNotBlank(result)) {
            return new ResponseInfo(result);
        }
        return new ResponseInfo("500", "导出失败！");
    }


    @RequestMapping(value = "/export/downloadExport/{resourcesId}", method = RequestMethod.GET)
    public void downloadExport(@PathVariable String resourcesId) throws IOException {
        if (StringUtils.isBlank(resourcesId)) {
            return;
        }
        storeOrderService.downloadExportData(response, resourcesId);
    }

    @RequestMapping(value = "/updateExpress")
    @PermissionController(value = PermitType.MERCHANT, operationName = "修改订单物流信息")
    public ResponseInfo updateExpress(@Valid @RequestBody OrderDeliveryDTO deliveryDTO, BindingResult bindingResult) {
        BindResultUtil.dealBindResult(bindingResult);
        return new ResponseInfo(storeOrderService.updateExpress(deliveryDTO));
    }

    @RequestMapping(value = "/addRemarks")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家添加订单备注")
    public ResponseInfo addRemarks(@RequestBody JSONObject jsonObject) {
        Long orderId = jsonObject.getLong("orderId");
        String remarks = jsonObject.getString("remarks");
        if (orderId == null || StringUtils.isBlank(remarks) || remarks.length() > 200) {
            return new ResponseInfo("500", "参数不正确！");
        }
        return new ResponseInfo(storeOrderService.addRemark(orderId, remarks));
    }


    /**
     * 定时器任务
     */
    @RequestMapping(value = "/receivingOrder")
    @PermissionController(value = PermitType.MERCHANT, operationName = "自动收货")
    public void auditPassRefund() {
        orderTaskServiceApi.receivingOrder();
    }

    @RequestMapping(value = "/cancelUnPayOrder")
    @PermissionController(value = PermitType.MERCHANT, operationName = "自动取消未支付的订单")
    public void waitReceive() {
        orderTaskServiceApi.cancelUnPayOrder();
    }

    @RequestMapping(value = "/completeOrders")
    @PermissionController(value = PermitType.MERCHANT, operationName = "自动完成")
    public void auditPassReturn() {
        orderTaskServiceApi.completeOrders();
    }

}