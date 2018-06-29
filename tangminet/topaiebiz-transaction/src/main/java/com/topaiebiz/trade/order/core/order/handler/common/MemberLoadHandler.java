package com.topaiebiz.trade.order.core.order.handler.common;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.trade.order.core.order.context.BuyerContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.exception.PaymentExceptionEnum;
import com.topaiebiz.trade.order.facade.MemberServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-19 14:44
 */
@Component("orderMemberLoadHandler")
public class MemberLoadHandler implements OrderSubmitHandler {

    @Autowired
    private MemberServiceFacade memberServiceFacade;

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        BuyerBO buyer = BuyerContext.get();
        MemberDto memberDto = memberServiceFacade.getMember(buyer.getMemberId());
        if (memberDto.isAccountLock()) {
            throw new GlobalException(PaymentExceptionEnum.ACCOUNT_LOCK_ERROR);
        }
    }
}
