package com.topaiebiz.dec.service;

import com.baomidou.mybatisplus.service.IService;
import com.topaiebiz.dec.dto.ModuleDetailDto;
import com.topaiebiz.dec.dto.ModuleInfoDto;
import com.topaiebiz.dec.entity.ModuleInfoEntity;

import java.util.List;


/**
 * <p>
 * 模块信息详情表
 服务类
 * </p>
 *
 * @author hzj
 * @since 2018-01-08
 */
public interface ModuleInfoService extends IService<ModuleInfoEntity> {

    void saveModuleInfoDto(List<ModuleDetailDto> moduleDetailDtos);

    void modifyModuleInfo(List<ModuleDetailDto> moduleDetailDtos);

    Integer deleteModuleInfo(Long moduleInfoDtoId);

    List<ModuleInfoDto> getByModuleIds(List<Long> moduleIds);
}
