package com.topaiebiz.trade.order.core.order.handler.pageinit;

import com.topaiebiz.member.dto.address.MemberAddressDto;
import com.topaiebiz.trade.order.core.order.context.AddressContext;
import com.topaiebiz.trade.order.core.order.context.BuyerContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.facade.MemberAddressServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-09 16:57
 */
@Component("defaultAddressLoadHandler")
public class DefaultAddressHandler implements OrderSubmitHandler {

    @Autowired
    private MemberAddressServiceFacade addressServiceFacade;

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        BuyerBO buyerBO = BuyerContext.get();
        MemberAddressDto addressDto = addressServiceFacade.queryDefaultAddress(buyerBO.getMemberId());
        AddressContext.set(addressDto);
    }
}