package com.topaiebiz.system.config.service;


import com.baomidou.mybatisplus.service.IService;
import com.topaiebiz.system.config.dto.ConfigDto;
import com.topaiebiz.system.config.entity.ConfigEntity;

/**
 * <p>
 * 系统公共配置表 服务类
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */

public interface ConfigService extends IService<ConfigEntity> {

    ConfigDto getConfigDto(String configCode);

    Boolean editConfig(ConfigDto config);

}
