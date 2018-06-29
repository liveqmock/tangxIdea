package com.topaiebiz.goods.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryDao;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryEntity;
import com.topaiebiz.goods.dao.AttrGroupDao;
import com.topaiebiz.goods.dao.AttrGroupEditDao;
import com.topaiebiz.goods.dao.BackendCategoryEditDao;
import com.topaiebiz.goods.dto.AttrGroupDTO;
import com.topaiebiz.goods.dto.AttrGroupEditDTO;
import com.topaiebiz.goods.dto.AttrGroupSortNoDTO;
import com.topaiebiz.goods.dto.CategoryIdDTO;
import com.topaiebiz.goods.entity.AttrGroup;
import com.topaiebiz.goods.entity.AttrGroupEdit;
import com.topaiebiz.goods.entity.BackendCategoryEdit;
import com.topaiebiz.goods.enums.GoodsExceptionEnum;
import com.topaiebiz.goods.enums.SyncStatusEnum;
import com.topaiebiz.goods.service.AttrGroupService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 上午10:31 2018/5/18
 */
@Service
class AttrGroupServiceImpl implements AttrGroupService {

  @Autowired
  private AttrGroupDao attrGroupDao;
  @Autowired
  private AttrGroupEditDao attrGroupEditDao;
  @Autowired
  private BackendCategoryEditDao backendCategoryEditDao;
  @Autowired
  private BackendCategoryDao backendCategoryDao;

  @Override
  public boolean saveAttrGroupEdit(AttrGroupEditDTO attrGroupEditDTO) {
    List<AttrGroupEdit> attrGroups = attrGroupEditDao.selectAddAttrGroupEditName(attrGroupEditDTO);
    if (CollectionUtils.isNotEmpty(attrGroups)) {
      throw new GlobalException(GoodsExceptionEnum.ATTR_GROUP_NAME_NOT_REPETITION);
    }
    BackendCategoryEdit backendCategoryEdit =
        backendCategoryEditDao.selectById(attrGroupEditDTO.getCategoryId());
    if (backendCategoryEdit.getIsLeaf().equals(0)) {
      throw new GlobalException(GoodsExceptionEnum.CATEGORY_LEAF);
    }
    AttrGroupEdit attrGroupEdit = new AttrGroupEdit();
    BeanCopyUtil.copy(attrGroupEditDTO, attrGroupEdit);
    attrGroupEdit.setSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
    attrGroupEdit.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
    attrGroupEdit.setCreatedTime(new Date());
    return attrGroupEditDao.insert(attrGroupEdit) > 0;
  }

  @Override
  public boolean modifyAttrGroupEdit(AttrGroupEditDTO attrGroupEditDTO) {
    List<AttrGroupEdit> attrGroups =
        attrGroupEditDao.selectUpdateAttrGroupEditName(attrGroupEditDTO);
    if (CollectionUtils.isNotEmpty(attrGroups)) {
      throw new GlobalException(GoodsExceptionEnum.ATTR_GROUP_NAME_NOT_REPETITION);
    }
    AttrGroupEdit attrGroupEdit = new AttrGroupEdit();
    BeanCopyUtil.copy(attrGroupEditDTO, attrGroupEdit);
    attrGroupEdit.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
    attrGroupEdit.setLastModifiedTime(new Date());
    attrGroupEdit.setSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
    attrGroupEdit.setVersion(null);
    return attrGroupEditDao.updateById(attrGroupEdit) > 0;
  }

