package com.topaiebiz.goods.category.frontend.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.nebulapaas.common.redis.aop.JedisOperation;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.topaiebiz.goods.brand.dto.BrandDto;
import com.topaiebiz.goods.category.backend.dto.BackendCategorysDto;
import com.topaiebiz.goods.dto.category.backend.BackendCategoryDTO;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryDao;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryDto;
import com.topaiebiz.goods.category.frontend.dao.FrontBackCategoryDao;
import com.topaiebiz.goods.category.frontend.dao.FrontendCategoryDao;
import com.topaiebiz.goods.category.frontend.dto.FrontBackCategoryDto;
import com.topaiebiz.goods.category.frontend.dto.FrontendCategoryDto;
import com.topaiebiz.goods.category.frontend.entity.FrontBackCategoryEntity;
import com.topaiebiz.goods.category.frontend.entity.FrontendCategoryEntity;
import com.topaiebiz.goods.category.frontend.exception.FrontendCategoryExceptionEnum;
import com.topaiebiz.goods.category.frontend.service.FrontendCategoryService;
import redis.clients.jedis.Jedis;

/**
 * Description 商品前台类目实现类
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年8月25日 下午3:14:47
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
public class FrontendCategoryServiceImpl implements FrontendCategoryService {

    @Autowired
    private FrontendCategoryDao frontendCategoryDao;

    @Autowired
    private FrontBackCategoryDao frontBackCategoryDao;

    /**
     * 后台类目。
     */
    @Autowired
    private BackendCategoryDao backendCategoryDao;

    @Autowired
    private RedisCache redisCache;

    @Override
    public List<FrontendCategoryDto> getFrontendCategoryList(FrontendCategoryDto frontendCategoryDto)
            throws GlobalException {
        /** 判断parentId是否为空 */
        if (frontendCategoryDto.getParentId() == null) {
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTENDCATEGORY_PARENTID_NOT_NULL);
        }
        if (frontendCategoryDto.getParentId() == 0) {
            return frontendCategoryDao.selectListOneLevelFrontendCategory(frontendCategoryDto);
        } else {
            return frontendCategoryDao.selectListTwoOrThreeLevelFrontendCategory(frontendCategoryDto);
        }
    }

    @Override
    public Integer saveFrontendCategory(FrontendCategoryDto frontendCategoryDto) throws GlobalException {
        FrontendCategoryEntity frontendCategory = new FrontendCategoryEntity();
        BeanUtils.copyProperties(frontendCategoryDto, frontendCategory);
        FrontendCategoryEntity frontendCategoryByName = frontendCategoryDao
                .selectFrontendCategoryByName(frontendCategory);
        /** 商品前台类目名称重复验证 */
        if (frontendCategoryByName != null) {
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTENDCATEGORY_NAME_NOT_REPETITION);
        }
        frontendCategory.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        frontendCategory.setCreatedTime(new Date());
        return frontendCategoryDao.insert(frontendCategory);
    }

    @Override
    public Integer modifyFrontendCategory(FrontendCategoryDto frontendCategoryDto) throws GlobalException {
        FrontendCategoryDto frontendCategoryByName = frontendCategoryDao
                .selectFrontendCategoryByNameAndId(frontendCategoryDto);
        /** 商品前台类目名称重复验证 */
        if (frontendCategoryByName != null) {
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTENDCATEGORY_NAME_NOT_REPETITION);
        }
        FrontendCategoryEntity frontendCategoryEntity = frontendCategoryDao.selectById(frontendCategoryDto.getId());
        BeanCopyUtil.copy(frontendCategoryDto, frontendCategoryEntity);
        frontendCategoryEntity.setLastModifiedTime(new Date());
        frontendCategoryEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        return frontendCategoryDao.updateById(frontendCategoryEntity);
    }

    @Override
    public Integer removeFrontendCategory(Long id) throws GlobalException {
        Integer i = 0;
        /** 判断id是否为空 */
        if (null == id) {
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTENDCATEGORY_ID_NOT_NULL);
        }
        /** 判断id是否存在 */
        FrontendCategoryEntity frontendCategoryEntity = frontendCategoryDao.selectById(id);
        if (null == frontendCategoryEntity) {
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTENDCATEGORY_ID_NOT_EXIST);
        }
        /** 查询商品前台类目及子类目 */
        List<FrontendCategoryEntity> allFrontendCategory = this.getAllFrontendCategory(id);
        for (FrontendCategoryEntity frontendCategory : allFrontendCategory) {
            /** 删除商品前台类目及子类目 */
            i = frontendCategoryDao.deleteFrontendCategory(frontendCategory.getId());
            /** 查询叶子类目下面绑定前后台类目数据 */
            List<FrontBackCategoryEntity> listFrontBackCategory = frontBackCategoryDao
                    .selectAllFrontBackCategoryById(frontendCategory.getId());
            for (FrontBackCategoryEntity frontBackCategory : listFrontBackCategory) {
                if (frontBackCategory != null) {
                    /** 删除商品前后台绑定类目 */
                    i = frontBackCategoryDao.deleteFrontBackCategory(frontBackCategory.getId());
                }
            }
        }
        return i;
    }

    /**
     * Description 查询子类目
     * <p>
     * Author Hedda
     *
     * @param id 商品前台类目id
     * @return
     */
    private List<FrontendCategoryEntity> getAllFrontendCategory(Long id) {
        List<FrontendCategoryEntity> resultList = new ArrayList<FrontendCategoryEntity>();
        /** 查询下级类目 */
        List<FrontendCategoryEntity> listFrontendCategory = frontendCategoryDao.selectAllFrontendCategoryById(id);
        FrontendCategoryEntity frontendCategoryEntity = frontendCategoryDao.selectById(id);
        resultList.add(frontendCategoryEntity);
        if (!(listFrontendCategory == null || listFrontendCategory.size() == 0)) {
            resultList.addAll(listFrontendCategory);
            for (FrontendCategoryEntity frontendCategory : listFrontendCategory) {
                /** 查询下级类目 */
                List<FrontendCategoryEntity> childList = this.getAllFrontendCategory(frontendCategory.getId());
                resultList.addAll(childList);
            }
            return resultList;
        } else {
            return resultList;
        }
    }

    @Override
    public FrontendCategoryDto getFrontendCategoryById(Long id) throws GlobalException {
        /** 判断id是否为空 */
        if (id == null) {
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTENDCATEGORY_ID_NOT_NULL);
        }
        FrontendCategoryDto frontendCategoryDto = frontendCategoryDao.selectFrontendCategoryById(id);
        /** 判断id是否存在 */
        if (frontendCategoryDto == null) {
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTENDCATEGORY_ID_NOT_EXIST);
        }
        return frontendCategoryDto;
    }

    @Override
    public Integer addFrontendCategoryById(FrontendCategoryDto frontendCategoryDto) throws GlobalException {
        /** 判断id是否为空 */
        if (frontendCategoryDto.getId() == null) {
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTENDCATEGORY_ID_NOT_NULL);
        }
        FrontendCategoryEntity frontendCategoryEntity = frontendCategoryDao.selectById(frontendCategoryDto.getId());
        /** 判断id是否存在 */
        if (frontendCategoryEntity == null) {
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTENDCATEGORY_ID_NOT_EXIST);
        }
        frontendCategoryEntity.setImage(frontendCategoryDto.getImage());
        return frontendCategoryDao.updateById(frontendCategoryEntity);
    }

    @Override
    public List<BackendCategorysDto> getBackendCategoryList(Long frontId) throws GlobalException {
        List<BackendCategorysDto> backendCategorysDtos = new ArrayList<BackendCategorysDto>();
        List<FrontBackCategoryEntity> allFrontBackCategoryById = frontBackCategoryDao
                .selectAllFrontBackCategoryById(frontId);
        if (!(allFrontBackCategoryById == null || allFrontBackCategoryById.size() == 0)) {
            for (FrontBackCategoryEntity frontBackCategory : allFrontBackCategoryById) {
                /** 创建一个resultList集合 */
                List<BackendCategoryDto> resultList = new ArrayList<BackendCategoryDto>();
                BackendCategorysDto backendCategorysDto = new BackendCategorysDto();
                //第三级类目
                BackendCategoryDto backendCategoryDto3 = backendCategoryDao
                        .selectBackendCategoryByBackId(frontBackCategory.getBackId());
                backendCategorysDto.setFrontBackId(frontBackCategory.getId());
                if (backendCategoryDto3 != null) {
                    // 第三级parent Id查询第二级类目
                    BackendCategoryDto backendCategoryDto2 = new BackendCategoryDto();
                    BackendCategoryDTO twobackendCategoryDto = backendCategoryDao
                            .selectTwoBackendCategoryDTOByParentId(backendCategoryDto3.getParentId());
                    BeanCopyUtil.copy(twobackendCategoryDto, backendCategoryDto2);
                    resultList.add(backendCategoryDto3);
                    if (null != twobackendCategoryDto) {
                        // 通过商家id和第二级parentId查询第一级类目
                        BackendCategoryDto backendCategoryDto1 = new BackendCategoryDto();
                        BackendCategoryDTO onebackendCategoryDto = backendCategoryDao
                                .selectOneBackendCategoryDTOByParentId(twobackendCategoryDto.getParentId());
                        BeanCopyUtil.copy(onebackendCategoryDto, backendCategoryDto1);
                        resultList.add(backendCategoryDto2);
                        if (null != onebackendCategoryDto) {
                            resultList.add(backendCategoryDto1);
                        }
                    }
                }
                backendCategorysDto.setBackendCategoryDto(resultList);
                backendCategorysDtos.add(backendCategorysDto);
            }
        }
        return backendCategorysDtos;
    }

    @Override
    public Integer cancelFrontendCategoryImage(Long id) throws GlobalException {
        /** 判断id是否为空 */
        if (id == null) {
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTENDCATEGORY_ID_NOT_NULL);
        }
        FrontendCategoryEntity frontendCategoryEntity = frontendCategoryDao.selectById(id);
        /** 判断id是否存在 */
        if (frontendCategoryEntity == null) {
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTENDCATEGORY_ID_NOT_EXIST);
        }
        if (frontendCategoryEntity.getImage() != null) {
            frontendCategoryEntity.setImage(" ");
        }
        return frontendCategoryDao.updateById(frontendCategoryEntity);
    }

    @Override
    public List<FrontendCategoryDto> getMerchantAppFrontendCategoryList(FrontendCategoryDto frontendCategoryDto) throws GlobalException {
        /** 判断parentId是否为空 */
        if (frontendCategoryDto.getParentId() == null) {
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTENDCATEGORY_PARENTID_NOT_NULL);
        }
        int expire = 60 * 5;
        String oneKey = "frontend_ccategory_cache_" + frontendCategoryDto.getParentId();
        List<FrontendCategoryDto> oneFrontendCategoryDto = redisCache.getListValue(oneKey, FrontendCategoryDto.class);
        if (CollectionUtils.isEmpty(oneFrontendCategoryDto)) {
            FrontendCategoryEntity cond = new FrontendCategoryEntity();
            cond.clearInit();
            cond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            cond.setParentId(frontendCategoryDto.getParentId());
            List<FrontendCategoryEntity> entities = frontendCategoryDao.selectList(new EntityWrapper<>(cond));
            oneFrontendCategoryDto = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(entities)){
                for (FrontendCategoryEntity frontendCategoryEntity: entities){
                    FrontendCategoryDto frontendCategory = new FrontendCategoryDto();
                    BeanCopyUtil.copy(frontendCategoryEntity,frontendCategory);
                    oneFrontendCategoryDto.add(frontendCategory);
                }
            }
            redisCache.set(oneKey, oneFrontendCategoryDto, expire);
        }
        return oneFrontendCategoryDto;
    }

}
