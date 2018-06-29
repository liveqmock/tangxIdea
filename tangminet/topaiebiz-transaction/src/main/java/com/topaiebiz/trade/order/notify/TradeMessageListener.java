package com.topaiebiz.trade.order.notify;

import com.nebulapaas.common.msg.core.MessageListener;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.topaiebiz.merchant.dto.store.MerchantMemberDTO;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.facade.StoreServiceFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/***
 * @author yfeng
 * @date 2018-03-03 15:21
 */
@Slf4j
@Component
public class TradeMessageListener implements MessageListener {

    @Autowired
    private StoreServiceFacade storeServiceFacade;

    @Override
    public Set<MessageTypeEnum> getTargetMessageTypes() {
        Set<MessageTypeEnum> types = new HashSet<>();
        types.add(MessageTypeEnum.ORDER_SUBMIT);
        return types;
    }

    @Override
    public void onMessage(MessageDTO msg) {
        Long memberId = msg.getMemberId();
        OrderSubmitContext orderContext = (OrderSubmitContext) msg.getParams().get("orderContext");
        Set<Long> storeIds = orderContext.getStoreOrderMap().keySet();
        for (Long storeId : storeIds) {
            MerchantMemberDTO merchantMemberDTO = new MerchantMemberDTO();
            merchantMemberDTO.setStoreId(storeId);
            merchantMemberDTO.setMemberId(memberId);
            storeServiceFacade.saveMerchantMemberRelation(merchantMemberDTO);
        }
    }
}