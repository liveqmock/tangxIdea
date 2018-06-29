package com.topaiebiz.payment.service;


import com.topaiebiz.pay.dto.refund.RefundParamDTO;
import com.topaiebiz.pay.dto.refund.RefundResultDTO;
import com.topaiebiz.pay.dto.ReportCustomsDTO;
import com.topaiebiz.payment.dto.WeixinPayParamDTO;
import com.topaiebiz.trade.dto.order.OrderCustomsResultDTO;

import java.util.Map;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/16 16:31
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface WechatPayService {


    /**
    *
    * Description: 构建预支付,返回JSON 预支付报文
    *
    * Author: hxpeng
    * createTime: 2018/1/16
    *
    * @param:
    **/
    Map<String, String> buildPrepPaySign(WeixinPayParamDTO payParamDTO);


    /**
    *
    * Description: 支付异步通知
    *
    * Author: hxpeng
    * createTime: 2018/1/16
    *
    * @param: 
    **/
    Boolean payNotice(String noticeXml);


    /**
    *
    * Description: 退款
    *
    * Author: hxpeng
    * createTime: 2018/1/16
    *
    * @param: 
    **/
    RefundResultDTO refund(RefundParamDTO refundParamDTO);


    /**
     *
     * Description: 查询退款详情demo 线上
     *
     * Author: hxpeng
     * createTime: 2018/3/8
     *
     * @param:
     **/
    String refundResult(Long refundId);

    /**
    *
    * Description: 微信报关
    *
    * Author: hxpeng
    * createTime: 2018/3/17
    *
    * @param:
    **/
    OrderCustomsResultDTO reportCustoms(ReportCustomsDTO customsDTO);
}
