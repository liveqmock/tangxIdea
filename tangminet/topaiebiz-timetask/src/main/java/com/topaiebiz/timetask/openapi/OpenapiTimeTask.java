package com.topaiebiz.timetask.openapi;

import com.topaiebiz.openapi.api.OrderPayMessageTaskApi;
import com.topaiebiz.timetask.quartzaop.aop.QuartzContextOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/3 14:35
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class OpenapiTimeTask {


    @Autowired
    private OrderPayMessageTaskApi orderPayMessageTaskApi;


    @Value(value = "${push.order.enable:false}")
    private boolean pushOrderEnable;

    @QuartzContextOperation
    @Scheduled(cron = "0/10 * *  * * ? ")
    public void pushOrderPayMessage() throws InterruptedException {
        if (pushOrderEnable) {
            log.info(">>>>>>>>>>open api>>  pushOrderPayMessage task start;");
            orderPayMessageTaskApi.pushOrderPayMessage();
        }
    }


}
