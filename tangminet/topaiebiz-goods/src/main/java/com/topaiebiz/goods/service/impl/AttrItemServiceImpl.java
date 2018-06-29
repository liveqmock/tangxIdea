package com.topaiebiz.goods.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.dao.*;
import com.topaiebiz.goods.dto.AttrItemDTO;
import com.topaiebiz.goods.dto.AttrItemReq;
import com.topaiebiz.goods.entity.*;
import com.topaiebiz.goods.enums.AttrValueSourceEnum;
import com.topaiebiz.goods.enums.GoodsExceptionEnum;
import com.topaiebiz.goods.enums.SyncStatusEnum;
import com.topaiebiz.goods.service.AttrItemService;
import com.topaiebiz.goods.service.BackendCategoryNewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: Jeff Chen
 * @date: created in 下午3:01 2018/5/19
 */
@Service
public class AttrItemServiceImpl implements AttrItemService {
    @Autowired
    private AttrItemDao attrItemDao;
    @Autowired
    private AttrItemEditDao attrItemEditDao;
    @Autowired
    private AttrValueDao attrValueDao;
    @Autowired
    private AttrValueEditDao attrValueEditDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private AttrGroupEditDao attrGroupEditDao;
    @Autowired
    private BackendCategoryEditDao backendCategoryEditDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveAttrItemEdit(AttrItemDTO attrItemDTO) {
        BackendCategoryEdit backendCategoryEdit = backendCategoryEditDao.selectById(attrItemDTO.getCategoryId());
        if (null == backendCategoryEdit || backendCategoryEdit.getIsLeaf().equals(0)) {
            throw new GlobalException(GoodsExceptionEnum.ONLY_RELATE_TO_LEAF);
        }
        AttrItemEdit attrItemEdit = new AttrItemEdit();
        BeanCopyUtil.copy(attrItemDTO, attrItemEdit);
        attrItemEdit.setSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
        //判断名称不能重复
        EntityWrapper<AttrItemEdit> wrapper = new EntityWrapper<>();
        wrapper.eq("categoryId", attrItemDTO.getCategoryId());
        wrapper.eq("attrName", attrItemDTO.getAttrName());
        wrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        if (attrItemEditDao.selectCount(wrapper) > 0) {
            throw new GlobalException(GoodsExceptionEnum.ATTR_NAME_CANT_SAME);
        }
        if (attrItemEditDao.insert(attrItemEdit) > 0) {
            //添加属性值
            if (!CollectionUtils.isEmpty(attrItemDTO.getValueList())) {
                attrItemDTO.getValueList().forEach(s -> {
                    AttrValueEdit attrValueEdit = new AttrValueEdit();
                    BeanCopyUtil.copy(attrItemDTO, attrValueEdit);
                    attrValueEdit.setAttrId(attrItemEdit.getId());
                    attrValueEdit.setSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
                    attrValueEdit.setValue(s);
                    attrValueEdit.setSource(AttrValueSourceEnum.PLATFORM.getValue());
                    attrValueEditDao.insert(attrValueEdit);
                });
            }
        }
        return true;
    }

