package com.topaiebiz.goods.repair.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryDao;
import com.topaiebiz.goods.category.backend.dao.BackendMerchantCategoryDao;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryEntity;
import com.topaiebiz.goods.category.backend.entity.BackendMerchantCategoryEntity;
import com.topaiebiz.goods.repair.dao.CategoryRepairDao;
import com.topaiebiz.goods.repair.service.CategoryRepairService;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.constants.MerchantConstants;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by hecaifeng on 2018/2/28.
 */
@Service
public class CategoryRepairServiceImpl implements CategoryRepairService {

    @Autowired
    private CategoryRepairDao categoryRepairDao;

    @Autowired
    private BackendCategoryDao backendCategoryDao;

    @Autowired
    private StoreApi storeApi;

    @Autowired
    private BackendMerchantCategoryDao backendMerchantCategoryDao;

    @Override
    public void addCategory() {
        EntityWrapper<BackendMerchantCategoryEntity> backendMerchantCategoryEntity = new EntityWrapper<BackendMerchantCategoryEntity>();
        backendMerchantCategoryEntity.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        backendMerchantCategoryEntity.ne("categoryId", "null");
        List<BackendMerchantCategoryEntity> backendMerchantCategoryEntities = categoryRepairDao.selectList(backendMerchantCategoryEntity);
        if (CollectionUtils.isNotEmpty(backendMerchantCategoryEntities)) {
            for (BackendMerchantCategoryEntity backendMerchantCategory : backendMerchantCategoryEntities) {
                if (backendMerchantCategory.getCategoryId() != null || backendMerchantCategory.getCategoryId() != 0) {
                    //判断是否为第三级类目
                    BackendCategoryEntity threeBackendCategory = this.selectCategory(backendMerchantCategory.getCategoryId());
                    //如果是第三级类目添加第二级类目
                    if (threeBackendCategory != null) {
                        if (threeBackendCategory.getLevel() == 3) {
                            //第二级类目
                            BackendCategoryEntity twoBackendCategory = this.selectCategory(threeBackendCategory.getParentId());
                            if (twoBackendCategory != null) {
                                //添加第二级类目
                                this.addCategorys(twoBackendCategory, backendMerchantCategory);
                                // 第一级类目
                                BackendCategoryEntity oneBackendCategory = this.selectCategory(twoBackendCategory.getParentId());
                                if (oneBackendCategory != null) {
                                    //添加第一级类目
                                    this.addCategorys(oneBackendCategory, backendMerchantCategory);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addOwnCategory(Long merchantId) {
        StoreInfoDetailDTO storeByMerchantId = storeApi.getStoreByMerchantId(merchantId);
        Integer ownShop = storeByMerchantId.getOwnShop();
        if (MerchantConstants.IsOwnShop.NOSELF_STORE.getCode().equals(ownShop)) {
            throw new GlobalException(null);
        }
        EntityWrapper<BackendCategoryEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<BackendCategoryEntity> backendCategoryEntities = backendCategoryDao.selectList(cond);
        if (CollectionUtils.isNotEmpty(backendCategoryEntities)) {
            for (BackendCategoryEntity backendCategoryEntity : backendCategoryEntities) {
                BackendMerchantCategoryEntity backendMerchantCategoryEntity = new BackendMerchantCategoryEntity();
                backendMerchantCategoryEntity.cleanInit();
                backendMerchantCategoryEntity.setMerchantId(merchantId);
                backendMerchantCategoryEntity.setCategoryId(backendCategoryEntity.getId());
                backendMerchantCategoryEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
                BackendMerchantCategoryEntity backendMerchantCategoryEntity1 = backendMerchantCategoryDao.selectOne(backendMerchantCategoryEntity);
                if (backendMerchantCategoryEntity1 == null) {
                    BackendMerchantCategoryEntity backendMerchantCategory = new BackendMerchantCategoryEntity();
                    backendMerchantCategory.setCategoryId(backendCategoryEntity.getId());
                    backendMerchantCategory.setMerchantId(merchantId);
                    backendMerchantCategory.setCreatedTime(new Date());
                    backendMerchantCategoryDao.insert(backendMerchantCategory);
                }
            }
        }
    }

    private BackendCategoryEntity selectCategory(Long categoryId) {
        BackendCategoryEntity backendCategory = new BackendCategoryEntity();
        backendCategory.clearInit();
        backendCategory.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        backendCategory.setId(categoryId);
        BackendCategoryEntity backendCategoryEntity = backendCategoryDao.selectOne(backendCategory);
        return backendCategoryEntity;
    }

    private void addCategorys(BackendCategoryEntity backendCategoryEntity, BackendMerchantCategoryEntity backendMerchantCategoryEntity) {
        BackendMerchantCategoryEntity backendMerchantCategory = backendCategoryDao
                .selectBackendMerchantCategoryById(backendCategoryEntity.getId(), backendMerchantCategoryEntity.getMerchantId());
        if (backendMerchantCategory == null) {
            BackendMerchantCategoryEntity backendMerchantCategoryEntity2 = new BackendMerchantCategoryEntity();
            backendMerchantCategoryEntity2.setMerchantId(backendMerchantCategoryEntity.getMerchantId());
            backendMerchantCategoryEntity2.setStoreId(backendMerchantCategoryEntity.getMerchantId());
            backendMerchantCategoryEntity2.setCategoryId(backendCategoryEntity.getId());
            backendMerchantCategoryEntity2.setCreatedTime(new Date());
            categoryRepairDao.insert(backendMerchantCategoryEntity2);
        }
    }
}