  @Override
  public PageInfo<AttrGroupEditDTO> getAttrGroupList(CategoryIdDTO categoryIdDTO) {
    if (categoryIdDTO.getCategoryId() == null) {
      throw new GlobalException(GoodsExceptionEnum.CATEGORY_ID_NEED);
    }
    BackendCategoryEdit backendCategoryEdit =
        backendCategoryEditDao.selectById(categoryIdDTO.getCategoryId());
    if (backendCategoryEdit == null) {
      throw new GlobalException(GoodsExceptionEnum.CATEGORY_NOT_EXIST);
    }
    PagePO pagePO = new PagePO();
    pagePO.setPageNo(categoryIdDTO.getPageNo());
    pagePO.setPageSize(categoryIdDTO.getPageSize());
    Page<AttrGroupEditDTO> page = PageDataUtil.buildPageParam(pagePO);
    EntityWrapper<AttrGroupEdit> cond = new EntityWrapper<>();
    cond.notIn("deletedFlag", Constants.DeletedFlag.DELETED_YES);
    cond.eq("categoryId", categoryIdDTO.getCategoryId());
    cond.orderBy("sortNo", true);
    List<AttrGroupEdit> attrGroupEdits = attrGroupEditDao.selectPage(page, cond);
    if (CollectionUtils.isEmpty(attrGroupEdits)) {
      return PageDataUtil.copyPageInfo(page);
    }
    List<AttrGroupEditDTO> attrGroupEditDTOS =
        BeanCopyUtil.copyList(attrGroupEdits, AttrGroupEditDTO.class);
    page.setRecords(attrGroupEditDTOS);
    return PageDataUtil.copyPageInfo(page);
  }

  @Override
  public boolean modifyAttrGroupSortNo(AttrGroupSortNoDTO attrGroupSortNoDTO) {
    if (attrGroupSortNoDTO.getId() == null && attrGroupSortNoDTO.getId() < 0) {
      throw new GlobalException(GoodsExceptionEnum.ATTR_GROUP_ID);
    }
    AttrGroupEdit attrGroup = new AttrGroupEdit();
    attrGroup.cleanInit();
    attrGroup.setSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
    attrGroup.setId(attrGroupSortNoDTO.getId());
    attrGroup.setSortNo(attrGroupSortNoDTO.getSortNo());
    attrGroup.setLastModifiedTime(new Date());
    attrGroup.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
    return attrGroupEditDao.updateById(attrGroup) > 0;
  }

  @Override
  @Transactional
  public boolean removeAttrGroupEdit(Long id) {
    if (id == null && id < 0) {
      throw new GlobalException(GoodsExceptionEnum.ATTR_GROUP_ID);
    }
    AttrGroupEdit attrGroupEdit = attrGroupEditDao.selectById(id);
    if (attrGroupEdit == null) {
      return false;
    }
    // 待同步（syncStatus 0 deletedFlag 0）进行删除（syncStatus 0 deletedFlag 2）
    if (attrGroupEdit.getSyncStatus().equals(SyncStatusEnum.SYNC_NO.getValue())
        && attrGroupEdit.getDeletedFlag().equals(Constants.DeletedFlag.DELETED_NO)) {
      AttrGroupEdit attrGroup = new AttrGroupEdit();
      attrGroup.cleanInit();
      attrGroup.setDeleteFlag((byte) 2);
      attrGroup.setId(id);
      attrGroup.setLastModifiedTime(new Date());
      attrGroup.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
      return attrGroupEditDao.updateById(attrGroup) > 0;
    }
    // 已同步（syncStatus 1 deletedFlag 0）进行删除（syncStatus 1 deletedFlag 2）
    if (attrGroupEdit.getSyncStatus().equals(SyncStatusEnum.SYNC_YES.getValue())
        && attrGroupEdit.getDeletedFlag().equals(Constants.DeletedFlag.DELETED_NO)) {
      AttrGroupEdit attrGroup = new AttrGroupEdit();
      attrGroup.cleanInit();
      attrGroup.setDeleteFlag((byte) 2);
      attrGroup.setId(id);
      attrGroup.setLastModifiedTime(new Date());
      attrGroup.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
      return attrGroupEditDao.updateById(attrGroup) > 0;
    }
    return true;
  }

