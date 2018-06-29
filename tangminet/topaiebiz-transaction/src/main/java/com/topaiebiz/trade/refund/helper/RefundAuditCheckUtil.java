package com.topaiebiz.trade.refund.helper;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.order.util.OrdersQueryUtil;
import com.topaiebiz.trade.refund.core.context.RefundParamsContext;
import com.topaiebiz.trade.refund.core.executer.RefundMoneyExecuter;
import com.topaiebiz.trade.refund.dao.RefundOrderDao;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.trade.refund.enumdata.RefundOrderStateEnum;
import com.topaiebiz.trade.refund.enumdata.RefundProcessEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/5 12:02
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class RefundAuditCheckUtil {

    @Autowired
    private RefundMoneyExecuter refundMoneyExecuter;

    @Autowired
    private OrdersQueryUtil ordersQueryUtil;

    @Autowired
    private OrderRefundStateUtil orderRefundStateUtil;

    @Autowired
    private RefundQueryUtil refundQueryUtil;

    @Autowired
    private RefundOrderDao refundOrderDao;

    @Transactional(rollbackFor = Exception.class)
    public void check(RefundOrderEntity refundOrderEntity) {
        log.warn(">>>>>>>>>>checK refund state, params:{}", JSON.toJSONString(refundOrderEntity));
        RefundParamsContext refundParamsContext = new RefundParamsContext();
        refundParamsContext.setRefundOrderEntity(refundOrderEntity);

        Date currentDate = new Date();
        Long refundId = refundOrderEntity.getId();
        Integer refundState = null;

        // 修改的实体容器
        RefundOrderEntity refundUpdate = new RefundOrderEntity();
        refundUpdate.cleanInit();
        refundUpdate.setId(refundId);

        RefundOrderStateEnum refundOrderStateEnum = RefundOrderStateEnum.getByCode(refundOrderEntity.getRefundState());
        switch (refundOrderStateEnum) {
            // 申请退款
            case APPLY_FOR_REFUND:
                // 等待商家签收
            case WAITING_FOR_RECEIVE:
//                boolean refund;
//                if (refundOrderStateEnum.equals(RefundOrderStateEnum.APPLY_FOR_REFUND)){
//                    // 退款审核三天
//                    refund = this.checkTimeOut(refundOrderEntity.getRefundTime(), Constants.Refund.REFUND_AUTO_AUDIT_SECONDS);
//                }else{
//                    // 签收退货七天
//                    refund = this.checkTimeOut(refundOrderEntity.getShipmentsTime(), Constants.Refund.ACCEPT_REFUND_GOODS_MAX_SECONDS);
//                }
//                if (refund){
//                    refundParamsContext.setOrderEntity(ordersQueryUtil.queryOrder(refundOrderEntity.getOrderId()));
//                    refundMoneyExecuter.execute(refundParamsContext);
//                    refundUpdate.setCallBackNo(refundParamsContext.getRefundOrderEntity().getCallBackNo());
//                    refundUpdate.setExpenditureTime(currentDate);
//                    refundUpdate.setProcessState(RefundProcessEnum.REFUNDED.getCode());
//                    refundState = RefundOrderStateEnum.REFUNDED.getCode();
//                }else {
//                    return;
//                }
                break;

            // 申请退货
            case APPLY_FOR_RETURNS:
                boolean applyForReturns = this.checkTimeOut(refundOrderEntity.getRefundTime(), Constants.Refund.REFUND_AUTO_AUDIT_SECONDS);
                if (applyForReturns) {
                    refundUpdate.setProcessState(RefundProcessEnum.ALREADY.getCode());
                    refundState = RefundOrderStateEnum.WAITING_FOR_RETURN.getCode();
                }
                break;

            // 等待用户寄回
            case WAITING_FOR_RETURN:
                // 退款已拒绝
            case REJECTED_REFUND:
                // 退货已拒绝
            case REJECTED_RETURNS:
                boolean close;
                if (refundOrderStateEnum.equals(RefundOrderStateEnum.WAITING_FOR_RETURN)) {
                    // 待寄回货物，7天
                    close = this.checkTimeOut(refundOrderEntity.getAuditTime(), Constants.Refund.WAIT_GOODS_RETURN_MAX_SECONDS);
                } else {
                    // 售后拒绝 不继续处理 7天
                    close = this.checkTimeOut(refundOrderEntity.getAuditTime(), Constants.Refund.REJECTED_AND_DO_NOT_DEAL_SECONDS);
                }
                if (close) {
                    refundUpdate.setProcessState(RefundProcessEnum.CANCEL.getCode());
                    refundState = RefundOrderStateEnum.CLOSE.getCode();
                }
                break;

            default:
                break;
        }
        if (null == refundState) {
            return;
        }
        refundUpdate.setRefundState(refundState);
        refundUpdate.setAuditTime(currentDate);
        refundUpdate.setLastModifiedTime(currentDate);

        int rows = refundOrderDao.updateById(refundUpdate);
        if (rows < 1) {
            return;
        }
        log.warn(">>>>>>>>>>checK refund state, result:{refundUpdate}", JSON.toJSONString(refundUpdate));


        // 退款成功，未发货之前更新订单状态为关闭
        if (refundState.equals(RefundOrderStateEnum.REFUNDED.getCode())) {
            OrderEntity orderEntity = refundParamsContext.getOrderEntity();
            Set<Long> orderDetailIds = Collections.emptySet();
            // 非整单退
            if (refundOrderEntity.getRefundRange().equals(OrderConstants.OrderRefundStatus.ALL_REFUND_NO)) {
                orderDetailIds = refundQueryUtil.queryDetailIdsByRefundId(refundId);
            }
            orderRefundStateUtil.updateRefundStateByRefundSuccess(orderEntity.getId(), orderDetailIds, Constants.Order.TIME_TASK_USER_ID);
        }
        // 超时未处理，关闭
        if (refundState.equals(RefundOrderStateEnum.CLOSE.getCode())) {
            orderRefundStateUtil.updateRefundStateByRefundClose(refundOrderEntity, Constants.Order.TIME_TASK_USER_ID, false);
        }
    }

    private boolean checkTimeOut(Date startTime, Long timeOutSecond) {
        return null != startTime && Duration.between(startTime.toInstant(), Instant.now()).getSeconds() > timeOutSecond;
    }

}
