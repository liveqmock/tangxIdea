package com.topaiebiz.dec.service;

import com.baomidou.mybatisplus.service.IService;
import com.topaiebiz.dec.dto.TemplateInfoDto;
import com.topaiebiz.dec.entity.TemplateInfoEntity;

import java.util.List;

/**
 * <p>
 * 装修模板信息表 服务类
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
public interface TemplateInfoService extends IService<TemplateInfoEntity> {


    void saveTemplateInfoDto(TemplateInfoDto templateInfoDto);

    void modifyTemplateInfo(TemplateInfoDto templateInfoDto);

    Integer deleteTemplateInfo(Long id);

    List<TemplateInfoDto> getTemplateInfoDtos();

    TemplateInfoDto getStoreTemplate(Long storeId);

}
