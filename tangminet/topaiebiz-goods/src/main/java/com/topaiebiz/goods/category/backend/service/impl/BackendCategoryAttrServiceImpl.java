package com.topaiebiz.goods.category.backend.service.impl;

import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryAttrDao;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryAttrDto;
import com.topaiebiz.goods.category.backend.dto.ItemBackendCategoryAttrDto;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryAttrEntity;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryEntity;
import com.topaiebiz.goods.category.backend.exception.BackendCategoryExceptionEnum;
import com.topaiebiz.goods.category.backend.service.BackendCategoryAttrService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * Created by dell on 2018/1/10.
 */
@Service
public class BackendCategoryAttrServiceImpl implements BackendCategoryAttrService{

    @Autowired
    private BackendCategoryAttrDao backendCategoryAttrDao;

    @Override
    public List<BackendCategoryAttrEntity> getListBackendCategoryAttr(ItemBackendCategoryAttrDto itemBackendCategoryAttrDto) throws GlobalException {
        /** 判断商品后台类目id是否为空 */
        if (itemBackendCategoryAttrDto.getBelongCategory() == null) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORYATTR_BELONGCATEGORY_NOT_NULL);
        }
        List<BackendCategoryAttrEntity> selectListBackendCategoryAttr = backendCategoryAttrDao.selectListBackendCategoryAttr(itemBackendCategoryAttrDto);
        if(!(null == selectListBackendCategoryAttr || selectListBackendCategoryAttr.size() == 0)) {
            for (BackendCategoryAttrEntity backendCategoryAttrEntity : selectListBackendCategoryAttr) {
                if(backendCategoryAttrEntity.getDefaultUnit() != null) {
                    if(backendCategoryAttrEntity.getDefaultUnit().equals(" ")) {
                        backendCategoryAttrEntity.setDefaultUnit(null);
                    }
                }
            }
        }
        return selectListBackendCategoryAttr;
    }

    @Override
    public Integer modifyBackendCategoryAttr(BackendCategoryAttrDto backendCategoryAttrDto) throws GlobalException {
        /*List<BackendCategoryAttrDto> backendCategoryAttrByName = backendCategoryAttrDao
                .selectBackendCategoryAttrByNameAndId(backendCategoryAttrDto);
        *//** 判断商品后台类目名称是否重复 *//*
        if (backendCategoryAttrByName != null) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORYATTR_NAME_NOT_REPETITION);
        }*/
        BackendCategoryAttrEntity backendCategoryAttr = backendCategoryAttrDao
                .selectById(backendCategoryAttrDto.getId());
        if(backendCategoryAttrDto.getValueList() == null || backendCategoryAttrDto.getValueList() ==""){
            backendCategoryAttrDto.setValueList(" ");
        }
        BeanCopyUtil.copy(backendCategoryAttrDto, backendCategoryAttr);
        backendCategoryAttr.setLastModifiedTime(new Date());
        backendCategoryAttr.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        return backendCategoryAttrDao.updateById(backendCategoryAttr);
    }

    @Override
    public Integer removeBackendCategoryAttr(Long id) throws GlobalException {
        /** 判断商品后台类目属性id是非为空 */
        if (id == null) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORYATTR_ID_NOT_NULL);
        }
        BackendCategoryAttrEntity backendCategoryAttr = backendCategoryAttrDao.selectBackendCategoryAttrEntityById(id);
        /** 判断商品后台类目属性是非存在 */
        if (backendCategoryAttr == null) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORYATTR_ID_NOT_EXIST);
        }
        return backendCategoryAttrDao.deleteBackendCategoryAttr(id);
    }

    @Override
    public Integer saveBackendCategoryAttr(BackendCategoryAttrEntity backendCategoryAttr) throws GlobalException {
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        if(storeId == null){
            List<BackendCategoryAttrEntity> backendCategoryAttrByName = backendCategoryAttrDao
                    .selectBackendCategoryAttrByName(backendCategoryAttr);
            /** 对商品后台类目属性名称重复验证 */
            if (CollectionUtils.isNotEmpty(backendCategoryAttrByName)) {
                throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORYATTR_NAME_NOT_REPETITION);
            }
        }
        BackendCategoryAttrDto maxSortNoByBackendCategory = backendCategoryAttrDao
                .selectMaxSortNoByBackendCategoryAttr(backendCategoryAttr.getBelongCategory());
        // 添加排序号时每个类目下面的属性排序号从0 开始
        if (maxSortNoByBackendCategory == null) {
            // 不存在最大值为0
            backendCategoryAttr.setSortNo(0);
        } else {
            // 存在时在最大排序号上加1
            Integer sortNo = maxSortNoByBackendCategory.getSortNo();
            backendCategoryAttr.setSortNo(sortNo + 1);
        }
        backendCategoryAttr.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        backendCategoryAttr.setCreatedTime(new Date());
        return backendCategoryAttrDao.insert(backendCategoryAttr);
    }

    @Override
    public BackendCategoryAttrDto findBackendCategoryAttrById(Long id) throws GlobalException {
        /** 判断商品后台类目属性是否为空 */
        if (id == null) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORYATTR_ID_NOT_NULL);
        }
        BackendCategoryAttrDto backendCategoryAttrById = backendCategoryAttrDao.selectBackendCategoryAttrById(id);
        /** 判断商品后台类目属性是否存在 */
        if (backendCategoryAttrById == null) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORYATTR_ID_NOT_EXIST);
        }
        if (backendCategoryAttrById.getDefaultUnit() == null) {
            backendCategoryAttrById.setDefaultUnit(" ");
        }
        return backendCategoryAttrById;
    }

    @Override
    public Integer modifyBackendCategoryAttrBySortNo(List<BackendCategoryAttrDto> backendCategoryAttrDto) {
        Integer i = 0;
        if (!(backendCategoryAttrDto == null || backendCategoryAttrDto.size() == 0)) {
            for (BackendCategoryAttrDto backendCategoryAttrDto2 : backendCategoryAttrDto) {
                BackendCategoryAttrEntity backendCategoryAttr = backendCategoryAttrDao
                        .selectById(backendCategoryAttrDto2.getId());
                BeanCopyUtil.copy(backendCategoryAttrDto, backendCategoryAttr);
                // 将新的排序号更新到商品后台类目属性中
                backendCategoryAttr.setSortNo(backendCategoryAttrDto2.getSortNo());
                i = backendCategoryAttrDao.updateById(backendCategoryAttr);
            }
        }
        return i;
    }

    @Override
    public Integer saveGoodsSpuBackendCategoryAttr(BackendCategoryAttrDto backendCategoryAttrDto) {
        BackendCategoryAttrEntity backendCategoryAttr = new BackendCategoryAttrEntity();
        BeanCopyUtil.copy(backendCategoryAttrDto,backendCategoryAttr);
        /** 对商品后台类目属性名称重复验证 */
       /* BackendCategoryAttrEntity backendCategoryAttrByName = backendCategoryAttrDao
                .selectBackendCategoryAttrByName(backendCategoryAttr);
        if (backendCategoryAttrByName != null) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORYATTR_NAME_NOT_REPETITION);
        }*/
        BackendCategoryAttrDto maxSortNoByBackendCategory = backendCategoryAttrDao
                .selectMaxSortNoByBackendCategoryAttr(backendCategoryAttrDto.getBelongCategory());
        // 添加排序号时每个类目下面的属性排序号从0 开始
        if (maxSortNoByBackendCategory == null) {
            // 不存在最大值为0
            backendCategoryAttr.setSortNo(0);
        } else {
            // 存在时在最大排序号上加1
            Integer sortNo = maxSortNoByBackendCategory.getSortNo();
            backendCategoryAttr.setSortNo(sortNo + 1);
        }
        backendCategoryAttr.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        backendCategoryAttr.setCreatedTime(new Date());
        return backendCategoryAttrDao.insert(backendCategoryAttr);
    }

    @Override
    public List<BackendCategoryAttrEntity> getSpuListBackendCategoryAttr(ItemBackendCategoryAttrDto itemBackendCategoryAttrDto) {
        /** 判断商品后台类目id是否为空 */
        if (itemBackendCategoryAttrDto.getBelongCategory() == null) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORYATTR_BELONGCATEGORY_NOT_NULL);
        }
        List<BackendCategoryAttrEntity> selectListBackendCategoryAttr = backendCategoryAttrDao.selectSpuListBackendCategoryAttr(itemBackendCategoryAttrDto);
        if(!(null == selectListBackendCategoryAttr || selectListBackendCategoryAttr.size() == 0)) {
            for (BackendCategoryAttrEntity backendCategoryAttrEntity : selectListBackendCategoryAttr) {
                if(backendCategoryAttrEntity.getDefaultUnit() != null) {
                    if(backendCategoryAttrEntity.getDefaultUnit().equals(" ")) {
                        backendCategoryAttrEntity.setDefaultUnit(null);
                    }
                }
            }
        }
        return selectListBackendCategoryAttr;
    }


}
