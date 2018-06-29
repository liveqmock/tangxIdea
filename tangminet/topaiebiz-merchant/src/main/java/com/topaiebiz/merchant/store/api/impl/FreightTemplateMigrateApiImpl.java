package com.topaiebiz.merchant.store.api.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.topaiebiz.merchant.api.FreightTemplateMigrateApi;
import com.topaiebiz.merchant.freight.dao.FreightTempleteDao;
import com.topaiebiz.merchant.freight.dao.FreightTempleteDetailDao;
import com.topaiebiz.merchant.freight.entity.FreightTempleteDetailEntity;
import com.topaiebiz.merchant.freight.entity.FreightTempleteEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/***
 * @author yfeng
 * @date 2018-02-11 19:20
 */
@Service
@Slf4j
public class FreightTemplateMigrateApiImpl implements FreightTemplateMigrateApi {

    @Autowired
    private FreightTempleteDao freightTempleteDao;
    @Autowired
    private FreightTempleteDetailDao freightTempleteDetailDao;

    private static final Cache<String, Long> fixPriceTeplateCache = CacheBuilder.newBuilder()
            .initialCapacity(50000)
            .concurrencyLevel(8)
            .expireAfterWrite(4, TimeUnit.HOURS)
            .build();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copyTemplate(Long templateId, Long targetStoreId) {
        //查询运费模板和详情
        FreightTempleteEntity templeteEntity = freightTempleteDao.selectById(templateId);

        //查询运费模板详情
        List<FreightTempleteDetailEntity> templeteDetails = freightTempleteDetailDao.selectByFreightId(templateId);

        //拷贝运费模板
        FreightTempleteEntity newTemplate = new FreightTempleteEntity();
        BeanCopyUtil.copy(templeteEntity, newTemplate);
        newTemplate.setId(null);
        newTemplate.setStoreId(targetStoreId);
        freightTempleteDao.insert(newTemplate);

        //拷贝运费模板详情
        for (FreightTempleteDetailEntity templateDetail : templeteDetails) {
            FreightTempleteDetailEntity newTemplateDetail = new FreightTempleteDetailEntity();
            BeanCopyUtil.copy(templateDetail, newTemplateDetail);
            newTemplateDetail.setId(null);
            newTemplateDetail.setFreightId(newTemplate.getId());
            freightTempleteDetailDao.insert(newTemplateDetail);
        }
        return newTemplate.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDefaultTemplateDetail(Long temmplateId, BigDecimal fixFreight) {
        FreightTempleteDetailEntity newTemplateDetail = new FreightTempleteDetailEntity();
        newTemplateDetail.setFirstNum(1);
        newTemplateDetail.setFirstPrice(fixFreight.doubleValue());
        newTemplateDetail.setAddNum(0);
        newTemplateDetail.setAddPrice(0);
        //默认模板详情
        newTemplateDetail.setIsDefault(0);
        newTemplateDetail.setFreightId(temmplateId);
        newTemplateDetail.setDistrictIdList("");
        newTemplateDetail.setNameListStr("");
        newTemplateDetail.setType(0);
        newTemplateDetail.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        freightTempleteDetailDao.insert(newTemplateDetail);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTemplate(Long belongStore, BigDecimal maxFixFreight) {
        BigDecimal fixFreight = toStanardValue(maxFixFreight);
        String cacheKey = belongStore.toString() + "-" + fixFreight.toString();
        Long templateId = fixPriceTeplateCache.getIfPresent(cacheKey);
        if (templateId != null) {
            return templateId;
        }

        FreightTempleteEntity fixTemplate = query(belongStore, fixFreight);
        if (fixTemplate != null) {
            fixPriceTeplateCache.put(cacheKey, fixTemplate.getId());
            return fixTemplate.getId();
        }

        fixTemplate = new FreightTempleteEntity();
        fixTemplate.setStoreId(belongStore);
        fixTemplate.setIsDefault(0);
        fixTemplate.setFreightName("固定" + fixFreight.toString() + "元运费");
        fixTemplate.setOnlyThis(0);

        //按件计费
        fixTemplate.setPricing(1);
        freightTempleteDao.insert(fixTemplate);


        FreightTempleteDetailEntity newTemplateDetail = new FreightTempleteDetailEntity();
        newTemplateDetail.setFirstNum(1);
        newTemplateDetail.setFirstPrice(fixFreight.doubleValue());
        newTemplateDetail.setAddNum(0);
        newTemplateDetail.setAddPrice(0);
        //默认模板详情
        newTemplateDetail.setIsDefault(0);
        newTemplateDetail.setFreightId(fixTemplate.getId());
        newTemplateDetail.setDistrictIdList("");
        newTemplateDetail.setNameListStr("");
        newTemplateDetail.setType(0);
        newTemplateDetail.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        freightTempleteDetailDao.insert(newTemplateDetail);

        fixPriceTeplateCache.put(cacheKey, fixTemplate.getId());
        return fixTemplate.getId();
    }

    private static BigDecimal toStanardValue(BigDecimal maxFixFreight) {
        return maxFixFreight.setScale(2);
    }

    private FreightTempleteEntity query(Long belongStore, BigDecimal fixFreight) {
        FreightTempleteEntity cond = new FreightTempleteEntity();
        cond.cleanInit();
        cond.setStoreId(belongStore);
        cond.setIsDefault(0);
        cond.setOnlyThis(0);
        cond.setFreightName("固定" + fixFreight.toString() + "元运费");
        cond.setPricing(1);
        List<FreightTempleteEntity> tepls = freightTempleteDao.selectList(new EntityWrapper<>(cond));
        if (CollectionUtils.isNotEmpty(tepls)) {
            return tepls.get(0);
        }
        return null;
    }
}
