package com.topaiebiz.system.config.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.system.config.dao.ConfigDao;
import com.topaiebiz.system.config.dto.ConfigDto;
import com.topaiebiz.system.config.entity.ConfigEntity;
import com.topaiebiz.system.config.exception.ConfigExceptionEnum;
import com.topaiebiz.system.config.service.ConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 系统公共配置表 服务实现类
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigDao, ConfigEntity> implements ConfigService {

    @Autowired
    ConfigDao configDao;

    @Override
    public ConfigDto getConfigDto(String configCode) {
        if (StringUtils.isEmpty(configCode)) {
            throw new GlobalException(ConfigExceptionEnum.CODE_NOT_NULL);
        }
        ConfigEntity configEntity = configDao.selectConfig(configCode);
        ConfigDto configDto = new ConfigDto();
        BeanUtils.copyProperties(configEntity, configDto);
        return configDto;
    }

    @Override
    public Boolean editConfig(ConfigDto config) {
        ConfigEntity configEntity = new ConfigEntity();
        configEntity.cleanInit();
        BeanUtils.copyProperties(config, configEntity);
        Integer count = configDao.updateById(configEntity);
        return count > 0;
    }
}
