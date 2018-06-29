package com.topaiebiz.monitor.api.impl;

import com.nebulapaas.common.DateUtils;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.topaiebiz.monitor.api.MonitorApi;
import com.topaiebiz.monitor.dto.MonitorPerformanceDTO;
import com.topaiebiz.monitor.service.MonitorLogService;
import com.topaiebiz.monitor.util.MonitorCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/***
 * @author yfeng
 * @date 2018-05-30 10:08
 */
@Service
public class MonitorApiImpl implements MonitorApi {

    @Autowired
    private MonitorLogService monitorLogService;

    @Autowired
    private RedisCache redisCache;

    //此处使用volatile保证多线程之间的可见性
    private volatile LongAdder requestCounter = new LongAdder();
    private ScheduledExecutorService scheduledExecutorService;

    private Runnable task = () -> {
        Date now = new Date();
        if (requestCounter.longValue() > 0) {
            LongAdder cutData = requestCounter;

            //新开一个计数器
            requestCounter = new LongAdder();

            //采集数据进入redis
            redisCache.incr(MonitorCacheUtil.getCacheKey(now), cutData.intValue());
        }

        Date hourEnd = DateUtils.getHourEndTime(now);
        int secDiff = DateUtils.secondDiff(now, hourEnd);
        long delay = 5;
        if (secDiff > 1 && secDiff <= delay) {
            //在今天最后一秒前处理掉计数，将误差缩小到1秒钟
            delay = secDiff - 1;
        }

        scheduledExecutorService.schedule(this.task, delay, TimeUnit.SECONDS);
    };

    @PostConstruct
    public void init() {
        ThreadFactory threadFactory = new CustomizableThreadFactory("RequestCounter-");
        scheduledExecutorService = new ScheduledThreadPoolExecutor(1, threadFactory);
        scheduledExecutorService.schedule(task, 5, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destory() {
        scheduledExecutorService.shutdown();
    }

    @Override
    public void publishPerformance(MonitorPerformanceDTO monitorPerformanceDTO) {
        monitorLogService.publishPerformance(monitorPerformanceDTO);
    }

    @Override
    public void requestCountIncrease() {
        requestCounter.increment();
    }
}