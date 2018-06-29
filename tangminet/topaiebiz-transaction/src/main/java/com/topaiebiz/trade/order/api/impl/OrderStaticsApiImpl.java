package com.topaiebiz.trade.order.api.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.trade.api.OrderStaticsApi;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.dto.statics.OrderStatusCountDTO;
import com.topaiebiz.trade.dto.statics.OrderVolumeDTO;
import com.topaiebiz.trade.dto.statics.PromotionStaticsDTO;
import com.topaiebiz.trade.order.dao.OrderDao;
import com.topaiebiz.trade.order.dao.OrderDetailDao;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * @author yfeng
 * @date 2018-01-06 15:16
 */
@Slf4j
@Service
public class OrderStaticsApiImpl implements OrderStaticsApi {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Override
    public Map<Long, PromotionStaticsDTO> promotionStatics(List<Long> promotionIds) {
        if (CollectionUtils.isEmpty(promotionIds)) {
            log.error("----------OrderStaticsApi promotionStatics: param promotionId is null!");
            return Collections.emptyMap();
        }

        List<PromotionStaticsDTO> promotionStaticsDTOS = orderDetailDao.orderDetailStaticsByPromotionId(promotionIds);
        if (CollectionUtils.isEmpty(promotionStaticsDTOS)) {
            return Collections.emptyMap();
        }
        Map<Long, PromotionStaticsDTO> map = new HashMap<>(promotionStaticsDTOS.size());
        for (PromotionStaticsDTO promotionStaticsDTO : promotionStaticsDTOS) {
            promotionStaticsDTO.setOrderTotalPrice(promotionStaticsDTO.getOrderTotalPrice().subtract(promotionStaticsDTO.getOrderTotalDiscount()));
            map.put(promotionStaticsDTO.getPromotionId(), promotionStaticsDTO);
        }
        return map;
    }

    @Override
    public OrderVolumeDTO queryOrderStatics(Long memberId) {
        return new OrderVolumeDTO();
    }

    @Override
    public OrderVolumeDTO queryStoreOrderStatics(Long memberId, Long storeId) {
        return new OrderVolumeDTO();
    }

    @Override
    public OrderStatusCountDTO queryOrderStatusCount(Long memberId) {
        EntityWrapper<OrderEntity> unPay = this.buildCountWrapper(memberId);
        unPay.eq("orderState", OrderStatusEnum.UNPAY.getCode());
        Long unpayRows = Long.valueOf(orderDao.selectCount(unPay));


        EntityWrapper<OrderEntity> pendingDelivery = this.buildCountWrapper(memberId);
        pendingDelivery.eq("orderState", OrderStatusEnum.PENDING_DELIVERY.getCode());
        Long pendingDeliveryRows = Long.valueOf(orderDao.selectCount(pendingDelivery));


        EntityWrapper<OrderEntity> pendingReceived = this.buildCountWrapper(memberId);
        pendingReceived.eq("orderState", OrderStatusEnum.PENDING_RECEIVED.getCode());
        Long pendingReceivedRows = Long.valueOf(orderDao.selectCount(pendingReceived));

        EntityWrapper<OrderEntity> waitEvaluation = this.buildCountWrapper(memberId);
        waitEvaluation.gt("orderState", OrderStatusEnum.PENDING_RECEIVED.getCode());
        waitEvaluation.lt("orderState", OrderStatusEnum.ORDER_CLOSE.getCode());
        waitEvaluation.eq("commentFlag", Constants.Order.COMMENT_NO);
        waitEvaluation.eq("refundState", OrderConstants.OrderRefundStatus.NO_REFUND);
        Long waitEvaluationRow = Long.valueOf(orderDao.selectCount(waitEvaluation));

        return new OrderStatusCountDTO(unpayRows, pendingDeliveryRows, pendingReceivedRows, waitEvaluationRow);
    }

    private EntityWrapper<OrderEntity> buildCountWrapper(Long memberId) {
        EntityWrapper<OrderEntity> orderWrapper = new EntityWrapper<>();
        orderWrapper.setSqlSelect("count(id)");
        orderWrapper.eq("memberId", memberId);
        orderWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        return orderWrapper;
    }

}