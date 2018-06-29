package com.topaiebiz.goods.brand.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Maps;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.topaiebiz.goods.brand.dao.SuitableAgeDao;
import com.topaiebiz.goods.brand.dto.SuitableAgeDto;
import com.topaiebiz.goods.brand.entity.SuitableAgeEntity;
import com.topaiebiz.goods.brand.service.SuitableAgeService;
import com.topaiebiz.goods.sku.entity.GoodsSkuEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description 商品年龄段实现类
 *
 * Author Hedda
 *
 * Date 2017年8月23日 下午4:14:59
 *
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 *
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
public class SuitableAgeServiceImpl implements SuitableAgeService{

    @Autowired
    private SuitableAgeDao suitableAgeDao;

    @Override
    public List<SuitableAgeDto> getSuitableAgeList() {
        EntityWrapper<SuitableAgeEntity> condition = new EntityWrapper<>();
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<SuitableAgeEntity> suitableAgeEntities = suitableAgeDao.selectList(condition);
        if (CollectionUtils.isEmpty(suitableAgeEntities)) {
            return Collections.emptyList();
        }
        List<SuitableAgeDto> suitableAgeDtos = new ArrayList<>();
        for (SuitableAgeEntity suitableAgeEntity : suitableAgeEntities){
            SuitableAgeDto suitableAgeDto = new SuitableAgeDto();
            BeanCopyUtil.copy(suitableAgeEntity, suitableAgeDto);
            suitableAgeDtos.add(suitableAgeDto);
        }
        return suitableAgeDtos;
    }

    @Override
    public List<SuitableAgeDto> getAppListSuitableAge() {
        EntityWrapper<SuitableAgeEntity> condition = new EntityWrapper<>();
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.notIn("ageGroup","其他");
        List<SuitableAgeEntity> suitableAgeEntities = suitableAgeDao.selectList(condition);
        if (CollectionUtils.isEmpty(suitableAgeEntities)) {
            return Collections.emptyList();
        }
        List<SuitableAgeDto> suitableAgeDtos = new ArrayList<>();
        for (SuitableAgeEntity suitableAgeEntity : suitableAgeEntities){
            SuitableAgeDto suitableAgeDto = new SuitableAgeDto();
            BeanCopyUtil.copy(suitableAgeEntity, suitableAgeDto);
            suitableAgeDtos.add(suitableAgeDto);
        }
        return suitableAgeDtos;
    }

}
