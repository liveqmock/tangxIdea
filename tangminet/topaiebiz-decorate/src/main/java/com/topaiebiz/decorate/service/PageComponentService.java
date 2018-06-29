package com.topaiebiz.decorate.service;

import com.baomidou.mybatisplus.service.IService;
import com.topaiebiz.decorate.dto.PageComponentDto;
import com.topaiebiz.decorate.entity.PageComponentEntity;

import java.util.List;

public interface PageComponentService extends IService<PageComponentEntity> {

    /**
     * 往页面上添加一个组件
     *
     * @param pageComponentDto
     * @return
     */
    void create(PageComponentDto pageComponentDto);

    /**
     * 删除页面上一个组件
     *
     * @param id
     */
    void remove(Long id);

    /**
     * 修改页面组件
     *
     * @param pageComponentDto
     */
    void modify(PageComponentDto pageComponentDto);
}
