package com.topaiebiz.trade.api.refund;

/**
 * Description 售后审核时间已过，自动通过
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/12 14:57
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface RefundTaskServiceApi {

    /**
    *
    * Description: 仅退款超时未处理，自动申通通过
    *
    * Author: hxpeng
    * createTime: 2018/2/5
    *
    * @param:
    **/
    void auditPassRefund();

    /**
    *
    * Description: 超时未签收，自动确认退款
    *
    * Author: hxpeng
    * createTime: 2018/3/5
    *
    * @param:
    **/
    void waitReceive();

    /**
    *
    * Description: 退货退款超时未处理，自动申请通过
    *
    * Author: hxpeng
    * createTime: 2018/2/5
    *
    * @param:
    **/
    void auditPassReturn();


    /**
    *
    * Description: 等待被寄回，超时未寄回，自动取消
    *
    * Author: hxpeng
    * createTime: 2018/2/5
    *
    * @param:
    **/
    void waitingReturn();

    /**
    *
    * Description: 关闭 被拒绝超时未处理的售后订单
    *
    * Author: hxpeng
    * createTime: 2018/2/5
    *
    * @param:
    **/
    void closeRejectRefund();

}