package com.topaiebiz.decorate.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.topaiebiz.decorate.entity.ComponentItemEntity;

import java.util.List;

public interface ComponentItemDao extends BaseMapper<ComponentItemEntity> {

    void insertBatch(List<ComponentItemEntity> componentItemEntities);
}
