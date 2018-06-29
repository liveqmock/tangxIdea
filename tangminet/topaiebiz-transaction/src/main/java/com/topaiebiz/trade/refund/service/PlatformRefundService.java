package com.topaiebiz.trade.refund.service;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.trade.refund.dto.RefundAuditdDTO;
import com.topaiebiz.trade.refund.dto.detail.PlatformRefundDetailDTO;
import com.topaiebiz.trade.refund.dto.page.RefundOrderPageDTO;
import com.topaiebiz.trade.refund.dto.page.RefundPageParamsDTO;

/**
 * Description 平台售后订单业务层
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/20 19:06
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface PlatformRefundService {

    /**
     * Description: 运营端--获取售后订单分页
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/10
     *
     * @param:
     **/
    PageInfo<RefundOrderPageDTO> getPlatformRefundOrderPage(RefundPageParamsDTO pageParams);

    /**
     * Description: 平台端--获取售后详情
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/10
     *
     * @param:
     **/
    PlatformRefundDetailDTO getPlatformRefundOrderDetail(Long refundOrderId);

    /**
     * Description: 平台端--审核平台介入的售后订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/21
     *
     * @param:
     **/
    boolean auditApplyForRefund(RefundAuditdDTO refundAuditdDTO);
}
