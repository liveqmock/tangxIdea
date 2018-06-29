package com.topaiebiz.goods.category.backend.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryAttrDao;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryDao;
import com.topaiebiz.goods.category.backend.dao.BackendMerchantCategoryDao;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryAdd;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryDto;
import com.topaiebiz.goods.category.backend.dto.BackendMerchantCategoryDto;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryAttrEntity;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryEntity;
import com.topaiebiz.goods.category.backend.entity.BackendMerchantCategoryEntity;
import com.topaiebiz.goods.category.backend.exception.BackendCategoryExceptionEnum;
import com.topaiebiz.goods.category.backend.service.BackendCategoryService;
import com.topaiebiz.goods.category.frontend.dao.FrontBackCategoryDao;
import com.topaiebiz.goods.constants.GoodsConstants;
import com.topaiebiz.goods.goodsenum.BackendCategoryLevelEnum;
import com.topaiebiz.goods.goodsenum.ItemStatusEnum;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import com.topaiebiz.goods.spu.dao.GoodsSpuDao;
import com.topaiebiz.goods.spu.dto.GoodsSpuDto;
import com.topaiebiz.goods.spu.entity.GoodsSpuEntity;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * Description 商品后台类目实现类
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年8月24日 下午4:48:07
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
public class BackendCategoryServiceImpl implements BackendCategoryService {

    @Autowired
    private BackendCategoryDao backendCategoryDao;

    /**
     * 商品后台类目属性。
     */
    @Autowired
    private BackendCategoryAttrDao backendCategoryAttrDao;

    /**
     * 商品spu。
     */
    @Autowired
    private GoodsSpuDao goodsSpuDao;

    /**
     * 商品item。
     */
    @Autowired
    private ItemDao itemDao;

    @Autowired
    private FrontBackCategoryDao frontBackCategoryDao;

    @Autowired
    private BackendMerchantCategoryDao backendMerchantCategoryDao;

    @Autowired
    private StoreApi storeApi;

