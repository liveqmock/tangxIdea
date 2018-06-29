package com.topaiebiz.trade.refund.service;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.trade.refund.dto.RefundAuditdDTO;
import com.topaiebiz.trade.refund.dto.detail.StoreRefundDetailDTO;
import com.topaiebiz.trade.refund.dto.page.RefundOrderPageDTO;
import com.topaiebiz.trade.refund.dto.page.RefundPageParamsDTO;

/**
 * Description 商家订单业务层
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/20 19:16
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface StoreRefundService {

    /**
     * Description: 商家端--获取售后订单分页
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/10
     *
     * @param:
     **/
    PageInfo<RefundOrderPageDTO> getRefundOrderPage(RefundPageParamsDTO pageParams);

    /**
     * Description: 商家端--获取售后详情
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/10
     *
     * @param:
     **/
    StoreRefundDetailDTO getRefundOrderDetail(Long refundOrderId);

    /**
     * Description: 审核通过售后申请
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/9
     *
     * @param:
     **/
    Boolean auditApplyForRefund(RefundAuditdDTO refundAuditdDTO);
}
