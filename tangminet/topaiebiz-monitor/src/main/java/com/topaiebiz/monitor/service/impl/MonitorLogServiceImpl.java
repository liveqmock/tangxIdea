package com.topaiebiz.monitor.service.impl;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.topaiebiz.basic.api.ConfigApi;
import com.topaiebiz.monitor.dao.MonitorLogDao;
import com.topaiebiz.monitor.dao.MonitorPerformanceDao;
import com.topaiebiz.monitor.dto.MonitorLogDTO;
import com.topaiebiz.monitor.dto.MonitorPerformanceDTO;
import com.topaiebiz.monitor.entity.MonitorLogEntity;
import com.topaiebiz.monitor.entity.MonitorPerformanceEntity;
import com.topaiebiz.monitor.service.MonitorLogService;
import com.topaiebiz.monitor.util.MonitorCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * 监控日志服务
 *
 * @author xyhua
 * @date 2018-04-11 16:55
 */
@Slf4j
@Component
public class MonitorLogServiceImpl implements MonitorLogService, DisposableBean {
    @Autowired
    private MonitorLogDao monitorLogDao;
    @Autowired
    private MonitorPerformanceDao monitorPerformanceDao;
    @Autowired
    private ConfigApi configApi;
    @Autowired
    private RedisCache redisCache;

    private int coreSize = 15;
    private int maxSize = 15;
    private int keepAliveTime = 1;
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(2000);

    private ExecutorService executorService = new ThreadPoolExecutor(coreSize, maxSize, keepAliveTime, TimeUnit.SECONDS, queue, new MonitorExecutorPolicy());

    @Override
    public void destroy() throws Exception {
        executorService.shutdown();
    }

    @Override
    public void publishLog(MonitorLogDTO monitorLog) {
        String config = configApi.getConfig("monitor.error.open");
        if ("true".equals(config)) {
            executorService.submit(() -> {
                //添加日志
                Boolean res = addLog(monitorLog);

                if (!res) {
                    log.warn("发布日志失败！ {}", monitorLog.getContent());
                }
            });
        }
    }

    @Override
    public Boolean addLog(MonitorLogDTO monitorLog) {
        MonitorLogEntity entity = new MonitorLogEntity();
        BeanCopyUtil.copy(monitorLog, entity);
        Integer count = monitorLogDao.insert(entity);

        if (count > 0) {
            //发送日志
        }
        return count > 0;
    }

    @Override
    public void publishPerformance(MonitorPerformanceDTO monitorPerformance) {
        String config = configApi.getConfig("monitor.performance.open");
        if ("true".equals(config)) {
            executorService.submit(() -> {
                //添加性能提示
                Boolean res = addPerformance(monitorPerformance);

                if (!res) {
                    log.warn("发布性能提示失败！ {}", JSON.toJSONString(monitorPerformance));
                }

            });
        }
    }

    @Override
    public Boolean addPerformance(MonitorPerformanceDTO monitorPerformance) {
        MonitorPerformanceEntity entity = new MonitorPerformanceEntity();
        BeanCopyUtil.copy(monitorPerformance, entity);
        Integer count = monitorPerformanceDao.insert(entity);
        return count > 0;
    }

    /**
     * 监控线程池策略
     */
    public static class MonitorExecutorPolicy implements RejectedExecutionHandler {

        public MonitorExecutorPolicy() {
        }

        /**
         * 发出警告
         *
         * @param r
         * @param e
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            log.warn("Task {}, rejected from {}", r.toString(), e.toString());
        }
    }

    /**
     * 每日QPS监测
     *
     * @param dateTime
     * @return
     */
    @Override
    public List<Map<String,Long>> getDayQPSCount(Date dateTime){
        List<Map<String, Long>> countList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 24; i++) {
            Map<String, Long> map = new HashMap<>();
            cal.setTime(dateTime);
            cal.set(Calendar.HOUR, i);
            Long value = redisCache.getLong(MonitorCacheUtil.getCacheKey(cal.getTime()));
            if (value == null ){
                value = 0L;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
            map.put(sdf.format(cal.getTime()),value);
            countList.add(map);
        }
        return countList;
    }
}