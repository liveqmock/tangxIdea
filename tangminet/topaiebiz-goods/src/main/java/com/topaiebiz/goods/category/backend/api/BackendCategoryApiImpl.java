package com.topaiebiz.goods.category.backend.api;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.api.BackendCategoryApi;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryAttrDao;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryDao;
import com.topaiebiz.goods.category.backend.dao.BackendMerchantCategoryDao;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryAttrEntity;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryEntity;
import com.topaiebiz.goods.category.backend.entity.BackendMerchantCategoryEntity;
import com.topaiebiz.goods.category.backend.exception.BackendCategoryExceptionEnum;
import com.topaiebiz.goods.constants.GoodsConstants;
import com.topaiebiz.goods.dto.category.backend.BackendCategoryDTO;
import com.topaiebiz.goods.dto.category.backend.BackendCategoryStatusDTO;
import com.topaiebiz.goods.dto.category.backend.BackendCategorysDTO;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.topaiebiz.goods.constants.GoodsConstants.BackendCateLevel.THREE_LEVEL;

/**
 * Created by dell on 2018/1/6.
 */
@Service
public class BackendCategoryApiImpl implements BackendCategoryApi {

    @Autowired
    private BackendCategoryDao backendCategoryDao;

    @Autowired
    private BackendMerchantCategoryDao backendMerchantCategoryDao;

    @Autowired
    private BackendCategoryAttrDao backendCategoryAttrDao;