  @Override
  @Transactional
  public boolean giveUpAttrGroupEdit(Long id) {
    if (id == null && id < 0) {
      throw new GlobalException(GoodsExceptionEnum.ATTR_GROUP_ID);
    }
    AttrGroupEdit attrGroupEdit = attrGroupEditDao.selectById(id);
    if (attrGroupEdit == null) {
      return false;
    }
    // 待删除（syncStatus 0 deletedFlag 2）待同步（syncStatus 0 deletedFlag 0）
    if (attrGroupEdit.getSyncStatus().equals(SyncStatusEnum.SYNC_NO.getValue())
        && attrGroupEdit.getDeletedFlag().equals((byte) 2)) {
      AttrGroupEdit attrGroup = new AttrGroupEdit();
      attrGroup.cleanInit();
      attrGroup.setId(id);
      attrGroup.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
      attrGroup.setLastModifiedTime(new Date());
      attrGroup.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
      return attrGroupEditDao.updateById(attrGroup) > 0;
    }
    // 待删除（syncStatus 1 deletedFlag 2）已同步（syncStatus 1 deletedFlag 0）
    if (attrGroupEdit.getSyncStatus().equals(SyncStatusEnum.SYNC_YES.getValue())
        && attrGroupEdit.getDeletedFlag().equals((byte) 2)) {
      AttrGroupEdit attrGroup = new AttrGroupEdit();
      attrGroup.cleanInit();
      attrGroup.setId(id);
      attrGroup.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
      attrGroup.setLastModifiedTime(new Date());
      attrGroup.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
      return attrGroupEditDao.updateById(attrGroup) > 0;
    }
    return true;
  }

  @Override
  public AttrGroupEditDTO findAttrGroupEdit(Long id) {
    if (id == null && id < 0) {
      throw new GlobalException(GoodsExceptionEnum.ATTR_GROUP_ID);
    }
    AttrGroupEdit attrGroupEdit = attrGroupEditDao.selectById(id);
    if (attrGroupEdit == null) {
      throw new GlobalException(GoodsExceptionEnum.ATTR_GROUP_ID_NOT_EXIST);
    }
    AttrGroupEditDTO attrGroupEditDTO = new AttrGroupEditDTO();
    BeanCopyUtil.copy(attrGroupEdit, attrGroupEditDTO);
    return attrGroupEditDTO;
  }

  @Override
  public List<AttrGroupEditDTO> queryAttrGroups(Long categoryId) {
    if (categoryId == null) {
      throw new GlobalException(GoodsExceptionEnum.CATEGORY_ID_NEED);
    }
    BackendCategoryEdit backendCategoryEdit = backendCategoryEditDao.selectById(categoryId);
    if (backendCategoryEdit == null) {
      throw new GlobalException(GoodsExceptionEnum.CATEGORY_NOT_EXIST);
    }
    EntityWrapper<AttrGroupEdit> cond = new EntityWrapper<>();
    cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
    cond.eq("categoryId", categoryId);
    cond.orderBy("sortNo", true);
    List<AttrGroupEdit> attrGroups = attrGroupEditDao.selectList(cond);
    List<AttrGroupEditDTO> attrGroupDTOS = BeanCopyUtil.copyList(attrGroups, AttrGroupEditDTO.class);
    return attrGroupDTOS;
  }

  @Override
  public PageInfo<AttrGroupDTO> getAttrGroups(CategoryIdDTO categoryIdDTO) {
    if (categoryIdDTO.getCategoryId() == null) {
      throw new GlobalException(GoodsExceptionEnum.CATEGORY_ID_NEED);
    }
    BackendCategoryEntity backendCategoryEntity =
        backendCategoryDao.selectById(categoryIdDTO.getCategoryId());
    if (backendCategoryEntity == null) {
      throw new GlobalException(GoodsExceptionEnum.CATEGORY_NOT_EXIST);
    }
    PagePO pagePO = new PagePO();
    pagePO.setPageSize(categoryIdDTO.getPageSize());
    pagePO.setPageNo(categoryIdDTO.getPageNo());
    Page<AttrGroupDTO> page = PageDataUtil.buildPageParam(pagePO);
    EntityWrapper<AttrGroup> cond = new EntityWrapper<>();
    cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
    cond.eq("categoryId", categoryIdDTO.getCategoryId());
    cond.orderBy("sortNo", true);
    List<AttrGroup> attrGroups = attrGroupDao.selectPage(page, cond);
    List<AttrGroupDTO> attrGroupDTOS = BeanCopyUtil.copyList(attrGroups, AttrGroupDTO.class);
    page.setRecords(attrGroupDTOS);
    return PageDataUtil.copyPageInfo(page);
  }
}
