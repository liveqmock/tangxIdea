/**
 *
 */
package com.topaiebiz.system.district.service.impl;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.system.common.PageUtil;
import com.topaiebiz.system.district.dao.DistrictDao;
import com.topaiebiz.system.district.dto.DistrictDto;
import com.topaiebiz.system.district.entity.DistrictEntity;
import com.topaiebiz.system.district.exception.DistrictExceptionEnum;
import com.topaiebiz.system.district.service.DistrictService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Description： 区域 业务逻辑实现类
 * <p>
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017年10月19日 下午2:23:22
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Service
public class DistrictServiceImpl implements DistrictService {

    @Autowired
    private DistrictDao districtDao;

    @Override
    public DistrictDto selectOneById(Long id) {
        DistrictEntity districtEntity = districtDao.selectById(id);
        DistrictDto districtDto = new DistrictDto();
        BeanUtils.copyProperties(districtEntity, districtDto);
        return districtDto;
    }

    @Override
    public List<DistrictDto> selectOneLevelDistrict() {
        List<DistrictEntity> districtEntities = districtDao.selectOneLevelDistrict();
        List<DistrictDto> districtDtos = PageUtil.copyList(districtEntities, DistrictDto.class);
        return districtDtos;
    }

    @Override
    public List<DistrictDto> selectAllDistrict() {
        List<DistrictEntity> districtEntities = districtDao.selectAllDistrict();
        List<DistrictDto> districtDtos = PageUtil.copyList(districtEntities, DistrictDto.class);
        return districtDtos;
    }

    @Override
    public List<DistrictDto> selectChildDistrictData(Long parentDistrictId) {
        DistrictEntity districtEntity = districtDao.selectById(parentDistrictId);
        if (districtEntity != null && districtEntity.getDeletedFlag().equals(new Byte("0"))) {
            return districtDao.selectChildDistrict(parentDistrictId);
        }
        return null;
    }

    @Override
    public Integer deleteDistrictDataById(Long id) {
        return districtDao.deleteDistrictById(id);
    }

    @Override
    public Integer createDistrict(DistrictDto districtDto) throws GlobalException {
        DistrictEntity districtEntity = districtDao.selectOneByCode(districtDto.getCode());
        if (null != districtEntity) {
            throw new GlobalException(DistrictExceptionEnum.DISTRICT_CODE_CANNOT_BE_REPEATED);
        }
        districtEntity = new DistrictEntity();
        BeanUtils.copyProperties(districtDto, districtEntity);
        districtEntity.setCreatedTime(new Date());
        districtEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        districtDao.insert(districtEntity);

        // 如果存父区域字段存在值，更新该区域的序列号和名称
        Long parentDistrictId = districtEntity.getParentDistrictId();
        if (null != parentDistrictId) {
            DistrictEntity parentDistrictEntity = districtDao.selectById(parentDistrictId);
            if (null != parentDistrictEntity) {
                districtEntity.setParentDistrictName(parentDistrictEntity.getFullName());
                String parentDistrictSerialNo = parentDistrictEntity.getSerialNo();
                String parentDistrictSerialName = parentDistrictEntity.getSerialName();
                if (!StringUtils.isEmpty(parentDistrictSerialNo)) {
                    districtEntity.setSerialNo(parentDistrictSerialNo + "." + districtEntity.getId());
                }
                if (!StringUtils.isEmpty(parentDistrictSerialName)) {
                    districtEntity.setSerialName(parentDistrictSerialName + "->" + districtEntity.getShortName());
                }
            }
        } else {
            districtEntity.setParentDistrictId(0l);
            districtEntity.setParentDistrictName(" ");
            districtEntity.setSerialNo(districtEntity.getId().toString());
            districtEntity.setSerialName(districtEntity.getShortName());
        }
        return districtDao.updateById(districtEntity);
    }

    @Override
    public Integer modifyDistrict(DistrictDto districtDto) throws GlobalException {
        DistrictEntity districtEntity = districtDao.selectById(districtDto.getId());
        if (districtEntity == null) {
            throw new GlobalException(DistrictExceptionEnum.DISTRICT_NOT_EXIST);
        }
        //检查唯一性
        String code = districtDto.getCode();
        if (!code.equals(districtEntity.getCode())) {
            if (null != districtDao.selectOneByCode(code)) {
                throw new GlobalException(DistrictExceptionEnum.DISTRICT_CODE_CANNOT_BE_REPEATED);
            }
        }
        //父区域被改，更新序列号和名称
        Boolean seriaChangeFalg = false;
        Long parentDistrictId = districtDto.getParentDistrictId();
        if (parentDistrictId == null) {
            districtDto.setParentDistrictId(0l);
            districtDto.setParentDistrictName(" ");
            districtDto.setSerialNo(districtEntity.getId().toString());
            districtDto.setSerialName(districtEntity.getShortName());
        } else {
            if (!parentDistrictId.equals(districtEntity.getParentDistrictId())) {
                seriaChangeFalg = true;
            }
        }
        if (seriaChangeFalg) {
            DistrictEntity parentDistrictEntity = districtDao.selectById(parentDistrictId);
            if (null != parentDistrictEntity) {
                districtDto.setParentDistrictName(parentDistrictEntity.getFullName());
                String parentDistrictSerialNo = parentDistrictEntity.getSerialNo();
                String parentDistrictSerialName = parentDistrictEntity.getSerialName();
                if (!StringUtils.isEmpty(parentDistrictSerialNo)) {
                    districtDto.setSerialNo(parentDistrictSerialNo + "." + districtEntity.getId());
                }
                if (!StringUtils.isEmpty(parentDistrictSerialName)) {
                    districtDto.setSerialName(parentDistrictSerialName + "->" + districtEntity.getShortName());
                }
            }
        }
        BeanUtils.copyProperties(districtDto, districtEntity);
        districtEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        districtEntity.setLastModifiedTime(new Date());
        return districtDao.updateById(districtEntity);
    }

}
