package com.topaiebiz.trade.order.core.cancel.action;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.order.core.cancel.CancelParamContext;
import com.topaiebiz.trade.order.dao.OrderDao;
import com.topaiebiz.trade.order.dao.OrderDetailDao;
import com.topaiebiz.trade.order.dao.OrderPayDao;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/***
 * @author yfeng
 * @date 2018-01-21 19:03
 */
@Component("orderCancelAction")
public class OrderCancelAction implements CancelAction {

    @Autowired
    private OrderPayDao orderPayDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderDetailDao orderDetailDao;

    @Override
    public boolean action(BuyerBO buyerBO, CancelParamContext context) {
        OrderPayEntity payEntity = context.getPayEntity();

        Date now = new Date();
        //step 1 : 支付订单
        OrderPayEntity update = new OrderPayEntity();
        update.cleanInit();
        update.setId(payEntity.getId());
        update.setLastModifiedTime(new Date());
        update.setPayState(OrderConstants.PayStatus.CANCEL);
        orderPayDao.updateById(update);

        //step 2 : 店铺订单
        //step 2.1 : 加载店铺订单
        //step 2.2 执行修改
        EntityWrapper<OrderEntity> updateCond = new EntityWrapper<>();
        updateCond.in("id", context.getOrderIds());

        OrderEntity orderUpdate = new OrderEntity();
        orderUpdate.cleanInit();
        orderUpdate.setOrderState(OrderStatusEnum.ORDER_CANCELLATION.getCode());
        orderUpdate.setLastModifiedTime(now);
        orderDao.update(orderUpdate, updateCond);

        //step 3 : 商品快照
        EntityWrapper<OrderDetailEntity> detailCond = new EntityWrapper<>();
        detailCond.eq("memberId", buyerBO.getMemberId());
        detailCond.in("orderId", context.getOrderIds());

        OrderDetailEntity detailUpdate = new OrderDetailEntity();
        detailUpdate.cleanInit();
        detailUpdate.setOrderState(OrderStatusEnum.ORDER_CANCELLATION.getCode());
        detailUpdate.setLastModifiedTime(now);
        orderDetailDao.update(detailUpdate, detailCond);

        return true;
    }
}
