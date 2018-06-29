package com.topaiebiz.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.wxpay.sdk.WXPayConstants;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.enumdata.PayMethodEnum;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.card.api.GiftCardApi;
import com.topaiebiz.card.constant.CardOrderStatusEnum;
import com.topaiebiz.card.dto.BriefCardOrderDTO;
import com.topaiebiz.card.dto.CardPaidResultDTO;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.pay.dto.ReportCustomsDTO;
import com.topaiebiz.pay.dto.refund.RefundParamDTO;
import com.topaiebiz.pay.dto.refund.RefundResultDTO;
import com.topaiebiz.payment.constants.PaymentConstants;
import com.topaiebiz.payment.dto.WeixinPayParamDTO;
import com.topaiebiz.payment.exception.PaymentExceptionEnum;
import com.topaiebiz.payment.service.WechatPayService;
import com.topaiebiz.payment.util.WeChatPayHelper;
import com.topaiebiz.thirdparty.config.WeChatConfig;
import com.topaiebiz.thirdparty.constants.WecahtPayMethod;
import com.topaiebiz.trade.api.order.OrderPayServiceApi;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.dto.order.OrderCustomsResultDTO;
import com.topaiebiz.trade.dto.order.PayInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Description 微信支付服务实现类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/16 16:31
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Service
public class WechatPayServiceImpl implements WechatPayService {

    private final static String SUCCESS = "SUCCESS";

    private final static String REFUND_QUERY_URL = "https://api.mch.weixin.qq.com/pay/refundquery";

    @Autowired
    private OrderPayServiceApi orderPayServiceApi;

    @Autowired
    private GiftCardApi giftCardApi;

    @Autowired
    private DistLockSservice distLockSservice;

