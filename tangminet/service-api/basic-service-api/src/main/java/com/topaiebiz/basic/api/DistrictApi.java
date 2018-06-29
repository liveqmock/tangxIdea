package com.topaiebiz.basic.api;

import com.topaiebiz.basic.dto.DistrictDto;

import java.util.List;


/**
 * Created by wzj on 2018-01-03.
 */
public interface DistrictApi {


    DistrictDto getDistrict(Long districtId);

    List<DistrictDto> getDistricts(List<Long> districtIds);

    /**
     * 根据id查询一级区域
     *
     * @param id
     * @return
     */
    DistrictDto selectOneLevel(Long id);

    /**
     * 根据id和父区域id查询区域
     *
     * @param parentId
     * @param id
     * @return
     */
    DistrictDto selectByIdAndParentId(Long parentId, Long id);
}
