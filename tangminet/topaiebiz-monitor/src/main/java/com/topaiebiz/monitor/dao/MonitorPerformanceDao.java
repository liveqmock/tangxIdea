package com.topaiebiz.monitor.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.monitor.entity.MonitorPerformanceEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MonitorPerformanceDao extends BaseDao<MonitorPerformanceEntity> {
}
