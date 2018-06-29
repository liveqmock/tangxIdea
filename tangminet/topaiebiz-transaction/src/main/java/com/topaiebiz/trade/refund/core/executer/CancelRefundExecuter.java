package com.topaiebiz.trade.refund.core.executer;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.trade.refund.core.context.RefundParamsContext;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.trade.refund.entity.RefundOrderLogEntity;
import com.topaiebiz.trade.refund.enumdata.RefundOrderStateEnum;
import com.topaiebiz.trade.refund.enumdata.RefundProcessEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Description 取消售后
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/3 13:18
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class CancelRefundExecuter extends AbstractRefundExecuter {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean execute(RefundParamsContext refundParamsContext) {
        Date currentDate = new Date();
        Long memberId = refundParamsContext.getExecuteUserDTO().getMemberId();
        RefundOrderEntity refundOrderEntity = refundParamsContext.getRefundOrderEntity();

        RefundOrderEntity updateEntity = new RefundOrderEntity();
        updateEntity.cleanInit();
        updateEntity.setId(refundOrderEntity.getId());
        updateEntity.setRefundState(RefundOrderStateEnum.CLOSE.getCode());
        updateEntity.setProcessState(RefundProcessEnum.CANCEL.getCode());
        updateEntity.setCancelTime(currentDate);
        updateEntity.setLastModifierId(memberId);
        updateEntity.setLastModifiedTime(currentDate);

        boolean updateResult = refundOrderDao.updateById(updateEntity) > 0;
        if (updateResult) {
            // 修改订单状态
            orderRefundStateUtil.updateRefundStateByRefundClose(refundOrderEntity, memberId, false);

            // 逻辑删除退款日志
            refundOrderLogHelper.deleteLog(refundOrderEntity.getId());
        }
        log.info(">>>>>>>>>>member:{} cancel refund, params:{}, result:{}", memberId, JSON.toJSONString(updateEntity), updateResult);
        return updateResult;
    }

}
