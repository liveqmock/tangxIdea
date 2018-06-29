package com.topaiebiz.trade.refund.controller;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.trade.api.refund.RefundTaskServiceApi;
import com.topaiebiz.trade.refund.dto.RefundAuditdDTO;
import com.topaiebiz.trade.refund.dto.page.RefundPageParamsDTO;
import com.topaiebiz.trade.refund.exception.RefundOrderExceptionEnum;
import com.topaiebiz.trade.refund.service.StoreRefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description 商家售后控制台
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/21 13:52
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@RestController
@Slf4j
@RequestMapping(value = "/trade/refund/store", method = RequestMethod.POST)
public class StoreRefundController {

    @Autowired
    private StoreRefundService storeRefundService;

    @Autowired
    private RefundTaskServiceApi refundTaskServiceApi;

    /**
     * Description: 商家--售后列表分页
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/10
     *
     * @param:
     **/
    @RequestMapping(value = "/getRefundOrderPage")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家售后列表")
    public ResponseInfo getRefundOrderPage(@RequestBody RefundPageParamsDTO pageParams) {
        return new ResponseInfo(storeRefundService.getRefundOrderPage(pageParams));
    }

    /**
     * Description: 商家--售后详情
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/10
     *
     * @param:
     **/
    @RequestMapping(value = "/getOrderDetail/{refundOrderId}")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家售后详情")
    public ResponseInfo getOrderDetail(@PathVariable Long refundOrderId) {
        if (null == refundOrderId) {
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_ORDER_ID_CANT_BE_NULL);
        }
        return new ResponseInfo(storeRefundService.getRefundOrderDetail(refundOrderId));
    }

    /**
     * Description: 审核通过售后申请
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/9
     *
     * @param:
     **/
    @RequestMapping(value = "/auditRefund")
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家审核售后")
    public ResponseInfo auditRefund(@RequestBody RefundAuditdDTO refundAuditdDTO) {
        return new ResponseInfo(storeRefundService.auditApplyForRefund(refundAuditdDTO));
    }


    // 定时器触发接口



    @RequestMapping(value = "/auditPassRefund")
    @PermissionController(value = PermitType.MERCHANT, operationName = "自动同意退款定时器")
    public void auditPassRefund(){
        refundTaskServiceApi.auditPassRefund();
    }

    @RequestMapping(value = "/waitReceive")
    @PermissionController(value = PermitType.MERCHANT, operationName = "自动签收退款定时器")
    public void waitReceive(){
        refundTaskServiceApi.waitReceive();
    }

    @RequestMapping(value = "/auditPassReturn")
    @PermissionController(value = PermitType.MERCHANT, operationName = "自动同意退货定时器")
    public void auditPassReturn(){
        refundTaskServiceApi.auditPassReturn();
    }

    @RequestMapping(value = "/waitingReturn")
    @PermissionController(value = PermitType.MERCHANT, operationName = "自动同意退货定时器")
    public void waitingReturn(){
        refundTaskServiceApi.waitingReturn();
    }

    @RequestMapping(value = "/closeRejectRefund")
    @PermissionController(value = PermitType.MERCHANT, operationName = "自动同意退货定时器")
    public void closeRejectRefund(){
        refundTaskServiceApi.closeRejectRefund();
    }


}
