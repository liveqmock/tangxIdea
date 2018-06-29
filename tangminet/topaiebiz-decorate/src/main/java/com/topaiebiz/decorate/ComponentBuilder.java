package com.topaiebiz.decorate;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.decorate.dao.ContentDao;
import com.topaiebiz.decorate.dto.ComponentContentDto;
import com.topaiebiz.decorate.entity.ComponentContentEntity;
import com.topaiebiz.decorate.transformer.ComponentTransformer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ComponentBuilder implements InitializingBean {
    @Autowired
    private List<ComponentTransformer> componentTransformers;

    @Autowired
    private ContentDao contentDao;

    private Map<String, ComponentTransformer> componentTransformerMap;

    @Override
    public void afterPropertiesSet() throws Exception {
        componentTransformerMap = new HashMap<>();
        for (ComponentTransformer transformer : componentTransformers) {
            componentTransformerMap.put(transformer.componentType(), transformer);
        }
    }

    public Object buildComponent(ComponentContentDto componentContentDto) {
        ComponentTransformer transformer = componentTransformerMap.get(componentContentDto.getType());
        Object component = transformer.transform(componentContentDto.getContent());
        transformer.validate(component);
        return component;
    }


    public ComponentContentDto loadDataFromDB(Long id) {
        EntityWrapper<ComponentContentEntity> contentCondition = new EntityWrapper<>();
        contentCondition.eq("componentId", id);
        contentCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        ComponentContentEntity contentEntity = contentDao.selectList(contentCondition).get(0);
        ComponentTransformer transformer = componentTransformerMap.get(contentEntity.getType());
        ComponentContentDto componentContentDto = new ComponentContentDto();
        BeanUtils.copyProperties(contentEntity, componentContentDto);
        componentContentDto.setContent(transformer.getPageContent(contentEntity.getContent()));
        return componentContentDto;
    }

    public ComponentContentDto makeUpContent(ComponentContentEntity componentContentEntity) {
        ComponentTransformer transformer = componentTransformerMap.get(componentContentEntity.getType());
        ComponentContentDto componentContentDto = new ComponentContentDto();
        BeanUtils.copyProperties(componentContentEntity, componentContentDto);
        componentContentDto.setContent(transformer.getPageContent(componentContentEntity.getContent()));
        return componentContentDto;
    }

    public void dealItem(ComponentContentDto componentContentDto) {
        ComponentTransformer transformer = componentTransformerMap.get(componentContentDto.getType());
        transformer.dealItem(componentContentDto);
    }
}
