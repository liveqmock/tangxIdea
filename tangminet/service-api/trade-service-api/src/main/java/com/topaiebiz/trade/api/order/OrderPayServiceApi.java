package com.topaiebiz.trade.api.order;

import com.nebulapaas.base.enumdata.PayMethodEnum;
import com.topaiebiz.trade.dto.order.OrderPayDTO;
import com.topaiebiz.trade.dto.order.PayInfoDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Description 订单对外接口
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/17 11:41
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及商业目的。
 */
public interface OrderPayServiceApi {

    /**
     * Description: 支付模块--支付/微信退款功能--查询订单信息
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/17
     *
     * @param:
     **/
    PayInfoDTO queryUnpayOrder(Long memberId, Long payId);

    /**
     * 支付通知
     *
     * @param payOrderId 支付单ID
     * @param payMethod  支付方式 见 com.topaiebiz.trade.constants.OrderConstants.PayType
     * @param amount     通知金额，用于防防刷校验
     * @param outTradeNo 外部支付单号(微信或支付宝单号)
     * @return
     */
    Boolean payNotify(Long payOrderId, PayMethodEnum payMethod, BigDecimal amount, String outTradeNo);

    /**
     * 批量查询订单支付单信息
     *
     * @param payIds
     * @return
     */
    Map<Long, OrderPayDTO> queryPayInfos(List<Long> payIds);

    /**
     * 获取支付信息
     *
     * @param payId
     * @return
     */
    OrderPayDTO getPayInfo(Long payId);
}
