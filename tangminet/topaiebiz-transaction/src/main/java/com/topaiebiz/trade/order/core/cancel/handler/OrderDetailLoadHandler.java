package com.topaiebiz.trade.order.core.cancel.handler;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.topaiebiz.trade.order.core.cancel.CancelParamContext;
import com.topaiebiz.trade.order.dao.OrderDetailDao;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/***
 * @author yfeng
 * @date 2018-01-21 19:05
 */
@Order(3)
@Component("detailCancelLoadHandler")
public class OrderDetailLoadHandler extends AbstractHandler {

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Override
    public void handle(BuyerBO buyerBO, Long payId, CancelParamContext context) {
        List<Long> orderIds = context.getOrderIds();

        //step 1 : 查询订单详情
        EntityWrapper<OrderDetailEntity> detailCond = new EntityWrapper<>();
        detailCond.in("orderId", orderIds);
        detailCond.eq("memberId", buyerBO.getMemberId());
        List<OrderDetailEntity> details = orderDetailDao.selectList(detailCond);

        //step 2 : 订单按照店铺订单ID分组
        Map<Long, List<OrderDetailEntity>> detailGroupMap = new HashMap();
        for (OrderDetailEntity detail : details) {
            List<OrderDetailEntity> storeDetails = detailGroupMap.get(detail.getOrderId());
            if (storeDetails == null) {
                storeDetails = new ArrayList<>();
                detailGroupMap.put(detail.getOrderId(), storeDetails);
            }
            storeDetails.add(detail);
        }

        List<Long> detailIds = details.stream().map(item -> item.getId()).collect(Collectors.toList());
        context.setDetailIds(detailIds);
        context.setDetaiMaps(detailGroupMap);
    }
}