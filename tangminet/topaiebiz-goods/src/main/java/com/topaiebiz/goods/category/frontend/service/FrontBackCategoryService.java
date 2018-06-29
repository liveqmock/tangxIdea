package com.topaiebiz.goods.category.frontend.service;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.category.frontend.dto.FrontBackCategoryDto;
import com.topaiebiz.goods.category.frontend.entity.FrontBackCategoryEntity;

/**
 * Created by dell on 2018/1/10.
 */
public interface FrontBackCategoryService {

    /**
     * Description 绑定前后台类目
     *
     * Author Hedda
     *
     * @param frontBackCategoryDto
     *            绑定桥后台类目信息
     * @return
     * @throws GlobalException
     */
    Integer saveFrontBackCategory(FrontBackCategoryDto frontBackCategoryDto) throws GlobalException;

    /**
     * Description 删除绑定前后台信息
     *
     * Author Hedda
     *
     * @param id
     *            商品前后台绑定id
     */
    Integer removeFrontBackCategory(Long id) throws GlobalException;

}
