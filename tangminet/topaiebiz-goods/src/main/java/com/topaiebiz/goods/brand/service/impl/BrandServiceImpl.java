package com.topaiebiz.goods.brand.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.brand.dao.BrandDao;
import com.topaiebiz.goods.brand.dto.BrandDto;
import com.topaiebiz.goods.brand.dto.BrandQueryDto;
import com.topaiebiz.goods.brand.entity.BrandEntity;
import com.topaiebiz.goods.brand.exception.BrandExceptionEnum;
import com.topaiebiz.goods.brand.service.BrandService;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryDao;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryEntity;
import com.topaiebiz.goods.dao.CategoryBrandDao;
import com.topaiebiz.goods.dao.CategoryBrandEditDao;
import com.topaiebiz.goods.entity.CategoryBrand;
import com.topaiebiz.goods.entity.CategoryBrandEdit;
import com.topaiebiz.goods.enums.GoodsExceptionEnum;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description 商品品牌实现类
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年8月23日 下午4:15:52
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private CategoryBrandDao categoryBrandDao;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private CategoryBrandEditDao categoryBrandEditDao;

    @Autowired
    private BackendCategoryDao backendCategoryDao;

    @Override
    public Integer saveBrand(BrandDto brandDto) throws GlobalException {
        Integer i = 0;
        /**对商品品牌名称进行重复验证*/
        EntityWrapper<BrandEntity> condition = new EntityWrapper<>();
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.eq("name", brandDto.getName());
        List<BrandEntity> brandEntities = brandDao.selectList(condition);
        if (CollectionUtils.isNotEmpty(brandEntities)) {
            throw new GlobalException(BrandExceptionEnum.BRAND_NAME_NOT_REPETITION);
        }
        BrandEntity brand = new BrandEntity();
        BeanCopyUtil.copy(brandDto, brand);
        brand.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        brand.setCreatedTime(new Date());
        i = brandDao.insert(brand);
        return i;
    }

    @Override
    public Integer modifyBrand(BrandDto brandDto) throws GlobalException {
        /**对商品品牌名称进行重复验证*/
        EntityWrapper<BrandEntity> condition = new EntityWrapper<>();
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.eq("name", brandDto.getName());
        condition.notIn("id", brandDto.getId());
        List<BrandEntity> brandEntities = brandDao.selectList(condition);
        if (CollectionUtils.isNotEmpty(brandEntities)) {
            throw new GlobalException(BrandExceptionEnum.BRAND_NAME_NOT_REPETITION);
        }
        if (brandDto.getBrandImage() == "") {
            brandDto.setBrandImage(" ");
        }
        BrandEntity brand = brandDao.selectById(brandDto.getId());
        BeanCopyUtil.copy(brandDto, brand);
        brand.setLastModifiedTime(new Date());
        brand.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        return brandDao.updateById(brand);
    }

    @Override
    public BrandDto findBrandById(Long id) throws GlobalException {
        /**判断id是否为空*/
        if (null == id) {
            throw new GlobalException(BrandExceptionEnum.BRAND_ID_NOT_NULL);
        }
        /**对id进行查询*/
        BrandEntity condition = new BrandEntity();
        condition.clearInt();
        condition.setId(id);
        condition.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        BrandEntity brandEntity = brandDao.selectOne(condition);
        if (null == brandEntity) {
            throw new GlobalException(BrandExceptionEnum.BRAND_ID_NOT_EXIST);
        }
        BrandDto brandDto = new BrandDto();
        BeanCopyUtil.copy(brandEntity, brandDto);
        return brandDto;
    }

    @Override
    public PageInfo<BrandDto> getBrandList(BrandQueryDto brandDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageSize(brandDto.getPageSize());
        pagePO.setPageNo(brandDto.getPageNo());
        Page<BrandDto> page = PageDataUtil.buildPageParam(pagePO);
        EntityWrapper<BrandEntity> condition = new EntityWrapper<>();
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.orderBy("brandInitials", true);
        condition.orderBy("createdTime", true);
        if (brandDto.getId() != null) {
            condition.eq("id", brandDto.getId());
        }
        if (StringUtils.isNotBlank(brandDto.getEnglishName())) {
            condition.like("englishName", brandDto.getEnglishName());
        }
        if (StringUtils.isNotBlank(brandDto.getName())) {
            condition.like("name", brandDto.getName());
        }
        if (StringUtils.isNotBlank(brandDto.getCategoryName())) {
            EntityWrapper<BackendCategoryEntity> cond = new EntityWrapper<>();
            cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            cond.like("name", brandDto.getCategoryName());
            List<BackendCategoryEntity> backendCategoryEntities = backendCategoryDao.selectList(cond);
            if (CollectionUtils.isEmpty(backendCategoryEntities)) {
                return PageDataUtil.copyPageInfo(page);
            }
            List<Long> categoryIds = backendCategoryEntities.stream().map(backendCategoryEntity -> backendCategoryEntity.getId()).collect(Collectors.toList());
            EntityWrapper<CategoryBrand> condCategoryBrand = new EntityWrapper<>();
            condCategoryBrand.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            condCategoryBrand.in("categoryId", categoryIds);
            List<CategoryBrand> categoryBrands = categoryBrandDao.selectList(condCategoryBrand);
            if (CollectionUtils.isEmpty(categoryBrands)) {
                return PageDataUtil.copyPageInfo(page);
            }
            List<Long> brandIds = categoryBrands.stream().map(categoryBrand -> categoryBrand.getBrandId()).collect(Collectors.toList());
            condition.in("id", brandIds);

        }
        List<BrandEntity> brandEntities = brandDao.selectPage(page, condition);
        if (CollectionUtils.isEmpty(brandEntities)) {
            return PageDataUtil.copyPageInfo(page);
        }
        List<BrandDto> brandDtos = BeanCopyUtil.copyList(brandEntities, BrandDto.class);
        getBrandDto(brandDtos);
        page.setRecords(brandDtos);
        return PageDataUtil.copyPageInfo(page);
    }

    private void getBrandDto(List<BrandDto> brandDtos) {
        for (BrandDto brandDto : brandDtos) {
            EntityWrapper<CategoryBrand> cond = new EntityWrapper<>();
            cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            cond.eq("brandId", brandDto.getId());
            List<CategoryBrand> categoryBrands = categoryBrandDao.selectList(cond);
            if (CollectionUtils.isNotEmpty(categoryBrands)) {
                brandDto.setIsOwn(1);
            }
        }
        return;
    }

    @Override
    public Integer removeBrands(Long[] id) throws GlobalException {
        Integer i = 0;
        /**判断id是否为空*/
        if (0 == id.length) {
            throw new GlobalException(BrandExceptionEnum.BRAND_ID_NOT_NULL);
        }
        for (Long long1 : id) {
            /**对id进行查询*/
            BrandEntity brandEntity = brandDao.selectById(long1);
            if (brandEntity == null) {
                throw new GlobalException(BrandExceptionEnum.BRAND_ID_NOT_EXIST);
            }
            EntityWrapper<ItemEntity> cond = new EntityWrapper<>();
            cond.eq("belongBrand", long1);
            cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            List<ItemEntity> itemEntities = itemDao.selectList(cond);
            if (CollectionUtils.isNotEmpty(itemEntities)) {
                throw new GlobalException(GoodsExceptionEnum.BRAND_HAVE_ITEM);
            }
            EntityWrapper<CategoryBrand> condCategoryBrand = new EntityWrapper<>();
            condCategoryBrand.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            condCategoryBrand.eq("brandId", long1);
            List<CategoryBrand> categoryBrands = categoryBrandDao.selectList(condCategoryBrand);
            if (CollectionUtils.isNotEmpty(categoryBrands)) {
                throw new GlobalException(GoodsExceptionEnum.BRAND_HAVE_CATEGORY);
            }

            EntityWrapper<CategoryBrandEdit> condCategoryBrandEdit = new EntityWrapper<>();
            condCategoryBrandEdit.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            condCategoryBrandEdit.eq("brandId", long1);
            List<CategoryBrandEdit> categoryBrandEdits = categoryBrandEditDao.selectList(condCategoryBrandEdit);
            if (CollectionUtils.isNotEmpty(categoryBrandEdits)) {
                throw new GlobalException(GoodsExceptionEnum.BRAND_HAVE_CATEGORY);
            }

            brandEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
            brandEntity.setLastModifiedTime(new Date());
            brandEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            i = brandDao.updateById(brandEntity);
        }
        return i;
    }

    @Override
    public List<BrandDto> getBrands() {
        String cacheKey = "brand_list";
        int expire = 60 * 5;
        List<BrandDto> brandDtoList = redisCache.getListValue(cacheKey, BrandDto.class);
        if (CollectionUtils.isEmpty(brandDtoList)) {
            EntityWrapper<BrandEntity> brandCondition = new EntityWrapper<>();
            brandCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            List<BrandEntity> brandEntities = brandDao.selectList(brandCondition);
            if (CollectionUtils.isEmpty(brandEntities)) {
                return Collections.emptyList();
            }
            brandDtoList = new ArrayList<>();
            for (BrandEntity brandEntity : brandEntities) {
                BrandDto brandDto = new BrandDto();
                BeanCopyUtil.copy(brandEntity, brandDto);
                brandDtoList.add(brandDto);
            }
            brandDtoList = brandSort(brandDtoList);
            redisCache.set(cacheKey, JSON.toJSONString(brandDtoList), expire);
        }

        return brandDtoList;
    }

    private List<BrandDto> brandSort(List<BrandDto> brandDtos) {
        Collections.sort(brandDtos, new Comparator<BrandDto>() {
            @Override
            public int compare(BrandDto brandDto1, BrandDto brandDto2) {
                String name1 = brandDto1.getName();
                String name2 = brandDto2.getName();
                return Collator.getInstance(java.util.Locale.CHINA).compare(name1, name2);
            }
        });
        return brandDtos;
    }

    @Override
    public List<BrandDto> getAppBrandList() {
        String cacheKey = "home_brand_list";
        int expire = 60 * 5;
        List<BrandDto> brandDtos = redisCache.getListValue(cacheKey, BrandDto.class);

        if (CollectionUtils.isEmpty(brandDtos)) {
            EntityWrapper<BrandEntity> brandCondition = new EntityWrapper<>();
            brandCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            brandCondition.isNotNull("brandImage");
            brandCondition.isNotNull("lastModifiedTime");
            brandCondition.orderBy("sortNo",true);
            List<BrandEntity> brandEntities = brandDao.selectList(brandCondition);
            if (CollectionUtils.isEmpty(brandEntities)) {
                brandDtos = Collections.emptyList();
            } else {
                brandDtos = new ArrayList<>();
                for (BrandEntity brandEntity : brandEntities) {
                    BrandDto brandDto = new BrandDto();
                    BeanCopyUtil.copy(brandEntity, brandDto);
                    brandDtos.add(brandDto);
                }
            }
            redisCache.set(cacheKey, JSON.toJSONString(brandDtos), expire);
        }
        return brandDtos;
    }

    @Override
    public List<String> queryName(String name) {
        EntityWrapper<BrandEntity> brandCondition = new EntityWrapper<>();
        brandCondition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        if (name == null || name == "") {
            return Collections.emptyList();
        }
        brandCondition.like("name", name);
        List<BrandEntity> brandEntities = brandDao.selectList(brandCondition);
        List<String> names = brandEntities.stream().map(brandEntity -> brandEntity.getName()).collect(Collectors.toList());
        return names;
    }

}
