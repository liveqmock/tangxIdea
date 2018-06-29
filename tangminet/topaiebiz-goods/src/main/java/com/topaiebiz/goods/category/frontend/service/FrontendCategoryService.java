package com.topaiebiz.goods.category.frontend.service;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryDto;
import com.topaiebiz.goods.category.backend.dto.BackendCategorysDto;
import com.topaiebiz.goods.category.frontend.dto.FrontBackCategoryDto;
import com.topaiebiz.goods.category.frontend.dto.FrontendCategoryDto;
import com.topaiebiz.goods.category.frontend.entity.FrontBackCategoryEntity;
import com.topaiebiz.goods.category.frontend.entity.FrontendCategoryEntity;

/**
 * Description 商品前台类目接口
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年8月25日 下午3:14:26
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

public interface FrontendCategoryService {

    /**
     * Description 平台端商品前台类目一,二，三级类目列表
     * <p>
     * Author Hedda
     *
     * @param frontendCategoryDto 商品前台类目dto
     * @return
     * @throws GlobalException
     */
    List<FrontendCategoryDto> getFrontendCategoryList(FrontendCategoryDto frontendCategoryDto) throws GlobalException;

    /**
     * Description 平台端商品前台类目添加
     * <p>
     * Author Hedda
     *
     * @param frontendCategoryDto 商品前台类目
     * @return
     */
    Integer saveFrontendCategory(FrontendCategoryDto frontendCategoryDto) throws GlobalException;

    /**
     * Description 平台端商品前台类目修改
     * <p>
     * Author Hedda
     *
     * @param frontendCategoryDto 商品前台类目dto
     * @return
     * @throws GlobalException
     */
    Integer modifyFrontendCategory(FrontendCategoryDto frontendCategoryDto) throws GlobalException;

    /**
     * Description 平台端商品前台类目逻辑删除
     * <p>
     * Author Hedda
     *
     * @param id 商品前台类目id
     */
    Integer removeFrontendCategory(Long id) throws GlobalException;

    /**
     * Description 根据id查询商品前台类目
     * <p>
     * Author Hedda
     *
     * @param id 商品前台类目id
     * @return
     * @throws GlobalException
     */
    FrontendCategoryDto getFrontendCategoryById(Long id) throws GlobalException;

    /**
     * Description 根据id查询前台类目添加图片
     * <p>
     * Author Hedda
     *
     * @param frontendCategoryDto 商品前台类目
     * @return
     * @throws GlobalException
     */
    Integer addFrontendCategoryById(FrontendCategoryDto frontendCategoryDto) throws GlobalException;

    /**
     * Description 根据id查询商品后台类目
     * <p>
     * Author Hedda
     *
     * @param frontId
     * @return
     * @throws GlobalException
     */
    List<BackendCategorysDto> getBackendCategoryList(Long frontId) throws GlobalException;

    /**
     * Description 给三级类目删除图片
     * <p>
     * Author Hedda
     *
     * @param id 商品后台类目id
     * @return
     * @throws GlobalException
     */
    Integer cancelFrontendCategoryImage(Long id) throws GlobalException;

    /**
     * Description app端商家前台类目一,二，三级类目列表
     * <p>
     * Author Hedda
     *
     * @param frontendCategoryDto 商品前台类目dto
     * @return
     * @throws GlobalException
     */
    List<FrontendCategoryDto> getMerchantAppFrontendCategoryList(FrontendCategoryDto frontendCategoryDto) throws GlobalException;

}
