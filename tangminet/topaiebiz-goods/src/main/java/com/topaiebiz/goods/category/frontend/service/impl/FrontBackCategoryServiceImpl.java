package com.topaiebiz.goods.category.frontend.service.impl;

import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryDao;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryDto;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryEntity;
import com.topaiebiz.goods.category.frontend.dao.FrontBackCategoryDao;
import com.topaiebiz.goods.category.frontend.dao.FrontendCategoryDao;
import com.topaiebiz.goods.category.frontend.dto.FrontBackCategoryDto;
import com.topaiebiz.goods.category.frontend.dto.FrontendCategoryDto;
import com.topaiebiz.goods.category.frontend.entity.FrontBackCategoryEntity;
import com.topaiebiz.goods.category.frontend.entity.FrontendCategoryEntity;
import com.topaiebiz.goods.category.frontend.exception.FrontendCategoryExceptionEnum;
import com.topaiebiz.goods.category.frontend.service.FrontBackCategoryService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by dell on 2018/1/10.
 */
@Service
public class FrontBackCategoryServiceImpl implements FrontBackCategoryService{

    @Autowired
    private FrontBackCategoryDao frontBackCategoryDao;

    @Autowired
    private BackendCategoryDao backendCategoryDao;

    @Autowired
    private FrontendCategoryDao frontendCategoryDao;

    @Override
    public Integer saveFrontBackCategory(FrontBackCategoryDto frontBackCategoryDto) throws GlobalException {
        FrontBackCategoryEntity frontBackCategory = new FrontBackCategoryEntity();
        BeanUtils.copyProperties(frontBackCategoryDto, frontBackCategory);
        BackendCategoryEntity backendCategoryEntity = new BackendCategoryEntity();
        backendCategoryEntity.clearInit();
        backendCategoryEntity.setId(frontBackCategory.getBackId());
        backendCategoryEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        BackendCategoryEntity backendCategoryEntity1 = backendCategoryDao.selectOne(backendCategoryEntity);
        /** 判断backId是否存在。 */
        if (backendCategoryEntity1 == null) {
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTBACKCATEGORY_BACKID_NOT_EXIST);
        }
        FrontendCategoryEntity frontendCategoryEntity = new FrontendCategoryEntity();
        frontendCategoryEntity.clearInit();
        frontendCategoryEntity.setId(frontBackCategory.getFrontId());
        frontendCategoryEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        FrontendCategoryEntity frontendCategoryEntity1 = frontendCategoryDao.selectOne(frontendCategoryEntity);
        /** 判断frontId是否存在。 */
        if (frontendCategoryEntity1 == null) {
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTBACKCATEGORY_FRONTID_NOT_EXIST);
        }
        frontBackCategory.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        frontBackCategory.setCreatedTime(new Date());
        return frontBackCategoryDao.insert(frontBackCategory);
    }

    @Override
    public Integer removeFrontBackCategory(Long id) throws GlobalException {
        if(null == id){
            throw new GlobalException(FrontendCategoryExceptionEnum.FRONTENDCATEGORY_PARENTID_NOT_NULL);
        }
        FrontBackCategoryEntity frontBackCategoryEntity = frontBackCategoryDao.selectById(id);
        frontBackCategoryEntity.setDeletedFlag(Constants.DeletedFlag.DELETED_YES);
        return  frontBackCategoryDao.updateById(frontBackCategoryEntity);
    }


}
