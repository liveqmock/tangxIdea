package com.topaiebiz.decorate.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.topaiebiz.decorate.entity.ComponentContentEntity;

import java.util.List;

public interface ContentDao extends BaseMapper<ComponentContentEntity> {

    /**
     * 批量插入组件内容
     *
     * @param contentEntities
     */
    void insertBatch(List<ComponentContentEntity> contentEntities);
}
