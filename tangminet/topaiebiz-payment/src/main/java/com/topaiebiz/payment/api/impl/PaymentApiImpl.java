package com.topaiebiz.payment.api.impl;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.base.enumdata.PayMethodEnum;
import com.topaiebiz.pay.api.PaymentApi;
import com.topaiebiz.pay.dto.ReportCustomsDTO;
import com.topaiebiz.payment.service.WechatPayService;
import com.topaiebiz.payment.util.AlipayUtil;
import com.topaiebiz.trade.api.order.OrderServiceApi;
import com.topaiebiz.trade.dto.order.OrderCustomsResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description 第三方支付接口 实现层
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/15 14:39
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Service
public class PaymentApiImpl implements PaymentApi {

    @Autowired
    private OrderServiceApi orderServiceApi;

    @Autowired
    private WechatPayService wechatPayService;

    @Override
    public void reportCustoms(ReportCustomsDTO reportCustomsDTO) {
        if (null == reportCustomsDTO){
            return;
        }
        log.warn(">>>>>>>>>>report the order to customs, params:{}", JSON.toJSONString(reportCustomsDTO));

        OrderCustomsResultDTO orderCustomsResultDTO = null;
        if (reportCustomsDTO.getPayMethodEnum().equals(PayMethodEnum.ALIPAY)){
            orderCustomsResultDTO = AlipayUtil.reportCustoms(reportCustomsDTO);
        }else if (reportCustomsDTO.getPayMethodEnum().equals(PayMethodEnum.WX_JSAPI)){
            orderCustomsResultDTO = wechatPayService.reportCustoms(reportCustomsDTO);
        }
        if (null == orderCustomsResultDTO || !orderServiceApi.saveOrderReportCustomsResult(orderCustomsResultDTO)){
            log.error(">>>>>>>>>>save the result for report order to customs fail!");
        }else{
            log.error(">>>>>>>>>>save the result for report order to customs success!");
        }
    }
}
