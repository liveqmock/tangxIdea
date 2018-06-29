package com.topaiebiz.trade.refund.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.trade.refund.dto.RefundAuditdDTO;
import com.topaiebiz.trade.refund.dto.page.RefundPageParamsDTO;
import com.topaiebiz.trade.refund.service.PlatformRefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description 平台售后管理控制器
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/9 17:21
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@RestController
@Slf4j
@RequestMapping(value = "/trade/refund/platform", method = RequestMethod.POST)
public class PlatformRefundController {

    @Autowired
    private PlatformRefundService platformRefundService;


    /**
     * Description: 平台--售后列表分页
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/10
     *
     * @param:
     **/
    @RequestMapping(value = "/refundPage")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台售后列表")
    public ResponseInfo refundPage(@RequestBody RefundPageParamsDTO pageParams) {
        return new ResponseInfo(platformRefundService.getPlatformRefundOrderPage(pageParams));
    }

    /**
     * Description: 平台--售后详情
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/10
     *
     * @param:
     **/
    @RequestMapping(value = "/refundDetail/{refundOrderId}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台售后详情")
    public ResponseInfo refundDetail(@PathVariable Long refundOrderId) {
        return new ResponseInfo(platformRefundService.getPlatformRefundOrderDetail(refundOrderId));
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
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台售后审核")
    public ResponseInfo auditRefund(@RequestBody RefundAuditdDTO refundAuditdDTO) {
        return new ResponseInfo(platformRefundService.auditApplyForRefund(refundAuditdDTO));
    }


}
