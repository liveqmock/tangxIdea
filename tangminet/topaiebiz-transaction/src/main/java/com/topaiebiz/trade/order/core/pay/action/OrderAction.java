package com.topaiebiz.trade.order.core.pay.action;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.base.enumdata.PayMethodEnum;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.dto.pay.GoodPayDTO;
import com.topaiebiz.trade.order.core.pay.bo.GoodsPayBO;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.bo.StorePayBO;
import com.topaiebiz.trade.order.dao.OrderDao;
import com.topaiebiz.trade.order.dao.OrderDetailDao;
import com.topaiebiz.trade.order.dao.OrderPayDao;
import com.topaiebiz.trade.order.exception.PaymentExceptionEnum;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


/***
 * @author yfeng
 * @date 2018-01-20 16:55
 */
@Slf4j
@Component
public class OrderAction implements PayAction {

    @Autowired
    private OrderPayDao orderPayDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Override
    public boolean action(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        try {
            //支付订单更新
            updatePayEntity(buyer, paramContext, payRequest);

            //更新订单和商品快照
            for (StorePayBO storePayBO : paramContext.getStorePayDetails()) {
                updateOrderEntity(buyer, paramContext, storePayBO);
                for (GoodsPayBO goodsPayBO : storePayBO.getGoodsPayDetails()) {
                    updateOrderDetailEntity(buyer, paramContext, goodsPayBO);
                }
            }
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(),ex);
            throw new GlobalException(PaymentExceptionEnum.PAY_FAIL_ERROR);
        }
    }

    private void updateOrderDetailEntity(BuyerBO buyer, PayParamContext paramContext, GoodsPayBO goodsPayBO) {
        OrderDetailEntity update = new OrderDetailEntity();
        update.cleanInit();
        update.setId(goodsPayBO.getDetailId());
        update.setLastModifiedTime(new Date());
        update.setLastModifierId(buyer.getMemberId());
        //金额记录
        update.setPayDetail(orderDetailPaySummary(goodsPayBO));

        if (paramContext.isPkgFull()) {
            update.setOrderState(OrderStatusEnum.PENDING_DELIVERY.getCode());
        }
        orderDetailDao.updateById(update);
    }

    private String orderDetailPaySummary(GoodsPayBO goodsPayBO) {
        GoodPayDTO detail = new GoodPayDTO();
        detail.setBalance(goodsPayBO.getBalance());
        detail.setCardDetail(goodsPayBO.getCardsDetail());
        detail.setCardPrice(goodsPayBO.getCardAmount());
        detail.setScorePrice(goodsPayBO.getScore());
        return JSON.toJSONString(detail);
    }

    private void updatePayEntity(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        OrderPayEntity payEntity = paramContext.getOrderPayEntity();

        OrderPayEntity update = new OrderPayEntity();
        update.cleanInit();
        update.setId(payEntity.getId());
        update.setVersion(payEntity.getVersion());
        update.setLastModifiedTime(new Date());
        update.setLastModifierId(buyer.getMemberId());

        //金额更新
        update.setBalance(payRequest.getBalance());
        update.setScoreNum(MathUtil.getFenVal(payRequest.getScore()));
        update.setScorePrice(payRequest.getScore());
        update.setCardPrice(payRequest.getCardAmount());
        if (paramContext.isPkgFull()) {
            update.setPayState(OrderConstants.PayStatus.SUCCESS);
            update.setPayTime(new Date());
            update.setPayType(PayMethodEnum.PREDEPOSIT.getName());
        }
        orderPayDao.updateById(update);
    }

    private void updateOrderEntity(BuyerBO buyer, PayParamContext paramContext, StorePayBO storePayBO) {
        OrderEntity update = new OrderEntity();
        update.cleanInit();
        update.setId(storePayBO.getOrderId());
        update.setLastModifiedTime(new Date());
        update.setLastModifierId(buyer.getMemberId());

        //金额记录
        update.setBalance(storePayBO.getBalance());
        update.setScoreNum(MathUtil.getFenVal(storePayBO.getScore()));
        update.setScore(storePayBO.getScore());
        update.setCardPrice(storePayBO.getCardAmount());
        update.setCardDetail(JSON.toJSONString(storePayBO.getCardsDetail()));
        update.setCardFreightDetail(JSON.toJSONString(storePayBO.getCardsFreightDetail()));

        if (paramContext.isPkgFull()) {
            update.setOrderState(OrderStatusEnum.PENDING_DELIVERY.getCode());
            update.setPayType(PayMethodEnum.PREDEPOSIT.getName());
            update.setPayTime(new Date());
        }
        orderDao.updateById(update);
    }

    @Override
    public void rollback(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        throw new GlobalException(PaymentExceptionEnum.PAY_FAIL_ERROR);
    }
}