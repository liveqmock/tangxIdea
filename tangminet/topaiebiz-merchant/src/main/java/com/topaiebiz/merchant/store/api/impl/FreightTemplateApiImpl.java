package com.topaiebiz.merchant.store.api.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.api.FreightTemplateApi;
import com.topaiebiz.merchant.dto.template.FreightTemplateDTO;
import com.topaiebiz.merchant.dto.template.FreightTemplateDetailDTO;
import com.topaiebiz.merchant.enter.exception.MerchantEnterException;
import com.topaiebiz.merchant.freight.dao.FreightTempleteDao;
import com.topaiebiz.merchant.freight.dao.FreightTempleteDetailDao;
import com.topaiebiz.merchant.freight.entity.FreightTempleteDetailEntity;
import com.topaiebiz.merchant.freight.entity.FreightTempleteEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/***
 * @author yfeng
 * @date 2018-01-06 17:23
 */
@Service
public class FreightTemplateApiImpl implements FreightTemplateApi {

    @Autowired
    private FreightTempleteDao freightTempleteDao;

    @Autowired
    private  FreightTempleteDetailDao freightTempleteDetailDao;

    @Override
    public Map<Long, FreightTemplateDTO> getFreightTemplete(List<Long> freightIds) {
        if (CollectionUtils.isEmpty(freightIds)) {
            throw new GlobalException(MerchantEnterException.FREIGHTNAME_ID_NOT_NOLL);
        }
        EntityWrapper<FreightTempleteEntity> cond = new EntityWrapper<>();
        cond.in("id", freightIds);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<FreightTempleteEntity> templeteEntities = freightTempleteDao.selectList(cond);
        if (CollectionUtils.isEmpty(templeteEntities)){
            return Maps.newHashMap();
        }
        List<Long> teplateIds = templeteEntities.stream().map(item -> item.getId()).distinct().collect(Collectors.toList());
        EntityWrapper<FreightTempleteDetailEntity> detailCond = new EntityWrapper<>();
        detailCond.in("freightId", teplateIds);
        detailCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<FreightTempleteDetailEntity> detailEntities = freightTempleteDetailDao.selectList(detailCond);

        Map<Long, FreightTemplateDTO> reightTempleteMap = new HashMap<>();
        for (FreightTempleteEntity templeteEntity : templeteEntities) {
            FreightTemplateDTO dto = new FreightTemplateDTO();
            BeanCopyUtil.copy(templeteEntity, dto);
            reightTempleteMap.put(templeteEntity.getId(), dto);
        }

        for (FreightTempleteDetailEntity detailEntity : detailEntities) {
            FreightTemplateDTO templateDTO = reightTempleteMap.get(detailEntity.getFreightId());
            FreightTemplateDetailDTO detailDTO = new FreightTemplateDetailDTO();
            BeanCopyUtil.copy(detailEntity, detailDTO);
            if (StringUtils.isNotBlank(detailEntity.getDistrictIdList())){
                List<String> idValLists = Lists.newArrayList(detailEntity.getDistrictIdList().split(","));
                List<Long> cityIds = idValLists.stream().map(item->Long.parseLong(item)).collect(Collectors.toList());
                detailDTO.setSupportCityIds(cityIds);
            }

            templateDTO.getFreightTempleteDetailList().add(detailDTO);
        }
        return reightTempleteMap;
    }


    @Override
    public FreightTemplateDetailDTO getFreightTempleteDetail(Long freightId) {
        if(freightId== null){
            throw new GlobalException(MerchantEnterException.FREIGHTTEMPLATEDETAIL_ID_NOT_NULL);
        }

        EntityWrapper<FreightTempleteDetailEntity> condition = new EntityWrapper<>();
        condition.eq("freightId", freightId);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<FreightTempleteDetailEntity> freightTempleteDetailEntities = freightTempleteDetailDao.selectList(condition);
        if (CollectionUtils.isEmpty(freightTempleteDetailEntities)){
            return null;
        }
        Collections.sort(freightTempleteDetailEntities);
        FreightTempleteDetailEntity freightTempleteDetailEntity = freightTempleteDetailEntities.get(0);
        FreightTemplateDetailDTO freightTemplateDTO = new FreightTemplateDetailDTO();
        BeanCopyUtil.copy(freightTempleteDetailEntity, freightTemplateDTO);
        return freightTemplateDTO;
    }

    @Override
    public FreightTemplateDTO getFreighTemplateDTO(Long freightId) {
        FreightTempleteEntity freightTempleteEntity = new FreightTempleteEntity();
        freightTempleteEntity.cleanInit();
        freightTempleteEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        freightTempleteEntity.setId(freightId);
        FreightTempleteEntity freightTempleteEntity1 = freightTempleteDao.selectOne(freightTempleteEntity);
        FreightTemplateDTO freightTemplateDTO = new FreightTemplateDTO();
        BeanCopyUtil.copy(freightTempleteEntity1,freightTemplateDTO);
        return freightTemplateDTO;
    }
}