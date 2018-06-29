package com.topaiebiz.system.district.service;

import com.topaiebiz.basic.dto.DistrictDto;

import java.util.List;

public interface DistrictApiService {

    DistrictDto getDistrict(Long districtId);

    List<DistrictDto> getDistricts(List<Long> districtIds);

    DistrictDto selectOneLevel(Long id);

    DistrictDto selectByIdAndParentId(Long parentId, Long id);


}
