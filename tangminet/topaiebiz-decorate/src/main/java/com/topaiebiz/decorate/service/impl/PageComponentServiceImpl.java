package com.topaiebiz.decorate.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.decorate.dao.ContentDao;
import com.topaiebiz.decorate.dao.PageComponentDao;
import com.topaiebiz.decorate.dto.ComponentDto;
import com.topaiebiz.decorate.dto.PageComponentDto;
import com.topaiebiz.decorate.entity.ComponentContentEntity;
import com.topaiebiz.decorate.entity.PageComponentEntity;
import com.topaiebiz.decorate.exception.DecorateExcepionEnum;
import com.topaiebiz.decorate.service.PageComponentService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PageComponentServiceImpl extends ServiceImpl<PageComponentDao, PageComponentEntity> implements PageComponentService {

    @Autowired
    private PageComponentDao pageComponentDao;

    @Autowired
    private ContentDao contentDao;

    @Override
    @Transactional
    public void create(PageComponentDto pageComponentDto) {
        Long pageId = pageComponentDto.getPageId();
        List<ComponentDto> componentDtos = pageComponentDto.getComponentDtos();
        if (null == pageId) {
            throw new GlobalException(DecorateExcepionEnum.PAGE_ID_NOT_NULL);
        }
        if (CollectionUtils.isEmpty(componentDtos)) {
            throw new GlobalException(DecorateExcepionEnum.COMPONENT_NOT_NULL);
        }
        List<PageComponentEntity> entities = new ArrayList<>();
        for (ComponentDto componentDto : componentDtos) {
            PageComponentEntity entity = new PageComponentEntity();
            entity.setPageId(pageId);
            BeanUtils.copyProperties(componentDto, entity);
            entity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
            entity.setCreatedTime(new Date());
            entities.add(entity);
        }
        //批量插入
        pageComponentDao.insertBatch(entities);
    }

    @Override
    @Transactional
    public void remove(Long id) {
        if (null == id) {
            throw new GlobalException(DecorateExcepionEnum.ID_NOT_NULL);
        }
        //删除页面上的组件
        PageComponentEntity componentEntity = new PageComponentEntity();
        componentEntity.cleanInit();
        componentEntity.setId(id);
        componentEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        componentEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        componentEntity.setLastModifiedTime(new Date());
        pageComponentDao.updateById(componentEntity);
        //删除组件的内容,一个页面中可能有多个相同组件
        EntityWrapper<ComponentContentEntity> contentCondition = new EntityWrapper<>();
        contentCondition.eq("componentId", id);
        contentCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        ComponentContentEntity contentEntity = new ComponentContentEntity();
        contentEntity.cleanInit();
        contentEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        contentEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        componentEntity.setLastModifiedTime(new Date());
        contentDao.update(contentEntity, contentCondition);
    }

    @Override
    @Transactional
    public void modify(PageComponentDto pageComponentDto) {
        Long pageId = pageComponentDto.getPageId();
        if (null == pageId) {
            throw new GlobalException(DecorateExcepionEnum.PAGE_ID_NOT_NULL);
        }
        //先删除该页面下的所有组件
        EntityWrapper<PageComponentEntity> componentCondition = new EntityWrapper<>();
        componentCondition.eq("pageId", pageId);
        componentCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);

        //未删除前或许取所有的组件id
        List<PageComponentEntity> componentEntities = pageComponentDao.selectList(componentCondition);
        List<Long> componentIds = componentEntities.stream().map(PageComponentEntity::getId).collect(Collectors.toList());
        PageComponentEntity componentEntity = new PageComponentEntity();
        componentEntity.cleanInit();
        componentEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        componentEntity.setLastModifiedTime(new Date());
        componentEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        pageComponentDao.update(componentEntity, componentCondition);

        //删除所有组件的内容
        if (CollectionUtils.isNotEmpty(componentIds)) {
            EntityWrapper<ComponentContentEntity> contentCondition = new EntityWrapper<>();
            contentCondition.in("componentId", componentIds);
            contentCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            ComponentContentEntity contentEntity = new ComponentContentEntity();
            contentEntity.cleanInit();
            contentEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
            contentEntity.setLastModifiedTime(new Date());
            contentEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            contentDao.update(contentEntity, contentCondition);
        }
        //重新保存一份
        create(pageComponentDto);
    }
}
