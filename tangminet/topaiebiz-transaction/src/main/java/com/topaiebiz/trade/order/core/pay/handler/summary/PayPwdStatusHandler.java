package com.topaiebiz.trade.order.core.pay.handler.summary;

import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.context.MemberContext;
import com.topaiebiz.trade.order.core.pay.context.PaySummaryContext;
import com.topaiebiz.trade.order.core.pay.handler.AbstractPayContextHandler;
import com.topaiebiz.trade.order.dto.pay.PaySummaryDTO;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-17 21:55
 */
@Component("pwdStatusHandler")
public class PayPwdStatusHandler extends AbstractPayContextHandler {

    @Override
    public void doPrepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        PaySummaryDTO paySummaryDTO = PaySummaryContext.get();
        MemberDto memberDto = MemberContext.get();

        //标记用户支付密码状态
        paySummaryDTO.setHasPayPwd(memberDto.hasSetPayPwd());
        paySummaryDTO.setMobile(memberDto.getTelephone());
    }
}