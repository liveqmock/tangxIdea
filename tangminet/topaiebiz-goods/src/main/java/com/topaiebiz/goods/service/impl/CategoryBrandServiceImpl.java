package com.topaiebiz.goods.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.brand.dao.BrandDao;
import com.topaiebiz.goods.brand.dto.BrandDto;
import com.topaiebiz.goods.brand.entity.BrandEntity;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryDao;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryEntity;
import com.topaiebiz.goods.dao.BackendCategoryEditDao;
import com.topaiebiz.goods.dao.CategoryBrandDao;
import com.topaiebiz.goods.dao.CategoryBrandEditDao;
import com.topaiebiz.goods.dto.*;
import com.topaiebiz.goods.entity.BackendCategoryEdit;
import com.topaiebiz.goods.entity.CategoryBrand;
import com.topaiebiz.goods.entity.CategoryBrandEdit;
import com.topaiebiz.goods.enums.GoodsExceptionEnum;
import com.topaiebiz.goods.enums.SyncStatusEnum;
import com.topaiebiz.goods.service.BackendCategoryNewService;
import com.topaiebiz.goods.service.CategoryBrandService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hecaifeng on 2018/5/18.
 */
@Service
public class CategoryBrandServiceImpl implements CategoryBrandService {

    @Autowired
    private CategoryBrandDao categoryBrandDao;
    @Autowired
    private CategoryBrandEditDao categoryBrandEditDao;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private BackendCategoryDao backendCategoryDao;
    @Autowired
    private BackendCategoryEditDao backendCategoryEditDao;
    @Autowired
    private BackendCategoryNewService backendCategoryNewService;

