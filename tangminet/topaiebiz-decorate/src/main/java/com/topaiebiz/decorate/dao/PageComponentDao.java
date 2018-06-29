package com.topaiebiz.decorate.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.topaiebiz.decorate.entity.PageComponentEntity;

import java.util.List;

public interface PageComponentDao extends BaseMapper<PageComponentEntity> {

    /**
     * 批量插入
     * @param entities
     */
    void insertBatch(List<PageComponentEntity> entities);
}
