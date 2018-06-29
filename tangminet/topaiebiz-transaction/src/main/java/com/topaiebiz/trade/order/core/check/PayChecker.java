package com.topaiebiz.trade.order.core.check;

import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.trade.order.core.cancel.OrderCancelChain;
import com.topaiebiz.trade.order.facade.MemberServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description 支付超时自动取消
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/1 11:34
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class PayChecker extends AbstractOrderChecker {

    @Autowired
    private OrderCancelChain orderCancelChain;

    @Autowired
    private MemberServiceFacade memberServiceFacade;

    @Override
    public boolean check(OrderEntity orderEntity){
        boolean timeCheck = super.checkTimeOut(orderEntity.getOrderTime(), Constants.Order.UNPAY_AUDIT_CANCEL_SECONDS);
        if (timeCheck){
            Long memberId = orderEntity.getMemberId();
            MemberDto memberDto = memberServiceFacade.getMember(memberId);

            BuyerBO buyerBO = new BuyerBO();
            buyerBO.setMemberId(memberId);
            buyerBO.setMobile(memberDto.getTelephone());
            buyerBO.setMemberName(orderEntity.getMemberName());
            boolean result = orderCancelChain.cancel(buyerBO, orderEntity.getPayId());
            if (result) {
                log.info("----------The order:{} was not paid overtime and has been canceled automatically", orderEntity.getId());
            }
        }
        return timeCheck;
    }

}
