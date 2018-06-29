package com.topaiebiz.dec.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.dec.dao.TemplateInfoDao;
import com.topaiebiz.dec.dto.TemplateInfoDto;
import com.topaiebiz.dec.entity.TemplateInfoEntity;
import com.topaiebiz.dec.exception.DecExceptionEnum;
import com.topaiebiz.dec.service.TemplateInfoService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 装修模板信息表 服务实现类
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@Service
public class TemplateInfoServiceImpl extends ServiceImpl<TemplateInfoDao, TemplateInfoEntity> implements TemplateInfoService {

    @Autowired
    private TemplateInfoDao templateInfoDao;


    @Override
    public void saveTemplateInfoDto(TemplateInfoDto templateInfoDto) {
        TemplateInfoEntity templateInfoEntity = new TemplateInfoEntity();
        BeanUtils.copyProperties(templateInfoDto, templateInfoEntity);
        templateInfoEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        templateInfoDao.insert(templateInfoEntity);
    }

    @Override
    public void modifyTemplateInfo(TemplateInfoDto temeplateInfoDto) {
        if (StringUtils.isEmpty(temeplateInfoDto.getId())) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        TemplateInfoEntity templateInfoEntity = new TemplateInfoEntity();
        templateInfoEntity.cleanInit();
        templateInfoEntity.setId(temeplateInfoDto.getId());
        templateInfoEntity.setIsUsed(temeplateInfoDto.getIsUsed());
        templateInfoEntity.setStoreId(temeplateInfoDto.getStoreId());
        templateInfoEntity.setTemplateName(temeplateInfoDto.getTemplateName());
        templateInfoEntity.setTemplateId(temeplateInfoDto.getTemplateId());
        templateInfoEntity.setLastModifiedTime(new Date());
        templateInfoEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        templateInfoDao.updateById(templateInfoEntity);
    }

    @Override
    public Integer deleteTemplateInfo(Long id) {
        if (StringUtils.isEmpty(id)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        TemplateInfoEntity templateInfoEntity = new TemplateInfoEntity();
        templateInfoEntity.cleanInit();
        templateInfoEntity.setId(id);
        templateInfoEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        templateInfoEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        templateInfoEntity.setLastModifiedTime(new Date());
        return templateInfoDao.updateById(templateInfoEntity);
    }

    @Override
    public List<TemplateInfoDto> getTemplateInfoDtos() {
        EntityWrapper<TemplateInfoEntity> condition = new EntityWrapper();
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        // condition.orderBy("sortNo", false);
        List<TemplateInfoEntity> templateInfoEntityList = templateInfoDao.selectList(condition);
        List<TemplateInfoDto> templateInfoDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(templateInfoEntityList)) {
            for (TemplateInfoEntity templateInfoEntity : templateInfoEntityList) {
                TemplateInfoDto templateInfoDto = new TemplateInfoDto();
                BeanUtils.copyProperties(templateInfoEntity, templateInfoDto);
                templateInfoDtoList.add(templateInfoDto);
            }

        }
        return templateInfoDtoList;
    }

    @Override
    public TemplateInfoDto getStoreTemplate(Long storeId) {
        if (null == storeId) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        TemplateInfoDto templateInfoDto = new TemplateInfoDto();
        EntityWrapper<TemplateInfoEntity> condition  = new EntityWrapper<>();
        condition.eq("storeId",storeId);
        condition.eq("deletedFlag",Constants.DeletedFlag.DELETED_NO);
        List<TemplateInfoEntity> entities = templateInfoDao.selectList(condition);
        if(!CollectionUtils.isEmpty(entities)){
           BeanUtils.copyProperties(entities.get(0),templateInfoDto);
        }
        return templateInfoDto;
    }
}
