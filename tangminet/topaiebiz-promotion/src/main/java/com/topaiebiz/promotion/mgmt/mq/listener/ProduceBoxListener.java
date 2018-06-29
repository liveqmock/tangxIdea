package com.topaiebiz.promotion.mgmt.mq.listener;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.common.msg.core.MessageListener;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.topaiebiz.promotion.mgmt.service.BoxActivityService;
import com.topaiebiz.trade.api.order.OrderPayServiceApi;
import com.topaiebiz.trade.dto.order.OrderPayDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static com.topaiebiz.promotion.constants.PromotionConstants.NodeType.PAY_NODE;

@Component
@Slf4j
public class ProduceBoxListener implements MessageListener {
    //活动最小支付金额
    private BigDecimal MinPayPrice = new BigDecimal("61.8");

    @Autowired
    private BoxActivityService boxActivityService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private OrderPayServiceApi orderPayServiceApi;

    @Override
    public Set<MessageTypeEnum> getTargetMessageTypes() {
        Set<MessageTypeEnum> msgTypes = new HashSet<>();
        msgTypes.add(MessageTypeEnum.CARD_BUY);
        msgTypes.add(MessageTypeEnum.ORDER_PAY);
        return msgTypes;
    }

    @Override
    public void onMessage(MessageDTO msg) {
        log.info(" receive message : {}", JSON.toJSONString(msg));
        //商品订单支付
        if (MessageTypeEnum.ORDER_PAY.equals(msg.getType())) {
            Long payId = (Long) msg.getParams().get("payId");
            if (payId == null) {
                log.warn("数据不正确，payId为空, msg: {}", JSON.toJSONString(msg));
                return;
            }
            OrderPayDTO orderPay = orderPayServiceApi.getPayInfo(payId);
            if (orderPay == null) {
                log.warn("查询不到订单支付记录，payId: {} , msg: {}", payId, JSON.toJSONString(msg));
                return;
            }
            //支付金额未超过61.8的订单
            if (orderPay.getPayPrice().compareTo(MinPayPrice) < 0) {
                log.warn("支付金额未超过61.8订单！orderPay: {}", JSON.toJSONString(orderPay));
                return;
            }
        } else if (MessageTypeEnum.CARD_BUY.equals(msg.getType())) {
            BigDecimal payAmount = (BigDecimal) msg.getParams().get("payAmount");
            if (payAmount == null) {
                log.warn("数据不正确，payAmount为空, msg: {}", JSON.toJSONString(msg));
                return;
            }
            //支付金额未超过61.8的订单
            if (payAmount.compareTo(MinPayPrice) < 0) {
                log.warn("支付金额未超过61.8的订单！payAmount: {}", payAmount);
                return;
            }
        }
        //产生宝箱
        Boolean res = boxActivityService.produceBox(msg.getMemberId(), PAY_NODE);
        if (!res) {
            log.error("支付成功后，产生宝箱失败！msg:" + JSON.toJSONString(msg));
        }
    }
}
