package com.topaiebiz.trade.order.core.cancel.handler;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.order.core.cancel.CancelParamContext;
import com.topaiebiz.trade.order.dao.OrderPayDao;
import com.topaiebiz.trade.order.exception.PaymentExceptionEnum;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.topaiebiz.trade.constants.OrderConstants.PayStatus.CANCEL;
import static com.topaiebiz.trade.constants.OrderConstants.PayStatus.SUCCESS;

/***
 * @author yfeng
 * @date 2018-01-21 22:06
 */
@Order(1)
@Component("payCancelLoadHandler")
public class OrderPayLoadHandler extends AbstractHandler {

    @Autowired
    private OrderPayDao orderPayDao;

    @Override
   public void handle(BuyerBO buyerBO, Long payId, CancelParamContext context) {
        //step 1 : 查询支付订单
        OrderPayEntity cond = new OrderPayEntity();
        cond.cleanInit();
        cond.setId(payId);
        cond.setMemberId(buyerBO.getMemberId());
        OrderPayEntity payEntity = orderPayDao.selectOne(cond);

        //step 2 : 校验订单状态
        if (payEntity == null) {
            throw new GlobalException(PaymentExceptionEnum.ORDER_NOT_FOUND);
        }
        Integer payStatus = payEntity.getPayState();
        if (SUCCESS.equals(payStatus) || CANCEL.equals(payStatus)) {
            throw new GlobalException(PaymentExceptionEnum.ORDER_CAN_NOT_CANCEL);
        }

        //step 3 : 保存到上下文变量
        context.setPayEntity(payEntity);
    }
}