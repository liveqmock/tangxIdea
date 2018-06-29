package com.topaiebiz.goods.service;

import com.topaiebiz.goods.dto.*;

import java.util.List;

/**
 * @description: 后台类目服务：操作正式表和编辑表,用来替换 BackendCategoryService
 * @author: Jeff Chen
 * @date: created in 上午10:29 2018/5/18
 */
public interface BackendCategoryNewService {

    /**
     * 保存类目编辑实体
     * @param backendCategoryDTO
     * @return
     */
    boolean saveBackendCategoryEdit(BackendCategoryDTO backendCategoryDTO);

    /**
     * 更新类目编辑实体
     * @param backendCategoryDTO
     * @return
     */
    boolean updateBackendCategoryEdit(BackendCategoryDTO backendCategoryDTO);

    /**
     * 类目详情
     * @param categoryId
     * @return
     */
    BackendCategoryDTO getCategoryEditById(Long categoryId);

    /**
     * 删除
     * @param backendCategoryDTO
     * @return
     */
    boolean deleteCategoryEditById(BackendCategoryDTO backendCategoryDTO);

    /**
     * 同级移动类目编实体
     * @param backendCategoryDTO
     * @return
     */
    boolean siblingMoveCategoryEdit(BackendCategoryDTO backendCategoryDTO);

    /**
     * 自由移动
     * @param backendCategoryDTO
     * @return
     */
    boolean freeMoveCategoryEdit(BackendCategoryDTO backendCategoryDTO);

    /**
     * 获取指定类目的子类目编辑树
     * @param parentId
     * @return
     */
    List<CategoryNodeDTO> getChildrenByCategoryEdit(Long parentId);

    /**
     * 类目编辑实体概况
     * @param categoryId
     * @return
     */
    CategoryProfileDTO profileEdit(Long categoryId);
    /**
     * 获取指定类目的子类目正式树
     * @param parentId
     * @return
     */
    List<CategoryNodeDTO> getChildrenByCategoryFormal(Long parentId);

    /**
     * 类目正式实体概况
     * @param categoryId
     * @return
     */
    CategoryProfileDTO profileFormal(Long categoryId);

    /**
     * 更新关联类目的同步状态（向上递归）
     * @param categoryId
     * @return
     */
    boolean updateRelatedCategorySyncStatus(Long categoryId,Integer syncStatus);

    /**
     * 品牌关联类目编辑列表
     * @param categoryIdList
     * @return
     */
    List<String> brandRelateCategoryEdit(List<Long> categoryIdList);

    /**
     * 品牌关联类目正式列表
     * @param categoryIdList
     * @return
     */
    List<String> brandRelateCategoryFormal(List<Long> categoryIdList);

    /**
     *查询指定父类目的儿子类目
     * @return
     */
    List<CategoryLevelNodeDTO> getSonCategoryEdit(Long parentId);

    /**
     * 同步类目树的顺序和层级
     * @return
     */
    SyncResultDTO syncCategoryTree();

    /**
     * 同步类目级别信息：类目名称和删除以及关联的属性和品牌
     * @param categoryId
     * @return
     */
    SyncResultDTO syncCategoryDataByCategory(Long categoryId);
}
