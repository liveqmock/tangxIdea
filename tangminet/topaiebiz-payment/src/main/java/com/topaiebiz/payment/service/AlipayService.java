package com.topaiebiz.payment.service;


import com.topaiebiz.pay.dto.refund.RefundParamDTO;
import com.topaiebiz.pay.dto.refund.RefundResultDTO;
import com.topaiebiz.payment.dto.PayParamDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/15 17:20
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface AlipayService {

    /**
     * Description: 构建支付表单
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/15
     *
     * @param:
     **/
    String buildPayForm(Long memberId, PayParamDTO payParamDTO);

    /**
     * Description: 创建app的支付宝支付签名
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/23
     *
     * @param:
     **/
    String buildPaySignForApp(Long memberId, PayParamDTO payParamDTO);

    /**
     * Description: 支付宝同步/异步 请求 处理
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/15
     *
     * @param:
     **/
    Boolean payNotice(HttpServletRequest request);

    /**
     * Description: 退款
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/19
     *
     * @param:
     **/
    RefundResultDTO refund(RefundParamDTO refundParamDTO);

}