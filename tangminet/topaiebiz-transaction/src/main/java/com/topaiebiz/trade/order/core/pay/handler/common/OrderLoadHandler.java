package com.topaiebiz.trade.order.core.pay.handler.common;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.bo.StorePayBO;
import com.topaiebiz.trade.order.core.pay.context.PaySummaryContext;
import com.topaiebiz.trade.order.core.pay.context.PkgPayedContext;
import com.topaiebiz.trade.order.core.pay.handler.PayContextHandler;
import com.topaiebiz.trade.order.core.pay.util.StorePayUtil;
import com.topaiebiz.trade.order.dao.OrderDao;
import com.topaiebiz.trade.order.dao.OrderDetailDao;
import com.topaiebiz.trade.order.dao.OrderPayDao;
import com.topaiebiz.trade.order.dto.pay.PaySummaryDTO;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.transaction.order.merchant.entity.OrderDetailEntity;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import com.topaiebiz.transaction.order.total.entity.OrderPayEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.topaiebiz.trade.order.exception.PaymentExceptionEnum.ORDER_ALREADY_PAYED;
import static com.topaiebiz.transaction.order.merchant.exception.StoreOrderExceptionEnum.ORDER_NOT_FOUND;

/***
 * @author yfeng
 * @date 2018-01-18 15:53
 */
@Component("orderLoadHandler")
public class OrderLoadHandler implements PayContextHandler {

    @Autowired
    private OrderDetailDao orderDetailDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderPayDao orderPayDao;

    @Override
    public void prepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        //step 1 : 加载支付单
        OrderPayEntity payEntity = getOrderPayEntity(buyer.getMemberId(), payRequest.getPayId());
        if (payEntity == null) {
            throw new GlobalException(ORDER_NOT_FOUND);
        }
        if (OrderConstants.PayStatus.SUCCESS.equals(payEntity.getPayState())) {
            //若已经完成支付 , 则不需要做详情加载
            throw new GlobalException(ORDER_ALREADY_PAYED);
        }
        Boolean pkgPayed = pkgPayed(payEntity);
        PkgPayedContext.set(pkgPayed);
        paramContext.setOrderPayEntity(payEntity);

        //step 2 : 加载店铺订单
        List<OrderEntity> orderList = getStoreOrders(buyer, payEntity.getId());

        //step 3 : 保存支付摘要到上下文
        savePaySummary(payEntity, orderList);

        //step 4 : 加载订单详情
        Map<Long, List<OrderDetailEntity>> orderDetails = getOrderDetails(buyer, orderList);
        List<StorePayBO> storePays = StorePayUtil.buildStorePayDetails(orderList, orderDetails);
        paramContext.setStorePayDetails(storePays);
    }

    private void savePaySummary(OrderPayEntity payEntity, List<OrderEntity> orderList) {
        PaySummaryDTO paySummaryDTO = new PaySummaryDTO();
        paySummaryDTO.setPayId(payEntity.getId());

        //站内支付不允许支付运费部分
        BigDecimal pkgLimit = payEntity.getPayPrice();

        //0运费时检查是否为海涛订单
        boolean haitao = false;
        for (OrderEntity orderEntity : orderList) {
            if (OrderConstants.HaitaoFlag.YES.equals(orderEntity.getHaitao())) {
                haitao = true;
                paySummaryDTO.setHaitao(true);
            }
        }
        if (haitao && MathUtil.sameValue(pkgLimit, payEntity.getPayPrice())) {
            //海淘店铺订单至少要1分钱现金支付
            pkgLimit = pkgLimit.subtract(MathUtil.oneFen);
        }

        paySummaryDTO.setMaxPkgPay(pkgLimit);
        paySummaryDTO.setOrderAmount(payEntity.getPayPrice());
        paySummaryDTO.updateNeedPay();

        PaySummaryContext.set(paySummaryDTO);
    }

    private Map<Long, List<OrderDetailEntity>> getOrderDetails(BuyerBO buyer, List<OrderEntity> orderList) {
        List<Long> orderIds = orderList.stream().map(item -> item.getId()).collect(Collectors.toList());
        EntityWrapper<OrderDetailEntity> cond = new EntityWrapper<>();
        cond.eq("memberId", buyer.getMemberId());
        cond.in("orderId", orderIds);
        List<OrderDetailEntity> orderDetails = orderDetailDao.selectList(cond);

        Map<Long, List<OrderDetailEntity>> orderDetailsMap = new HashMap<>();
        for (OrderDetailEntity item : orderDetails) {
            List<OrderDetailEntity> detailListInOrder = orderDetailsMap.get(item.getOrderId());
            if (detailListInOrder == null) {
                detailListInOrder = new ArrayList<>();
                orderDetailsMap.put(item.getOrderId(), detailListInOrder);
            }
            detailListInOrder.add(item);
        }
        return orderDetailsMap;
    }

    /**
     * 判断是否有站内支付
     *
     * @param payEntity
     * @return
     */
    private boolean pkgPayed(OrderPayEntity payEntity) {
        BigDecimal balance = payEntity.getBalance();
        BigDecimal card = payEntity.getCardPrice();
        BigDecimal score = payEntity.getScorePrice();

        if (balance != null && MathUtil.greaterThanZero(balance)) {
            return true;
        }
        if (card != null && MathUtil.greaterThanZero(card)) {
            return true;
        }
        if (score != null && MathUtil.greaterThanZero(score)) {
            return true;
        }
        return false;
    }

    private List<OrderEntity> getStoreOrders(BuyerBO buyer, Long payId) {
        OrderEntity cond = new OrderEntity();
        cond.cleanInit();
        cond.setMemberId(buyer.getMemberId());
        cond.setPayId(payId);
        return orderDao.selectList(new EntityWrapper<>(cond));
    }

    private OrderPayEntity getOrderPayEntity(Long memberId, Long payId) {
        OrderPayEntity cond = new OrderPayEntity();
        cond.cleanInit();
        cond.setId(payId);
        cond.setMemberId(memberId);
        cond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        return orderPayDao.selectOne(cond);
    }

}