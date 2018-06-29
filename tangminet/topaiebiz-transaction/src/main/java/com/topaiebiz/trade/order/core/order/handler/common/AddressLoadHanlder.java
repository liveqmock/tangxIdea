package com.topaiebiz.trade.order.core.order.handler.common;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.dto.address.MemberAddressDto;
import com.topaiebiz.trade.order.core.order.context.AddressContext;
import com.topaiebiz.trade.order.core.order.context.BuyerContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.facade.MemberAddressServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.ADDRESS_IS_EMPTY;
import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.ADDRESS_NOT_EXIST;

/***
 * @author yfeng
 * @date 2018-01-10 16:27
 */
@Component("addressLoadHandler")
public class AddressLoadHanlder implements OrderSubmitHandler {

    @Autowired
    private MemberAddressServiceFacade addressServiceFacade;

    /**
     * 子类通过开关控制是否需要做地址校验，默认关闭
     *
     * @return
     */
    protected boolean needValidate() {
        return false;
    }

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        BuyerBO buyerBO = BuyerContext.get();
        if (!MathUtil.validEntityId(orderRequest.getAddressId())) {
            if (needValidate()) {
                throw new GlobalException(ADDRESS_IS_EMPTY);
            } else {
                return;
            }
        }

        //step 1 : 加载地址
        MemberAddressDto addressDto = addressServiceFacade.queryMemberAddress(buyerBO.getMemberId(), orderRequest.getAddressId());

        //step 2 : 地址校验
        if ( addressDto == null) {
            throw new GlobalException(ADDRESS_NOT_EXIST);
        }

        //step 3 : 将地址存入线程上下文
        AddressContext.set(addressDto);
    }
}
