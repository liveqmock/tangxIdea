package com.topaiebiz.system.config.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.basic.api.ConfigApi;
import com.topaiebiz.system.config.dao.ConfigDao;
import com.topaiebiz.system.config.entity.ConfigEntity;
import com.topaiebiz.system.config.exception.ConfigExceptionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfigApiImpl implements ConfigApi {
    //分隔符
    private String SEPARATOR_COMMA = ",";

    @Autowired
    ConfigDao configDao;

    @Override
    public String getConfig(String configCode) {
        if (StringUtils.isEmpty(configCode)) {
            throw new GlobalException(ConfigExceptionEnum.CODE_NOT_NULL);
        }
        ConfigEntity configEntity = configDao.selectConfig(configCode);
        if (configEntity == null) {
            return null;
        }
        return configEntity.getConfigValue();
    }

    @Override
    public Integer deleteConfig(String code) {
        EntityWrapper<ConfigEntity> condWrapper = new EntityWrapper<>();
        condWrapper.eq("configCode", code);
        return configDao.delete(condWrapper);
    }

    @Override
    public Boolean insertConfig(String code, String value) {
        ConfigEntity entity = new ConfigEntity();
        entity.setConfigCode(code);
        entity.setConfigValue(value);
        return configDao.insert(entity) > 0;
    }

    @Override
    public List<Long> convertValueToList(String code) {
        String configValue = getConfig(code);
        if (StringUtils.isEmpty(configValue)) {
            return null;
        }
        List<Long> list = Arrays.asList(configValue.split(SEPARATOR_COMMA)).stream().map(s -> Long.valueOf(s.trim())).collect(Collectors.toList());
        return list;
    }
}
