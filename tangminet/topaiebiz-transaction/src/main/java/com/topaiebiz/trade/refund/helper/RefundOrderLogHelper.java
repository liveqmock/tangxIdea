package com.topaiebiz.trade.refund.helper;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.order.util.MathUtil;
import com.topaiebiz.trade.refund.dao.RefundOrderLogDao;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.trade.refund.entity.RefundOrderLogEntity;
import com.topaiebiz.trade.refund.exception.RefundOrderExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/4/3 22:00
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class RefundOrderLogHelper {

    @Autowired
    private RefundOrderLogDao refundOrderLogDao;

    /**
     * TODO 增加一个售后日志维护 的帮助类
     */


    public RefundOrderLogEntity findByRefundId(Long refundId) {
        RefundOrderLogEntity refundOrderLogEntity = new RefundOrderLogEntity();
        refundOrderLogEntity.cleanInit();
        refundOrderLogEntity.setRefundOrderId(refundId);
        refundOrderLogEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        return refundOrderLogDao.selectOne(refundOrderLogEntity);
    }

    public RefundOrderLogEntity insertLog(RefundOrderEntity refundOrderEntity) {
        if (null != findByRefundId(refundOrderEntity.getId())) {
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_LOG_EXISTED);
        }
        deleteLogByOrderId(refundOrderEntity.getOrderId());
        RefundOrderLogEntity insert = new RefundOrderLogEntity();
        insert.setTaskExecCount(0);
        insert.setRefundState(Constants.Refund.NO_REFUND);
        insert.setRefundOrderId(refundOrderEntity.getId());
        insert.setOrderId(refundOrderEntity.getOrderId());
        insert.setRefundCardPrice(refundOrderEntity.getRefundCardPrice());
        insert.setRefundCardResult(MathUtil.greator(refundOrderEntity.getRefundCardPrice(), BigDecimal.ZERO) ? Constants.Refund.REFUND_NO : Constants.Refund.REFUND_YES);
        insert.setRefundAssetPrice(refundOrderEntity.getRefundIntegralPrice().add(refundOrderEntity.getRefundBalance()));
        insert.setRefundAssetResult(MathUtil.greator(insert.getRefundAssetPrice(), BigDecimal.ZERO) ? Constants.Refund.REFUND_NO : Constants.Refund.REFUND_YES);
        insert.setRefundAmounts(refundOrderEntity.getRefundThirdAmount());
        insert.setRefundAmountsResult(MathUtil.greator(refundOrderEntity.getRefundThirdAmount(), BigDecimal.ZERO) ? Constants.Refund.REFUND_NO : Constants.Refund.REFUND_YES);
        if (refundOrderLogDao.insert(insert) > 0) {
            return insert;
        } else {
            return null;
        }
    }

    public boolean updateLog(RefundOrderEntity refundOrderEntity) {
        RefundOrderLogEntity condition = new RefundOrderLogEntity();
        condition.cleanInit();
        condition.setRefundOrderId(refundOrderEntity.getId());
        condition.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);

        RefundOrderLogEntity refundOrderLogEntity = refundOrderLogDao.selectOne(condition);
        if (null == refundOrderLogEntity) {
            throw new GlobalException(RefundOrderExceptionEnum.REFUND_LOG_DON_NOT_EXISTED);
        }

        RefundOrderLogEntity update = new RefundOrderLogEntity();
        update.cleanInit();
        update.setId(refundOrderLogEntity.getId());
        update.setRefundCardPrice(refundOrderEntity.getRefundCardPrice());
        update.setRefundCardResult(MathUtil.greator(refundOrderEntity.getRefundCardPrice(), BigDecimal.ZERO) ? Constants.Refund.REFUND_NO : Constants.Refund.REFUND_YES);
        update.setRefundAssetPrice(refundOrderEntity.getRefundIntegralPrice().add(refundOrderEntity.getRefundBalance()));
        update.setRefundAssetResult(MathUtil.greator(update.getRefundAssetPrice(), BigDecimal.ZERO) ? Constants.Refund.REFUND_NO : Constants.Refund.REFUND_YES);
        update.setRefundAmounts(refundOrderEntity.getRefundThirdAmount());
        update.setRefundAmountsResult(MathUtil.greator(refundOrderEntity.getRefundThirdAmount(), BigDecimal.ZERO) ? Constants.Refund.REFUND_NO : Constants.Refund.REFUND_YES);
        update.setLastModifiedTime(new Date());
        update.setLastModifierId(0L);
        update.setLastModifiedTime(new Date());
        return refundOrderLogDao.updateById(update) > 0;
    }

    public boolean deleteLog(Long refundId) {
        if (null == refundId) {
            return false;
        }
        RefundOrderLogEntity refundOrderLogEntity = findByRefundId(refundId);
        if (null == refundOrderLogEntity) {
            return true;
        }

        RefundOrderLogEntity update = new RefundOrderLogEntity();
        update.cleanInit();
        update.setId(refundOrderLogEntity.getId());
        update.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        update.setLastModifierId(0L);
        update.setLastModifiedTime(new Date());
        return refundOrderLogDao.updateById(update) > 0;
    }


    /**
     * 重新申请售后，回
     * @param orderId
     * @return
     */
    public boolean deleteLogByOrderId(Long orderId){
        EntityWrapper<RefundOrderLogEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("orderId", orderId);
        wrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

        RefundOrderLogEntity update = new RefundOrderLogEntity();
        update.cleanInit();
        update.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        update.setLastModifierId(0L);
        update.setLastModifiedTime(new Date());
        return refundOrderLogDao.update(update, wrapper) > 0;
    }


}
