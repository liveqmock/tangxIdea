package com.topaiebiz.trade.refund.service.impl;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.trade.order.util.OrdersQueryUtil;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.trade.refund.helper.RefundOrderHelper;
import com.topaiebiz.trade.refund.service.RefundOrderService;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/3 17:14
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Service
public class RefundOrderServiceImpl implements RefundOrderService {

    @Autowired
    private OrdersQueryUtil ordersQueryUtil;

    @Autowired
    private RefundOrderHelper refundOrderHelper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoRefund(List<RefundOrderEntity> refundOrderEntities) {
        // 自动通过仅退款的售后
        Date currentDate = new Date();
        int size = refundOrderEntities.size();
        List<Long> orderIds = refundOrderEntities.stream().map(RefundOrderEntity::getOrderId).collect(Collectors.toList());
        log.info(">>>>>>>>>>Timing tasks: do autoRefund, refundOrderEntities size:{}, orderIds size:{}", refundOrderEntities.size(), orderIds.size());
        Map<Long, OrderEntity> orderMap = ordersQueryUtil.queryOrderMap(orderIds);

        int updateRows = 0;
        // 退款成功的售后订单ID集合
        ArrayList<Long> successOrderIds = new ArrayList<>(size);
        for (RefundOrderEntity refundOrderEntity : refundOrderEntities) {
            try {
                updateRows += refundOrderHelper.refundOrder(refundOrderEntity, orderMap.get(refundOrderEntity.getOrderId()), currentDate);
            } catch (Exception e) {
                log.error(">>>>>>>>>>refund fail, refund refundEntity:{}, error:", JSON.toJSONString(refundOrderEntity), e);
            }
        }
        log.info("----------Timing tasks: auto pass refund, query data rows:{}, success rows:{}, fail rows:{} ！", size, updateRows, size - updateRows);
    }
}
