package com.topaiebiz.monitor.service;

import com.topaiebiz.monitor.dto.MonitorLogDTO;
import com.topaiebiz.monitor.dto.MonitorPerformanceDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 监控日志服务
 */
public interface MonitorLogService {

    /**
     * 发布日志
     *
     * @param monitorLog
     */
    void publishLog(MonitorLogDTO monitorLog);

    /**
     * 新增日志
     *
     * @param monitorLog
     * @return
     */
    Boolean addLog(MonitorLogDTO monitorLog);

    /**
     * 发布性能提示
     *
     * @param monitorPerformance
     */
    void publishPerformance(MonitorPerformanceDTO monitorPerformance);

    /**
     * 新增性能提示记录
     *
     * @param monitorPerformance
     * @return
     */
    Boolean addPerformance(MonitorPerformanceDTO monitorPerformance);

    /**
     * 每日QPS监测
     *
     * @param dateTime
     * @return
     */
    List<Map<String,Long>> getDayQPSCount(Date dateTime);
}
