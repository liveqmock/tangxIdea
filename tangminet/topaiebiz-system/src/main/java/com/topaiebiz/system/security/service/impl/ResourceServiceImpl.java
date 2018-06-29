package com.topaiebiz.system.security.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.topaiebiz.system.dto.ResourceDto;
import com.topaiebiz.system.security.dao.ResourceCollectDao;
import com.topaiebiz.system.security.entity.ResourceCollectEntity;
import com.topaiebiz.system.security.service.ResourceService;
import com.topaiebiz.system.util.ResourceCollectUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceCollectDao resourceCollectDao;

    @Override
    @Transactional
    public void addResources() {
        List<ResourceDto> resourceDtos = ResourceCollectUtil.getResourceList();
        if(resourceDtos != null ){
            EntityWrapper<ResourceCollectEntity> condition = new EntityWrapper<>();
            resourceCollectDao.delete(condition);
            for(ResourceDto resourceDto : resourceDtos){
                ResourceCollectEntity entity = new ResourceCollectEntity();
                BeanUtils.copyProperties(resourceDto, entity);
                resourceCollectDao.insert(entity);
            }
        }
    }
}
