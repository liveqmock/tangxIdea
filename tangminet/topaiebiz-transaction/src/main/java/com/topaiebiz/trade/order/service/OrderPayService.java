package com.topaiebiz.trade.order.service;

import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.dto.pay.PaySummaryDTO;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;

import java.math.BigDecimal;

/***
 * @author yfeng
 * @date 2018-01-17 22:14
 */
public interface OrderPayService {

    PaySummaryDTO getPaySummary(BuyerBO buyerBO, Long payId);

    PayParamContext submitPay(BuyerBO buyerBO, PayRequest payRequest);

    Boolean payNotify(Long payOrderId, String payMethod, BigDecimal amount, String outTradeNo);
}
