package com.topaiebiz.trade.refund.service;

import com.topaiebiz.trade.refund.entity.RefundOrderEntity;

import java.util.List;

/**
 * Description TODO
 * <p>
 *
 * @Author hxpeng
 * <p>
 * Date 2018/4/3 17:13
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface RefundOrderService {

    /**
     * Description: 自动退款
     * <p>
     * Author: hxpeng
     * createTime: 2018/4/3
     *
     * @param:
     **/
    void autoRefund(List<RefundOrderEntity> refundOrderEntities);

}