    @Override
    public Map<String, String> buildPrepPaySign(WeixinPayParamDTO payParamDTO) {
        Long memberId = MemberContext.getCurrentMemberToken().getMemberId();
        if (StringUtils.isBlank(payParamDTO.getOpenId())) {
            log.error(">>>>>>>>>>wechat pay get opend id fail!");

            /*
             * 获取openID 失败， 一般都是 在支付页面按了返回，然后再次调用获取openId 接口失败， 则出现空白页面，现在跳转到订单列表页面！
             */
//            PayInfoDTO payInfoDTO = orderPayServiceApi.queryUnpayOrder(memberId, payParamDTO.getOrderPayId());
//            if (Constants.Order.ORDER_TYPE_GOOD.equals(payParamDTO.getOrderType())){
//                wechatPayDTO.setGoodType(Constants.Order.ORDER_TYPE_GOOD);
//            }else{
//                wechatPayDTO.setGoodType(Constants.Order.ORDER_TYPE_CARD);
//                wechatPayDTO.setHasBeenPay(payInfoDTO.getPayState().equals(OrderConstants.PayStatus.SUCCESS));
//            }
//            wechatPayDTO.setHasBeenPay(false);
//            return wechatPayDTO;
            throw new GlobalException(PaymentExceptionEnum.UNABLE_TO_GET_USER_OPENID);
        }

        Long orderPayId = payParamDTO.getOrderPayId();
        // 1: 封装支付参数DTO
        String orderSubject;
        String payId;
        BigDecimal orderPrice;
        if (Constants.Order.ORDER_TYPE_GOOD.equals(payParamDTO.getOrderType())) {
            PayInfoDTO orderPayDTO = orderPayServiceApi.queryUnpayOrder(memberId, orderPayId);
            log.warn(">>>>>>>>>>orderPayServiceApi.queryUnpayOrder--  memberId:{},orderPayId:{}; response:{}", memberId, orderPayId, JSON.toJSONString(orderPayDTO));
            // 判断订单是否未支付
            if (orderPayDTO.getPayState().equals(OrderConstants.PayStatus.UNPAY)) {
                orderSubject = Constants.Order.GOOD_BODY;
                payId = StringUtils.join(orderPayDTO.getPayId(), PaymentConstants.UNDER_LINE, Constants.Order.ORDER_TYPE_GOOD);
                orderPrice = orderPayDTO.getPayPrice();
            } else {
                log.error(">>>>>>>>>>wechatpay buildPrepPaySign-- order:{} has been payed or cancelled!", orderPayId);
                return null;
            }
        } else {
            BriefCardOrderDTO briefCardOrderDTO = giftCardApi.getOrderInfoById(orderPayId);
            log.warn(">>>>>>>>>>giftCardApi.getOrderInfoById--  orderPayId:{}; response:{}", orderPayId, JSON.toJSONString(briefCardOrderDTO));
            if (briefCardOrderDTO.getOrderStatus().equals(CardOrderStatusEnum.UNPAID.getStatusCode())) {
                orderSubject = Constants.Order.CARD_BODY;
                payId = StringUtils.join(briefCardOrderDTO.getOrderId(), PaymentConstants.UNDER_LINE, Constants.Order.ORDER_TYPE_CARD);
                orderPrice = briefCardOrderDTO.getPayAmount();
            } else {
                log.error(">>>>>>>>>>wechatpay buildPayForm-- order:{} has been payed or cancelled!", orderPayId);
                throw new GlobalException(PaymentExceptionEnum.ORDER_CANT_PAID);
            }
        }
        orderPrice = orderPrice.multiply(Constants.Order.INTEGRAL_RATE);


        // 2: 拼装微信支付参数MAP
        SortedMap<String, String> sortedMap = new TreeMap<>();
        sortedMap.put("appid", WeChatConfig.APP_ID);
        sortedMap.put("mch_id", WeChatConfig.MCH_ID);
        sortedMap.put("notify_url", WeChatConfig.PAY_NOTIFY_URL);
        sortedMap.put("body", orderSubject);
        sortedMap.put("nonce_str", WeChatPayHelper.generateNonceStr());
        sortedMap.put("openid", payParamDTO.getOpenId());
        sortedMap.put("out_trade_no", payId);
        sortedMap.put("spbill_create_ip", payParamDTO.getIp());
        sortedMap.put("total_fee", String.valueOf(orderPrice.intValue()));
        sortedMap.put("trade_type", WecahtPayMethod.JSAPI);


        // 3：获取预支付报文
        try {
            // 调取微信统一下单结果，返回xml字符串
            sortedMap.put("sign", WeChatPayHelper.generateSignature(sortedMap, WeChatConfig.APP_KEY));
            String xmlRequest = WeChatPayHelper.mapToXml(sortedMap);
            log.warn(">>>>>>>>>>wechat pay xml request:{}", xmlRequest);
            String result = WeChatPayHelper.httpRequest(WeChatConfig.WECHAT_PAY_URL, xmlRequest, false);
            log.warn(">>>>>>>>>>wechat pay xml response:{}", result);
            if (WeChatPayHelper.isSignatureValid(result, WeChatConfig.APP_KEY)) {
                Map<String, String> resultMap = WeChatPayHelper.xmlToMap(result);
                String returnCode = resultMap.get("return_code");
                String resultCode = resultMap.get("result_code");
                if (SUCCESS.equals(returnCode) && SUCCESS.equals(resultCode)) {
                    // 创建预支付报文Map
                    Map<String, String> parpPayMap = new HashMap<>();
                    parpPayMap.put("appId", WeChatConfig.APP_ID);
                    parpPayMap.put("timeStamp", String.valueOf(WeChatPayHelper.getCurrentTimestamp()));
                    parpPayMap.put("nonceStr", WeChatPayHelper.generateNonceStr());
                    parpPayMap.put("package", "prepay_id=" + resultMap.get("prepay_id"));
                    parpPayMap.put("signType", "MD5");
                    parpPayMap.put("paySign", WeChatPayHelper.generateSignature(parpPayMap, WeChatConfig.APP_KEY, WXPayConstants.SignType.MD5));
//                    wechatPayDTO.setHasBeenPay(false);
//                    wechatPayDTO.setResultMap(parpPayMap);
                    return parpPayMap;
                }

                log.warn(">>>>>>>>>>wechatPay buildPrepPaySign-- return code is't success！params：{}-return{}", JSON.toJSONString(sortedMap), JSON.toJSONString(result));
            } else {
                log.warn(">>>>>>>>>>wechatPay buildPrepPaySign-- verification failed！params：{}-return{}", JSON.toJSONString(sortedMap), JSON.toJSONString(result));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        throw new GlobalException(PaymentExceptionEnum.FAILED_TO_BUILD_PREPPAY_SIGN);
    }

    @Override
    public Boolean payNotice(String noticeXml) {
        //1： 验签
        Map<String, String> noticeMap;
        boolean result;
        try {
            noticeMap = WeChatPayHelper.xmlToMap(noticeXml);
            result = WeChatPayHelper.isSignatureValid(noticeMap, WeChatConfig.APP_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (!result) {
            log.error(">>>>>>>>>>wecahtpay payNotice-- verification failed！params：{}", noticeXml);
            return false;
        }
        if (!noticeMap.get("return_code").equals("SUCCESS")) {
            log.error(">>>>>>>>>>wecahtpay payNotice-- pay was failed, param：{}", noticeMap.get("return_msg"));
            return false;
        }
        log.warn(">>>>>>>>>>wecht pay is success, notice order to update!");
        //2：校验公众账号ID
        if (!WeChatConfig.APP_ID.equals(noticeMap.get("appid"))) {
            log.error(">>>>>>>>>>wecahtpay payNotice-- appid is not equals");
            return false;
        }
        //3：校验商户号
        if (!WeChatConfig.MCH_ID.equals(noticeMap.get("mch_id"))) {
            log.error(">>>>>>>>>>wecahtpay payNotice-- mchId is not equals");
            return false;
        }
        //4：校验订单
        String outTradeNo = noticeMap.get("out_trade_no");

        LockResult payNoticeLock = null;
        try {
            payNoticeLock = distLockSservice.tryLock(Constants.LockOperatons.PAY_NOTICE_LOCK, outTradeNo);
            if (!payNoticeLock.isSuccess()) {
                throw new GlobalException(PaymentExceptionEnum.PAY_NOTICE_AGAING);
            }

            BigDecimal totalFee = new BigDecimal(noticeMap.get("total_fee")).divide(Constants.Order.INTEGRAL_RATE, 2, RoundingMode.UP);
            String[] outTradeNoArr = outTradeNo.split(PaymentConstants.UNDER_LINE);
            Long payId = Long.parseLong(outTradeNoArr[0]);
            String suffix = outTradeNoArr[1];
            String transactionId = noticeMap.get("transaction_id");
            if (StringUtils.isBlank(transactionId)) {
                log.error(">>>>>>>>>>Transaction failed, third party payment serial number is empty");
                return false;
            }
            switch (suffix) {
                case Constants.Order.ORDER_TYPE_GOOD:
                    log.warn(">>>>>>>>>>wechat pay is going to notice good order!");
                    return orderPayServiceApi.payNotify(payId, PayMethodEnum.WX_JSAPI, totalFee, transactionId);
                case Constants.Order.ORDER_TYPE_CARD:
                    log.warn(">>>>>>>>>>wechat pay is going to notice card order!");
                    CardPaidResultDTO cardPaidResultDTO = new CardPaidResultDTO();
                    cardPaidResultDTO.setOrderId(payId);
                    cardPaidResultDTO.setPayAmount(totalFee);
                    cardPaidResultDTO.setPayCode(Constants.Order.WECHATPAY);
                    cardPaidResultDTO.setPaySn(transactionId);
                    return giftCardApi.cardPaidCallBack(cardPaidResultDTO);
                default:
                    log.error(">>>>>>>>>>wecahtpay payNotice-- orderpay's tradeType is illegal");
                    return false;
            }
        } finally {
            distLockSservice.unlock(payNoticeLock);
        }
    }

    @Override
    public RefundResultDTO refund(RefundParamDTO refundParamDTO) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("appid", WeChatConfig.APP_ID);
        paramsMap.put("mch_id", WeChatConfig.MCH_ID);
        paramsMap.put("sign_type", WeChatConfig.SIGN_TYPE);
        paramsMap.put("nonce_str", WeChatPayHelper.generateNonceStr());

        // out_trade_no / transaction_id 二选一
        paramsMap.put("out_trade_no", refundParamDTO.getPayId());
//        paramsMap.put("transaction_id", refundParamDTO.getPayCallbackNo());

        paramsMap.put("out_refund_no", refundParamDTO.getRefundOrderId());

        paramsMap.put("total_fee", String.valueOf(refundParamDTO.getPayPrice().intValue()));
        paramsMap.put("refund_fee", String.valueOf(refundParamDTO.getRefundPrice().intValue()));
        paramsMap.put("refund_desc", refundParamDTO.getRefundReason());

        RefundResultDTO refundResultDTO = new RefundResultDTO();
        try {
            paramsMap.put("sign", WeChatPayHelper.generateSignature(paramsMap, WeChatConfig.APP_KEY, WXPayConstants.SignType.MD5));
            // 调用退款请求
            String resultXml = WeChatPayHelper.httpRequest(WeChatConfig.REFUND_PAY_URL, WeChatPayHelper.generateSignedXml(paramsMap, WeChatConfig.APP_KEY), true);
            log.warn(">>>>>>>>>>wechatpay refund-- request refund url params：{}，return：{}", JSON.toJSONString(paramsMap), resultXml);

            // 1:验签
            if (!WeChatPayHelper.isSignatureValid(resultXml, WeChatConfig.APP_KEY)) {
                throw new RuntimeException(">>>>>>>>>>wechatpay refund-- verification failed！");
            }
            Map<String, String> resultMap = WeChatPayHelper.xmlToMap(resultXml);

            if (!SUCCESS.equals(resultMap.get("return_code")) || !SUCCESS.equals(resultMap.get("result_code"))) {
                throw new RuntimeException(">>>>>>>>>>wechatpay refund-- return code is't success,refundId:" + refundParamDTO.getRefundOrderId() + ", return code:" + resultMap.get("return_code") + ", result_code:" + resultMap.get("result_code"));
            }
            //2：校验公众账号ID
            String appId = resultMap.get("appid");
            if (StringUtils.isBlank(appId) || !appId.equals(WeChatConfig.APP_ID)) {
                throw new RuntimeException(">>>>>>>>>>wechatpay refund-- appid is not equals");
            }
            //3：校验商户号
            String mchId = resultMap.get("mch_id");
            if (StringUtils.isBlank(mchId) || !mchId.equals(WeChatConfig.MCH_ID)) {
                throw new RuntimeException(">>>>>>>>>>wechatpay refund-- mchId is not equals");
            }
            //3：校验商户退款单号
            String refundId = resultMap.get("out_refund_no");
            if (StringUtils.isBlank(refundId) || !refundId.equals(refundParamDTO.getRefundOrderId())) {
                throw new RuntimeException(">>>>>>>>>>wechatpay refund-- refundOrderId is not equals");
            }
            // 退款金额
            BigDecimal refundFee = new BigDecimal(resultMap.get("refund_fee"));
            if (refundFee.compareTo(refundParamDTO.getRefundPrice()) != 0) {
                throw new RuntimeException(">>>>>>>>>>wechatpay refund-- response'amount does not match the amount of refund order！");
            }
            refundResultDTO.setResultCode(Constants.Order.REFUND_SUCCESS);
            refundResultDTO.setCallBackNo(resultMap.get("refund_id"));
            return refundResultDTO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        refundResultDTO.setResultCode(Constants.Order.REFUND_FAIL);
        return refundResultDTO;
    }


    @Override
    public String refundResult(Long refundId) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("appid", WeChatConfig.APP_ID);
        paramsMap.put("mch_id", WeChatConfig.MCH_ID);
        paramsMap.put("sign_type", WeChatConfig.SIGN_TYPE);
        paramsMap.put("nonce_str", WeChatPayHelper.generateNonceStr());
        paramsMap.put("out_refund_no", String.valueOf(refundId));
        try {
            paramsMap.put("sign", WeChatPayHelper.generateSignature(paramsMap, WeChatConfig.APP_KEY, WXPayConstants.SignType.MD5));
            return WeChatPayHelper.httpRequest("https://api.mch.weixin.qq.com/pay/refundquery", WeChatPayHelper.generateSignedXml(paramsMap, WeChatConfig.APP_KEY), false);
        } catch (Exception e) {
            log.error(StringUtils.join(">>>>>>>>>>微信退款结果查询失败：", e.getMessage()), e);
            return e.getMessage();
        }
    }

    @Override
    public OrderCustomsResultDTO reportCustoms(ReportCustomsDTO customsDTO) {
        SortedMap<String, String> sortedMap = new TreeMap<>();
        sortedMap.put("appid", WeChatConfig.APP_ID);
        sortedMap.put("mch_id", WeChatConfig.MCH_ID);
        sortedMap.put("out_trade_no", customsDTO.getOutTradeNo());
        sortedMap.put("transaction_id", customsDTO.getTransactionId());
        sortedMap.put("customs", WeChatConfig.CUSTOMS);
        sortedMap.put("mch_customs_no", WeChatConfig.MCH_CUSTOMS_NO);
        try {
            sortedMap.put("sign", WeChatPayHelper.generateSignature(sortedMap, WeChatConfig.APP_KEY));
            String xmlRequest = WeChatPayHelper.mapToXml(sortedMap);
            log.warn(">>>>>>>>>>wechat customdeclareorder xml request:{}", xmlRequest);
            String resultXml = WeChatPayHelper.httpRequest(WeChatConfig.MCH_CUSTOMS_URL, xmlRequest, false);
            log.warn(">>>>>>>>>>wechat customdeclareorder xml response:{}", resultXml);
            // 1:验签
            if (!WeChatPayHelper.isSignatureValid(resultXml, WeChatConfig.APP_KEY)) {
                throw new RuntimeException(">>>>>>>>>>wechatpay refund-- verification failed！");
            }
            Map<String, String> resultMap = WeChatPayHelper.xmlToMap(resultXml);

            if (!SUCCESS.equals(resultMap.get("return_code")) || !SUCCESS.equals(resultMap.get("result_code"))) {
                throw new RuntimeException(">>>>>>>>>>wechatpay refund-- return code is't success");
            }
            //2：校验公众账号ID
            String appId = resultMap.get("appid");
            if (StringUtils.isBlank(appId) || !appId.equals(WeChatConfig.APP_ID)) {
                throw new RuntimeException(">>>>>>>>>>wechatpay refund-- appid is not equals");
            }
            //3：校验商户号
            String mchId = resultMap.get("mch_id");
            if (StringUtils.isBlank(mchId) || !mchId.equals(WeChatConfig.MCH_ID)) {
                throw new RuntimeException(">>>>>>>>>>wechatpay refund-- mchId is not equals");
            }

            // 报关结果DTO
            OrderCustomsResultDTO orderCustomsResultDTO = new OrderCustomsResultDTO();
            orderCustomsResultDTO.setReportTime(new Date());
            orderCustomsResultDTO.setReportWay(customsDTO.getPayMethodEnum().getName());
            orderCustomsResultDTO.setWxResultCode(resultMap.get("state"));
            orderCustomsResultDTO.setOutTradeNo(resultMap.get("transaction_id"));
            orderCustomsResultDTO.setMmgOrderId(customsDTO.getOrderId());

            //4：获取结果
            return orderCustomsResultDTO;
        } catch (Exception e) {
            log.error(">>>>>>>>>>wechatpay report order to customs fail,", e);
        }
        return null;
    }


//    public static void main(String[] args) {
//        SortedMap<String, String> sortedMap = new TreeMap<>();
//        sortedMap.put("appid", "wxd17c02bad3d9d844");
//        sortedMap.put("mch_id", "1255340701");
//        sortedMap.put("out_trade_no", "976349366777303041_good");
//        sortedMap.put("nonce_str", WeChatPayHelper.generateNonceStr());
//        sortedMap.put("sign_type", "MD5");
//        try {
//            sortedMap.put("sign", WeChatPayHelper.generateSignature(sortedMap, "96e79218965eb72c92a549dd5a330112"));
//            String xmlRequest = WeChatPayHelper.mapToXml(sortedMap);
//            System.out.println(xmlRequest);
//            String resultXml = WeChatPayHelper.httpRequest("https://api.mch.weixin.qq.com/pay/orderquery", xmlRequest, false);
//            System.out.println(resultXml);
//        } catch (Exception e) {
//            log.error(">>>>>>>>>>wechatpay report order to customs fail,", e);
//        }

//        Map<String, String> paramsMap = new HashMap<>();
//        paramsMap.put("appid", "wxd17c02bad3d9d844");
//        paramsMap.put("mch_id", "1255340701");
//        paramsMap.put("sign_type", "MD5");
//        paramsMap.put("nonce_str", WeChatPayHelper.generateNonceStr());
//
//        // out_trade_no / transaction_id 二选一
//        paramsMap.put("out_trade_no", "976349366777303041_good");
////        paramsMap.put("transaction_id", refundParamDTO.getPayCallbackNo());
//
//        paramsMap.put("out_refund_no", "12345678911");
//
//        paramsMap.put("total_fee", "53700");
//        paramsMap.put("refund_fee", "53700");
//        paramsMap.put("refund_desc", "系统异常导致取消");
//
//        RefundResultDTO refundResultDTO = new RefundResultDTO();
//        try {
//            paramsMap.put("sign", WeChatPayHelper.generateSignature(paramsMap, "96e79218965eb72c92a549dd5a330112"));
//            String xmlRequest = WeChatPayHelper.mapToXml(paramsMap);
//            System.out.println(xmlRequest);
//            String resultXml = WeChatPayHelper.httpRequest("https://api.mch.weixin.qq.com/secapi/pay/refund", xmlRequest, true);
//            System.out.println(resultXml);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//    }


}
