package com.topaiebiz.goods.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryDao;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryEntity;
import com.topaiebiz.goods.constant.GoodsConstants;
import com.topaiebiz.goods.dao.*;
import com.topaiebiz.goods.dto.*;
import com.topaiebiz.goods.entity.*;
import com.topaiebiz.goods.enums.GoodsExceptionEnum;
import com.topaiebiz.goods.enums.SyncStatusEnum;
import com.topaiebiz.goods.goodsenum.BackendCategoryLevelEnum;
import com.topaiebiz.goods.service.BackendCategoryNewService;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 下午2:02 2018/5/19
 */
@Service
@Slf4j
public class BackendCategoryNewServiceImpl implements BackendCategoryNewService {

    @Autowired
    private BackendCategoryEditDao backendCategoryEditDao;
    @Autowired
    private BackendCategoryDao backendCategoryDao;
    @Autowired
    private AttrItemEditDao attrItemEditDao;
    @Autowired
    private AttrGroupEditDao attrGroupEditDao;
    @Autowired
    private CategoryBrandEditDao categoryBrandEditDao;
    @Autowired
    private AttrItemDao attrItemDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private CategoryBrandDao categoryBrandDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private AttrValueDao attrValueDao;
    @Autowired
    private AttrValueEditDao attrValueEditDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBackendCategoryEdit(BackendCategoryDTO backendCategoryDTO) {
        BackendCategoryEdit backendCategoryEdit = new BackendCategoryEdit();
        BeanCopyUtil.copy(backendCategoryDTO, backendCategoryEdit);
        //查询同类目下最大序号
        EntityWrapper<BackendCategoryEdit> wrapper = new EntityWrapper<>();
        wrapper.eq("parentId", backendCategoryDTO.getParentId());
        wrapper.ne("deletedFlag", Constants.DeletedFlag.DELETED_YES);
        wrapper.orderBy("sortNo", false);
        wrapper.last("limit 1");
        List<BackendCategoryEdit> backendCategoryEditList = backendCategoryEditDao.selectList(wrapper);
        //设置排序号
        if (CollectionUtils.isEmpty(backendCategoryEditList)) {
            //同级第一个类目
            backendCategoryEdit.setSortNo(1);
        } else {
            backendCategoryEdit.setSortNo(backendCategoryEditList.get(0).getSortNo() + 1);
        }
        //类目分级
        if (backendCategoryDTO.getParentId().equals(0L)) {
            backendCategoryEdit.setLevel(BackendCategoryLevelEnum.BACKEND_LEVEL_ONE.getCode());
        } else {
            BackendCategoryEdit backendCategoryEditParent = backendCategoryEditDao.selectById(backendCategoryDTO.getParentId());
            if (null == backendCategoryEditParent || null == backendCategoryEditParent.getLevel()) {
                throw new GlobalException(GoodsExceptionEnum.PARENT_LEVEL_NOT_EXIST);
            } else if (backendCategoryEditParent.getLevel().compareTo(BackendCategoryLevelEnum.BACKEND_LEVEL_FIVE.getCode()) >= 0) {
                throw new GlobalException(GoodsExceptionEnum.LEVEL_OUT_LIMIT);
            } else if (backendCategoryEditParent.getLevel() == 4 && backendCategoryDTO.getIsLeaf() == 0) {
                throw new GlobalException(GoodsExceptionEnum.LEVEL_FIVE_ONLY_LEAF);
            } else {
                backendCategoryEdit.setLevel(backendCategoryEditParent.getLevel() + 1);
            }
        }
        //同级不能同名
        wrapper.eq("name", backendCategoryDTO.getName());
        Integer nameTotal = backendCategoryEditDao.selectCount(wrapper);
        if (null != nameTotal && nameTotal > 0) {
            throw new GlobalException(GoodsExceptionEnum.SIBILING_CANNOT_SAME);
        }
        //新增只设置treeSyncStatus为待同步
        backendCategoryEdit.setSyncStatus(SyncStatusEnum.SYNC_YES.getValue());
        backendCategoryEdit.setTreeSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
        return backendCategoryEditDao.insert(backendCategoryEdit) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBackendCategoryEdit(BackendCategoryDTO backendCategoryDTO) {
        BackendCategoryEdit backendCategoryEdit = backendCategoryEditDao.selectById(backendCategoryDTO.getCategoryId());
        if (null == backendCategoryEdit) {
            throw new GlobalException(GoodsExceptionEnum.CATEGORY_NOT_EXIST);
        }
        //非叶子转叶子要判断是否存在下级类目
        if (backendCategoryDTO.getIsLeaf().equals(1) && backendCategoryEdit.getIsLeaf().equals(0)) {
            EntityWrapper<BackendCategoryEdit> wrapper = new EntityWrapper<>();
            wrapper.eq("parentId", backendCategoryDTO.getCategoryId());
            if (backendCategoryEditDao.selectCount(wrapper) > 0) {
                throw new GlobalException(GoodsExceptionEnum.CATEGORY_HAS_SON);
            }
        }
        //同级不能同名
        EntityWrapper<BackendCategoryEdit> wrapper = new EntityWrapper<>();
        wrapper.eq("parentId", backendCategoryEdit.getParentId());
        wrapper.ne("id", backendCategoryEdit.getId());
        wrapper.ne("deletedFlag", Constants.DeletedFlag.DELETED_YES);
        wrapper.eq("name", backendCategoryDTO.getName());
        wrapper.last("limit 1");
        Integer nameTotal = backendCategoryEditDao.selectCount(wrapper);
        if (null != nameTotal && nameTotal > 0) {
            throw new GlobalException(GoodsExceptionEnum.SIBILING_CANNOT_SAME);
        }
        BackendCategoryEdit updateEntity = new BackendCategoryEdit();
        //isLeaf变动也改变treeSyncStatus
        if (!backendCategoryDTO.getIsLeaf().equals(backendCategoryEdit.getIsLeaf())) {
            updateEntity.setTreeSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
        }
        //名称变动修改syncStatus
        if (!backendCategoryDTO.getName().equals(backendCategoryEdit.getName())) {
            updateEntity.setSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
        }
        updateEntity.setId(backendCategoryDTO.getCategoryId());
        BeanCopyUtil.copy(backendCategoryDTO, updateEntity);
        updateEntity.setVersion(backendCategoryEdit.getVersion());
        return backendCategoryEditDao.updateById(updateEntity) > 0;
    }

    @Override
    public BackendCategoryDTO getCategoryEditById(Long categoryId) {
        BackendCategoryDTO backendCategoryDTO = null;
        EntityWrapper<BackendCategoryEdit> wrapper = new EntityWrapper<>();
        wrapper.eq("id", categoryId);
        wrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        BackendCategoryEdit backendCategoryEdit = backendCategoryEditDao.selectById(categoryId);
        if (null != backendCategoryEdit) {
            backendCategoryDTO = new BackendCategoryDTO();
            backendCategoryDTO.setCategoryId(backendCategoryEdit.getId());
            backendCategoryDTO.setParentId(backendCategoryEdit.getParentId());
            backendCategoryDTO.setIsLeaf(backendCategoryEdit.getIsLeaf());
            backendCategoryDTO.setLevel(backendCategoryEdit.getLevel());
            backendCategoryDTO.setName(backendCategoryEdit.getName());
            backendCategoryDTO.setSortNo(backendCategoryEdit.getSortNo());
        }
        return backendCategoryDTO;
    }

    @Override
    public boolean deleteCategoryEditById(BackendCategoryDTO backendCategoryDTO) {
        BackendCategoryEdit backendCategoryEdit = backendCategoryEditDao.selectById(backendCategoryDTO.getCategoryId());
        if (null != backendCategoryEdit) {
            Map<String, Integer> resMap = countGoodsUnderCategory(backendCategoryEdit);
            if (resMap.get("goodsCount") > 0) {
                throw new GlobalException(GoodsExceptionEnum.CATEGORY_LINKED_DATA);
            }
            Map<String, Integer> dataMap = countRelatedDataUnderCategory(backendCategoryEdit);
            if (dataMap.get("groupCount") > 0 || dataMap.get("attrCount") > 0
                    || dataMap.get("brandCount") > 0) {
                throw new GlobalException(GoodsExceptionEnum.CATEGORY_LINKED_DATA);
            }
            BackendCategoryEdit updateCatory = new BackendCategoryEdit();
            //2-待删除
            updateCatory.setDeleteFlag((byte) 2);
            updateCatory.setId(backendCategoryDTO.getCategoryId());
            updateCatory.setVersion(backendCategoryEdit.getVersion());
            return backendCategoryEditDao.updateById(updateCatory) > 0;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean siblingMoveCategoryEdit(BackendCategoryDTO backendCategoryDTO) {
        BackendCategoryEdit backendCategoryEdit = backendCategoryEditDao.selectById(backendCategoryDTO.getCategoryId());
        BackendCategoryEdit siblingCategory = backendCategoryEditDao.selectById(backendCategoryDTO.getSiblingId());
        if (null == backendCategoryEdit || null == siblingCategory
                || !backendCategoryEdit.getLevel().equals(siblingCategory.getLevel())) {
            throw new GlobalException(GoodsExceptionEnum.ONLY_SIBLING_MOVE);
        }
        BackendCategoryEdit updateCategory = new BackendCategoryEdit();
        BackendCategoryEdit updateSibling = new BackendCategoryEdit();
        updateCategory.setId(backendCategoryEdit.getId());
        //顺序变动改变treeSyncStatus
        updateCategory.setTreeSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
        updateCategory.setSortNo(siblingCategory.getSortNo());
        updateCategory.setLastModifiedTime(backendCategoryDTO.getLastModifiedTime());
        updateCategory.setVersion(backendCategoryEdit.getVersion());
        updateCategory.setLastModifierId(backendCategoryDTO.getLastModifierId());
        if (backendCategoryEditDao.updateById(updateCategory) > 0) {
            updateSibling.setId(siblingCategory.getId());
            updateSibling.setTreeSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
            updateSibling.setSortNo(backendCategoryEdit.getSortNo());
            updateSibling.setLastModifiedTime(backendCategoryDTO.getLastModifiedTime());
            updateSibling.setLastModifierId(backendCategoryDTO.getLastModifierId());
            updateSibling.setVersion(siblingCategory.getVersion());
            if (backendCategoryEditDao.updateById(updateSibling) > 0) {
                return true;
            } else {
                //回滚
                throw new GlobalException(GoodsExceptionEnum.CATEGORY_MOVE_ERROR);
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean freeMoveCategoryEdit(BackendCategoryDTO backendCategoryDTO) {
        BackendCategoryEdit backendCategoryEdit = backendCategoryEditDao.selectById(backendCategoryDTO.getCategoryId());
        if (null == backendCategoryEdit) {
            throw new GlobalException(GoodsExceptionEnum.CATEGORY_NOT_EXIST);
        }
        BackendCategoryEdit parentCategory = backendCategoryEditDao.selectById(backendCategoryDTO.getParentId());
        BackendCategoryEdit frontCategory = backendCategoryEditDao.selectById(backendCategoryDTO.getFrontId());
        BackendCategoryEdit backCategory = backendCategoryEditDao.selectById(backendCategoryDTO.getBackId());
        if (!backendCategoryDTO.getParentId().equals(0L) && null == parentCategory) {
            throw new GlobalException(GoodsExceptionEnum.PARENT_LEVEL_NOT_EXIST);
        }
        if (null != frontCategory && !frontCategory.getParentId().equals(backendCategoryDTO.getParentId())) {
            throw new GlobalException(GoodsExceptionEnum.FRONT_BACK_NO_PARENT);
        }
        if (null != backCategory && !backCategory.getParentId().equals(backendCategoryDTO.getParentId())) {
            throw new GlobalException(GoodsExceptionEnum.FRONT_BACK_NO_PARENT);
        }
        if (null != parentCategory) {
            //父类目不能为叶子
            if (parentCategory.getIsLeaf() == 1) {
                throw new GlobalException(GoodsExceptionEnum.PARENT_NOT_LEAF);
            }
            //非叶子层级树
            Integer totalNormalLevel = parentCategory.getLevel() + getMaxChildrenLevel(backendCategoryEdit, 1);
            if (backendCategoryEdit.getIsLeaf() == 0 && totalNormalLevel.equals(GoodsConstants.MAX_CATEGORY_LEVEL)) {
                throw new GlobalException(GoodsExceptionEnum.LEVEL_FIVE_ONLY_LEAF);
            } else if (totalNormalLevel.compareTo(GoodsConstants.MAX_CATEGORY_LEVEL) > 0) {
                throw new GlobalException(GoodsExceptionEnum.OUT_OF_MAX_LEVEL);
            }
        }
        if (null != frontCategory && null != backCategory && frontCategory.getSortNo().compareTo(backCategory.getSortNo()) >= 0) {
            throw new GlobalException(GoodsExceptionEnum.FRONT_BACK_ERROR);
        }
        EntityWrapper<BackendCategoryEdit> wrapper = new EntityWrapper<>();
        wrapper.eq("parentId", backendCategoryDTO.getParentId());
        wrapper.ne("deletedFlag", Constants.DeletedFlag.DELETED_YES);
        wrapper.orderBy("sortNo", false);
        List<BackendCategoryEdit> siblingCategoryList = backendCategoryEditDao.selectList(wrapper);
        if (!CollectionUtils.isEmpty(siblingCategoryList) && null == frontCategory && null == backCategory) {
            throw new GlobalException(GoodsExceptionEnum.NEAD_FRONT_OR_BACK);
        }
        //同级不能同名
        wrapper.ne("id", backendCategoryEdit.getId());
        wrapper.eq("name", backendCategoryEdit.getName());
        wrapper.last("limit 1");
        Integer nameTotal = backendCategoryEditDao.selectCount(wrapper);
        if (null != nameTotal && nameTotal > 0) {
            throw new GlobalException(GoodsExceptionEnum.SIBILING_CANNOT_SAME);
        }
        BackendCategoryEdit updateCategory = new BackendCategoryEdit();
        updateCategory.setId(backendCategoryDTO.getCategoryId());
        updateCategory.setVersion(backendCategoryEdit.getVersion());
        updateCategory.setParentId(backendCategoryDTO.getParentId());
        updateCategory.setTreeSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
        updateCategory.setLevel(null == parentCategory ? 1 : parentCategory.getLevel() + 1);
        updateCategory.setCreatedTime(null);
        updateCategory.setLastModifierId(backendCategoryDTO.getLastModifierId());
        updateCategory.setLastModifiedTime(backendCategoryDTO.getLastModifiedTime());
        //1.父类目之前没有下级
        if (null == frontCategory) {
            updateCategory.setSortNo(1);
        } else  {
            updateCategory.setSortNo(frontCategory.getSortNo()+1);
        }
        //同级后面的兄弟节点顺移一个位置
        if (null != backCategory) {
            EntityWrapper<BackendCategoryEdit> backWrapper = new EntityWrapper<>();
            backWrapper.eq("parentId", backendCategoryDTO.getParentId());
            backWrapper.ne("deletedFlag", Constants.DeletedFlag.DELETED_YES);
            backWrapper.ge("sortNo", backCategory.getSortNo());
            backWrapper.orderBy("sortNo", false);
            //
            List<BackendCategoryEdit> backCategoryList = backendCategoryEditDao.selectList(backWrapper);
            if (!CollectionUtils.isEmpty(backCategoryList)) {
                for (BackendCategoryEdit backEntity : backCategoryList) {
                    if (backEntity.getSortNo().compareTo(backCategory.getSortNo()) >= 0
                            && !backEntity.getId().equals(backendCategoryDTO.getCategoryId())) {
                        BackendCategoryEdit updateBackCategory = new BackendCategoryEdit();
                        updateBackCategory.setId(backEntity.getId());
                        updateBackCategory.setSortNo(backEntity.getSortNo() + 1);
                        updateBackCategory.setTreeSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
                        updateBackCategory.setLastModifierId(backendCategoryDTO.getLastModifierId());
                        updateBackCategory.setLastModifiedTime(backendCategoryDTO.getLastModifiedTime());
                        updateBackCategory.setCreatedTime(null);
                        updateBackCategory.setVersion(backEntity.getVersion());
                        if (backendCategoryEditDao.updateById(updateBackCategory) < 1) {
                            //回滚
                            throw new GlobalException(GoodsExceptionEnum.CATEGORY_MOVE_ERROR);
                        }
                    }
                }
            }
        }
        if (backendCategoryEditDao.updateById(updateCategory) > 0) {
            //递归更新下级的level
            updateChidrenLevel(updateCategory);
        } else {
            //回滚
            throw new GlobalException(GoodsExceptionEnum.CATEGORY_MOVE_ERROR);
        }
        return true;
    }

    /**
     * 获取指定类目包含的层级（包括自己,不包括叶子）
     */
    private Integer getMaxChildrenLevel(BackendCategoryEdit backendCategoryEdit, Integer level) {
        if (null != backendCategoryEdit && backendCategoryEdit.getIsLeaf() == 0) {
            EntityWrapper<BackendCategoryEdit> wrapper = new EntityWrapper<>();
            wrapper.eq("parentId", backendCategoryEdit.getId());
            wrapper.eq("isLeaf", 0);
            wrapper.ne("deletedFlag", Constants.DeletedFlag.DELETED_YES);
            List<BackendCategoryEdit> siblingCategoryList = backendCategoryEditDao.selectList(wrapper);
            if (!CollectionUtils.isEmpty(siblingCategoryList)) {
                level++;
                for (BackendCategoryEdit sibling : siblingCategoryList) {
                    //递归
                    Integer tmp = getMaxChildrenLevel(sibling, level);
                    if (level < tmp) {
                        level = tmp;
                    }
                }
            }
        }
        return level;
    }

    /**
     * 指定类目的所有下级的level逐层+1
     *
     * @param backendCategoryEdit
     */
    private void updateChidrenLevel(BackendCategoryEdit backendCategoryEdit) {
        EntityWrapper<BackendCategoryEdit> wrapper = new EntityWrapper<>();
        wrapper.eq("parentId", backendCategoryEdit.getId());
        wrapper.ne("deletedFlag", Constants.DeletedFlag.DELETED_YES);
        wrapper.orderBy("sortNo", false);
        List<BackendCategoryEdit> siblingCategoryList = backendCategoryEditDao.selectList(wrapper);
        if (!CollectionUtils.isEmpty(siblingCategoryList)) {
            for (BackendCategoryEdit childCategory : siblingCategoryList) {
                BackendCategoryEdit updateCategory = new BackendCategoryEdit();
                updateCategory.setId(childCategory.getId());
                updateCategory.setParentId(backendCategoryEdit.getId());
                updateCategory.setTreeSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
                updateCategory.setLevel(backendCategoryEdit.getLevel() + 1);
                updateCategory.setLastModifierId(backendCategoryEdit.getLastModifierId());
                updateCategory.setLastModifiedTime(backendCategoryEdit.getLastModifiedTime());
                updateCategory.setVersion(childCategory.getVersion());
                updateCategory.setCreatedTime(null);
                if (backendCategoryEditDao.updateById(updateCategory) < 1) {
                    //回滚
                    throw new GlobalException(GoodsExceptionEnum.CATEGORY_MOVE_ERROR);
                }
                //递归
                updateChidrenLevel(updateCategory);
            }
        }
    }

    @Override
    public List<CategoryNodeDTO> getChildrenByCategoryEdit(Long parentId) {
        EntityWrapper<BackendCategoryEdit> wrapper = new EntityWrapper<>();
        wrapper.eq("parentId", parentId);
        wrapper.ne("deletedFlag", Constants.DeletedFlag.DELETED_YES);
        wrapper.orderBy("sortNo", true);
        List<BackendCategoryEdit> backendCategoryEditList = backendCategoryEditDao.selectList(wrapper);
        List<CategoryNodeDTO> categoryNodeDTOList = null;
        if (!CollectionUtils.isEmpty(backendCategoryEditList)) {
            categoryNodeDTOList = new ArrayList<>(backendCategoryEditList.size());
            for (BackendCategoryEdit backendCategoryEdit : backendCategoryEditList) {
                CategoryNodeDTO categoryNodeDTO = new CategoryNodeDTO();
                categoryNodeDTO.setCategoryId(backendCategoryEdit.getId());
                //类目自己同步状态转换为前端标记
                categoryNodeDTO.setCategoryNeedSync(null != backendCategoryEdit.getTreeSyncStatus()
                        && backendCategoryEdit.getTreeSyncStatus().equals(0) ? 1 : 0);
                BeanCopyUtil.copy(backendCategoryEdit, categoryNodeDTO);
                categoryNodeDTO.setDataNeedSync(null != backendCategoryEdit.getSyncStatus()
                        && backendCategoryEdit.getSyncStatus().equals(0) ? 1 : 0);
                if (backendCategoryEdit.getIsLeaf() == 0) {
                    List<CategoryNodeDTO> children = this.getChildrenByCategoryEdit(backendCategoryEdit.getId());
                    categoryNodeDTO.setChildren(children);
                } else {
                    // 叶子节点关联数据是否需要同步
                    categoryNodeDTO.setDataNeedSync(this.countSyncDataByCategory(backendCategoryEdit.getId()) > 0 ? 1 : categoryNodeDTO.getDataNeedSync());
                }
                //待删除标记dataNeedSync
                categoryNodeDTO.setDataNeedSync(null != backendCategoryEdit.getDeletedFlag()
                        && backendCategoryEdit.getDeletedFlag() == 2 ? 1 : categoryNodeDTO.getDataNeedSync());
                categoryNodeDTOList.add(categoryNodeDTO);
            }
        }
        return categoryNodeDTOList;
    }

    @Override
    public CategoryProfileDTO profileEdit(Long categoryId) {
        CategoryProfileDTO categoryProfileDTO = new CategoryProfileDTO();
        BackendCategoryEdit backendCategoryEdit = backendCategoryEditDao.selectById(categoryId);
        if (null != backendCategoryEdit) {
            categoryProfileDTO.setCategoryPath(recurseParentEdit(backendCategoryEdit.getParentId()) + "/" + backendCategoryEdit.getName());
            categoryProfileDTO.setCreatedTime(backendCategoryEdit.getCreatedTime());
            categoryProfileDTO.setLastSyncTime(backendCategoryEdit.getLastSyncTime());
            Map<String, Integer> resMap = countGoodsUnderCategory(backendCategoryEdit);
            categoryProfileDTO.setLeafCount(resMap.get("leafCount"));
            categoryProfileDTO.setGoodsCount(resMap.get("goodsCount"));
            categoryProfileDTO.setLevel(backendCategoryEdit.getLevel());
            categoryProfileDTO.setIsLeaf(backendCategoryEdit.getIsLeaf());
            //叶子查询待同步的关联数据
            if (backendCategoryEdit.getIsLeaf() == 1) {
                //属性
                EntityWrapper<AttrItemEdit> attrItemWrapper = new EntityWrapper<>();
                attrItemWrapper.eq("categoryId", categoryId);
                attrItemWrapper.andNew();
                attrItemWrapper.eq("syncStatus", SyncStatusEnum.SYNC_NO.getValue());
                attrItemWrapper.or();
                attrItemWrapper.eq("deletedFlag", 2);
                categoryProfileDTO.setAttrNeedSync(attrItemEditDao.selectCount(attrItemWrapper) > 0 ? 1 : 0);
                //属性分组
                EntityWrapper<AttrGroupEdit> attrGroupWrapper = new EntityWrapper<>();
                attrGroupWrapper.eq("categoryId", categoryId);
                attrGroupWrapper.andNew();
                attrGroupWrapper.eq("syncStatus", SyncStatusEnum.SYNC_NO.getValue());
                attrGroupWrapper.or();
                attrGroupWrapper.eq("deletedFlag", 2);
                categoryProfileDTO.setGroupNeedSync(attrGroupEditDao.selectCount(attrGroupWrapper) > 0 ? 1 : 0);
                //品牌
                EntityWrapper<CategoryBrandEdit> categoryBrancWrapper = new EntityWrapper<>();
                categoryBrancWrapper.eq("categoryId", categoryId);
                categoryBrancWrapper.andNew();
                categoryBrancWrapper.eq("syncStatus", SyncStatusEnum.SYNC_NO.getValue());
                categoryBrancWrapper.or();
                categoryBrancWrapper.eq("deletedFlag", 2);
                categoryProfileDTO.setBrandNeedSync(categoryBrandEditDao.selectCount(categoryBrancWrapper)>0?1:0);
            }
        }
        return categoryProfileDTO;
    }

    /**
     * 递归上级类目名称-编辑
     *
     * @param parentId
     * @return
     */
    private String recurseParentEdit(Long parentId) {
        StringBuffer sb = new StringBuffer();
        if (null != parentId && !parentId.equals(0L)) {
            BackendCategoryEdit backendCategoryEdit = backendCategoryEditDao.selectById(parentId);
            if (null != backendCategoryEdit) {
                sb.append(recurseParentEdit(backendCategoryEdit.getParentId())).append("/").append(backendCategoryEdit.getName());
            }

        }
        return sb.toString();
    }

    @Override
    public List<CategoryNodeDTO> getChildrenByCategoryFormal(Long parentId) {
        return recurseChildrenFormal(parentId);
    }

    /**
     * 构建子树--正式
     *
     * @param parentId
     * @return
     */
    private List<CategoryNodeDTO> recurseChildrenFormal(Long parentId) {
        EntityWrapper<BackendCategoryEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("parentId", parentId);
        wrapper.ne("deletedFlag", Constants.DeletedFlag.DELETED_YES);
        wrapper.orderBy("sortNo", true);
        List<BackendCategoryEntity> backendCategoryEntityList = backendCategoryDao.selectList(wrapper);
        List<CategoryNodeDTO> categoryNodeDTOList = null;
        if (!CollectionUtils.isEmpty(backendCategoryEntityList)) {
            categoryNodeDTOList = new ArrayList<>(backendCategoryEntityList.size());
            for (BackendCategoryEntity backendCategoryEntity : backendCategoryEntityList) {
                CategoryNodeDTO categoryNodeDTO = new CategoryNodeDTO();
                categoryNodeDTO.setCategoryId(backendCategoryEntity.getId());
                BeanCopyUtil.copy(backendCategoryEntity, categoryNodeDTO);
                if (backendCategoryEntity.getIsLeaf() == 0) {
                    categoryNodeDTO.setChildren(recurseChildrenFormal(backendCategoryEntity.getId()));
                }
                categoryNodeDTOList.add(categoryNodeDTO);
            }
        }
        return categoryNodeDTOList;
    }

    @Override
    public CategoryProfileDTO profileFormal(Long categoryId) {
        CategoryProfileDTO categoryProfileDTO = new CategoryProfileDTO();
        BackendCategoryEntity backendCategoryEntity = backendCategoryDao.selectById(categoryId);
        if (null != backendCategoryEntity) {
            categoryProfileDTO.setCategoryPath(recurseParentFormal(backendCategoryEntity.getParentId()) + "/" + backendCategoryEntity.getName());
            categoryProfileDTO.setCreatedTime(backendCategoryEntity.getCreatedTime());
            categoryProfileDTO.setLastSyncTime(backendCategoryEntity.getLastSyncTime());
            BackendCategoryEdit backendCategoryEdit = new BackendCategoryEdit();
            BeanCopyUtil.copy(backendCategoryEntity, backendCategoryEdit);
            Map<String, Integer> resMap = countGoodsUnderCategory(backendCategoryEdit);
            categoryProfileDTO.setLeafCount(resMap.get("leafCount"));
            categoryProfileDTO.setGoodsCount(resMap.get("goodsCount"));
            categoryProfileDTO.setLevel(backendCategoryEntity.getLevel());
            categoryProfileDTO.setIsLeaf(backendCategoryEntity.getIsLeaf());
        }
        return categoryProfileDTO;
    }

    /**
     * 递归上级类目名称-正式
     *
     * @param parentId
     * @return
     */
    private String recurseParentFormal(Long parentId) {
        StringBuffer sb = new StringBuffer();
        if (null != parentId && !parentId.equals(0L)) {
            BackendCategoryEntity backendCategoryEntity = backendCategoryDao.selectById(parentId);
            if (null != backendCategoryEntity) {
                sb.append(recurseParentFormal(backendCategoryEntity.getParentId())).append("/").append(backendCategoryEntity.getName());
            }

        }
        return sb.toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRelatedCategorySyncStatus(Long categoryId, Integer syncStatus) {
        BackendCategoryEdit backendCategoryEdit = backendCategoryEditDao.selectById(categoryId);
        if (null != backendCategoryEdit && !backendCategoryEdit.getSyncStatus().equals(syncStatus)) {
            BackendCategoryEdit updateCategoryEdit = new BackendCategoryEdit();
            updateCategoryEdit.setId(categoryId);
            updateCategoryEdit.setSyncStatus(syncStatus);
            updateCategoryEdit.setCreatedTime(null);
            updateCategoryEdit.setVersion(null);
            if (SyncStatusEnum.SYNC_YES.getValue().equals(syncStatus)) {
                //同步时更新时间
                updateCategoryEdit.setLastSyncTime(new Date());
            }
            if (backendCategoryEditDao.updateById(updateCategoryEdit) > 0) {
                //递归
                if (!backendCategoryEdit.getParentId().equals(0L)) {
                    updateRelatedCategorySyncStatus(backendCategoryEdit.getParentId(), syncStatus);
                }
            } else {
                //回滚
                throw new GlobalException(GoodsExceptionEnum.CATEGORY_SYNC_ERROR);
            }
        }
        return true;
    }

    @Override
    public List<String> brandRelateCategoryEdit(List<Long> categoryIdList) {
        if (!CollectionUtils.isEmpty(categoryIdList)) {
            List<String> categoryPathList = new ArrayList<>(categoryIdList.size());
            categoryIdList.forEach(categoryId -> {
                BackendCategoryEdit backendCategoryEdit = backendCategoryEditDao.selectById(categoryId);
                if (null != backendCategoryEdit) {
                    categoryPathList.add(recurseParentEdit(backendCategoryEdit.getParentId()) + "/" + backendCategoryEdit.getName());
                }
            });
            return categoryPathList;
        }
        return null;
    }

    @Override
    public List<String> brandRelateCategoryFormal(List<Long> categoryIdList) {
        if (!CollectionUtils.isEmpty(categoryIdList)) {
            List<String> categoryPathList = new ArrayList<>(categoryIdList.size());
            categoryIdList.forEach(categoryId -> {
                BackendCategoryEntity backendCategoryEntity = backendCategoryDao.selectById(categoryId);
                if (null != backendCategoryEntity) {
                    categoryPathList.add(recurseParentFormal(backendCategoryEntity.getParentId()) + "/" + backendCategoryEntity.getName());
                }
            });
            return categoryPathList;
        }
        return null;
    }

    @Override
    public List<CategoryLevelNodeDTO> getSonCategoryEdit(Long parentId) {
        EntityWrapper<BackendCategoryEdit> wrapper = new EntityWrapper<>();
        wrapper.eq("parentId", parentId);
        wrapper.ne("deletedFlag", Constants.DeletedFlag.DELETED_YES);
        wrapper.orderBy("sortNo", true);
        List<BackendCategoryEdit> backendCategoryEditList = backendCategoryEditDao.selectList(wrapper);
        List<CategoryLevelNodeDTO> categoryLevelNodeDTOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(backendCategoryEditList)) {
            backendCategoryEditList.forEach(backendCategoryEdit -> {
                CategoryLevelNodeDTO categoryLevelNodeDTO = new CategoryLevelNodeDTO();
                categoryLevelNodeDTO.setCategoryId(backendCategoryEdit.getId());
                categoryLevelNodeDTO.setName(backendCategoryEdit.getName());
                categoryLevelNodeDTO.setLevel(backendCategoryEdit.getLevel());
                categoryLevelNodeDTO.setIsLeaf(backendCategoryEdit.getIsLeaf());
                //类目自己同步状态转换为前端标记
                categoryLevelNodeDTO.setCategoryNeedSync(null != backendCategoryEdit.getTreeSyncStatus()
                        && backendCategoryEdit.getTreeSyncStatus().equals(0) ? 1 : 0);
                categoryLevelNodeDTO.setDeletedFlag(backendCategoryEdit.getDeletedFlag());
                categoryLevelNodeDTO.setDataNeedSync(null != backendCategoryEdit.getSyncStatus()
                        && backendCategoryEdit.getSyncStatus().equals(0) ? 1 : 0);
                if (backendCategoryEdit.getIsLeaf() == 1) {
                    // 叶子节点关联数据是否需要同步
                    categoryLevelNodeDTO.setDataNeedSync(this.countSyncDataByCategory(backendCategoryEdit.getId()) > 0 ? 1 : categoryLevelNodeDTO.getDataNeedSync());
                }
                //待删除标记dataNeedSync
                categoryLevelNodeDTO.setDataNeedSync(null != backendCategoryEdit.getDeletedFlag()
                        && backendCategoryEdit.getDeletedFlag() == 2 ? 1 : categoryLevelNodeDTO.getDataNeedSync());
                categoryLevelNodeDTOList.add(categoryLevelNodeDTO);
            });
        }
        return categoryLevelNodeDTOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SyncResultDTO syncCategoryTree() {
        //树同步：只同步排序和层级以及新增
        SyncResultDTO syncResultDTO = new SyncResultDTO();
        EntityWrapper<BackendCategoryEdit> editWrapper = new EntityWrapper<>();
        editWrapper.eq("treeSyncStatus", SyncStatusEnum.SYNC_NO.getValue());
        List<BackendCategoryEdit> editList = backendCategoryEditDao.selectList(editWrapper);
        log.info("同步类目树{}调数据:{}", JSONObject.toJSON(editList));
        if (CollectionUtils.isEmpty(editList)) {
            syncResultDTO.setTotal(0);
            syncResultDTO.setSuccCount(0);
            syncResultDTO.setFailCount(0);
            return syncResultDTO;
        }
        syncResultDTO.setTotal(editList.size());
        Integer succCount = 0;
        Integer failCount = 0;
        List<SyncFailDTO> failMsgList = new ArrayList<>();
        for (BackendCategoryEdit backendCategoryEdit : editList) {
            BackendCategoryEntity backendCategoryEntity = backendCategoryDao.selectById(backendCategoryEdit.getId());
            if (null == backendCategoryEntity) {
                //新增
                backendCategoryEntity = new BackendCategoryEntity();
                BeanCopyUtil.copy(backendCategoryEdit, backendCategoryEntity);
                //新建未同步再删除，同步时直接删除
                backendCategoryEntity.setDeleteFlag((byte) (backendCategoryEdit.getDeletedFlag() != 0 ? 1 : 0));
                backendCategoryEdit.setDeleteFlag((byte) (backendCategoryEdit.getDeletedFlag() != 0 ? 1 : 0));
                backendCategoryEdit.setSyncStatus(SyncStatusEnum.SYNC_YES.getValue());
            } else {
                //只更新顺序和层级
                backendCategoryEntity.setLevel(backendCategoryEdit.getLevel());
                backendCategoryEntity.setIsLeaf(backendCategoryEdit.getIsLeaf());
                backendCategoryEntity.setSortNo(backendCategoryEdit.getSortNo());
                backendCategoryEntity.setParentId(backendCategoryEdit.getParentId());
            }
            backendCategoryEntity.setLastSyncTime(new Date());
            if (backendCategoryDao.replaceEntity(backendCategoryEntity) > 0) {
                backendCategoryEdit.setTreeSyncStatus(SyncStatusEnum.SYNC_YES.getValue());
                backendCategoryEdit.setLastSyncTime(new Date());
                if (backendCategoryEditDao.updateById(backendCategoryEdit) > 0) {
                    succCount++;
                }
            }
        }
        syncResultDTO.setSuccCount(succCount);
        syncResultDTO.setFailCount(failCount);
        syncResultDTO.setFailMsgList(failMsgList);
        return syncResultDTO;
    }

    /**
     * 1.叶子类目：同步自己名称和删除状态以及关联的属性品牌
     * 2.普通类目：同步自己名称和删除，递归执行下一级
     *
     * @param categoryId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SyncResultDTO syncCategoryDataByCategory(Long categoryId) {
        BackendCategoryEdit backendCategoryEdit = backendCategoryEditDao.selectById(categoryId);
        if (null == backendCategoryEdit) {
            throw new GlobalException(GoodsExceptionEnum.CATEGORY_NOT_EXIST);
        }
        return recurseSyncCategoryData(backendCategoryEdit);
    }

    /**
     * 统计叶子底下需要同步的数据条数
     *
     * @param categoryId
     * @return
     */
    private Integer countSyncDataByCategory(Long categoryId) {
        Integer total = 0;
        //属性
        EntityWrapper<AttrItemEdit> attrItemWrapper = new EntityWrapper<>();
        attrItemWrapper.eq("categoryId", categoryId);
        attrItemWrapper.andNew();
        attrItemWrapper.eq("syncStatus", SyncStatusEnum.SYNC_NO.getValue());
        attrItemWrapper.or();
        attrItemWrapper.eq("deletedFlag", 2);
        total += attrItemEditDao.selectCount(attrItemWrapper);
        //属性分组
        EntityWrapper<AttrGroupEdit> attrGroupWrapper = new EntityWrapper<>();
        attrGroupWrapper.eq("categoryId", categoryId);
        attrGroupWrapper.andNew();
        attrGroupWrapper.eq("syncStatus", SyncStatusEnum.SYNC_NO.getValue());
        attrGroupWrapper.or();
        attrGroupWrapper.eq("deletedFlag", 2);
        total += attrGroupEditDao.selectCount(attrGroupWrapper);
        //品牌
        EntityWrapper<CategoryBrandEdit> categoryBrancWrapper = new EntityWrapper<>();
        categoryBrancWrapper.eq("categoryId", categoryId);
        categoryBrancWrapper.andNew();
        categoryBrancWrapper.eq("syncStatus", SyncStatusEnum.SYNC_NO.getValue());
        categoryBrancWrapper.or();
        categoryBrancWrapper.eq("deletedFlag", 2);
        total += categoryBrandEditDao.selectCount(categoryBrancWrapper);
        return total;
    }

    /**
     * 计算类目下挂的商品数量和叶子数量
     *
     * @return
     */
    private Map<String, Integer> countGoodsUnderCategory(BackendCategoryEdit categoryEdit) {
        Map<String, Integer> resMap = new HashMap<>(2);
        resMap.put("goodsCount", 0);
        resMap.put("leafCount", 0);
        if (null != categoryEdit) {
            if (categoryEdit.getIsLeaf() == 0) {
                EntityWrapper<BackendCategoryEdit> wrapper = new EntityWrapper<>();
                wrapper.eq("parentId", categoryEdit.getId());
                wrapper.ne("deletedFlag", 1);
                List<BackendCategoryEdit> backendCategoryEditList = backendCategoryEditDao.selectList(wrapper);
                if (!CollectionUtils.isEmpty(backendCategoryEditList)) {
                    for (BackendCategoryEdit entity : backendCategoryEditList) {
                        Map<String, Integer> subResMap = countGoodsUnderCategory(entity);
                        resMap.put("leafCount", resMap.get("leafCount") + subResMap.get("leafCount"));
                        resMap.put("goodsCount", resMap.get("goodsCount") + subResMap.get("goodsCount"));
                    }
                }
            } else {
                EntityWrapper<ItemEntity> itemWrapper = new EntityWrapper<>();
                itemWrapper.eq("belongCategory", categoryEdit.getId());
                itemWrapper.eq("deletedFlag", 0);
                Integer goodsCount = itemDao.selectCount(itemWrapper);
                resMap.put("leafCount", resMap.get("leafCount") + 1);
                resMap.put("goodsCount", resMap.get("goodsCount") + (null == goodsCount ? 0 : goodsCount));
            }
        }
        return resMap;
    }

    /**
     * 计算类目下挂的属性分组，属性，品牌数据
     *
     * @param backendCategoryEdit
     * @return
     */
    private Map<String, Integer> countRelatedDataUnderCategory(BackendCategoryEdit backendCategoryEdit) {
        Map<String, Integer> resMap = new HashMap<>(3);
        resMap.put("groupCount", 0);
        resMap.put("attrCount", 0);
        resMap.put("brandCount", 0);
        if (null != backendCategoryEdit) {
            if (backendCategoryEdit.getIsLeaf() == 1) {
                EntityWrapper<AttrGroupEdit> groupWrapper = new EntityWrapper<>();
                groupWrapper.eq("categoryId", backendCategoryEdit.getId());
                groupWrapper.ne("deletedFlag", 1);
                Integer groupCount = attrGroupEditDao.selectCount(groupWrapper);
                EntityWrapper<AttrItemEdit> attrWrapper = new EntityWrapper<>();
                attrWrapper.eq("categoryId", backendCategoryEdit.getId());
                attrWrapper.ne("deletedFlag", 1);
                Integer attrCount = attrItemEditDao.selectCount(attrWrapper);
                EntityWrapper<CategoryBrandEdit> brandWrapper = new EntityWrapper<>();
                brandWrapper.eq("categoryId", backendCategoryEdit.getId());
                brandWrapper.ne("deletedFlag", 1);
                Integer brandCount = categoryBrandEditDao.selectCount(brandWrapper);
                resMap.put("groupCount", resMap.get("groupCount") + groupCount);
                resMap.put("attrCount", resMap.get("attrCount") + attrCount);
                resMap.put("brandCount", resMap.get("brandCount") + brandCount);

            } else {
                EntityWrapper<BackendCategoryEdit> wrapper = new EntityWrapper<>();
                wrapper.eq("parentId", backendCategoryEdit.getId());
                wrapper.ne("deletedFlag", 1);
                List<BackendCategoryEdit> backendCategoryEditList = backendCategoryEditDao.selectList(wrapper);
                if (!CollectionUtils.isEmpty(backendCategoryEditList)) {
                    for (BackendCategoryEdit entity : backendCategoryEditList) {
                        Map<String, Integer> subResMap = countRelatedDataUnderCategory(entity);
                        resMap.put("groupCount", resMap.get("groupCount") + subResMap.get("groupCount"));
                        resMap.put("attrCount", resMap.get("attrCount") + subResMap.get("attrCount"));
                        resMap.put("brandCount", resMap.get("brandCount") + subResMap.get("brandCount"));
                    }
                }
            }
        }
        return resMap;
    }

    private SyncResultDTO recurseSyncCategoryData(BackendCategoryEdit backendCategoryEdit) {
        SyncResultDTO syncResultDTO = new SyncResultDTO();
        syncResultDTO.setTotal(0);
        syncResultDTO.setFailCount(0);
        syncResultDTO.setSuccCount(0);
        //1.同步名称和删除状态
        BackendCategoryEntity backendCategoryEntity = backendCategoryDao.selectById(backendCategoryEdit.getId());
        if (null == backendCategoryEntity) {
            //新增
            backendCategoryEntity = new BackendCategoryEntity();
            BeanCopyUtil.copy(backendCategoryEdit, backendCategoryEntity);
        } else {
            backendCategoryEntity.setName(backendCategoryEdit.getName());
        }
        if (backendCategoryEdit.getDeletedFlag() == 2) {
            //判断是否挂有商品
            Map<String, Integer> goodsMap = countGoodsUnderCategory(backendCategoryEdit);
            if (goodsMap.get("goodsCount") > 0) {
                throw new GlobalException(GoodsExceptionEnum.CATEGORY_LINKED_DATA);
            }
            Map<String, Integer> relateMap = countRelatedDataUnderCategory(backendCategoryEdit);
            if (relateMap.get("groupCount") > 0 || relateMap.get("attrCount") > 0
                    || relateMap.get("brandCount") > 0) {
                throw new GlobalException(GoodsExceptionEnum.CATEGORY_LINKED_DATA);
            }
            backendCategoryEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
            backendCategoryEdit.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        }
        backendCategoryEntity.setLastSyncTime(new Date());
        if (backendCategoryDao.replaceEntity(backendCategoryEntity) > 0) {
            log.info("{}同步了基本信息", JSONObject.toJSON(backendCategoryEdit));
            backendCategoryEdit.setSyncStatus(SyncStatusEnum.SYNC_YES.getValue());
            backendCategoryEdit.setLastSyncTime(new Date());
            if (backendCategoryEditDao.updateById(backendCategoryEdit) > 0) {
                syncResultDTO.setTotal(syncResultDTO.getTotal() + 1);
                syncResultDTO.setSuccCount(syncResultDTO.getSuccCount() + 1);
            }
        }
        //2.如果是叶子同步关联属性和品牌,非叶子类目递归下一级
        if (backendCategoryEdit.getIsLeaf() == 0) {
            EntityWrapper<BackendCategoryEdit> editWrapper = new EntityWrapper<>();
            editWrapper.eq("parentId", backendCategoryEdit.getId());
            editWrapper.ne("deletedFlag", 1);
            List<BackendCategoryEdit> backendCategoryEditList = backendCategoryEditDao.selectList(editWrapper);
            if (!CollectionUtils.isEmpty(backendCategoryEditList)) {
                for (BackendCategoryEdit edit : backendCategoryEditList) {
                    //递归
                    SyncResultDTO subResultDTO = recurseSyncCategoryData(edit);
                    syncResultDTO.setTotal(syncResultDTO.getTotal() + subResultDTO.getTotal());
                    syncResultDTO.setSuccCount(syncResultDTO.getSuccCount() + subResultDTO.getSuccCount());
                }
            }
        } else {
            //属性
            EntityWrapper<AttrItemEdit> attrItemWrapper = new EntityWrapper<>();
            attrItemWrapper.eq("categoryId", backendCategoryEdit.getId());
            attrItemWrapper.andNew();
            attrItemWrapper.eq("syncStatus", SyncStatusEnum.SYNC_NO.getValue());
            attrItemWrapper.or();
            attrItemWrapper.eq("deletedFlag", 2);
            List<AttrItemEdit> attrItemEditList = attrItemEditDao.selectList(attrItemWrapper);
            //属性分组
            EntityWrapper<AttrGroupEdit> attrGroupWrapper = new EntityWrapper<>();
            attrGroupWrapper.eq("categoryId", backendCategoryEdit.getId());
            attrGroupWrapper.andNew();
            attrGroupWrapper.eq("syncStatus", SyncStatusEnum.SYNC_NO.getValue());
            attrGroupWrapper.or();
            attrGroupWrapper.eq("deletedFlag", 2);
            List<AttrGroupEdit> attrGroupEditList = attrGroupEditDao.selectList(attrGroupWrapper);
            //品牌
            EntityWrapper<CategoryBrandEdit> categoryBrancWrapper = new EntityWrapper<>();
            categoryBrancWrapper.eq("categoryId", backendCategoryEdit.getId());
            categoryBrancWrapper.andNew();
            categoryBrancWrapper.eq("syncStatus", SyncStatusEnum.SYNC_NO.getValue());
            categoryBrancWrapper.or();
            categoryBrancWrapper.eq("deletedFlag", 2);
            List<CategoryBrandEdit> categoryBrandEditList = categoryBrandEditDao.selectList(categoryBrancWrapper);

            Integer total = 0;
            total += CollectionUtils.isEmpty(attrItemEditList) ? 0 : attrItemEditList.size();
            total += CollectionUtils.isEmpty(attrGroupEditList) ? 0 : attrGroupEditList.size();
            total += CollectionUtils.isEmpty(categoryBrandEditList) ? 0 : categoryBrandEditList.size();
            syncResultDTO.setTotal(syncResultDTO.getTotal() + total);
            Integer succCount = 0;
            if (!CollectionUtils.isEmpty(attrItemEditList)) {
                log.info("{}同步关联的属性{}条：{}", backendCategoryEdit.getName(), attrItemEditList.size()
                        , JSONObject.toJSON(attrItemEditList));
                for (AttrItemEdit attrItemEdit : attrItemEditList) {
                    //TODO 属性关联商品判断
                    AttrItem attrItem = new AttrItem();
                    BeanCopyUtil.copy(attrItemEdit, attrItem);
                    if (attrItemEdit.getDeletedFlag() == 2) {
                        attrItemEdit.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                        attrItem.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                    }
                    if (attrItemDao.replaceEntity(attrItem) > 0) {
                        attrItemEdit.setSyncStatus(SyncStatusEnum.SYNC_YES.getValue());
                        if (attrItemEditDao.updateById(attrItemEdit) > 0) {
                            succCount++;
                            //同步属性值
                            EntityWrapper<AttrValueEdit> valueWrapper = new EntityWrapper<>();
                            valueWrapper.eq("attrId", attrItemEdit.getId());
                            valueWrapper.eq("syncStatus", SyncStatusEnum.SYNC_NO.getValue());
                            List<AttrValueEdit> attrValueEditList = attrValueEditDao.selectList(valueWrapper);
                            if (!CollectionUtils.isEmpty(attrValueEditList)) {
                                for (AttrValueEdit valueEdit : attrValueEditList) {
                                    AttrValue attrValue = new AttrValue();
                                    BeanCopyUtil.copy(valueEdit, attrValue);
                                    if (valueEdit.getDeletedFlag() == 2) {
                                        valueEdit.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                                        attrValue.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);

                                    }
                                    if (attrValueDao.replaceEntity(attrValue) > 0) {
                                        valueEdit.setSyncStatus(SyncStatusEnum.SYNC_YES.getValue());
                                        attrValueEditDao.updateById(valueEdit);
                                    }
                                }
                            }

                        }
                    }

                }

            }
            if (!CollectionUtils.isEmpty(attrGroupEditList)) {
                log.info("{}同步关联的属性分组{}条：{}", backendCategoryEdit.getName(), attrGroupEditList.size()
                        , JSONObject.toJSON(attrGroupEditList));
                for (AttrGroupEdit attrGroupEdit : attrGroupEditList) {
                    AttrGroup attrGroup = new AttrGroup();
                    BeanCopyUtil.copy(attrGroupEdit, attrGroup);
                    if (attrGroupEdit.getDeletedFlag() == 2) {
                        attrGroup.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                        attrGroupEdit.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                    }
                    if (attrGroupDao.replaceEntity(attrGroup) > 0) {
                        attrGroupEdit.setSyncStatus(SyncStatusEnum.SYNC_YES.getValue());
                        if (attrGroupEditDao.updateById(attrGroupEdit) > 0) {
                            succCount++;
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(categoryBrandEditList)) {
                log.info("{}同步关联的品牌{}条：{}", backendCategoryEdit.getName(), categoryBrandEditList.size()
                        , JSONObject.toJSON(categoryBrandEditList));
                for (CategoryBrandEdit categoryBrandEdit : categoryBrandEditList) {
                    CategoryBrand categoryBrand = new CategoryBrand();
                    BeanCopyUtil.copy(categoryBrandEdit, categoryBrand);
                    if (categoryBrandEdit.getDeletedFlag() == 2) {
                        categoryBrandEdit.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                        categoryBrand.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                    }
                    if (categoryBrandDao.replaceEntity(categoryBrand) > 0) {
                        categoryBrandEdit.setSyncStatus(SyncStatusEnum.SYNC_YES.getValue());
                        if (categoryBrandEditDao.updateById(categoryBrandEdit) > 0) {
                            succCount++;
                        }
                    }
                }
            }
            syncResultDTO.setSuccCount(syncResultDTO.getSuccCount() + succCount);
        }

        return syncResultDTO;
    }
}
