package com.topaiebiz.dec.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.contants.Constants;


import com.nebulapaas.web.exception.GlobalException;

import com.topaiebiz.dec.dao.ModuleInfoDao;
import com.topaiebiz.dec.dto.ModuleDetailDto;
import com.topaiebiz.dec.dto.ModuleInfoDto;
import com.topaiebiz.dec.entity.ModuleInfoEntity;
import com.topaiebiz.dec.exception.DecExceptionEnum;
import com.topaiebiz.dec.service.MQProducerService;
import com.topaiebiz.dec.service.ModuleInfoService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p>
 * 模块信息详情表
 * 服务实现类
 * </p>
 *
 * @author hzj
 * @since 2018-01-08
 */
@Service
public class ModuleInfoServiceImpl extends ServiceImpl<ModuleInfoDao, ModuleInfoEntity> implements ModuleInfoService {
    @Autowired
    private ModuleInfoDao moduleInfoDao;


    @Autowired
    private MQProducerService mqProducerService;


    @Override
    public void saveModuleInfoDto(List<ModuleDetailDto> moduleDetailDtos) {
        if (CollectionUtils.isEmpty(moduleDetailDtos)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        List<Long> moduleIds = new ArrayList<>();
        for (ModuleDetailDto moduleDetailDto : moduleDetailDtos) {
            ModuleInfoEntity moduleInfoEntity = new ModuleInfoEntity();
            BeanUtils.copyProperties(moduleDetailDto, moduleInfoEntity);
            moduleIds.add(moduleDetailDto.getModuleId());
            moduleInfoEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
            moduleInfoDao.insert(moduleInfoEntity);
        }
        mqProducerService.produceMQByModuleIds(moduleIds);
    }

    @Override
    public void modifyModuleInfo(List<ModuleDetailDto> moduleDetailDtos) {
        if (CollectionUtils.isEmpty(moduleDetailDtos)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        List<Long> moduleIds = new ArrayList<>();
        for (ModuleDetailDto moduleDetailDto : moduleDetailDtos) {
            ModuleInfoEntity moduleInfoEntity = new ModuleInfoEntity();
            moduleInfoEntity.cleanInit();
            BeanUtils.copyProperties(moduleDetailDto, moduleInfoEntity);
            moduleIds.add(moduleDetailDto.getModuleId());
            moduleInfoEntity.setLastModifiedTime(new Date());
            moduleInfoEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            moduleInfoDao.updateById(moduleInfoEntity);
        }


        //发送修改消息 更新redis缓存
        mqProducerService.produceMQByModuleIds(moduleIds);
    }

    @Override
    public Integer deleteModuleInfo(Long moduleInfoDtoId) {
        if (StringUtils.isEmpty(moduleInfoDtoId)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        ModuleInfoEntity moduleInfoEntity = new ModuleInfoEntity();
        moduleInfoEntity.cleanInit();
        moduleInfoEntity.setId(moduleInfoDtoId);
        moduleInfoEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        moduleInfoEntity.setVersion(moduleInfoEntity.getVersion());
        moduleInfoEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        moduleInfoEntity.setLastModifiedTime(new Date());


        //更新redis缓存
        ModuleInfoEntity entity = moduleInfoDao.selectById(moduleInfoDtoId);
        mqProducerService.produceMQByModuleId(entity.getModuleId());
        return moduleInfoDao.updateById(moduleInfoEntity);
    }

    @Override
    public List<ModuleInfoDto> getByModuleIds(List<Long> moduleIds) {
        if (CollectionUtils.isEmpty(moduleIds)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        List<ModuleInfoDto> moduleInfoDtoList = new ArrayList<>();
        EntityWrapper<ModuleInfoEntity> condition = new EntityWrapper();
        condition.in("moduleId", moduleIds);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.orderBy("sortNo", true);
        List<ModuleInfoEntity> moduleInfoEntityList = moduleInfoDao.selectList(condition);
        for (Long moduleId : moduleIds) {
            List<ModuleDetailDto> moduleDetailDtos = new ArrayList<>();
            for (ModuleInfoEntity entity : moduleInfoEntityList) {
                if (entity.getModuleId().equals(moduleId)) {
                    ModuleDetailDto moduleDetailDto = new ModuleDetailDto();
                    BeanUtils.copyProperties(entity, moduleDetailDto);
                    moduleDetailDtos.add(moduleDetailDto);
                }
            }
            ModuleInfoDto moduleInfoDto = new ModuleInfoDto();
            moduleInfoDto.setModuleId(moduleId);
            moduleInfoDto.setModuleDetailDtoList(moduleDetailDtos);
            moduleInfoDtoList.add(moduleInfoDto);
        }
        return moduleInfoDtoList;
    }

}