    @Override
    public BackendCategoryDTO getBackendCategoryById(Long backendCategoryId) {
        /** 判断类目id是否为空 */
        if (null == backendCategoryId) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_ID_NOT_NULL);
        }
        BackendCategoryEntity backendCategory = backendCategoryDao.selectBackendCategoryEntityById(backendCategoryId);
        if (backendCategory == null) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_ID_NOT_EXIST);
        }
        BackendCategoryDTO backendCategoryDTO = new BackendCategoryDTO();
        BeanUtils.copyProperties(backendCategory, backendCategoryDTO);
        return backendCategoryDTO;
    }

    @Override
    public List<BackendCategoryDTO> getBackendCategorys(List<Long> backendCategoryIds) {
        if (CollectionUtils.isEmpty(backendCategoryIds)) {
            return Collections.emptyList();
        }
        EntityWrapper<BackendCategoryEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.in("id", backendCategoryIds);
        List<BackendCategoryEntity> backendCategoryEntities = backendCategoryDao.selectList(cond);
        List<BackendCategoryDTO> backendCategoryDTOS = BeanCopyUtil.copyList(backendCategoryEntities, BackendCategoryDTO.class);
        return backendCategoryDTOS;
    }

    @Override
    public Integer addBackendCategoryDtoByStoreId(Long merchantId, Long[] backendCategoryIds, Integer status) {
        Integer i = 0;
        if (!(null == backendCategoryIds && null == merchantId)) {
            for (Long id : backendCategoryIds) {
                // 三级类目
                BackendCategoryEntity threeBackendCategory = backendCategoryDao.selectById(id);
                BackendMerchantCategoryEntity backendMerchantCategoryEntityThree = backendCategoryDao.selectBackendMerchantCategoryById(threeBackendCategory.getId(), merchantId);
                if (backendMerchantCategoryEntityThree != null) {
                    throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_ALREADY);
                }
                addBackendMerchantCate(merchantId, threeBackendCategory, status);
                // 二级类目
                BackendCategoryEntity twoBackendCategory = backendCategoryDao.selectById(threeBackendCategory.getParentId());
                BackendMerchantCategoryEntity backendMerchantCategoryEntityTwo = backendCategoryDao.selectBackendMerchantCategoryById(twoBackendCategory.getId(), merchantId);
                if (backendMerchantCategoryEntityTwo == null) {
                    addBackendMerchantCate(merchantId, twoBackendCategory, status);
                }
                // 一级类目
                BackendCategoryEntity oneBackendCategory = backendCategoryDao.selectById(twoBackendCategory.getParentId());
                BackendMerchantCategoryEntity backendMerchantCategoryEntityOne = backendCategoryDao.selectBackendMerchantCategoryById(oneBackendCategory.getId(), merchantId);
                if (backendMerchantCategoryEntityOne == null) {
                    addBackendMerchantCate(merchantId, oneBackendCategory, status);
                }
            }
        }
        return i;
    }

    private void addBackendMerchantCate(Long merchantId, BackendCategoryEntity backendCategory, Integer status) {
        BackendMerchantCategoryEntity backendMerchantCategoryEntity = new BackendMerchantCategoryEntity();
        backendMerchantCategoryEntity.setMerchantId(merchantId);
        backendMerchantCategoryEntity.setCategoryId(backendCategory.getId());
        backendMerchantCategoryEntity.setCreatedTime(new Date());
        backendMerchantCategoryEntity.setStatus(status);
        backendMerchantCategoryEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        backendMerchantCategoryDao.insert(backendMerchantCategoryEntity);
    }

    @Override
    public List<BackendCategorysDTO> getMerchantCategory(Long merchantId, List<Integer> statuses) {
        // 查询出第三级类目的id
        List<BackendCategoryDTO> backendCategoryThree = new ArrayList<BackendCategoryDTO>();
        // 通过商家id查询商家类目
        EntityWrapper<BackendMerchantCategoryEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("merchantId", merchantId);
        if (CollectionUtils.isNotEmpty(statuses)) {
            cond.in("status",statuses);
        }
        List<BackendMerchantCategoryEntity> backendMerchantCategoryEntities = backendMerchantCategoryDao.selectList(cond);
        if (CollectionUtils.isEmpty(backendMerchantCategoryEntities)) {
            return null;
        }
        List<Long> categoryIds = backendMerchantCategoryEntities.stream().map(backendMerchantCategoryEntity ->
                backendMerchantCategoryEntity.getCategoryId()).collect(Collectors.toList());
        Map<Long, BackendMerchantCategoryEntity> collect = backendMerchantCategoryEntities.stream().collect(Collectors.toMap(BackendMerchantCategoryEntity::getCategoryId, backendMerchantCategoryEntity -> backendMerchantCategoryEntity));
        for (Long categoryId : categoryIds) {
            // 通过类目id查询出息类目是否为三级类目
            BackendCategoryDTO backendCategoryDTO = backendCategoryDao
                    .selectBackendCategoryDTOById(categoryId);
            BackendMerchantCategoryEntity backendMerchantCategoryEntity = collect.get(categoryId);
            if (backendCategoryDTO != null) {
                backendCategoryDTO.setStatus(backendMerchantCategoryEntity.getStatus());
                if (backendCategoryDTO.getLevel() == THREE_LEVEL) {
                    backendCategoryThree.add(backendCategoryDTO);
                }
            }
        }
        // 创建一个BackendCategorysDto集合
        List<BackendCategorysDTO> reaultsMsg = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(backendCategoryThree)) {
            for (BackendCategoryDTO backendCategoryDto : backendCategoryThree) {
                List<BackendCategoryDTO> reaultMsg = new ArrayList<BackendCategoryDTO>();
                // 第三级parentId查询第二级类目
                BackendCategoryDTO twobackendCategoryDTO = selectBackendCategoryDto(backendCategoryDto);
                // 通过商家id和第二级parentId查询第一级类目
                BackendCategoryDTO onebackendCategoryDTO = selectBackendCategoryDto(twobackendCategoryDTO);
                reaultMsg.add(onebackendCategoryDTO);
                reaultMsg.add(twobackendCategoryDTO);
                reaultMsg.add(backendCategoryDto);
                // 将一二三级类目添加到BackendCategoryDto集合
                BackendCategorysDTO backendCategorysDto = new BackendCategorysDTO();
                backendCategorysDto.setBackendCategoryDto(reaultMsg);
                reaultsMsg.add(backendCategorysDto);
            }
        }
        return reaultsMsg;
    }

    @Override
    public List<Long> getMerchantcategoryIds(Long merchantId, Integer status) {
        EntityWrapper<BackendMerchantCategoryEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("merchantId",merchantId);
        cond.eq("status",status);
        List<BackendMerchantCategoryEntity> backendMerchantCategoryEntities = backendMerchantCategoryDao.selectList(cond);
        if(CollectionUtils.isEmpty(backendMerchantCategoryEntities)){
            return null;
        }
        return backendMerchantCategoryEntities.stream().map(backendMerchantCategoryEntity -> backendMerchantCategoryEntity.getCategoryId()).collect(Collectors.toList());
    }

    @Override
    public List<Long> getMerchantcategoryListIds(Long merchantId, List<Integer> status) {

        EntityWrapper<BackendMerchantCategoryEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("merchantId",merchantId);
        cond.in("status",status);
        List<BackendMerchantCategoryEntity> backendMerchantCategoryEntities = backendMerchantCategoryDao.selectList(cond);
        if(CollectionUtils.isEmpty(backendMerchantCategoryEntities)){
            return null;
        }
        return backendMerchantCategoryEntities.stream().map(backendMerchantCategoryEntity -> backendMerchantCategoryEntity.getCategoryId()).collect(Collectors.toList());
    }

    @Override
    public List<BackendCategorysDTO> getMerchantBacCategory(Long merchantId) {
        // 查询出第三级类目的id
        List<BackendCategoryDTO> backendCategoryThree = new ArrayList<BackendCategoryDTO>();
        // 通过商家id查询商家类目
        EntityWrapper<BackendMerchantCategoryEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("merchantId", merchantId);
        List<BackendMerchantCategoryEntity> backendMerchantCategoryEntities = backendMerchantCategoryDao.selectList(cond);
        if (CollectionUtils.isEmpty(backendMerchantCategoryEntities)) {
            return null;
        }
        List<Long> categoryIds = backendMerchantCategoryEntities.stream().map(backendMerchantCategoryEntity ->
                backendMerchantCategoryEntity.getCategoryId()).collect(Collectors.toList());
        Map<Long, BackendMerchantCategoryEntity> collect = backendMerchantCategoryEntities.stream().collect(Collectors.toMap(BackendMerchantCategoryEntity::getCategoryId, backendMerchantCategoryEntity -> backendMerchantCategoryEntity));
        for (Long categoryId : categoryIds) {
            // 通过类目id查询出息类目是否为三级类目
            BackendCategoryDTO backendCategoryDTO = backendCategoryDao
                    .selectBackendCategoryDTOById(categoryId);
            BackendMerchantCategoryEntity backendMerchantCategoryEntity = collect.get(categoryId);
            if (backendCategoryDTO != null) {
                backendCategoryDTO.setStatus(backendMerchantCategoryEntity.getStatus());
                if (backendCategoryDTO.getLevel() == THREE_LEVEL) {
                    backendCategoryThree.add(backendCategoryDTO);
                }
            }
        }
        // 创建一个BackendCategorysDto集合
        List<BackendCategorysDTO> reaultsMsg = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(backendCategoryThree)) {
            for (BackendCategoryDTO backendCategoryDto : backendCategoryThree) {
                List<BackendCategoryDTO> reaultMsg = new ArrayList<BackendCategoryDTO>();
                // 第三级parentId查询第二级类目
                BackendCategoryDTO twobackendCategoryDTO = selectBackendCategoryDto(backendCategoryDto);
                // 通过商家id和第二级parentId查询第一级类目
                BackendCategoryDTO onebackendCategoryDTO = selectBackendCategoryDto(twobackendCategoryDTO);
                reaultMsg.add(onebackendCategoryDTO);
                reaultMsg.add(twobackendCategoryDTO);
                reaultMsg.add(backendCategoryDto);
                // 将一二三级类目添加到BackendCategoryDto集合
                BackendCategorysDTO backendCategorysDto = new BackendCategorysDTO();
                backendCategorysDto.setBackendCategoryDto(reaultMsg);
                reaultsMsg.add(backendCategorysDto);
            }
        }
        return reaultsMsg;
    }

    private BackendCategoryDTO selectBackendCategoryDto(BackendCategoryDTO backendCategory) {
        BackendCategoryDTO backendCategoryDto = new BackendCategoryDTO();
        BackendCategoryEntity cond = new BackendCategoryEntity();
        cond.clearInit();
        cond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        cond.setId(backendCategory.getParentId());
        BackendCategoryEntity backendCategoryEntity = backendCategoryDao.selectOne(cond);
        BeanCopyUtil.copy(backendCategoryEntity, backendCategoryDto);
        return backendCategoryDto;
    }

    @Override
    public Integer removeBackendMerchantCategoryByMerchantId(Long merchantId, Long[] categoryIds) {
        Integer i = 0;
        /** 判断id是否为空 */
        if (null == categoryIds) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_ID_NOT_NULL);
        }
        for (Long category : categoryIds) {
            i = backendCategoryDao.deleteBackendMerchantCategory(category, merchantId);
        }
        return i;
    }

    @Override
    public Integer removeBackendMerchantCategorys(Long merchantId) {
        Integer i = backendMerchantCategoryDao.deleteMerchantBackend(merchantId);
        return i;
    }

    @Override
    public Integer removeBackendMerchantCategory(Long merchantId, Integer status) {
        EntityWrapper<BackendMerchantCategoryEntity> backendMerchantCategoryEntityWrapper = new EntityWrapper<>();
        backendMerchantCategoryEntityWrapper.eq("merchantId",merchantId);
        backendMerchantCategoryEntityWrapper.eq("deleteFlag", Constants.DeletedFlag.DELETED_NO);
        backendMerchantCategoryEntityWrapper.eq("status", GoodsConstants.BackendMerchantCategoryStatus.TO_AUDIT);
        List<BackendMerchantCategoryEntity> backendMerchantCategoryEntities = backendMerchantCategoryDao.selectList(backendMerchantCategoryEntityWrapper);
        return null;
    }

    @Override
    public boolean modifyBackendMerchanntStatus(BackendCategoryStatusDTO backendCategoryStatusDTO) {
        Integer i = 0;
        /** 判断id是否为空 */
        if (null == backendCategoryStatusDTO.getIds()) {
            throw new GlobalException(BackendCategoryExceptionEnum.BACKENDCATEGORY_ID_NOT_NULL);
        }
        for (Long id : backendCategoryStatusDTO.getIds()) {
            BackendMerchantCategoryEntity cond = new BackendMerchantCategoryEntity();
            cond.cleanInit();
            cond.setCategoryId(id);
            cond.setMerchantId(backendCategoryStatusDTO.getMerchantId());
            cond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            BackendMerchantCategoryEntity backendMerchantCategoryEntity = backendMerchantCategoryDao.selectOne(cond);
            if (backendMerchantCategoryEntity != null) {
                backendMerchantCategoryEntity.setStatus(backendCategoryStatusDTO.getStatus());
                i = backendMerchantCategoryDao.updateById(backendMerchantCategoryEntity);
            }
        }
        return i > 0;
    }

    @Override
    public boolean getBackendMerchant(Long merchantId) {
        EntityWrapper<BackendMerchantCategoryEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("status",GoodsConstants.BackendMerchantCategoryStatus.TO_AUDIT);
        cond.eq("merchantId",merchantId);
        List<BackendMerchantCategoryEntity> backendMerchantCategoryEntities = backendMerchantCategoryDao.selectList(cond);
        if(CollectionUtils.isNotEmpty(backendMerchantCategoryEntities)){
            return true;
        }
        return false;
    }


    @Override
    public String jointSaleFieldValue(String saleFieldValue) {
        String newSaleFieldValue = "";
        String[] split = saleFieldValue.split(",");
        for (int i = 0; i < split.length; i++) {
            String[] strs = split[i].split(":");
            BackendCategoryAttrEntity backendCategoryAttrEntity = new BackendCategoryAttrEntity();
            backendCategoryAttrEntity.clearInit();
            backendCategoryAttrEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            String s = strs[0];
            Long id = Long.parseLong(s);
            backendCategoryAttrEntity.setId(id);
            BackendCategoryAttrEntity backendCategoryAttrEntity1 = backendCategoryAttrDao.selectOne(backendCategoryAttrEntity);
            if (backendCategoryAttrEntity1 != null) {
                String name = backendCategoryAttrEntity1.getName();
                String value = strs[1];
                newSaleFieldValue = StringUtils.join(name, ":", value, "  ", newSaleFieldValue);
            }
        }
        return newSaleFieldValue;
    }

}
