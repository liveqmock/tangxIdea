package com.topaiebiz.dec.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.topaiebiz.dec.entity.TemplateModuleEntity;

import java.util.List;

/**
 * <p>
  * 装修模板模块表 Mapper 接口
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
public interface TemplateModuleDao extends BaseMapper<TemplateModuleEntity> {

    //Integer deleteTemplateModule(Long temeplateModuleId);

    void insertBatch(List<TemplateModuleEntity> templateModuleEntitys);
}