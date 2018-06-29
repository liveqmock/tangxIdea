package com.topaiebiz.trade.order.core.pay.action;

import com.nebulapaas.common.BeanCopyUtil;
import com.topaiebiz.card.dto.PayCard;
import com.topaiebiz.card.dto.PayInfoDTO;
import com.topaiebiz.card.dto.PaySubOrder;
import com.topaiebiz.card.dto.RefundOrderDTO;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.trade.order.core.pay.bo.GoodsPayBO;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.bo.StorePayBO;
import com.topaiebiz.trade.order.core.pay.context.MemberContext;
import com.topaiebiz.trade.order.facade.GiftCardServiceFacade;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/***
 * @author yfeng
 * @date 2018-01-17 21:52
 */
@Slf4j
@Component
public class CardAction implements PayAction {

    @Autowired
    private GiftCardServiceFacade cardServiceFacade;

    @Override
    public boolean action(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        if (MathUtil.sameValue(payRequest.getCardAmount(), BigDecimal.ZERO)) {
            //未使用礼卡支付，直接返回true
            return true;
        }
        PayInfoDTO payInfoDTO = buildPayParam(MemberContext.get(), paramContext.getOrderPayEntity(), paramContext, payRequest);

        return cardServiceFacade.payByCards(payInfoDTO);
    }

    private PayInfoDTO buildPayParam(MemberDto memberDto, OrderPayEntity payEntity, PayParamContext paramContext, PayRequest payRequest) {
        PayInfoDTO payInfo = new PayInfoDTO();

        //用户数据
        payInfo.setMemberId(memberDto.getId());
        payInfo.setMemberName(memberDto.getUserName());
        payInfo.setMemberPhone(memberDto.getTelephone());
        payInfo.setPaySn(payEntity.getId().toString());
        payInfo.setTotalAmount(payRequest.getCardAmount());

        //店铺支付详情
        List<PaySubOrder> subOrders = new ArrayList<>();
        payInfo.setSubOrderList(subOrders);
        for (StorePayBO storePayBO : paramContext.getStorePayDetails()) {
            PaySubOrder subOrder = new PaySubOrder();

            BeanCopyUtil.copy(storePayBO, subOrder);
            subOrder.setOrderSn(storePayBO.getOrderId().toString());
            subOrder.setAmount(storePayBO.getCardAmount());

            //组装商品分摊结果
            List<PayCard> cardList = buildGoodsPay(storePayBO);

            //补充运费的礼卡记录
            List<PayCard> freightCardLists = buildFreightCards(storePayBO);

            cardList.addAll(freightCardLists);
            subOrder.setCardList(cardList);
            subOrders.add(subOrder);
        }
        return payInfo;
    }

    private List<PayCard> buildFreightCards(StorePayBO storePayBO) {
        List<PayCard> cardList = new ArrayList<>();
        storePayBO.getCardsFreightDetail().entrySet().forEach(entry -> {
            PayCard payCard = new PayCard();
            payCard.setAmount(entry.getValue());
            payCard.setCardNo(entry.getKey());
            payCard.setStoreId(storePayBO.getStoreId());
            payCard.setStoreName(storePayBO.getStoreName());
            payCard.setGoodsId(0L);
            payCard.setGoodsName("运费");
            cardList.add(payCard);
        });
        return cardList;
    }

    private List<PayCard> buildGoodsPay(StorePayBO storePayBO) {
        List<PayCard> cardList = new ArrayList<>();
        for (GoodsPayBO goodsPayBO : storePayBO.getGoodsPayDetails()) {
            goodsPayBO.getCardsDetail().entrySet().forEach(entry -> {
                PayCard payCard = new PayCard();
                payCard.setAmount(entry.getValue());
                payCard.setCardNo(entry.getKey());
                payCard.setGoodsId(goodsPayBO.getSkuId());
                payCard.setGoodsName(goodsPayBO.getGoodsName());
                payCard.setStoreId(storePayBO.getStoreId());
                payCard.setStoreName(storePayBO.getStoreName());
                cardList.add(payCard);
            });
        }
        return cardList;
    }

    @Override
    public void rollback(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        if (MathUtil.sameValue(payRequest.getCardAmount(), BigDecimal.ZERO)) {
            //未使用礼卡支付，直接返回true
            return;
        }
        RefundOrderDTO refundParam = buildRefundParam(MemberContext.get(), paramContext.getOrderPayEntity(), paramContext, payRequest);
        cardServiceFacade.refundCards(refundParam);
    }

    private RefundOrderDTO buildRefundParam(MemberDto memberDto, OrderPayEntity payEntity, PayParamContext paramContext, PayRequest payRequest) {
        RefundOrderDTO refundOrderDTO = new RefundOrderDTO();
        refundOrderDTO.setMemberId(memberDto.getId());
        refundOrderDTO.setMemberName(memberDto.getUserName());
        refundOrderDTO.setMemberPhone(memberDto.getTelephone());
        refundOrderDTO.setPaySn(payEntity.getId().toString());
        refundOrderDTO.setTotalAmount(payRequest.getCardAmount());

        List<PayCard> allCardList = new ArrayList<>();
        refundOrderDTO.setPayCardList(allCardList);
        for (StorePayBO storePayBO : paramContext.getStorePayDetails()) {
            //组装商品分摊结果
            List<PayCard> cardList = buildGoodsPay(storePayBO);
            allCardList.addAll(cardList);
        }
        return refundOrderDTO;
    }
}