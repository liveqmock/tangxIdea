package com.topaiebiz.timetask.elasticsearch;

import com.topaiebiz.card.api.GiftCardApi;
import com.topaiebiz.elasticsearch.api.ElasticSearchApi;
import com.topaiebiz.timetask.quartzaop.aop.QuartzContextOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Description 礼卡任务
 * <p>
 * Author tangx
 * <p>
 * Date 2018/06/28 10:07
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
@Slf4j
public class ElasticsearchTimeTask {
    @Autowired
    private ElasticSearchApi elasticSearchApi;
    /**
     * 同步数据到搜索引擎
     * 每周日晚上12点全量同步一次
     */
    @QuartzContextOperation
    @Scheduled(cron = "0 0 0 ? * SUN ")
    public void updateGiftcardOrderStatus() {
        log.info("同步数据到搜索引擎开始");
        //失效半小时之前未支付的订单
        elasticSearchApi.syncAllItems();
        log.info("同步数据到搜索引擎结束");

    }


}
