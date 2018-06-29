package com.topaiebiz.monitor.api;

import com.topaiebiz.monitor.dto.MonitorPerformanceDTO;

/***
 * @author yfeng
 * @date 2018-05-30 10:01
 */
public interface MonitorApi {

    void publishPerformance(MonitorPerformanceDTO monitorPerformanceDTO);

    void requestCountIncrease();
}