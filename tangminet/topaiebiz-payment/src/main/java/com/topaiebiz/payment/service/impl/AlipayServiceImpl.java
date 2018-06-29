package com.topaiebiz.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.enumdata.PayMethodEnum;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.card.api.GiftCardApi;
import com.topaiebiz.card.constant.CardOrderStatusEnum;
import com.topaiebiz.card.dto.BriefCardOrderDTO;
import com.topaiebiz.card.dto.CardPaidResultDTO;
import com.topaiebiz.pay.dto.refund.RefundParamDTO;
import com.topaiebiz.pay.dto.refund.RefundResultDTO;
import com.topaiebiz.payment.config.AlipayConfig;
import com.topaiebiz.payment.constants.PaymentConstants;
import com.topaiebiz.payment.dto.AlipayParamsDTO;
import com.topaiebiz.payment.dto.PayParamDTO;
import com.topaiebiz.payment.exception.PaymentExceptionEnum;
import com.topaiebiz.payment.service.AlipayService;
import com.topaiebiz.payment.util.AlipayUtil;
import com.topaiebiz.trade.api.order.OrderPayServiceApi;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.dto.order.PayInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Description 支付宝支付服务实现
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/15 17:22
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Service
public class AlipayServiceImpl implements AlipayService {

    @Autowired
    private OrderPayServiceApi orderPayServiceApi;

    @Autowired
    private GiftCardApi giftCardApi;

    @Autowired
    private DistLockSservice distLockSservice;

    @Override
    public String buildPayForm(Long memberId, PayParamDTO payParamDTO) {
        Long orderPayId = payParamDTO.getOrderPayId();
        AlipayParamsDTO alipayParamsDTO = buildPayInfo(memberId, orderPayId, payParamDTO.getOrderType());

        Map<String, String> params = new TreeMap<>();
        params.put("out_trade_no", alipayParamsDTO.getPayId());
        params.put("seller_id", AlipayConfig.PID);
        params.put("total_amount", alipayParamsDTO.getOrderPrice().toString());
        params.put("subject", alipayParamsDTO.getOrderSubject());
        return AlipayUtil.buildPayForm(params);
    }

    @Override
    public String buildPaySignForApp(Long memberId, PayParamDTO payParamDTO) {
        Long orderPayId = payParamDTO.getOrderPayId();
        AlipayParamsDTO alipayParamsDTO = buildPayInfo(memberId, orderPayId, payParamDTO.getOrderType());

        AlipayTradeAppPayModel appPayModel = new AlipayTradeAppPayModel();
        appPayModel.setSubject(alipayParamsDTO.getOrderSubject());
        appPayModel.setOutTradeNo(alipayParamsDTO.getPayId());
        appPayModel.setTotalAmount(alipayParamsDTO.getOrderPrice().toEngineeringString());
        appPayModel.setProductCode("QUICK_MSECURITY_PAY");
        return AlipayUtil.buildPayApp(appPayModel);
    }

    private AlipayParamsDTO buildPayInfo(Long memberId, Long orderPayId, String orderType) {
        String orderSubject;
        String payId;
        BigDecimal orderPrice;
        if (Constants.Order.ORDER_TYPE_GOOD.equals(orderType) || "good".equals(orderType)) {
            PayInfoDTO orderPayDTO = orderPayServiceApi.queryUnpayOrder(memberId, orderPayId);
            log.info("----------orderPayServiceApi.queryUnpayOrder--  memberId:{},orderPayId:{}; response:{}", memberId, orderPayId, JSON.toJSONString(orderPayDTO));
            // 判断订单是否未支付
            if (OrderConstants.PayStatus.UNPAY.equals(orderPayDTO.getPayState())) {
                orderSubject = Constants.Order.GOOD_BODY;
                payId = StringUtils.join(orderPayDTO.getPayId(), PaymentConstants.UNDER_LINE, Constants.Order.ORDER_TYPE_GOOD);
                orderPrice = orderPayDTO.getPayPrice();
            } else {
                log.error("----------alipay buildPayForm-- order:{} has been payed or cancelled!", orderPayId);
                throw new GlobalException(PaymentExceptionEnum.ORDER_CANT_PAID);
            }
        } else if (Constants.Order.ORDER_TYPE_CARD.equals(orderType)) {
            BriefCardOrderDTO briefCardOrderDTO = giftCardApi.getOrderInfoById(orderPayId);
            log.info("----------giftCardApi.getOrderInfoById--  orderPayId:{}; response:{}", orderPayId, JSON.toJSONString(briefCardOrderDTO));
            if (briefCardOrderDTO.getOrderStatus().equals(CardOrderStatusEnum.UNPAID.getStatusCode())) {
                orderSubject = Constants.Order.CARD_BODY;
                payId = StringUtils.join(briefCardOrderDTO.getOrderId(), PaymentConstants.UNDER_LINE, Constants.Order.ORDER_TYPE_CARD);
                orderPrice = briefCardOrderDTO.getPayAmount();
            } else {
                log.error("----------alipay buildPayForm-- order:{} has been payed or cancelled!", orderPayId);
                throw new GlobalException(PaymentExceptionEnum.ORDER_CANT_PAID);
            }
        } else {
            throw new GlobalException(PaymentExceptionEnum.PAY_TYPE_IS_ILLEGAL);
        }
        return new AlipayParamsDTO(orderSubject, payId, orderPrice);
    }

