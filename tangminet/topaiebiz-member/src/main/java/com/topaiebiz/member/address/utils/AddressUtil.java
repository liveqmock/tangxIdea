package com.topaiebiz.member.address.utils;

import com.topaiebiz.basic.dto.DistrictDto;
import com.topaiebiz.member.dto.address.MemberAddressDto;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ward on 2018-02-28.
 */
public class AddressUtil {


    public static List<Long> extractDistrictIds(List<MemberAddressDto> memberAddressDtoList) {
        if (CollectionUtils.isEmpty(memberAddressDtoList)) {
            return null;
        }
        List<Long> districtIdList = new ArrayList<>();
        for (MemberAddressDto memberAddressDto : memberAddressDtoList) {
            if (memberAddressDto.getDistrictId() > 0) {
                districtIdList.add(memberAddressDto.getDistrictId());
            }
        }
        return districtIdList;
    }

    public static List<Long> extractParentDistrictIds(List<DistrictDto> districtDtoList) {
        if (CollectionUtils.isEmpty(districtDtoList)) {
            return null;
        }
        List<Long> districtIdList = new ArrayList<>();
        for (DistrictDto districtDto : districtDtoList) {
            if (districtDto.getParentDistrictId() > 0) {
                districtIdList.add(districtDto.getParentDistrictId());
            }
        }
        return districtIdList;
    }

    public static Map<Long, DistrictDto> transforMap(List<DistrictDto> districtDtoList) {
        if (CollectionUtils.isEmpty(districtDtoList)) {
            return null;
        }
        HashMap<Long, DistrictDto> districtDtoMap = new HashMap<>();
        for (DistrictDto districtDto : districtDtoList) {
            if (districtDto.getParentDistrictId() > 0) {
                districtDtoMap.put(districtDto.getId(), districtDto);
            }
        }
        return districtDtoMap;
    }
}
