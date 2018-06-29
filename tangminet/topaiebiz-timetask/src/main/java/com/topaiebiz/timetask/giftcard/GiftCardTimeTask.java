package com.topaiebiz.timetask.giftcard;

import com.topaiebiz.card.api.GiftCardApi;
import com.topaiebiz.timetask.quartzaop.aop.QuartzContextOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Description 礼卡任务
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017/11/22 15:40
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
@Slf4j
public class GiftCardTimeTask {
    @Autowired
    private GiftCardApi giftCardApi;
    /**
     * 修改礼卡订单状态
     * 5分钟执行一次，取消30-35mins未支付
     */
    @QuartzContextOperation
    @Scheduled(cron = "0 0/5 * * * ? ")
    public void updateGiftcardOrderStatus() {
        log.info("更新礼卡订单状态开始");
        //失效半小时之前未支付的订单
        Integer total = giftCardApi.updGiftcardOrderStatus(new Date(System.currentTimeMillis()-30*60*1000));
        log.info("更新礼卡订单状态结束，本次更新数量:{}",total);

    }

    /**
     * 修改售出礼卡的状态
     * 每天凌晨一点执行一次
     */
    @QuartzContextOperation
    @Scheduled(cron = "0 0 1 * * ? ")
    public void updateGiftcarUnitStatus() {
        log.info("更新礼卡状态开始");
        Integer total = giftCardApi.updGiftcardUnitStatus();
        log.info("更新礼卡状态结束，本次更新数量:{}",total);
    }

}
