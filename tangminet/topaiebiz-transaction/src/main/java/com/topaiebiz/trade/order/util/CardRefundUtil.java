package com.topaiebiz.trade.order.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.topaiebiz.card.dto.PayCard;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-03-16 12:14
 */
public class CardRefundUtil {

    public static List<PayCard> buildFreightCards(OrderEntity orderEntity) {
        return buildFreightCards(orderEntity.getCardFreightDetail(), orderEntity.getStoreId(), orderEntity.getStoreName());
    }

    public static List<PayCard> buildFreightCards(String freihtCardDetail, Long storeId, String storeName) {
        List<PayCard> cardList = new ArrayList<>();
        Map<String, BigDecimal> cardDetail = Collections.emptyMap();
        if (StringUtils.isNotBlank(freihtCardDetail)) {
            cardDetail = JSON.parseObject(freihtCardDetail, new TypeReference<Map<String, BigDecimal>>() {
            });
        }
        if (MapUtils.isNotEmpty(cardDetail)) {
            cardDetail.entrySet().forEach(amountItem -> {
                PayCard payCard = new PayCard();
                payCard.setAmount(amountItem.getValue());
                payCard.setCardNo(amountItem.getKey());
                payCard.setGoodsId(0L);
                payCard.setGoodsName("运费");
                payCard.setStoreId(storeId);
                payCard.setStoreName(storeName);
                cardList.add(payCard);
            });
        }
        return cardList;
    }
}
