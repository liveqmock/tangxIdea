package com.topaiebiz.goods.category.backend.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryAdd;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryDto;
import com.topaiebiz.goods.category.backend.dto.BackendMerchantCategoryDto;

import java.util.List;

/**
 * Description 商品后台类目接口
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年8月24日 下午4:46:51
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

public interface BackendCategoryService {

    /**
     * Description 平台商品后台类目一，二，三级类目列表
     * <p>
     * Author Hedda
     *
     * @param backendCategoryDto 商品后台类目dto
     * @return
     * @throws GlobalException
     */
    List<BackendCategoryDto> getListLevelBackendCategory(BackendCategoryDto backendCategoryDto) throws GlobalException;

    /**
     * Description 平台商品后台类目添加
     * <p>
     * Author Hedda
     *
     * @param backendCategoryDto 商品后台类目
     * @return
     * @throws GlobalException
     */
    Integer saveBackendCategory(BackendCategoryDto backendCategoryDto) throws GlobalException;

    /**
     * Description 平台商品后台类目修改
     * <p>
     * Author Hedda
     *
     * @param backendCategoryDto 商品后台类目
     * @return
     */
    Integer modifyBackendCategory(BackendCategoryDto backendCategoryDto) throws GlobalException;

    /**
     * Description 平台商品类目根据id进行查询
     * <p>
     * Author Hedda
     *
     * @param id 商品
     * @return
     * @throws GlobalException
     */
    BackendCategoryDto findBackendCategoryById(Long id) throws GlobalException;

    /**
     * Description 商品后台类目逻辑删除
     * <p>
     * Author Hedda
     *
     * @param id 商品后台类目id
     * @return
     */
    Integer removeBackendCategory(Long id) throws GlobalException;

    /**
     * Description 查看平台商品后台第三级类目
     * <p>
     * Author Hedda
     *
     * @return
     */
    List<BackendCategoryDto> getThreeBackendCategoryList();

    /**
     * Description 商家 商品后台类目添加
     * <p>
     * Author Hedda
     *
     * @param backendCategory 商品后台类目dto
     * @return
     * @throws GlobalException
     */
    Integer saveMerchantBackendCategory(BackendCategoryDto backendCategory) throws GlobalException;

    /**
     * Description 商家 查看商品后台第三级类目
     * <p>
     * Author Hedda
     *
     * @return
     * @throws GlobalException
     */
    List<BackendCategoryDto> getMerchantThreeBackendCategoryList();

    /**
     * Description 根据商家 id删除对应的商家类目
     * <p>
     * Author Hedda
     *
     * @param storeId 商家id
     * @return
     */
    Integer removeBackendCategoryByStoreId(Long storeId);

    /**
     * Description： 查询商家类目
     * <p>
     * Author Aaron.Xue
     *
     * @param backendCategoryDto
     * @return
     */
    List<BackendCategoryDto> getMerchantCategory(BackendCategoryDto backendCategoryDto);

    /**
     * Description： 查看商家三级类目
     * <p>
     * Author Aaron.Xue
     *
     * @return
     */
    List<BackendMerchantCategoryDto> getMerchantThreeCategory();

    /**
     * Description 根据商家id添加商品后台类目
     *
     * Author Hedda
     *
     * @param backendCategoryAdd
     *            商家后台类目
     * @return
     * @throws GlobalException
     */
    Integer addBackendCategoryDtoByBelongStore(BackendCategoryAdd backendCategoryAdd);

    /**
     * Description 商品后台类目逻辑删除
     *
     * Author Hedda
     *
     * @param categoryId
     *            商品后台类目id
     * @return
     */
    Integer removeBackendMerchantCategoryByStoreId(Long[] categoryId);

    /**
     * Description  装修平台 查看商品后台第三级类目
     * <p>
     * Author Hedda
     * @param pagePO
     * @return
     */
    PageInfo<BackendCategoryDto> getDecorationThreeBackendCategory(PagePO pagePO);

    /**
     * Description  装修商家 查看商品后台第三级类目
     * <p>
     * Author Hedda*
     * @param pagePO
     * @return
     * @throws GlobalException
     */
    PageInfo<BackendCategoryDto> getDecorationMerchantThreeBackendCategory(PagePO pagePO);

    /**
     * Description 修改商家类目状态为审核通过
     * <p>
     * Author Hedda
     *
     * @param backendCategoryAdd 类目
     * @return
     * @throws GlobalException
     */
    boolean modifyBackendMerchanntStatus(BackendCategoryAdd backendCategoryAdd);
}
