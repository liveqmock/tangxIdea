package com.topaiebiz.trade.order.util;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.order.dao.OrderDao;
import com.topaiebiz.trade.order.dao.OrderDetailDao;
import com.topaiebiz.trade.order.dao.OrderPayDao;
import com.topaiebiz.trade.order.exception.PaymentOrderExceptionEnum;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/***
 * @author yfeng
 * @date 2018-01-21 17:21
 */
@Component
public class PayOrderHelper {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private OrderPayDao orderPayDao;

    @Autowired
    private MessageSender messageSender;

    public BigDecimal needPay(OrderPayEntity payEntity) {
        BigDecimal thirdAmount = payEntity.getPayPrice();
        if (MathUtil.greaterThanZero(payEntity.getBalance())) {
            thirdAmount = thirdAmount.subtract(payEntity.getBalance());
        }
        if (MathUtil.greaterThanZero(payEntity.getScorePrice())) {
            thirdAmount = thirdAmount.subtract(payEntity.getScorePrice());
        }
        if (MathUtil.greaterThanZero(payEntity.getCardPrice())) {
            thirdAmount = thirdAmount.subtract(payEntity.getCardPrice());
        }
        return thirdAmount;
    }

    public void updatePayOrderAndNotify(OrderPayEntity payEntity, String payMethod, String outTradeNo) {
        Date now = new Date();
        OrderEntity cond = new OrderEntity();
        cond.cleanInit();
        cond.setPayId(payEntity.getId());
        List<OrderEntity> orders = orderDao.selectList(new EntityWrapper<>(cond));
        List<Long> orderIds = orders.stream().map(item -> item.getId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(orderIds)) {
            throw new GlobalException(PaymentOrderExceptionEnum.ORDER_EMPTY_ERROR);
        }

        //step 1 : 支付订单
        OrderPayEntity update = new OrderPayEntity();
        update.cleanInit();
        update.setId(payEntity.getId());
        update.setLastModifiedTime(new Date());
        update.setPayState(OrderConstants.PayStatus.SUCCESS);
        update.setPayType(payMethod);
        update.setPayTime(now);
        update.setOuterPaySn(outTradeNo);
        orderPayDao.updateById(update);

        //step 2 : 店铺订单
        //step 2.1 : 加载店铺订单

        //step 2.2 执行修改
        EntityWrapper<OrderEntity> updateCond = new EntityWrapper<>();
        updateCond.in("id", orderIds);

        OrderEntity orderUpdate = new OrderEntity();
        orderUpdate.cleanInit();
        orderUpdate.setOrderState(OrderStatusEnum.PENDING_DELIVERY.getCode());
        orderUpdate.setPayTime(now);
        orderUpdate.setPayType(payMethod);
        orderUpdate.setLastModifiedTime(now);
        orderDao.update(orderUpdate, updateCond);

        //step 3 : 商品快照
        EntityWrapper<OrderDetailEntity> detailCond = new EntityWrapper<>();
        detailCond.in("orderId", orderIds);

        OrderDetailEntity detailUpdate = new OrderDetailEntity();
        detailUpdate.cleanInit();
        detailUpdate.setOrderState(OrderStatusEnum.PENDING_DELIVERY.getCode());
        detailUpdate.setLastModifiedTime(now);
        orderDetailDao.update(detailUpdate, detailCond);

        //step 4 : 整单完成支付通知
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMemberId(payEntity.getMemberId());
        messageDTO.setType(MessageTypeEnum.ORDER_PAY);
        messageDTO.getParams().put("payId", payEntity.getId());
        messageDTO.getParams().put("orderIds", orderIds);
        messageSender.publicMessage(messageDTO);
    }
}
