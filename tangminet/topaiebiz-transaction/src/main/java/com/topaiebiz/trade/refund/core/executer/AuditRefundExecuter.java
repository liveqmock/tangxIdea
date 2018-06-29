package com.topaiebiz.trade.refund.core.executer;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.refund.core.context.RefundParamsContext;
import com.topaiebiz.trade.refund.core.context.SimpleDateFormatContext;
import com.topaiebiz.trade.refund.dto.common.ExecuteUserDTO;
import com.topaiebiz.trade.refund.entity.RefundOrderEntity;
import com.topaiebiz.trade.refund.enumdata.RefundOrderStateEnum;
import com.topaiebiz.trade.refund.enumdata.RefundProcessEnum;
import com.topaiebiz.trade.refund.exception.RefundOrderExceptionEnum;
import com.topaiebiz.trade.refund.facade.MerchantReturnServiceFacade;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import com.topaiebiz.transaction.order.merchant.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * Description 审核售后者
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/2 10:18
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class AuditRefundExecuter extends AbstractRefundExecuter {

    @Autowired
    private RefundMoneyExecuter refundMoneyExecuter;

    @Autowired
    private MerchantReturnServiceFacade merchantReturnServiceFacade;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean execute(RefundParamsContext refundParamsContext) {
        log.warn(">>>>>>>>>>start audit refund, params:{}", JSON.toJSONString(refundParamsContext));
        try {
            //1：校验操作者
            this.verifyOperator(refundParamsContext);
            //2：审核 and 拼装修改参数
            RefundOrderEntity refundUpdate = refundParamsContext.isAuditSuccess() ? this.auditPassed(refundParamsContext) : this.auditRejected(refundParamsContext);
            if (null == refundUpdate) {
                return false;
            }
            log.warn(">>>>>>>>>>end audit refund, result:{}", JSON.toJSONString(refundUpdate));
            if (refundOrderDao.updateById(refundUpdate) <= 0) {
                throw new GlobalException(RefundOrderExceptionEnum.AUDIT_REFUND_FAIL);
            }
            return true;
        } finally {
            SimpleDateFormatContext.remove();
        }
    }

    /**
     * Description: 审核通过
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/2
     *
     * @param:
     **/
    private RefundOrderEntity auditPassed(RefundParamsContext refundParamsContext) {
        Date currentDate = new Date();
        Long refundId = refundParamsContext.getRefundOrderEntity().getId();
        Long memberId = refundParamsContext.getExecuteUserDTO().getMemberId();
        Integer refundState = refundParamsContext.getRefundOrderEntity().getRefundState();
        RefundOrderStateEnum refundOrderStateEnum = RefundOrderStateEnum.getByCode(refundState);
        RefundOrderEntity refundOrderEntity = refundParamsContext.getRefundOrderEntity();

        // 修改的实体容器
        RefundOrderEntity refundUpdate = new RefundOrderEntity();
        refundUpdate.cleanInit();
        refundUpdate.setId(refundId);

        switch (refundOrderStateEnum) {
            // 申请退款
            case APPLY_FOR_REFUND:
                // 等待商家签收
            case WAITING_FOR_RECEIVE:
                // 退款已拒绝(平台介入下出现)
            case REJECTED_REFUND:
                refundUpdate.setRefundState(RefundOrderStateEnum.REFUNDED.getCode());
                // 已退款 == 已处理
                refundUpdate.setProcessState(RefundProcessEnum.ALREADY.getCode());

                // 退款
                if (!refundMoneyExecuter.execute(refundParamsContext)) {
                    return null;
                }
                refundUpdate.setCallBackNo(refundOrderEntity.getCallBackNo());
                refundUpdate.setExpenditureTime(currentDate);
                refundUpdate.setCompleteTime(currentDate);
                break;

            // 申请退货
            case APPLY_FOR_RETURNS:
                // 判断商家有没有收货地址
                merchantReturnServiceFacade.getStoreReturnAddress(refundOrderEntity.getStoreId());

                refundUpdate.setRefundState(RefundOrderStateEnum.WAITING_FOR_RETURN.getCode());
                refundUpdate.setProcessState(RefundProcessEnum.ALREADY.getCode());
                break;

            // 退货已拒绝(平台介入下出现)
            case REJECTED_RETURNS:
                refundUpdate.setRefundState(RefundOrderStateEnum.WAITING_FOR_RETURN.getCode());
                refundUpdate.setProcessState(RefundProcessEnum.ALREADY.getCode());
                // 将签收退还商品的操作还给商家
                refundUpdate.setPfInvolved(Constants.Refund.PLATFORM_IS_NOT_INVOLVED);
                refundUpdate.setSpareField_1(StringUtils.join(StringUtils.isBlank(refundOrderEntity.getSpareField_1()) ? "" : refundOrderEntity.getSpareField_1(),
                        "\n", "操作日志：平台通过该售后的退货申请，操作时间：",
                        SimpleDateFormatContext.getFormat().format(currentDate),
                        ";"));
                break;

            default:
                log.error("----------The status of after-sales order：{} is not normal", refundId);
                return null;
        }
        refundUpdate.setAuditTime(currentDate);
        refundUpdate.setLastModifiedTime(currentDate);
        refundUpdate.setLastModifierId(memberId);

        // 退款成功，未发货之前更新订单状态为关闭， 发货之后的售后不影响订单状态
        if (RefundOrderStateEnum.REFUNDED.getCode().equals(refundUpdate.getRefundState())) {
            OrderEntity orderEntity = refundParamsContext.getOrderEntity();
            Set<Long> orderDetailIds = Collections.emptySet();
            // 非整单退
            if (refundOrderEntity.getRefundRange().equals(OrderConstants.OrderRefundStatus.ALL_REFUND_NO)) {
                orderDetailIds = refundQueryUtil.queryDetailIdsByRefundId(refundId);
            }
            orderRefundStateUtil.updateRefundStateByRefundSuccess(orderEntity.getId(), orderDetailIds, memberId);
        }
        return refundUpdate;
    }


    /**
     * Description: 审核拒绝
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/2
     *
     * @param:
     **/
    private RefundOrderEntity auditRejected(RefundParamsContext refundParamsContext) {
        Date currentDate = new Date();
        Long refundId = refundParamsContext.getRefundOrderEntity().getId();
        Integer refundState = refundParamsContext.getRefundOrderEntity().getRefundState();
        RefundOrderStateEnum refundOrderStateEnum = RefundOrderStateEnum.getByCode(refundState);
        OrderEntity orderEntity = refundParamsContext.getOrderEntity();
        RefundOrderEntity refundOrderEntity = refundParamsContext.getRefundOrderEntity();

        // 未发货状态下， 商家不能拒绝售后
        if (OrderStatusEnum.PENDING_DELIVERY.getCode().equals(orderEntity.getOrderState())) {
            throw new GlobalException(RefundOrderExceptionEnum.UN_DELIVERY_CANT_REJECT_REFUND);
        }

        // 修改的实体容器
        RefundOrderEntity refundUpdate = new RefundOrderEntity();
        refundUpdate.cleanInit();
        refundUpdate.setId(refundId);

        switch (refundOrderStateEnum) {
            // 申请退款
            case APPLY_FOR_REFUND:
                // 签收/审核 用户寄回的商品
            case WAITING_FOR_RECEIVE:
                refundUpdate.setRefundState(RefundOrderStateEnum.REJECTED_REFUND.getCode());
                refundUpdate.setProcessState(RefundProcessEnum.REFUSE.getCode());
                break;

            // 申请退货
            case APPLY_FOR_RETURNS:
                refundUpdate.setRefundState(RefundOrderStateEnum.REJECTED_RETURNS.getCode());
                refundUpdate.setProcessState(RefundProcessEnum.REFUSE.getCode());
                break;

            // 退款被拒绝(平台介入)
            // 退货被拒绝(平台介入)
            case REJECTED_RETURNS:
            case REJECTED_REFUND:
                refundUpdate.setRefundState(RefundOrderStateEnum.CLOSE.getCode());
                refundUpdate.setProcessState(RefundProcessEnum.REFUSE.getCode());
                refundUpdate.setSpareField_1(StringUtils.join(StringUtils.isBlank(refundOrderEntity.getSpareField_1()) ? "" : refundOrderEntity.getSpareField_1(),
                        "\n", "关闭原因：平台已拒绝该售后，操作时间：",
                        SimpleDateFormatContext.format(currentDate),
                        ";"));
                break;

            default:
                log.error("----------The status of after-sales order：{} is not normal", refundId);
                return null;
        }
        refundUpdate.setRefuseDescription(refundParamsContext.getRefuseDescription());
        refundUpdate.setAuditTime(currentDate);
        refundUpdate.setLastModifiedTime(currentDate);
        refundUpdate.setLastModifierId(refundParamsContext.getExecuteUserDTO().getMemberId());


        // 如果是平台拒绝， 则把售后的商品的售后状态改为平台拒绝，无法再申请售后
        if (refundUpdate.getRefundState().equals(RefundOrderStateEnum.CLOSE.getCode()) && refundParamsContext.isPlatformInvolved()) {
            orderRefundStateUtil.updateRefundStateByRefundClose(refundParamsContext.getRefundOrderEntity(), refundParamsContext.getExecuteUserDTO().getMemberId(), true);
        }
        return refundUpdate;
    }

    /**
     * Description: 校验操作者
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/2
     *
     * @param:
     **/
    private void verifyOperator(RefundParamsContext refundParamsContext) {
        ExecuteUserDTO executeUserDTO = refundParamsContext.getExecuteUserDTO();
        Long refundId = refundParamsContext.getRefundOrderEntity().getId();
        if (refundParamsContext.isPlatformInvolved()) {
            if (!executeUserDTO.isFromPlatform()) {
                log.error("----------auditer:{} has not authorized to operate the platform involved in the sale of orders:{}", JSON.toJSONString(executeUserDTO), refundId);
                throw new GlobalException(RefundOrderExceptionEnum.OPERATION_CAN_NOT_BE_EXECUTED);
            }
        } else {
            if (!executeUserDTO.getStoreId().equals(refundParamsContext.getRefundOrderEntity().getStoreId())) {
                log.error("----------auditer:{} does not belong the refund order's store", JSON.toJSONString(executeUserDTO), refundId);
                throw new GlobalException(RefundOrderExceptionEnum.OPERATION_CAN_NOT_BE_EXECUTED);
            }
        }
    }


}
