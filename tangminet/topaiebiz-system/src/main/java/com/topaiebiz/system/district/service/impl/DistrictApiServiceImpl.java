package com.topaiebiz.system.district.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.topaiebiz.basic.dto.DistrictDto;
import com.topaiebiz.system.common.PageUtil;
import com.topaiebiz.system.district.dao.DistrictDao;
import com.topaiebiz.system.district.entity.DistrictEntity;
import com.topaiebiz.system.district.service.DistrictApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DistrictApiServiceImpl implements DistrictApiService {
    @Autowired
    private DistrictDao districtDao;

    @Override
    public DistrictDto getDistrict(Long districtId) {
        DistrictEntity districtEntity = districtDao.selectById(districtId);
        if (districtEntity == null) {
            log.error("未查询到districtId:{}关联的区域！", districtId);
            return null;
        }
        DistrictDto districtDto = new DistrictDto();
        BeanUtils.copyProperties(districtEntity, districtDto);
        return districtDto;
    }

    @Override
    public List<DistrictDto> getDistricts(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            log.error("参数ids为空,ids:{}关联的区域！", ids);
            return null;
        }
        EntityWrapper<DistrictEntity> entityCond = new EntityWrapper<>();
        entityCond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        entityCond.in("id", ids);
        List<DistrictEntity> districtEntities = districtDao.selectList(entityCond);
        if (CollectionUtils.isEmpty(districtEntities)) {
            log.error("未查询到,ids:{}关联的区域！", ids);
            return null;
        }
        List<DistrictDto> districtDtos = new ArrayList<>();
        for(DistrictEntity entity : districtEntities){
            DistrictDto dto = new DistrictDto();
            BeanCopyUtil.copy(entity, dto);
            districtDtos.add(dto);
        }
        return districtDtos;
    }

    @Override
    public DistrictDto selectOneLevel(Long id) {
        DistrictEntity districtEntity = districtDao.selectOneLevelById(id);
        if (districtEntity == null) {
            log.error("未查询到id:{}关联的区域！", id);
            return null;
        }
        DistrictDto districtDto = new DistrictDto();
        BeanUtils.copyProperties(districtEntity, districtDto);
        return districtDto;
    }

    @Override
    public DistrictDto selectByIdAndParentId(Long parentId, Long id) {
        DistrictEntity districtEntity = districtDao.selectByIdAndParentId(parentId, id);
        if (districtEntity == null) {
            log.error("未查询到parentId:{},id:{}关联的区域！", parentId, id);
            return null;
        }
        DistrictDto districtDto = new DistrictDto();
        BeanUtils.copyProperties(districtEntity, districtDto);
        return districtDto;
    }
}
