package com.topaiebiz.system.config.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.system.config.entity.ConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
  * 系统公共配置表 Mapper 接口
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@Mapper
public interface ConfigDao extends BaseDao<ConfigEntity>  {
    /**
     * 根据code值查询config
     * @param configCode
     * @return
     */
    ConfigEntity selectConfig(@Param(value = "configCode") String configCode);

}