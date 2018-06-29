package com.topaiebiz.trade.refund.core.executer;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.trade.order.facade.ExpressageServiceFacade;
import com.topaiebiz.trade.refund.core.context.RefundParamsContext;
import com.topaiebiz.trade.refund.dto.RefundLogisticsDTO;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.trade.refund.enumdata.RefundOrderStateEnum;
import com.topaiebiz.trade.refund.enumdata.RefundProcessEnum;
import com.topaiebiz.transport.dto.LogisticsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Description 提交物流信息
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/3 11:34
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class SubmitLogisticsExecuter extends AbstractRefundExecuter {

    @Autowired
    private ExpressageServiceFacade expressageServiceFacade;

    @Override
    public boolean execute(RefundParamsContext refundParamsContext) {
        Date currentDate = new Date();
        RefundLogisticsDTO refundLogisticsDTO = refundParamsContext.getRefundLogisticsDTO();
        RefundOrderEntity refundOrderEntity = refundParamsContext.getRefundOrderEntity();

        LogisticsDto logisticsDto = expressageServiceFacade.getLogistics(refundLogisticsDTO.getLogisticsCompanyId());

        EntityWrapper<RefundOrderEntity> refundWrapper = new EntityWrapper<>();
        refundWrapper.eq("id", refundOrderEntity.getId());
        refundWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        refundWrapper.eq("refundState", RefundOrderStateEnum.WAITING_FOR_RETURN.getCode());

        RefundOrderEntity refundUpdate = new RefundOrderEntity();
        refundUpdate.cleanInit();
        refundUpdate.setLogisticsCompanyId(logisticsDto.getId());
        refundUpdate.setLogisticsCompanyName(logisticsDto.getComName());
        refundUpdate.setLogisticsNo(refundLogisticsDTO.getLogisticsNo());
        refundUpdate.setProcessState(RefundProcessEnum.WAIT.getCode());
        refundUpdate.setRefundState(RefundOrderStateEnum.WAITING_FOR_RECEIVE.getCode());
        refundUpdate.setShipmentsTime(currentDate);
        refundUpdate.setLastModifierId(refundParamsContext.getExecuteUserDTO().getMemberId());
        refundUpdate.setLastModifiedTime(currentDate);

        // 发送请求到快递100
        expressageServiceFacade.sendExpress(logisticsDto.getId(), refundLogisticsDTO.getLogisticsNo());

        return super.refundOrderDao.update(refundUpdate, refundWrapper) > 0;
    }
}
