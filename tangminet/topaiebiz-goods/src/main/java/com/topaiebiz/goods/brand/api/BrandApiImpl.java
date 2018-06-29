package com.topaiebiz.goods.brand.api;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.api.BrandApi;
import com.topaiebiz.goods.brand.dao.BrandDao;
import com.topaiebiz.goods.brand.entity.BrandEntity;
import com.topaiebiz.goods.brand.exception.BrandExceptionEnum;
import com.topaiebiz.goods.dto.Brand.BrandDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Created by hecaifeng on 2018/6/22.
 */
@Service
public class BrandApiImpl implements BrandApi {

    @Autowired
    private BrandDao brandDao;

    @Override
    public BrandDTO getBrand(Long brandId) {
        if (null == brandId) {
            throw new GlobalException(BrandExceptionEnum.BRAND_ID_NOT_NULL);
        }
        BrandEntity brandEntity = brandDao.selectById(brandId);
        if (brandEntity == null) {
            return null;
        }
        BrandDTO brandDTO = new BrandDTO();
        BeanCopyUtil.copy(brandEntity, brandDTO);
        return brandDTO;
    }

    @Override
    public List<BrandDTO> getBrands(List<Long> brandIds) {
        if (CollectionUtils.isEmpty(brandIds)) {
            return Collections.emptyList();
        }
        EntityWrapper<BrandEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.in("id", brandIds);
        List<BrandEntity> brandEntities = brandDao.selectList(cond);
        List<BrandDTO> brandDtos = BeanCopyUtil.copyList(brandEntities, BrandDTO.class);
        return brandDtos;
    }
}
