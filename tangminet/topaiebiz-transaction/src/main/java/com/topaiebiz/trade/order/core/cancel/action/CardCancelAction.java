package com.topaiebiz.trade.order.core.cancel.action;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.card.dto.PayCard;
import com.topaiebiz.card.dto.RefundOrderDTO;
import com.topaiebiz.trade.dto.pay.GoodPayDTO;
import com.topaiebiz.trade.order.core.cancel.CancelParamContext;
import com.topaiebiz.trade.order.facade.GiftCardServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.util.CardRefundUtil;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-21 19:03
 */
@Slf4j
@Component("cardCancelAction")
public class CardCancelAction implements CancelAction {
    @Autowired
    private GiftCardServiceFacade cardServiceFacade;

    @Override
    public boolean action(BuyerBO buyerBO, CancelParamContext context) {
        OrderPayEntity payEntity = context.getPayEntity();
        if (!MathUtil.greaterThanZero(payEntity.getCardPrice())) {
            log.warn("payEntity {} did not use card to pay", payEntity.getId());
            return true;
        }
        RefundOrderDTO refundParam = buildRefundParam(buyerBO, payEntity, context);
        return cardServiceFacade.refundCards(refundParam);
    }

    private RefundOrderDTO buildRefundParam(BuyerBO buyerBO, OrderPayEntity payEntity, CancelParamContext context) {
        RefundOrderDTO refundOrderDTO = new RefundOrderDTO();
        refundOrderDTO.setMemberId(buyerBO.getMemberId());
        refundOrderDTO.setMemberName(buyerBO.getMemberName());
        refundOrderDTO.setMemberPhone(buyerBO.getMobile());
        refundOrderDTO.setPaySn(payEntity.getId().toString());
        refundOrderDTO.setTotalAmount(payEntity.getCardPrice());

        List<PayCard> allCardList = new ArrayList<>();
        refundOrderDTO.setPayCardList(allCardList);

        context.getDetaiMaps().entrySet().forEach(entry -> {
            OrderEntity orderEntity = findOrderEntity(entry.getKey(), context);
            List<OrderDetailEntity> orderDetails = entry.getValue();
            for (OrderDetailEntity detail : orderDetails) {
                List<PayCard> payCards = buildPayCards(orderEntity, detail);
                if (CollectionUtils.isNotEmpty(payCards)) {
                    allCardList.addAll(payCards);
                }
            }
        });

        List<PayCard> freightCardList = getFreightCards(context);
        if (CollectionUtils.isNotEmpty(freightCardList)) {
            allCardList.addAll(freightCardList);
        }

        return refundOrderDTO;
    }

    private List<PayCard> getFreightCards(CancelParamContext context) {
        List<PayCard> freightCardList = new ArrayList<>();
        context.getOrders().forEach(orderEntity -> {
            List<PayCard> orderFreightCards = CardRefundUtil.buildFreightCards(orderEntity);
            if (CollectionUtils.isNotEmpty(orderFreightCards)) {
                freightCardList.addAll(orderFreightCards);
            }
        });
        return freightCardList;
    }

    private OrderEntity findOrderEntity(Long orderId, CancelParamContext context) {
        for (OrderEntity orderEntity : context.getOrders()) {
            if (orderEntity.getId().equals(orderId)) {
                return orderEntity;
            }
        }
        return null;
    }

    private List<PayCard> buildPayCards(OrderEntity orderEntity, OrderDetailEntity detail) {
        List<PayCard> cards = new ArrayList<>();
        GoodPayDTO goodsPay = JSON.parseObject(detail.getPayDetail(), GoodPayDTO.class);
        if (goodsPay == null) {
            return Collections.emptyList();
        }
        Map<String, BigDecimal> cardDetail = goodsPay.getCardDetail();
        if (MapUtils.isEmpty(cardDetail)) {
            return Collections.emptyList();
        }
        cardDetail.entrySet().forEach(entry -> {
            PayCard payCard = new PayCard();
            payCard.setAmount(entry.getValue());
            payCard.setCardNo(entry.getKey());
            payCard.setGoodsId(detail.getSkuId());
            payCard.setGoodsName(detail.getName());
            payCard.setStoreId(orderEntity.getStoreId());
            payCard.setStoreName(orderEntity.getStoreName());
            cards.add(payCard);
        });
        return cards;
    }
}