    @Override
    public List<BackendCategoryDto> getListLevelBackendCategory(BackendCategoryDto backendCategoryDto)
            throws GlobalException {
        /** 判断parentId是否为空 */
        if (null == backendCategoryDto.getParentId()) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_PARENTID_NOT_NULL);
        }
        if (backendCategoryDto.getParentId() == 0) {
            return backendCategoryDao.selectListOneLevelBackendCategory(backendCategoryDto);
        } else {
            return backendCategoryDao.selectListTwoOrThreeLevelBackendCategory(backendCategoryDto);
        }
    }

    @Override
    public Integer saveBackendCategory(BackendCategoryDto backendCategoryDto) throws GlobalException {
        Integer i = 0;
        BackendCategoryEntity backendCategoryEntity = new BackendCategoryEntity();
        BeanCopyUtil.copy(backendCategoryDto, backendCategoryEntity);
        if (backendCategoryEntity.getLevel() != 3) {
            BackendCategoryEntity BackendCategoryByName = backendCategoryDao.selectBackendCategoryByName(backendCategoryEntity);
            /** 判断商品后台类目名称是否重复 */
            if (BackendCategoryByName != null) {
                throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_NAME_NOT_REPETITION);
            }
        } else {
            BackendCategoryEntity backendCategoryEntity1 = new BackendCategoryEntity();
            backendCategoryEntity1.clearInit();
            backendCategoryEntity1.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            backendCategoryEntity1.setParentId(backendCategoryEntity.getParentId());
            backendCategoryEntity1.setName(backendCategoryEntity.getName());
            BackendCategoryEntity backendCategoryEntity2 = backendCategoryDao.selectOne(backendCategoryEntity1);
            /** 判断商品后台类目名称是否重复 */
            if (backendCategoryEntity2 != null) {
                throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_NAME_NOT_REPETITION);
            }
        }

        backendCategoryEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        backendCategoryEntity.setCreatedTime(new Date());
        i = backendCategoryDao.insert(backendCategoryEntity);
        return i;
    }

    @Override
    public Integer modifyBackendCategory(BackendCategoryDto backendCategoryDto) throws GlobalException {
        BackendCategoryDto backendCategoryByName = backendCategoryDao
                .selectBackendCategoryByNameAndId(backendCategoryDto);
        /** 判断商品后台类目名称重复验证 */
        if (backendCategoryByName != null) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_NAME_NOT_REPETITION);
        }
        BackendCategoryEntity backendCategory = backendCategoryDao.selectById(backendCategoryDto.getId());
        BeanCopyUtil.copy(backendCategoryDto, backendCategory);
        backendCategory.setLastModifiedTime(new Date());
        backendCategory.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        return backendCategoryDao.updateById(backendCategory);
    }

    @Override
    public BackendCategoryDto findBackendCategoryById(Long id) throws GlobalException {
        /** 判断商品后台类目是否为空 */
        if (id == null) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_ID_NOT_NULL);
        }
        BackendCategoryDto backendCategoryById = backendCategoryDao.selectBackendCategoryById(id);
        /** 判断商品后台类目是否存在 */
        if (backendCategoryById == null) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_ID_NOT_EXIST);
        }
        return backendCategoryById;
    }

    @Override
    public Integer removeBackendCategory(Long id) throws GlobalException {
        Integer i = 0;
        /** 判断id是否为空 */
        if (null == id) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_ID_NOT_NULL);
        }
        BackendCategoryEntity backendCategoryById = backendCategoryDao.selectById(id);
        if (null == backendCategoryById) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_ID_NOT_EXIST);
        }
        /** 通过id查询本级类目 */
        List<BackendCategoryEntity> allBackendCategory = this.getAllBackendCategory(id);
        for (BackendCategoryEntity backendCategory : allBackendCategory) {
            /** 查询叶子类目下的属性 */
            EntityWrapper<BackendCategoryAttrEntity> entityEntityWrapper = new EntityWrapper<>();
            entityEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            entityEntityWrapper.eq("belongCategory", backendCategory.getId());
            List<BackendCategoryAttrEntity> BackendCategoryAttr = backendCategoryAttrDao.selectList(entityEntityWrapper);

            for (BackendCategoryAttrEntity backendCategoryAttrs : BackendCategoryAttr) {
                if (backendCategoryAttrs != null) {
                    /** 删除叶子类目下的属性 */
                    i = backendCategoryAttrDao.deleteBackendCategoryAttr(backendCategoryAttrs.getId());
                }
            }
            // 查询本类目下面是否有spu商品
            List<GoodsSpuDto> goodsSpuDtos = goodsSpuDao.selectGoodsSpuByBelongCategory(backendCategory.getId());
            if (!(goodsSpuDtos == null || goodsSpuDtos.size() == 0)) {
                for (GoodsSpuDto goodsSpuDto : goodsSpuDtos) {
                    GoodsSpuEntity goodsSpu = goodsSpuDao.selectById(goodsSpuDto.getId());
                    BeanCopyUtil.copy(goodsSpuDto, goodsSpu);
                    goodsSpu.setBelongCategory(3L);
                    goodsSpuDao.updateById(goodsSpu);
                }
            }
            // 查询本类目下面是否有item商品
            List<ItemEntity> itemEntities = itemDao.selectItemByBelongCategory(backendCategory.getId());
            if (!(itemEntities == null || itemEntities.size() == 0)) {
                for (ItemEntity itemEntity : itemEntities) {
                    itemEntity.setBelongCategory(3L);
                    itemEntity.setStatus(ItemStatusEnum.ITEM_STATUS_REMOVE.getCode());
                    itemDao.updateById(itemEntity);
                }
            }
            i = backendCategoryDao.deleteBackendCategory(backendCategory.getId());
        }
        return i;
    }

    /**
     * @param id 商品后台类目id
     * @return
     * @Description 查询子类目
     * @Author Hedda
     */
    private List<BackendCategoryEntity> getAllBackendCategory(Long id) {
        /** 创建一个resultList集合 */
        List<BackendCategoryEntity> resultList = new ArrayList<BackendCategoryEntity>();
        /** 通过上级类目id查询出下级所有类目 */
        BackendCategoryEntity selectBackendCategoryById = backendCategoryDao.selectById(id);
        resultList.add(selectBackendCategoryById);
        List<BackendCategoryEntity> listBackendCategory = backendCategoryDao.selectAllBackendCategoryById(id);
        if (!(listBackendCategory == null || listBackendCategory.size() == 0)) {
            resultList.addAll(listBackendCategory);
            for (BackendCategoryEntity backendCategory : listBackendCategory) {
                List<BackendCategoryEntity> childList = this.getAllBackendCategory(backendCategory.getId());
                resultList.addAll(childList);
            }
            return resultList;
        } else {
            return resultList;
        }
    }

    @Override
    public Integer removeBackendCategoryByStoreId(Long storeId) {
        Integer i = null;
        List<BackendCategoryEntity> backendCategoryEntities = backendCategoryDao
                .selectBackendCategoryByStoreId(storeId);
        if (!(backendCategoryEntities == null || backendCategoryEntities.size() == 0)) {
            for (BackendCategoryEntity backendCategoryEntity : backendCategoryEntities) {
                i = backendCategoryDao.deleteBackendCategory(backendCategoryEntity.getId());
            }
        }
        return i;
    }


    @Override
    public Integer saveMerchantBackendCategory(BackendCategoryDto backendCategory) throws GlobalException {
        Integer i = null;
        BackendCategoryEntity backendCategoryEntity = new BackendCategoryEntity();
        BeanCopyUtil.copy(backendCategory, backendCategoryEntity);
        BackendCategoryEntity BackendCategoryByName = backendCategoryDao.selectBackendCategoryByName(backendCategoryEntity);
        /** 判断商品后台类目名称是否重复 */
        if (BackendCategoryByName != null) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_NAME_NOT_REPETITION);
        }
        backendCategoryEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        backendCategoryEntity.setCreatedTime(new Date());
        i = backendCategoryDao.insert(backendCategoryEntity);
        return i;
    }

    @Override
    public List<BackendCategoryDto> getThreeBackendCategoryList() {
        return backendCategoryDao.selectThreeBackendCategoryList();
    }

    @Override
    public List<BackendCategoryDto> getMerchantThreeBackendCategoryList() {
        // 获得到店铺的id
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        return backendCategoryDao.selectMerchantThreeBackendCategoryList(storeId);
    }

    @Override
    public List<BackendCategoryDto> getMerchantCategory(BackendCategoryDto backendCategoryDto) {
        /** 判断parentId是否为空 */
        if (backendCategoryDto.getParentId() == null) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_PARENTID_NOT_NULL);
        }
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        if (merchantId == null) {
            backendCategoryDto.setMerchantId(backendCategoryDto.getMerchantId());
        } else {
            backendCategoryDto.setMerchantId(merchantId);
        }
        return backendCategoryDao.selectMerchantCategory(backendCategoryDto);
    }

    @Override
    public List<BackendMerchantCategoryDto> getMerchantThreeCategory() {
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        List<BackendMerchantCategoryDto> selectMerchantThreeCategory = backendCategoryDao
                .selectMerchantThreeCategory(merchantId);
        return selectMerchantThreeCategory;
    }

    @Override
    public Integer addBackendCategoryDtoByBelongStore(BackendCategoryAdd backendCategoryAdd) {
        Integer i = 0;
        // 获得到店铺的id
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        if (backendCategoryAdd.getIds() != null && merchantId != null) {
            for (Long id : backendCategoryAdd.getIds()) {
                // 三级类目
                BackendCategoryEntity threeBackendCategory = backendCategoryDao.selectById(id);
                BackendMerchantCategoryEntity backendMerchantCategoryEntityThree = selectBackendCatMerchant(threeBackendCategory.getId(), merchantId);
                //如果第三级类目存在时，返回前台第三级类目存在
                if (backendMerchantCategoryEntityThree != null) {
                    throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_ALREADY);
                }
                i = saveBackendCatMerchant(merchantId, storeId, threeBackendCategory, backendCategoryAdd);
                // 二级类目
                BackendCategoryEntity twoBackendCategory = backendCategoryDao.selectById(threeBackendCategory.getParentId());
                BackendMerchantCategoryEntity backendMerchantCategoryEntityTwo = selectBackendCatMerchant(twoBackendCategory.getId(), merchantId);
                if (backendMerchantCategoryEntityTwo == null) {
                    i = saveBackendCatMerchant(merchantId, storeId, twoBackendCategory, backendCategoryAdd);
                }
                // 一级类目
                BackendCategoryEntity oneBackendCategory = backendCategoryDao.selectById(twoBackendCategory.getParentId());
                BackendMerchantCategoryEntity backendMerchantCategoryEntityOne = selectBackendCatMerchant(oneBackendCategory.getId(), merchantId);
                if (backendMerchantCategoryEntityOne == null) {
                    i = saveBackendCatMerchant(merchantId, storeId, oneBackendCategory, backendCategoryAdd);
                }
            }
        }
        return i;
    }

    private BackendMerchantCategoryEntity selectBackendCatMerchant(Long id, Long merchantId) {
        BackendMerchantCategoryEntity cond = new BackendMerchantCategoryEntity();
        cond.cleanInit();
        cond.setMerchantId(merchantId);
        cond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        cond.setCategoryId(id);
        return backendMerchantCategoryDao.selectOne(cond);
    }

    private Integer saveBackendCatMerchant(Long merchantId, Long storeId, BackendCategoryEntity backendCategoryEntity,
                                           BackendCategoryAdd backendCategoryAdd) {
        BackendMerchantCategoryEntity backendMerchantCategoryEntity = new BackendMerchantCategoryEntity();
        backendMerchantCategoryEntity.setMerchantId(merchantId);
        backendMerchantCategoryEntity.setStoreId(storeId);
        backendMerchantCategoryEntity.setStatus(backendCategoryAdd.getStatus());
        backendMerchantCategoryEntity.setCategoryId(backendCategoryEntity.getId());
        backendMerchantCategoryEntity.setLevel(backendCategoryEntity.getLevel());
        backendMerchantCategoryEntity.setCreatedTime(new Date());
        backendMerchantCategoryEntity.setParentId(backendCategoryEntity.getParentId());
        backendMerchantCategoryEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        return backendMerchantCategoryDao.insert(backendMerchantCategoryEntity);
    }

    @Override
    public Integer removeBackendMerchantCategoryByStoreId(Long[] categoryId) {
        Integer i = 0;
        // 获得店铺的id
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        /** 判断id是否为空 */
        if (null == categoryId) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_ID_NOT_NULL);
        }
        List<BackendMerchantCategoryEntity> backendMerchantCategoryEntities = new ArrayList<>();
        for (Long category : categoryId) {
            BackendMerchantCategoryEntity cond = new BackendMerchantCategoryEntity();
            cond.cleanInit();
            cond.setCategoryId(category);
            cond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            cond.setMerchantId(merchantId);
            BackendMerchantCategoryEntity backendMerchantCategoryEntity = backendMerchantCategoryDao.selectOne(cond);
            if(backendMerchantCategoryEntity.getLevel() == BackendCategoryLevelEnum.BACKEND_LEVEL_THERE.getCode()){
                EntityWrapper<BackendMerchantCategoryEntity> condMerchantCategory = new EntityWrapper<>();
                condMerchantCategory.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
                condMerchantCategory.eq("merchantId",merchantId);
                condMerchantCategory.eq("parentId",backendMerchantCategoryEntity.getParentId());
                condMerchantCategory.eq("level",backendMerchantCategoryEntity.getLevel());
                condMerchantCategory.notIn("categoryId",backendMerchantCategoryEntity.getCategoryId());
                backendMerchantCategoryEntities = backendMerchantCategoryDao.selectList(condMerchantCategory);
                if(CollectionUtils.isEmpty(backendMerchantCategoryEntities)){
                    BackendMerchantCategoryEntity backendMerCategory = getBackendMerCategory(backendMerchantCategoryEntity, merchantId);
                    getBackendMerCategory(backendMerCategory, merchantId);
                }
                backendMerchantCategoryEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                backendMerchantCategoryEntity.setLastModifiedTime(new Date());
                backendMerchantCategoryEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
                i = backendMerchantCategoryDao.updateById(backendMerchantCategoryEntity);
            }
        }
        return i;
    }

    private BackendMerchantCategoryEntity getBackendMerCategory(BackendMerchantCategoryEntity backendMerchantCategory,Long merchantId){
        BackendMerchantCategoryEntity cond = new BackendMerchantCategoryEntity();
        cond.cleanInit();
        cond.setCategoryId(backendMerchantCategory.getParentId());
        cond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        cond.setMerchantId(merchantId);
        BackendMerchantCategoryEntity backendMerchantCategoryEntity = backendMerchantCategoryDao.selectOne(cond);
        backendMerchantCategoryEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        backendMerchantCategoryEntity.setLastModifiedTime(new Date());
        backendMerchantCategoryEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        backendMerchantCategoryDao.updateById(backendMerchantCategoryEntity);
        return backendMerchantCategoryEntity;
    }

    @Override
    public PageInfo<BackendCategoryDto> getDecorationThreeBackendCategory(PagePO pagePO) {
        Page<BackendCategoryDto> page = PageDataUtil.buildPageParam(pagePO);
        List<BackendCategoryDto> backendCategoryDtos = backendCategoryDao.selectBackendCategorys(page);
        page.setRecords(backendCategoryDtos);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public PageInfo<BackendCategoryDto> getDecorationMerchantThreeBackendCategory(PagePO pagePO) {
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        Page<BackendCategoryDto> page = PageDataUtil.buildPageParam(pagePO);
        List<BackendCategoryDto> backendCategoryDtos = backendCategoryDao.selectMerchantBackendCategorys(pagePO, storeId);
        page.setRecords(backendCategoryDtos);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public boolean modifyBackendMerchanntStatus(BackendCategoryAdd backendCategoryAdd) {
        Integer i = 0;
        /** 判断id是否为空 */
        if (null == backendCategoryAdd.getIds()) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_ID_NOT_NULL);
        }
        for (Long id : backendCategoryAdd.getIds()) {
            BackendMerchantCategoryEntity cond = new BackendMerchantCategoryEntity();
            cond.cleanInit();
            cond.setCategoryId(id);
            cond.setMerchantId(backendCategoryAdd.getMerchantId());
            cond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            BackendMerchantCategoryEntity backendMerchantCategoryEntity = backendMerchantCategoryDao.selectOne(cond);
            if (backendMerchantCategoryEntity != null) {
                backendMerchantCategoryEntity.setStatus(backendCategoryAdd.getStatus());
                backendMerchantCategoryEntity.setVersion(null);
                i = backendMerchantCategoryDao.updateById(backendMerchantCategoryEntity);
            }
        }
        return i > 0;
    }

}
