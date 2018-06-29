package com.topaiebiz.trade.refund.api.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.trade.api.refund.RefundTaskServiceApi;
import com.topaiebiz.trade.refund.dao.RefundOrderDao;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.trade.refund.enumdata.RefundOrderStateEnum;
import com.topaiebiz.trade.refund.enumdata.RefundProcessEnum;
import com.topaiebiz.trade.refund.helper.OrderRefundStateUtil;
import com.topaiebiz.trade.refund.helper.RefundQueryUtil;
import com.topaiebiz.trade.refund.service.RefundOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description 售后定时任务
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/5 14:53
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class RefundTaskServiceApiImpl implements RefundTaskServiceApi {

    @Autowired
    private RefundOrderDao refundOrderDao;

    @Autowired
    private RefundQueryUtil refundQueryUtil;

    @Autowired
    private OrderRefundStateUtil orderRefundStateUtil;

    @Autowired
    private RefundOrderService refundOrderService;

    @Override
    public void auditPassRefund() {
        // 自动通过仅退款的售后
        List<RefundOrderEntity> refundOrderEntities = new ArrayList<>();
        // 非整单退, 五天审核时间
        refundOrderEntities.addAll(refundQueryUtil.queryPendingAuditRefunds());
        // 整单退, 两天审核时间
        refundOrderEntities.addAll(refundQueryUtil.queryPendingAuditAllRefund());
        if (CollectionUtils.isEmpty(refundOrderEntities)) {
            return;
        }
        refundOrderService.autoRefund(refundOrderEntities);
    }

    @Override
    public void waitReceive() {
        // 自动通过仅退款的售后
        List<RefundOrderEntity> refundOrderEntities;
        do {
            refundOrderEntities = refundQueryUtil.queryWaitReceive();
            if (CollectionUtils.isEmpty(refundOrderEntities)) {
                break;
            }
            refundOrderService.autoRefund(refundOrderEntities);
        } while (true);
    }


    @Override
    public void auditPassReturn() {
        // 自动通过退货退款的售后
        List<RefundOrderEntity> refundOrderEntities;
        do {
            refundOrderEntities = refundQueryUtil.queryPendingAuditReturns();
            if (CollectionUtils.isEmpty(refundOrderEntities)) {
                break;
            }
            int size = refundOrderEntities.size();
            List<Long> refundIds = refundOrderEntities.stream().map(RefundOrderEntity::getId).collect(Collectors.toList());

            EntityWrapper<RefundOrderEntity> refundWrapper = new EntityWrapper<>();
            refundWrapper.in("id", refundIds);
            refundWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            refundWrapper.eq("refundState", RefundOrderStateEnum.APPLY_FOR_RETURNS.getCode());

            RefundOrderEntity refundUpdate = new RefundOrderEntity();
            refundUpdate.cleanInit();
            refundUpdate.setRefundState(RefundOrderStateEnum.WAITING_FOR_RETURN.getCode());
            refundUpdate.setProcessState(RefundProcessEnum.ALREADY.getCode());
            Date currentDate = new Date();
            refundUpdate.setAuditTime(currentDate);
            refundUpdate.setLastModifiedTime(currentDate);
            refundUpdate.setLastModifierId(Constants.Order.TIME_TASK_USER_ID);
            int updateRows = refundOrderDao.update(refundUpdate, refundWrapper);
            log.warn("----------Timing tasks: auto pass returns, query data rows:{}, success rows:{}, fail rows:{} ！", size, updateRows, size - updateRows);
        } while (true);
    }

    @Override
    public void waitingReturn() {
        // 关闭 用户申请售后同意之后 超时未寄回来
        List<RefundOrderEntity> refundOrderEntities;
        do {
            refundOrderEntities = refundQueryUtil.queryPendingShipping();
            if (CollectionUtils.isEmpty(refundOrderEntities)) {
                break;
            }
            int size = refundOrderEntities.size();
            int updateRows = this.doCloseRefund(refundOrderEntities);
            log.warn("----------Timing tasks: auto closing orders that have not been shipped out of refund, query data rows:{}, success rows:{}, fail rows:{} ！", size, updateRows, size - updateRows);
        } while (true);
    }

    @Override
    public void closeRejectRefund() {
        // 关闭 售后被拒绝 超时未处理的售后订单
        List<RefundOrderEntity> refundOrderEntities;
        do {
            refundOrderEntities = refundQueryUtil.queryRejectedRefund();
            if (CollectionUtils.isEmpty(refundOrderEntities)) {
                break;
            }
            int size = refundOrderEntities.size();
            int updateRows = this.doCloseRefund(refundOrderEntities);
            log.warn("----------Timing tasks: auto close rejected and overtime unprocessed orders, query data rows:{}, success rows:{}, fail rows:{} ！", size, updateRows, size - updateRows);
        } while (true);
    }


    private int doCloseRefund(List<RefundOrderEntity> refundOrderEntities) {
        int updateRows = 0;
        Date currentDate = new Date();
        for (RefundOrderEntity refundOrderEntity : refundOrderEntities) {
            RefundOrderEntity refundUpdate = new RefundOrderEntity();
            refundUpdate.cleanInit();
            refundUpdate.setId(refundOrderEntity.getId());
            refundUpdate.setRefundState(RefundOrderStateEnum.CLOSE.getCode());
            refundUpdate.setLastModifierId(Constants.Order.TIME_TASK_USER_ID);
            refundUpdate.setLastModifiedTime(currentDate);

            if (refundOrderDao.updateById(refundUpdate) > 0) {
                updateRows++;
                // 修改订单明细的售后状态
                orderRefundStateUtil.updateRefundStateByRefundClose(refundOrderEntity, Constants.Order.TIME_TASK_USER_ID, false);
            }
        }
        return updateRows;
    }
}
