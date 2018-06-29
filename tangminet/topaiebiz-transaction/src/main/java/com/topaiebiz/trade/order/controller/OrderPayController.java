package com.topaiebiz.trade.order.controller;

import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.bo.StorePayBO;
import com.topaiebiz.trade.order.dto.pay.PaySummaryDTO;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.service.OrderPayService;
import com.topaiebiz.trade.order.util.BuyerBOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/***
 * @author yfeng
 * @date 2018-01-08 21:07
 */
@MemberLogin
@RestController
@RequestMapping(value = "/trade/pay/", method = RequestMethod.POST)
public class OrderPayController {

    @Autowired
    private OrderPayService payService;

    @Autowired
    private MessageSender messageSender;

    @RequestMapping("/summary/{payId}")
    public ResponseInfo pageInit(@PathVariable Long payId) {
        MemberTokenDto memberTokenDto = MemberContext.getCurrentMemberToken();
        BuyerBO buyerBO = BuyerBOUtil.buildBuyerBO(memberTokenDto);
        PaySummaryDTO paySummary = payService.getPaySummary(buyerBO, payId);
        return new ResponseInfo(paySummary);
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public ResponseInfo submitPay(@RequestBody PayRequest payRequest) {
        MemberTokenDto memberTokenDto = MemberContext.getCurrentMemberToken();
        BuyerBO buyerBO = BuyerBOUtil.buildBuyerBO(memberTokenDto);
        PayParamContext payParamContext = payService.submitPay(buyerBO, payRequest);
        if (payParamContext.isPkgFull()) {
            //订单完成整单支付
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setMemberId(buyerBO.getMemberId());
            messageDTO.setType(MessageTypeEnum.ORDER_PAY);
            messageDTO.getParams().put("payId", payRequest.getPayId());
            List<Long> orderIds = payParamContext.getStorePayDetails().stream().map(StorePayBO::getOrderId).collect(Collectors.toList());
            messageDTO.getParams().put("orderIds", orderIds);
            messageSender.publicMessage(messageDTO);
        }
        return new ResponseInfo(payRequest.getPayId());
    }
}