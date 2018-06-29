package com.topaiebiz.trade.order.core.order.action;

import com.google.common.collect.Lists;
import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.context.BuyerContext;
import com.topaiebiz.trade.order.core.order.context.OrderSummaryContext;
import com.topaiebiz.trade.order.core.order.context.PayIdContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.dto.ordersubmit.OrderSummaryDTO;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nebulapaas.common.msg.dto.MessageTypeEnum.ORDER_SUBMIT;
import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.ORDER_FAIL_ERROR;

/***
 * @author yfeng
 * @date 2018-01-16 20:37
 */
@Slf4j
@Component
public class OrderSubmitActionChain implements InitializingBean {

    @Resource(name = "orderSaveAction")
    private OrderSubmitAction orderSaveAction;

    @Resource(name = "goodsStorageAction")
    private OrderSubmitAction goodsStorageAction;

    @Resource(name = "promotionConsumeAction")
    private OrderSubmitAction promotionConsumeAction;

    private List<OrderSubmitAction> actions;

    @Autowired
    private MessageSender messageSender;

    @Override
    public void afterPropertiesSet() throws Exception {
        actions = Lists.newArrayList();
        actions.add(orderSaveAction);
        actions.add(goodsStorageAction);
        actions.add(promotionConsumeAction);
    }

    public Long submitOrder(OrderSubmitContext orderContext, OrderRequest orderRequest) {
        BuyerBO buyerBO = BuyerContext.get();
        boolean hasFailOperation = false;
        List<OrderSubmitAction> actionComponents = new ArrayList<>();

        for (OrderSubmitAction submitComponent : actions) {
            boolean succss = submitComponent.action(buyerBO, orderContext, orderRequest);
            actionComponents.add(submitComponent);
            if (!succss) {
                hasFailOperation = true;
                break;
            }
        }

        if (hasFailOperation) {
            throw new GlobalException(ORDER_FAIL_ERROR);
        }

        MessageDTO orderMessage = new MessageDTO();
        orderMessage.setType(ORDER_SUBMIT);
        orderMessage.setMemberId(buyerBO.getMemberId());
        orderMessage.getParams().put("orderContext", orderContext);
        messageSender.publicMessage(orderMessage);

        OrderSummaryDTO orderSummary = OrderSummaryContext.get();
        if (orderSummary.zeroAmountOrder()) {
            List<Long> orderIds = orderContext.getStoreOrderMap().values().stream().map(StoreOrderBO::getOrderId).collect(Collectors.toList());
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setMemberId(buyerBO.getMemberId());
            messageDTO.setType(MessageTypeEnum.ORDER_PAY);
            messageDTO.getParams().put("payId", PayIdContext.get());
            messageDTO.getParams().put("orderIds", orderIds);
            messageSender.publicMessage(messageDTO);
        }
        return PayIdContext.get();
    }

}