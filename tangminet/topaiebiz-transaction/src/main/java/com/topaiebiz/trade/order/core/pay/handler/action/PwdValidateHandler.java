package com.topaiebiz.trade.order.core.pay.handler.action;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.context.MemberContext;
import com.topaiebiz.trade.order.core.pay.handler.AbstractPayContextHandler;
import com.topaiebiz.trade.order.facade.MemberServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.topaiebiz.trade.order.exception.PaymentExceptionEnum.PAY_PWD_EMPTY_ERROR;
import static com.topaiebiz.trade.order.exception.PaymentExceptionEnum.PAY_PWD_HAS_NOT_SET;
import static com.topaiebiz.trade.order.exception.PaymentExceptionEnum.PAY_PWD_WRONG_ERROR;

/***
 * @author yfeng
 * @date 2018-01-17 21:53
 */
@Component("pwdValidateHandler")
public class PwdValidateHandler extends AbstractPayContextHandler {

    @Autowired
    private MemberServiceFacade memberServiceFacade;

    @Override
    public void doPrepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        MemberDto memberDto = MemberContext.get();
        if (!memberDto.hasSetPayPwd()){
            throw new GlobalException(PAY_PWD_HAS_NOT_SET);
        }

        String payPwd = payRequest.getPassword();
        if (StringUtils.isBlank(payPwd)) {
            throw new GlobalException(PAY_PWD_EMPTY_ERROR);
        }
        boolean validPayPwd = memberServiceFacade.validatePayPwd(buyer.getMemberId(), payPwd);
        if (!validPayPwd) {
            throw new GlobalException(PAY_PWD_WRONG_ERROR);
        }
    }
}