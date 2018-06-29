package com.topaiebiz.payment.api.impl;

import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.topaiebiz.pay.api.RefundApi;
import com.topaiebiz.pay.dto.refund.RefundParamDTO;
import com.topaiebiz.pay.dto.refund.RefundResultDTO;
import com.topaiebiz.payment.constants.PaymentConstants;
import com.topaiebiz.payment.service.AlipayService;
import com.topaiebiz.payment.service.WechatPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Description 售后对外接口实现曾
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/19 11:13
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Service
public class RefundApiImpl implements RefundApi {

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private WechatPayService wechatPayService;

    @Override
    public RefundResultDTO refund(RefundParamDTO refundDTO) {
        if (null == refundDTO) {
            log.error("----------no refund params");
            return null;
        }
        String payId = refundDTO.getPayId();
        if (StringUtils.isBlank(payId) && StringUtils.isBlank(refundDTO.getPayCallbackNo())) {
            log.error("----------RefundApi refund-- payId and paycallbackNo cant be null at the same time");
            return null;
        }
        String orderType = refundDTO.getOrderType();
        if (!Constants.Order.ORDER_TYPE_GOOD.equals(orderType) && Constants.Order.ORDER_TYPE_CARD.equals(orderType)) {
            log.error("----------RefundApi refund-- orderType is illegal");
            return null;
        }
        refundDTO.setPayId(StringUtils.join(payId, PaymentConstants.UNDER_LINE, orderType));

        String payType = refundDTO.getPayType();
        if (!Constants.Order.PREDEPOSIT.equals(payType) && !Constants.Order.ALIPAY.equals(payType) && !Constants.Order.WECHATPAY.equals(payType)) {
            log.error("----------RefundApi refund-- payType is illegal");
            return null;
        }
        if (null == refundDTO.getRefundPrice()) {
            log.error("----------RefundApi refund-- refund price is null");
            return null;
        }

        RefundParamDTO refundParamDTO = new RefundParamDTO();
        BeanCopyUtil.copy(refundDTO, refundParamDTO);

        if (payType.equals(Constants.Order.ALIPAY)) {
            return alipayService.refund(refundParamDTO);
        } else {
            BigDecimal moneyRate = new BigDecimal(100);
            // 金额转换为分
            refundParamDTO.setRefundPrice(refundParamDTO.getRefundPrice().multiply(moneyRate));
            refundParamDTO.setPayPrice(refundParamDTO.getPayPrice().multiply(moneyRate));
            return wechatPayService.refund(refundParamDTO);
        }
    }
}
