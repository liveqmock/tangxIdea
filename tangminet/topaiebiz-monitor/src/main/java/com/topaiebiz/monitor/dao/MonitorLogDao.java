package com.topaiebiz.monitor.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.monitor.entity.MonitorLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MonitorLogDao extends BaseDao<MonitorLogEntity> {
}
