package com.topaiebiz.decorate.service;

import com.topaiebiz.decorate.dto.ComponentContentDto;
import com.topaiebiz.decorate.dto.PageComponentDto;

/**
 * c端 service
 *
 * @author huzhenjia
 * @since 2018/03/30
 */
public interface CTerminalService {

    /**
     * 获取页面组件
     *
     * @param suffixUrl
     * @return
     */
    PageComponentDto getComponent(String suffixUrl);

    /**
     * 根据组件id获取组件内容
     *
     * @param id
     * @return
     */
    ComponentContentDto getContent(Long id);

    /**
     * 装载内容缓存
     *
     * @param id
     * @return
     */
    ComponentContentDto loadContentCache(Long id);
}
