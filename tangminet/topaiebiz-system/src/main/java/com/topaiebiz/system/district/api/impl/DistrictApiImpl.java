package com.topaiebiz.system.district.api.impl;


import com.topaiebiz.basic.api.DistrictApi;
import com.topaiebiz.basic.dto.DistrictDto;
import com.topaiebiz.system.district.service.DistrictApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DistrictApiImpl implements DistrictApi {

    @Autowired
    private DistrictApiService districtApiService;

    @Override
    public DistrictDto getDistrict(Long districtId) {
        return districtApiService.getDistrict(districtId);
    }

    @Override
    public List<DistrictDto> getDistricts(List<Long> districtIds) {
        return districtApiService.getDistricts(districtIds);
    }

    @Override
    public DistrictDto selectOneLevel(Long id) {
        return districtApiService.selectOneLevel(id);
    }

    @Override
    public DistrictDto selectByIdAndParentId(Long parentId, Long id) {
        return districtApiService.selectByIdAndParentId(parentId, id);
    }
}