    @Override
    public Boolean payNotice(HttpServletRequest request) {
        log.info("----------alipay notice-- context:{}", JSON.toJSONString(request.getParameterMap()));
        Boolean checkSign = false;
        try {
            checkSign = AlipayUtil.checkResponseSign(request.getParameterMap());
        } catch (AlipayApiException ex) {
            log.error(ex.getMessage(), ex);
        }
        if (!checkSign) {
            log.error("----------alipay notice-- verification fail！");
            return false;
        }
        //1：校验商户APP ID一致
        String appId = request.getParameter("app_id");
        String pid = request.getParameter("seller_id");
        if (!AlipayConfig.APP_ID.equals(appId) || !AlipayConfig.PID.equals(pid)) {
            log.error("---------- appId or pid not match error");
            return false;
        }

        //2：校验订单
        String outTradeNo = request.getParameter("out_trade_no");

        LockResult memberLock = null;
        try {
            memberLock = distLockSservice.tryLock(Constants.LockOperatons.PAY_NOTICE_LOCK, outTradeNo);
            if (!memberLock.isSuccess()) {
                throw new GlobalException(PaymentExceptionEnum.PAY_NOTICE_AGAING);
            }

            BigDecimal totalAmount = new BigDecimal(request.getParameter("total_amount"));
            String[] outTradeNoArr = outTradeNo.split(PaymentConstants.UNDER_LINE);
            Long orderPayId = Long.parseLong(outTradeNoArr[0]);
            String suffix = outTradeNoArr[1];
            String tradeNo = request.getParameter("trade_no");
            if (StringUtils.isBlank(tradeNo)) {
                log.error("----------Transaction failed, third party payment serial number is empty");
                return false;
            }
            switch (suffix) {
                case Constants.Order.ORDER_TYPE_GOOD:
                    return orderPayServiceApi.payNotify(orderPayId, PayMethodEnum.ALIPAY, totalAmount, tradeNo);
                case Constants.Order.ORDER_TYPE_CARD:
                    CardPaidResultDTO cardPaidResultDTO = new CardPaidResultDTO();
                    cardPaidResultDTO.setOrderId(orderPayId);
                    cardPaidResultDTO.setPayAmount(totalAmount);
                    cardPaidResultDTO.setPayCode(Constants.Order.ALIPAY);
                    cardPaidResultDTO.setPaySn(tradeNo);
                    return giftCardApi.cardPaidCallBack(cardPaidResultDTO);
                default:
                    log.error("----------alipay notice-- orderpay's tradeType is illegal");
                    return false;
            }
        } finally {
            distLockSservice.unlock(memberLock);
        }
    }

    @Override
    public RefundResultDTO refund(RefundParamDTO refundParamDTO) {
        Map<String, String> bizContent = new HashMap<>();
        // 商户支付订单号
        bizContent.put("out_trade_no", refundParamDTO.getPayId());
        // 第三方支付流水
        bizContent.put("trade_no", refundParamDTO.getPayCallbackNo());
        // 商户退款订单号
        bizContent.put("out_request_no", refundParamDTO.getRefundOrderId());
        // 退款金额
        BigDecimal refundPrice = refundParamDTO.getRefundPrice();
        bizContent.put("refund_amount", String.valueOf(refundPrice.doubleValue()));
        // 退款理由
        bizContent.put("refund_reason", refundParamDTO.getRefundReason());
        return AlipayUtil.refund(bizContent);
    }


}