package com.topaiebiz.decorate.service;

import com.baomidou.mybatisplus.service.IService;
import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.decorate.dto.PageComponentDto;
import com.topaiebiz.decorate.dto.PageDetailDto;
import com.topaiebiz.decorate.entity.ComponentContentEntity;
import com.topaiebiz.decorate.entity.PageComponentEntity;
import com.topaiebiz.decorate.entity.PageDetailEntity;

import java.util.List;

/**
 * 页面管理Service
 *
 * @author huzhenjia
 * @Since 2018/03/26
 */
public interface PageManagementService extends IService<PageDetailEntity> {


    /**
     * 添加页面
     *
     * @param pageDetailDto
     * @return primary key
     */
    Long newPage(PageDetailDto pageDetailDto);

    /**
     * 修改发布时间
     *
     * @param pageDetailDto
     */
    void modifyPublishTime(PageDetailDto pageDetailDto);

    /**
     * 分页查询
     *
     * @param pageDetailDto
     * @return PageInfo
     */
    PageInfo<PageDetailEntity> pagingQuery(PageDetailDto pageDetailDto);

    /**
     * 删除某个页面
     *
     * @param id
     */
    void remove(Long id);


    /**
     * 复制某个页面数据
     *
     * @param id
     */
    void copy(Long id);

    /**
     * 预览
     *
     * @param id
     * @return 该id页面下所有的组件信息
     */
    PageComponentDto preview(Long id);

    /**
     * 发布
     *
     * @param id
     */
    void publish(Long id);

    /**
     * 下线
     *
     * @param id
     */
    void offline(Long id);

    /**
     * 设置缓存失效时间
     *
     * @param entities
     * @param pageType
     */
    void saveContentCache(List<ComponentContentEntity> entities, Byte pageType);

    void resetPublishTime(PageDetailEntity pageDetailEntity);
}
