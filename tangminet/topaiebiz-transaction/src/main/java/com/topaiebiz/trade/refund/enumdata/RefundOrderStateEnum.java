package com.topaiebiz.trade.refund.enumdata;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.trade.constants.OrderConstants;
import com.topaiebiz.trade.refund.exception.RefundOrderExceptionEnum;
import com.topaiebiz.transaction.common.util.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * Description 售后订单状态枚举
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/10 14:07
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
public enum RefundOrderStateEnum {

    APPLY_FOR_REFUND(1, "申请退款"),

    APPLY_FOR_RETURNS(2, "申请退货"),

    WAITING_FOR_RETURN(3, "待寄回商品"),

    WAITING_FOR_RECEIVE(4, "待签收商品"),

    REFUNDING(5, "部分已退款"),

    REFUNDED(6, "已退款"),

    REJECTED_REFUND(7, "退款已拒绝"),

    REJECTED_RETURNS(8, "退货已拒绝"),

    CLOSE(9, "已关闭"),

    PLATFORM_REFUSE(10, "平台已拒绝");

    private Integer code;

    private String desc;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    RefundOrderStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
    *
    * Description: 是否能够被重新申请
    *
    * Author: hxpeng
    * createTime: 2018/4/12
    *
    * @param:
    **/
    public static boolean whetherRefundCanReapply(Integer refundState) {
        return RefundOrderStateEnum.REJECTED_RETURNS.getCode().equals(refundState) || RefundOrderStateEnum.REJECTED_REFUND.getCode().equals(refundState);
    }

    /**
     * Description: 判断商品是否能够继续提交售后申请
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/20
     *
     * @param:
     **/
    public static boolean whetherRefundCanBeSubmit(Integer orderRefundState) {
        return null == orderRefundState || orderRefundState.equals(OrderConstants.OrderRefundStatus.NO_REFUND);
    }


    /**
     * Description: 当前售后订单状态是否支持修改
     * <p>
     * Author: hxpeng
     * createTime: 2018/2/2
     *
     * @param:
     **/
    public static boolean whetherRefundCanUpdate(Integer orderRefundState) {
        return (orderRefundState.equals(APPLY_FOR_REFUND.getCode()) || orderRefundState.equals(APPLY_FOR_RETURNS.getCode()));
    }


    /**
     * Description: 修改的售后状态，是否为状态尾值
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/30
     *
     * @param:
     **/
    public static boolean isRefundFinish(Integer refundState) {
        return refundState.equals(REFUNDED.getCode()) || refundState.equals(CLOSE.getCode()) || refundState.equals(PLATFORM_REFUSE.getCode());
    }


    /**
     * Description: 根绝code 获取枚举
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/20
     *
     * @param:
     **/
    public static RefundOrderStateEnum getByCode(Integer code) {
        for (RefundOrderStateEnum refundOrderStateEnum : RefundOrderStateEnum.values()) {
            if (refundOrderStateEnum.getCode().equals(code)) {
                return refundOrderStateEnum;
            }
        }
        throw new GlobalException(RefundOrderExceptionEnum.INVALID_ENUMERATION_TYPE);
    }

    /**
     * Description: 是否能够取消售后订单
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/10
     *
     * @param:
     **/
    public static boolean whetherRefundCanBeCancel(Integer refundState) {
        if (refundState.equals(APPLY_FOR_REFUND.getCode()) || refundState.equals(APPLY_FOR_RETURNS.getCode())
                || refundState.equals(WAITING_FOR_RETURN.getCode()) || refundState.equals(REJECTED_REFUND.getCode())
                || refundState.equals(REJECTED_RETURNS.getCode())) {
            return true;
        }
        return false;
    }


    /**
     * Description: 判断状态是否允许申请售后
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/13
     *
     * @param:
     **/
    public static boolean whetherApplyRefund(Integer orderState) {
        return orderState.equals(OrderStatusEnum.PENDING_DELIVERY.getCode()) || orderState.equals(OrderStatusEnum.PENDING_RECEIVED.getCode())
                || orderState.equals(OrderStatusEnum.HAVE_BEEN_RECEIVED.getCode());
    }

}
