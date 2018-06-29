package com.topaiebiz.dec.service;

import com.baomidou.mybatisplus.service.IService;
import com.topaiebiz.dec.dto.TemplateModuleDto;
import com.topaiebiz.dec.entity.TemplateModuleEntity;

import java.util.List;

/**
 * <p>
 * 装修模板模块表 服务类
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
public interface TemplateModuleService extends IService<TemplateModuleEntity> {

    void saveTemplateModuleDto(List<TemplateModuleDto> templateModuleDtos);

    List<TemplateModuleDto> getTemplateModuleDto(Long infoId);

    Integer deleteById(Long id);

    Integer updateTemplateModule(TemplateModuleDto templateModuleDto);

    void refreshCacheByModuleId(Long moduleId);
}