    @Override
    public PageInfo<CategoryBrandEditDTO> getCategoryBrandList(CategoryIdDTO categoryIdDTO) {
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
        Page<CategoryBrandEditDTO> page = PageDataUtil.buildPageParam(pagePO);
        List<CategoryBrandEditDTO> list = categoryBrandEditDao.selectCategortBrand(page, categoryIdDTO);
        page.setRecords(list);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    @Transactional
    public boolean saveCategoryBrand(CategoryBrandAddDTO categoryBrandAddDTO) {
        if (categoryBrandAddDTO == null) {
            return false;
        }
        Long categoryId = categoryBrandAddDTO.getCategoryId();
        if (categoryId == null) {
            throw new GlobalException(GoodsExceptionEnum.CATEGORY_ID_NEED);
        }
        Long[] addBrandId = categoryBrandAddDTO.getAddBrandId();
        Long[] updateBrandId = categoryBrandAddDTO.getUpdateBrandId();
        boolean b1 = addBrandIds(addBrandId, categoryId);
        boolean b2 = updateBrandIds(updateBrandId, categoryId);
        if (b1 == false || b2 == false) {
            return false;
        }
        return true;
    }

    private boolean addBrandIds(Long[] addBrandId, Long categoryId) {
        boolean b = false;
        if (addBrandId.length < 0) {
            return b;
        }
        for (Long brandId : addBrandId) {
            CategoryBrandEdit categoryBrandEdit1 = getCategoryBrandEdit(categoryId, brandId);
            if (categoryBrandEdit1 != null) {
                //SyncStatus 0 deletedFlag 2 待删除变成待同步   SyncStatus 0 deletedFlag 0
                if (categoryBrandEdit1.getSyncStatus().equals(SyncStatusEnum.SYNC_NO.getValue()) && categoryBrandEdit1.getDeletedFlag().equals((byte) 2)) {
                    CategoryBrandEdit categoryBrandEdit = new CategoryBrandEdit();
                    categoryBrandEdit.cleanInit();
                    categoryBrandEdit.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
                    categoryBrandEdit.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
                    categoryBrandEdit.setLastModifiedTime(new Date());
                    categoryBrandEdit.setId(categoryBrandEdit1.getId());
                    b = categoryBrandEditDao.updateById(categoryBrandEdit) > 0;
                }
                //SyncStatus 1 deletedFlag 2  待删除变成已同步   SyncStatus 1 deletedFlag 0
                if (categoryBrandEdit1.getSyncStatus().equals(SyncStatusEnum.SYNC_YES.getValue())
                        && categoryBrandEdit1.getDeletedFlag().equals((byte) 2)) {
                    CategoryBrandEdit categoryBrandEdit = new CategoryBrandEdit();
                    categoryBrandEdit.cleanInit();
                    categoryBrandEdit.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
                    categoryBrandEdit.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
                    categoryBrandEdit.setLastModifiedTime(new Date());
                    categoryBrandEdit.setId(categoryBrandEdit1.getId());
                    categoryBrandEditDao.updateById(categoryBrandEdit);
                }
            } else {
                //新增  待同步  0
                CategoryBrandEdit categoryBrandEdit = new CategoryBrandEdit();
                categoryBrandEdit.setBrandId(brandId);
                categoryBrandEdit.setCategoryId(categoryId);
                categoryBrandEdit.setSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
                categoryBrandEdit.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
                categoryBrandEdit.setCreatedTime(new Date());
                b = categoryBrandEditDao.insert(categoryBrandEdit) > 0;
            }
        }
        return b;
    }

    private CategoryBrandEdit getCategoryBrandEdit(Long categoryId, Long brandId) {
        CategoryBrandEdit categoryBrandEdit = categoryBrandEditDao.selectCategortBrandEdit(categoryId, brandId);
        return categoryBrandEdit;
    }

    private boolean updateBrandIds(Long[] updateBrandId, Long categoryId) {
        boolean b = false;
        for (Long brandId : updateBrandId) {
            CategoryBrandEdit categoryBrandEdit1 = getCategoryBrandEdit(categoryId, brandId);
            if (categoryBrandEdit1 == null) {
                throw new GlobalException(GoodsExceptionEnum.BRAND_ID_ERROR);
            }
            //SyncStatus 0 deletedFlag 0  待同步变成待删除   SyncStatus 0 deletedFlag 2
            if (categoryBrandEdit1.getSyncStatus().equals(SyncStatusEnum.SYNC_NO.getValue())
                    && categoryBrandEdit1.getDeletedFlag().equals(Constants.DeletedFlag.DELETED_NO)) {
                CategoryBrandEdit categoryBrandEdit = new CategoryBrandEdit();
                categoryBrandEdit.cleanInit();
                categoryBrandEdit.setDeleteFlag((byte)2);
                categoryBrandEdit.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
                categoryBrandEdit.setLastModifiedTime(new Date());
                categoryBrandEdit.setId(categoryBrandEdit1.getId());
                b = categoryBrandEditDao.updateById(categoryBrandEdit) > 0;
            }
            //SyncStatus 1 deletedFlag 0  已同步变成待删除   SyncStatus 1 deletedFlag 2
            if (categoryBrandEdit1.getSyncStatus().equals(SyncStatusEnum.SYNC_YES.getValue())
                    && categoryBrandEdit1.getDeletedFlag().equals(Constants.DeletedFlag.DELETED_NO)) {
                CategoryBrandEdit categoryBrandEdit = new CategoryBrandEdit();
                categoryBrandEdit.cleanInit();
                categoryBrandEdit.setDeleteFlag((byte)2);
                categoryBrandEdit.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
                categoryBrandEdit.setLastModifiedTime(new Date());
                categoryBrandEdit.setId(categoryBrandEdit1.getId());
                b = categoryBrandEditDao.updateById(categoryBrandEdit) > 0;
            }
        }
        return b;
    }

    @Override
    public List<BrandDto> getCategoryBrand(Long categoryId) {
        List<Long> brandIds = getCategoryBrandIds(categoryId);
        if (CollectionUtils.isEmpty(brandIds)) {
            return Collections.EMPTY_LIST;
        }
        EntityWrapper<BrandEntity> brandCondition = new EntityWrapper<>();
        brandCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        brandCondition.in("id", brandIds);
        List<BrandEntity> brandEntities = brandDao.selectList(brandCondition);
        List<BrandDto> brandDtos = BeanCopyUtil.copyList(brandEntities, BrandDto.class);
        return brandDtos;
    }

    @Override
    public List<BrandDto> getBrand(Long categoryId) {
        List<Long> brandIds = getCategoryBrandIds(categoryId);
        EntityWrapper<BrandEntity> brandCondition = new EntityWrapper<>();
        brandCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        brandCondition.notIn("id", brandIds);
        List<BrandEntity> brandEntities = brandDao.selectList(brandCondition);
        List<BrandDto> brandDtos = BeanCopyUtil.copyList(brandEntities, BrandDto.class);
        return brandDtos;
    }

    @Override
    public boolean editCategoryNameSortNo(CategoryBrandSortNoDTO categoryBrandSortNoDTO) {
        CategoryBrandEdit categoryBrand = categoryBrandEditDao.selectById(categoryBrandSortNoDTO.getId());
        if (categoryBrand == null) {
            return false;
        }
        CategoryBrandEdit update = new CategoryBrandEdit();
        update.cleanInit();
        update.setSyncStatus(SyncStatusEnum.SYNC_NO.getValue());
        update.setSortNo(categoryBrandSortNoDTO.getSortNo());
        update.setId(categoryBrand.getId());
        update.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        update.setLastModifiedTime(new Date());
        return categoryBrandEditDao.updateById(update) > 0;
    }

    @Override
    public PageInfo<CategoryBrandDTO> queryCategoryBrand(CategoryIdDTO categoryIdDTO) {
        if (categoryIdDTO.getCategoryId() == null) {
            throw new GlobalException(GoodsExceptionEnum.CATEGORY_ID_NEED);
        }
        BackendCategoryEntity backendCategoryEntity =
                backendCategoryDao.selectById(categoryIdDTO.getCategoryId());
        if (backendCategoryEntity == null) {
            throw new GlobalException(GoodsExceptionEnum.CATEGORY_NOT_EXIST);
        }
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(categoryIdDTO.getPageNo());
        pagePO.setPageSize(categoryIdDTO.getPageSize());
        Page<CategoryBrandDTO> page = PageDataUtil.buildPageParam(pagePO);
        List<CategoryBrandDTO> list = categoryBrandEditDao.selectCategortBrands(page, categoryIdDTO);
        page.setRecords(list);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public List<String> getCategorys(Long brandId) {
        if (brandId == null) {
            throw new GlobalException(GoodsExceptionEnum.BRAND_ID_NOT_NULL);
        }
        EntityWrapper<CategoryBrand> cond = new EntityWrapper<>();
        cond.eq("brandId", brandId);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<CategoryBrand> categoryBrands = categoryBrandDao.selectList(cond);
        if (CollectionUtils.isEmpty(categoryBrands)) {
            throw new GlobalException(GoodsExceptionEnum.CATEGORY_NOT_EXIST);
        }
        List<Long> categoryIds = categoryBrands.stream().map(categoryBrand -> categoryBrand.getCategoryId()).collect(Collectors.toList());
        List<String> strings = backendCategoryNewService.brandRelateCategoryEdit(categoryIds);
        return strings;
    }

    private List<Long> getCategoryBrandIds(Long categoryId) {
        if (categoryId == null && categoryId > 0) {
            throw new GlobalException(GoodsExceptionEnum.CATEGORY_ID_NEED);
        }
        List<CategoryBrandEdit> allCcategoryBrandEdit = new ArrayList<>();
        //待同步的品牌  (deletedFlag 0 syncStatus 0)
        EntityWrapper<CategoryBrandEdit> cond = new EntityWrapper<>();
        cond.eq("categoryId", categoryId);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("syncStatus", SyncStatusEnum.SYNC_NO.getValue());
        List<CategoryBrandEdit> categoryBrandEditsNo = categoryBrandEditDao.selectList(cond);
        allCcategoryBrandEdit.addAll(categoryBrandEditsNo);
        //已同步的品牌 (deletedFlag 0 syncStatus 1)
        EntityWrapper<CategoryBrandEdit> condBrandIds = new EntityWrapper<>();
        condBrandIds.eq("categoryId", categoryId);
        condBrandIds.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condBrandIds.eq("syncStatus", SyncStatusEnum.SYNC_YES.getValue());
        List<CategoryBrandEdit> categoryBrandEditsYes = categoryBrandEditDao.selectList(condBrandIds);
        allCcategoryBrandEdit.addAll(categoryBrandEditsYes);
        if (CollectionUtils.isEmpty(allCcategoryBrandEdit)) {
            return Collections.EMPTY_LIST;
        }
        return allCcategoryBrandEdit.stream().map(categoryBrandEdit -> categoryBrandEdit.getBrandId()).collect(Collectors.toList());
    }
}
