package com.topaiebiz.trade.order.core.pay.handler.common;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.context.MemberContext;
import com.topaiebiz.trade.order.core.pay.handler.PayContextHandler;
import com.topaiebiz.trade.order.exception.PaymentExceptionEnum;
import com.topaiebiz.trade.order.facade.MemberServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/***
 * 加载用户的积分和余额资产信息
 * @author yfeng
 * @date 2018-01-19 14:44
 */
@Component("memberLoadHandler")
public class MemberLoadHandler implements PayContextHandler {

    @Autowired
    private MemberServiceFacade memberServiceFacade;

    @Override
    public void prepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        MemberDto memberDto = memberServiceFacade.getMember(buyer.getMemberId());
        if (memberDto.isAccountLock()) {
            throw new GlobalException(PaymentExceptionEnum.ACCOUNT_LOCK_ERROR);
        }
        MemberContext.set(memberDto);
    }
}
