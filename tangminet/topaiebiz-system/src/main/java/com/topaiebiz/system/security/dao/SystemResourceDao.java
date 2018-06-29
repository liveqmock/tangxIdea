package com.topaiebiz.system.security.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.system.security.dto.SecurityResourceDto;
import com.topaiebiz.system.security.entity.SystemResourceEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SystemResourceDao extends BaseDao<SystemResourceEntity> {
}