    @Override
    public AttrItemDTO getAttrItemEditById(Long attrId) {
        AttrItemEdit attrItemEdit = attrItemEditDao.selectById(attrId);
        if (null != attrItemEdit) {
            AttrItemDTO attrItemDTO = new AttrItemDTO();
            BeanCopyUtil.copy(attrItemEdit, attrItemDTO);
            AttrGroupEdit attrGroupEdit = attrGroupEditDao.selectById(attrItemEdit.getGroupId());
            attrItemDTO.setGroupName(null == attrGroupEdit ? "" : attrGroupEdit.getName());
            EntityWrapper<AttrValueEdit> wrapper = new EntityWrapper<>();
            wrapper.eq("attrId", attrItemEdit.getId());
            wrapper.eq("source", AttrValueSourceEnum.PLATFORM.getValue());
            wrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            List<AttrValueEdit> attrValueEditList = attrValueEditDao.selectList(wrapper);
            if (!CollectionUtils.isEmpty(attrValueEditList)) {
                List<String> valueList = new ArrayList<>(attrValueEditList.size());
                attrValueEditList.forEach(attrValueEdit -> {
                    valueList.add(attrValueEdit.getValue());

                });
                attrItemDTO.setValueList(valueList);
            }
            return attrItemDTO;
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAttrItemEdit(AttrItemEdit attrItemEdit) {
        AttrItemEdit attrItemEditOld = attrItemEditDao.selectById(attrItemEdit.getId());
        if (null != attrItemEditOld) {
            attrItemEdit.setCreatedTime(null);
            attrItemEdit.setVersion(attrItemEditOld.getVersion());
            attrItemEdit.setDeleteFlag((byte) 2);
            return attrItemEditDao.updateById(attrItemEdit) > 0;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean abortDel(AttrItemEdit attrItemEdit) {
        AttrItemEdit attrItemEditOld = attrItemEditDao.selectById(attrItemEdit.getId());
        if (null != attrItemEditOld) {
            attrItemEdit.setCreatedTime(null);
            attrItemEdit.setVersion(attrItemEditOld.getVersion());
            attrItemEdit.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            return attrItemEditDao.updateById(attrItemEdit) > 0;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAttrItemEdit(AttrItemDTO attrItemDTO) {
        AttrItemEdit attrItemEdit = attrItemEditDao.selectById(attrItemDTO.getAttrId());
        if (null != attrItemEdit) {
            //判断名称不能重复
            EntityWrapper<AttrItemEdit> wrapper = new EntityWrapper<>();
            wrapper.eq("categoryId", attrItemDTO.getCategoryId());
            wrapper.eq("attrName", attrItemDTO.getAttrName());
            wrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            wrapper.ne("id", attrItemDTO.getAttrId());
            if (attrItemEditDao.selectCount(wrapper) > 0) {
                throw new GlobalException(GoodsExceptionEnum.ATTR_NAME_CANT_SAME);
            }
            AttrItemEdit updateAttrItemEdit = new AttrItemEdit();
            BeanCopyUtil.copy(attrItemDTO, updateAttrItemEdit);
            updateAttrItemEdit.setCreatedTime(null);
            updateAttrItemEdit.setId(attrItemDTO.getAttrId());
            updateAttrItemEdit.setSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
            updateAttrItemEdit.setVersion(attrItemEdit.getVersion());
            if (attrItemEditDao.updateById(updateAttrItemEdit) > 0) {
                //没有属性值先删除再添加
                AttrValueEdit updValueEdit = new AttrValueEdit();
                updValueEdit.setAttrId(attrItemEdit.getId());
                updValueEdit.setSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
                updValueEdit.setDeleteFlag(Constants.DeletedFlag.DELETED_YES.byteValue());
                EntityWrapper<AttrValueEdit> valueEditEntityWrapper = new EntityWrapper<>();
                valueEditEntityWrapper.eq("attrId", attrItemEdit.getId());
                valueEditEntityWrapper.eq("source", AttrValueSourceEnum.PLATFORM.getValue());
                valueEditEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
                attrValueEditDao.update(updValueEdit, valueEditEntityWrapper);
                    if (!CollectionUtils.isEmpty(attrItemDTO.getValueList())) {
                        attrItemDTO.getValueList().forEach(s -> {
                            AttrValueEdit attrValueEdit = new AttrValueEdit();
                            BeanCopyUtil.copy(attrItemDTO, attrValueEdit);
                            attrValueEdit.setAttrId(attrItemEdit.getId());
                            attrValueEdit.setSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
                            attrValueEdit.setValue(s);
                            attrValueEdit.setCreatedTime(new Date());
                            attrValueEdit.setCreatorId(attrValueEdit.getLastModifierId());
                            attrValueEdit.setSource(AttrValueSourceEnum.PLATFORM.getValue());
                            attrValueEditDao.insert(attrValueEdit);
                        });
                    }
            }else {
                throw new GlobalException(GoodsExceptionEnum.ATTR_UPDATE_ERROR);
            }
        }

        return true;
    }

    @Override
    public boolean updateAttrItemEditSort(AttrItemDTO attrItemDTO) {
        AttrItemEdit attrItemEdit = attrItemEditDao.selectById(attrItemDTO.getAttrId());
        if (null != attrItemEdit && null != attrItemDTO.getAttrId() && null != attrItemDTO.getSortNo()) {
            AttrItemEdit updateAttrItemEdit = new AttrItemEdit();
            BeanCopyUtil.copy(attrItemDTO, updateAttrItemEdit);
            updateAttrItemEdit.setCreatedTime(null);
            updateAttrItemEdit.setId(attrItemDTO.getAttrId());
            updateAttrItemEdit.setSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
            updateAttrItemEdit.setVersion(attrItemEdit.getVersion());
            return attrItemEditDao.updateById(updateAttrItemEdit) > 0;
        }
        return false;
    }

    @Override
    public PageInfo<AttrItemDTO> selectAttrItemEdit(AttrItemReq attrItemReq) {
        Page page = PageDataUtil.buildPageParam(attrItemReq);
        List<AttrItemEdit> attrItemEditList = attrItemEditDao.queryAttrItemEdit(page, attrItemReq);
        page.setRecords(attrItemEditList);
        if (!CollectionUtils.isEmpty(attrItemEditList)) {
            List<AttrItemDTO> attrItemDTOList = new ArrayList<>(attrItemReq.getPageSize());
            attrItemEditList.forEach(attrItemEdit -> {
                AttrItemDTO attrItemDTO = new AttrItemDTO();
                attrItemDTO.setAttrId(attrItemEdit.getId());
                BeanCopyUtil.copy(attrItemEdit, attrItemDTO);
                switch (attrItemEdit.getSyncStatus()) {
                    case 0:
                        attrItemDTO.setStatus("待同步");
                        break;
                    default:
                        attrItemDTO.setStatus("已同步");
                }
                //待删除覆盖待同步
                if (attrItemEdit.getDeletedFlag() == 2) {
                    attrItemDTO.setStatus("待删除");
                }
                AttrGroupEdit attrGroupEdit = attrGroupEditDao.selectById(attrItemEdit.getGroupId());
                attrItemDTO.setGroupName(null == attrGroupEdit ? "" : attrGroupEdit.getName());
                attrItemDTOList.add(attrItemDTO);
            });
            page.setRecords(attrItemDTOList);
        }
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public PageInfo<AttrItemDTO> selectAttrItemFormal(AttrItemReq attrItemReq) {
        Page page = PageDataUtil.buildPageParam(attrItemReq);
        List<AttrItem> attrItemList = attrItemDao.queryAttrItemFormal(page, attrItemReq);
        page.setRecords(attrItemList);
        if (!CollectionUtils.isEmpty(attrItemList)) {
            List<AttrItemDTO> attrItemDTOList = new ArrayList<>(attrItemReq.getPageSize());
            attrItemList.forEach(attrItem-> {
                AttrItemDTO attrItemDTO = new AttrItemDTO();
                attrItemDTO.setAttrId(attrItem.getId());
                BeanCopyUtil.copy(attrItem, attrItemDTO);
                AttrGroup attrGroup = attrGroupDao.selectById(attrItem.getGroupId());
                attrItemDTO.setGroupName(null == attrGroup ? "" : attrGroup.getName());
                attrItemDTOList.add(attrItemDTO);
            });
            page.setRecords(attrItemDTOList);
        }
        return PageDataUtil.copyPageInfo(page);
    }
}
