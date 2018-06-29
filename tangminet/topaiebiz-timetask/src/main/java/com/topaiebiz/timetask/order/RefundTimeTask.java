package com.topaiebiz.timetask.order;

import com.topaiebiz.timetask.quartzaop.aop.QuartzContextOperation;
import com.topaiebiz.trade.api.refund.RefundTaskServiceApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/3 10:26
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Slf4j
@Component
public class RefundTimeTask {

    @Autowired
    private RefundTaskServiceApi refundTaskServiceApi;


    /**
    *
    * Description: 自动审核仅退款
    *
    * Author: hxpeng
    * createTime: 2018/3/5
    *
    * @param:
    **/
    @QuartzContextOperation
    @Scheduled(cron = "0 25 */2 * * ?")
    public void auditPassRefund(){
        log.info("----------audit Pass Refund task start;");
        refundTaskServiceApi.auditPassRefund();
    }


    /**
    *
    * Description: 自动退款 寄回超时未签收
    *
    * Author: hxpeng
    * createTime: 2018/3/5
    *
    * @param:
    **/
    @QuartzContextOperation
    @Scheduled(cron = "0 25 */2 * * ?")
    public void waitReceive(){
        log.info("----------audit Pass auto pass refund's receive task start;");
        refundTaskServiceApi.waitReceive();
    }


    /**
    *
    * Description: 自动审核退货退款
    *
    * Author: hxpeng
    * createTime: 2018/3/5
    *
    * @param:
    **/
    @QuartzContextOperation
    @Scheduled(cron = "0 25 */2 * * ?")
    public void auditPassReturn(){
        log.info("----------audit Pass Return task start;");
        refundTaskServiceApi.auditPassReturn();
    }

    /**
    *
    * Description: 自动取消超时未寄回
    *
    * Author: hxpeng
    * createTime: 2018/3/5
    *
    * @param:
    **/
    @QuartzContextOperation
    @Scheduled(cron = "0 0/15 * * * ?")
    public void waitingReturn(){
        log.info("----------cancel waiting Return task start;");
        refundTaskServiceApi.waitingReturn();
    }

    /**
    *
    * Description: 自动关闭 拒绝之后未处理
    *
    * Author: hxpeng
    * createTime: 2018/3/5
    *
    * @param:
    **/
    @QuartzContextOperation
    @Scheduled(cron = "0 0/15 * * * ?")
    public void closeRejectRefund(){
        log.info("----------cancel close Reject Refund task start;");
        refundTaskServiceApi.closeRejectRefund();
    }

}
