package com.topaiebiz.trade.refund.core.executer;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.refund.core.context.RefundParamsContext;
import com.topaiebiz.trade.refund.dto.RefundSubmitDTO;
import com.topaiebiz.trade.refund.dto.common.RefundGoodDTO;
import com.topaiebiz.trade.refund.entity.RefundOrderDetailEntity;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.trade.refund.enumdata.RefundOrderStateEnum;
import com.topaiebiz.trade.refund.enumdata.RefundProcessEnum;
import com.topaiebiz.trade.refund.enumdata.RefundReasonEnum;
import com.topaiebiz.trade.refund.exception.RefundOrderExceptionEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Description 提交申请售后
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/2 10:08
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class SubmitRefundExecuter extends AbstractRefundExecuter {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean execute(RefundParamsContext refundParamsContext) {
        RefundSubmitDTO refundSubmitDTO = refundParamsContext.getRefundSubmitDTO();
        OrderEntity orderEntity = refundParamsContext.getOrderEntity();

        boolean result;
        Date currentDate = new Date();
        Long memberId = refundParamsContext.getExecuteUserDTO().getMemberId();
        Long refundId = refundSubmitDTO.getRefundId();
        Long orderId = refundSubmitDTO.getOrderId();

        //1：保存售后订单
        RefundOrderEntity refundOrderEntity = new RefundOrderEntity();
        BeanCopyUtil.copy(refundSubmitDTO, refundOrderEntity);
        Integer refundType = refundSubmitDTO.getRefundType();
        Integer refundState = refundType.equals(Constants.Refund.RETURNS) ? RefundOrderStateEnum.APPLY_FOR_RETURNS.getCode() : RefundOrderStateEnum.APPLY_FOR_REFUND.getCode();
        refundOrderEntity.setProcessState(RefundProcessEnum.WAIT.getCode());
        refundOrderEntity.setRefundState(refundState);
        refundOrderEntity.setRefundType(refundType);
        refundOrderEntity.setRefundReason(RefundReasonEnum.getByCode(refundSubmitDTO.getRefundReasonCode()).getDesc());
        refundOrderEntity.setPfInvolved(Constants.Refund.PLATFORM_IS_NOT_INVOLVED);
        refundOrderEntity.setRefundRange(refundParamsContext.isAllRefund() ? OrderConstants.OrderRefundStatus.ALL_REFUND_YES : OrderConstants.OrderRefundStatus.ALL_REFUND_NO);

        // 修改售后订单
        if (refundParamsContext.isUpdate()) {
            refundOrderEntity.cleanInit();
            if (StringUtils.isBlank(refundOrderEntity.getRefundImg1())) {
                refundOrderEntity.setRefundImg1(" ");
            }
            if (StringUtils.isBlank(refundOrderEntity.getRefundImg2())) {
                refundOrderEntity.setRefundImg2(" ");
            }
            if (StringUtils.isBlank(refundOrderEntity.getRefundImg3())) {
                refundOrderEntity.setRefundImg3(" ");
            }
            refundOrderEntity.setId(refundId);
            refundOrderEntity.setLastModifierId(memberId);
            refundOrderEntity.setLastModifiedTime(currentDate);
            // 订单号不修改
            refundOrderEntity.setOrderId(null);
            result = super.refundOrderDao.updateById(refundOrderEntity) > 0;

            if (result) {
                // 删除旧售后明细
                this.deleteRefundDetail(refundId);
            }
            // 修改退款日志
            if (!refundOrderLogHelper.updateLog(refundOrderEntity)) {
                throw new GlobalException(RefundOrderExceptionEnum.CREATE_REFUND_FAIL);
            }
        } else {
            refundOrderEntity.setMemberId(memberId);
            refundOrderEntity.setMemberName(orderEntity.getMemberName());
            refundOrderEntity.setStoreId(orderEntity.getStoreId());
            refundOrderEntity.setStoreName(orderEntity.getStoreName());
            refundOrderEntity.setMerchantName("");
            refundOrderEntity.setCreatorId(memberId);
            refundOrderEntity.setRefundTime(currentDate);
            result = super.refundOrderDao.insert(refundOrderEntity) > 0;
            if (result) {
                // 创建退款日志
                if (null == refundOrderLogHelper.insertLog(refundOrderEntity)) {
                    throw new GlobalException(RefundOrderExceptionEnum.CREATE_REFUND_FAIL);
                }
                // 修改订单 及 相关商品的售后状态
                Set<Long> orderDetailIds = refundParamsContext.isAllRefund() ? Collections.emptySet() : refundSubmitDTO.getOrderDetailIds();
                orderRefundStateUtil.updateRefundStateByRefundCreate(orderId, orderDetailIds, memberId);
            }
        }
        refundId = refundOrderEntity.getId();
        if (result) {
            //2 ： 保存售后订单明细
            this.submitDetail(refundId, memberId, refundSubmitDTO.getRefundGoodDTOS());
        }
        return result;
    }


    /**
     * Description: 删除售后旧明细（售后修改时调用）
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/2
     *
     * @param:
     **/
    private void deleteRefundDetail(Long refundId) {
        EntityWrapper<RefundOrderDetailEntity> entityEntityWrapper = new EntityWrapper<>();
        entityEntityWrapper.eq("refundOrderId", refundId);
        super.refundOrderDetailDao.delete(entityEntityWrapper);
    }

    /**
     * Description: 插入售后明细
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/2
     *
     * @param:
     **/
    private void submitDetail(Long refundId, Long memberId, List<RefundGoodDTO> refundGoodDTOS) {
        for (RefundGoodDTO refundGoodDto : refundGoodDTOS) {
            RefundOrderDetailEntity refundOrderDetailEntity = new RefundOrderDetailEntity();
            BeanUtils.copyProperties(refundGoodDto, refundOrderDetailEntity);
            refundOrderDetailEntity.setRefundOrderId(refundId);
            refundOrderDetailEntity.setCreatorId(memberId);
            refundOrderDetailEntity.setGoodItemId(refundGoodDto.getItemId());
            super.refundOrderDetailDao.insert(refundOrderDetailEntity);
        }
    }

}
