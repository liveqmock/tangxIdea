package com.topaiebiz.goods.category.backend.service;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryAttrDto;
import com.topaiebiz.goods.category.backend.dto.ItemBackendCategoryAttrDto;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryAttrEntity;

import java.util.List;

/**
 * Created by dell on 2018/1/10.
 */
public interface BackendCategoryAttrService {

    /**
     * Description 商品三级类目中所对应的规格属性
     * <p>
     * Author Hedda
     *
     * @param itemBackendCategoryAttrDto 商品后台类目id
     * @return
     * @throws GlobalException
     */
    List<BackendCategoryAttrEntity> getListBackendCategoryAttr(ItemBackendCategoryAttrDto itemBackendCategoryAttrDto) throws GlobalException;

    /**
     * Description 商品后台类目中规格属性修改
     * <p>
     * Author Hedda
     *
     * @param backendCategoryAttrDto 商品后台类目规格属性
     * @return
     */
    Integer modifyBackendCategoryAttr(BackendCategoryAttrDto backendCategoryAttrDto) throws GlobalException;

    /**
     * Description 商品后台类目中规格属性逻辑删除
     * <p>
     * Author Hedda
     *
     * @param id 商品后台类目属性id
     * @return
     */
    Integer removeBackendCategoryAttr(Long id) throws GlobalException;

    /**
     * Description 商品后台类目中规格属性添加
     * <p>
     * Author Hedda
     *
     * @param backendCategoryAttr
     * @return
     */
    Integer saveBackendCategoryAttr(BackendCategoryAttrEntity backendCategoryAttr) throws GlobalException;

    /**
     * Description 商品类目规格属性根据id进行查询
     * <p>
     * Author Hedda
     *
     * @param id 商品后台类目属性id
     * @return
     * @throws GlobalException
     */
    BackendCategoryAttrDto findBackendCategoryAttrById(Long id) throws GlobalException;

    /**
     * Description 对商品后台类目属性进行升降序操作
     * <p>
     * Author Hedda
     *
     * @param backendCategoryAttrDto
     * @return
     */
    Integer modifyBackendCategoryAttrBySortNo(List<BackendCategoryAttrDto> backendCategoryAttrDto);

    /**
     * Description 平台端商品模板类目中规格属性添加
     * <p>
     * Author Hedda
     *
     * @param backendCategoryAttrDto 商品后台类目规格属性
     * @return
     * @throws GlobalException
     */
    Integer saveGoodsSpuBackendCategoryAttr(BackendCategoryAttrDto backendCategoryAttrDto);

    /**
     * Description 商品模板三级类目中所对应的规格属性
     * <p>
     * Author Hedda
     *
     * @param itemBackendCategoryAttrDto 商品后台类目id
     * @return
     * @throws GlobalException
     */
    List<BackendCategoryAttrEntity> getSpuListBackendCategoryAttr(ItemBackendCategoryAttrDto itemBackendCategoryAttrDto);
}
