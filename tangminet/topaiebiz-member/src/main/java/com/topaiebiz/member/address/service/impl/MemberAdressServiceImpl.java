package com.topaiebiz.member.address.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.basic.api.DistrictApi;
import com.topaiebiz.basic.dto.DistrictDto;
import com.topaiebiz.member.address.dao.MemberAddressDao;
import com.topaiebiz.member.address.entity.MemberAddressEntity;
import com.topaiebiz.member.address.service.MemberAddressService;
import com.topaiebiz.member.address.utils.AddressUtil;
import com.topaiebiz.member.dto.address.MemberAddressDto;
import com.topaiebiz.member.exception.AddressExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MemberAdressServiceImpl extends ServiceImpl<MemberAddressDao, MemberAddressEntity> implements MemberAddressService {


    private final Integer MEMBER_ADDRESS_LIMIT = 20;
    private final Integer ADDRESS_DEFAULT_TRUE = 1;

    @Autowired
    private MemberAddressDao memberAddressDao;
    /**
     * 区域 数据持久性接口
     */

    @Autowired
    private DistrictApi districtApi;

    @Override
    public Boolean addMemberAddress(MemberAddressDto memberAddressDto) {

        Long memberId = memberAddressDto.getMemberId();
        Integer addressCount = getAddressCount(memberId);
        if (addressCount >= MEMBER_ADDRESS_LIMIT) {
            log.warn("会员地址数量超限memberId={},addressCount={},sysLimit={}", memberId, addressCount, MEMBER_ADDRESS_LIMIT);
            throw new GlobalException(AddressExceptionEnum.MEMBER_ADDRESS_COUNT_LIMIT);
        }

        MemberAddressEntity entity = new MemberAddressEntity();
        BeanCopyUtil.copy(memberAddressDto, entity);
        entity.setCreatorId(memberAddressDto.getMemberId());
        entity.setCreatedTime(new Date());
        if (memberAddressDao.insert(entity) <= 0) {
            log.warn("执行添加地址失败memberId={},memberAddressDto={}", memberId, JSON.toJSONString(memberAddressDto));
            throw new GlobalException(AddressExceptionEnum.MEMBER_ADDRESS_SYSERR);
        }
        //如果新增 地址是默认地址，则设置为默认地址
        if (ADDRESS_DEFAULT_TRUE.equals(entity.getIsDefault())) {
            setDefaultAddress(memberId, entity.getId());
        }
        return true;
    }

    private Integer getAddressCount(Long memberId) {
        MemberAddressEntity param = new MemberAddressEntity();
        param.cleanInit();
        param.setMemberId(memberId);
        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        return memberAddressDao.selectCount(new EntityWrapper<>(param));
    }


    private MemberAddressEntity getAddress(Long memberId, Long addressId) {
        MemberAddressEntity param = new MemberAddressEntity();
        param.cleanInit();
        param.setMemberId(memberId);
        param.setId(addressId);
        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        return memberAddressDao.selectOne(param);
    }


    @Override
    public Boolean modifyMemberAddress(MemberAddressDto memberAddressDto) {
        Long memberId = memberAddressDto.getMemberId();
        Long addressId = memberAddressDto.getId();
        MemberAddressEntity memberAddressEntity = getAddress(memberId, addressId);
        if (null == memberAddressEntity) {
            log.warn("修改会员地址不存在memberId={},addressId={}", memberId, addressId);
            throw new GlobalException(AddressExceptionEnum.MEMBER_ADDRESS_NOT_EXIST);
        }
        if (null != memberAddressDto.getIsDefault() && ADDRESS_DEFAULT_TRUE.equals(memberAddressDto.getIsDefault())) {
            memberAddressDao.updateMemberAddressByMemberId(memberId);
        }
        BeanCopyUtil.copy(memberAddressDto, memberAddressEntity);
        memberAddressEntity.setLastModifierId(memberId);
        memberAddressEntity.setLastModifiedTime(new Date());
        if (memberAddressDao.updateById(memberAddressEntity) <= 0) {
            log.warn("执行修改地址失败memberId={},memberAddressDto={}", memberId, JSON.toJSONString(memberAddressDto));
            throw new GlobalException(AddressExceptionEnum.MEMBER_ADDRESS_SYSERR);
        }
        return true;
    }

    @Override
    public MemberAddressDto findMemberAddress(Long memberId, Long addressId) {
        MemberAddressEntity memberAddressEntity = getAddress(memberId, addressId);
        if (null == memberAddressEntity) {
            log.warn("查询会员地址不存在memberId={},addressId={}", memberId, addressId);
            throw new GlobalException(AddressExceptionEnum.MEMBER_ADDRESS_NOT_EXIST);
        }
        MemberAddressDto memberAddressDto = new MemberAddressDto();
        BeanCopyUtil.copy(memberAddressEntity, memberAddressDto);

        Long districtId = memberAddressDto.getDistrictId();
        /**根据区id查询市名称*/
        DistrictDto districtDto = districtApi.getDistrict(districtId);
        if (null == districtDto) {
            return memberAddressDto;
        }
        memberAddressDto.setDistrictName(districtDto.getFullName());
        DistrictDto cityDistricDto = districtApi.getDistrict(districtDto.getParentDistrictId());
        if (null == cityDistricDto) {
            return memberAddressDto;
        }
        memberAddressDto.setProvinceName(cityDistricDto.getParentDistrictName());
        memberAddressDto.setProvinceId(cityDistricDto.getParentDistrictId());
        memberAddressDto.setCityId(cityDistricDto.getId());
        memberAddressDto.setCityName(cityDistricDto.getFullName());
        return memberAddressDto;
    }

    @Override
    public Boolean removeAddress(Long memberId, Long addressId) {
        MemberAddressEntity memberAddressEntity = getAddress(memberId, addressId);
        if (null == memberAddressEntity) {
            log.warn("删除会员地址不存在memberId={},addressId={}", memberId, addressId);
            throw new GlobalException(AddressExceptionEnum.MEMBER_ADDRESS_NOT_EXIST);
        }

        MemberAddressEntity update = new MemberAddressEntity();
        update.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        EntityWrapper<MemberAddressEntity> condition = new EntityWrapper<>();
        condition.eq("id", addressId);
        condition.eq("memberId", memberId);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        if (memberAddressDao.update(update, condition) <= 0) {
            log.warn("执行删除地址失败memberId={},addressId={}", memberId, addressId);
            throw new GlobalException(AddressExceptionEnum.MEMBER_ADDRESS_SYSERR);
        }
        if (ADDRESS_DEFAULT_TRUE == memberAddressEntity.getIsDefault()) {
            MemberAddressEntity addressEntity = getLastAddress(memberId);
            if (null != addressEntity) {
                setDefaultAddress(memberId, addressEntity.getId());
            }
        }


        return true;

    }

    private MemberAddressEntity getLastAddress(Long memberId) {
        MemberAddressEntity selectParm = new MemberAddressEntity();
        selectParm.cleanInit();
        selectParm.setMemberId(memberId);
        selectParm.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        EntityWrapper<MemberAddressEntity> param = new EntityWrapper<>();
        param.setEntity(selectParm);
        param.orderBy("createdTime", false);
        param.last("limit 1");
        List<MemberAddressEntity> addressEntityList = memberAddressDao.selectList(param);
        if (CollectionUtils.isEmpty(addressEntityList)) {
            return null;
        }
        return addressEntityList.get(0);
    }


    @Override
    public List<MemberAddressDto> getMemberAddressList(Long memberId) {
        List<MemberAddressDto> memberAddressDtoList = memberAddressDao.selectMemberAddress(memberId);
        if (CollectionUtils.isEmpty(memberAddressDtoList)) {
            return null;
        }

        //批量查询区县信息
        List<Long> districtIdList = AddressUtil.extractDistrictIds(memberAddressDtoList);

        Long[] ass = new Long[districtIdList.size()];
        districtIdList.toArray(ass);

        List<DistrictDto> districtDtoList = districtApi.getDistricts(districtIdList);
        Map<Long, DistrictDto> districtMap = AddressUtil.transforMap(districtDtoList);

        //批量查询城市信息
        List<Long> cityDistrictIds = AddressUtil.extractParentDistrictIds(districtDtoList);
        List<DistrictDto> cityDistricList = districtApi.getDistricts(cityDistrictIds);
        Map<Long, DistrictDto> cityDistrictMap = AddressUtil.transforMap(cityDistricList);

        for (MemberAddressDto memberAddressDto : memberAddressDtoList) {
            Long districtId = memberAddressDto.getDistrictId();
            /**根据区id查询市名称*/
            DistrictDto districtDto = districtMap.get(districtId);
            if (null == districtDto) {
                continue;
            }
            DistrictDto cityDistricDto = cityDistrictMap.get(districtDto.getParentDistrictId());
            if (null == cityDistricDto) {
                continue;
            }
            /**根据市id查询省对象*/
            memberAddressDto.setDistrictName(districtDto.getFullName());
            memberAddressDto.setProvinceName(cityDistricDto.getParentDistrictName());
            memberAddressDto.setProvinceId(cityDistricDto.getParentDistrictId());
            memberAddressDto.setCityId(cityDistricDto.getId());
            memberAddressDto.setCityName(cityDistricDto.getFullName());
        }
        return memberAddressDtoList;
    }


    @Override
    public MemberAddressDto getDefaultAddress(Long memberId) {
        MemberAddressEntity memberAddressEntity = memberAddressDao.findDefaultAddressByMemberId(memberId);
        if (null == memberAddressEntity) {
            return null;
        }
        MemberAddressDto memberAddressDto = new MemberAddressDto();
        BeanCopyUtil.copy(memberAddressEntity, memberAddressDto);

        Long districtId = memberAddressDto.getDistrictId();
        /**根据区id查询市名称*/
        DistrictDto districtDto = districtApi.getDistrict(districtId);
        if (null == districtDto) {
            return memberAddressDto;
        }
        memberAddressDto.setDistrictName(districtDto.getFullName());
        /**根据市id查询省对象*/
        DistrictDto cityDistricDto = districtApi.getDistrict(districtDto.getParentDistrictId());
        if (null != cityDistricDto) {
            memberAddressDto.setProvinceName(cityDistricDto.getParentDistrictName());
            memberAddressDto.setProvinceId(cityDistricDto.getParentDistrictId());
            memberAddressDto.setCityId(cityDistricDto.getId());
            memberAddressDto.setCityName(cityDistricDto.getFullName());
        }
        return memberAddressDto;
    }


    @Override
    public Boolean setDefaultAddress(Long memberId, Long addressId) {
        MemberAddressEntity memberAddressEntity = getAddress(memberId, addressId);
        if (null == memberAddressEntity) {
            log.warn("默认会员地址不存在memberId={},addressId={}", memberId, addressId);
            throw new GlobalException(AddressExceptionEnum.MEMBER_ADDRESS_NOT_EXIST);
        }
        memberAddressDao.updateMemberAddressByMemberId(memberId);
        MemberAddressEntity update = new MemberAddressEntity();
        update.cleanInit();
        update.setLastModifierId(memberId);
        update.setLastModifiedTime(new Date());
        update.setId(addressId);
        update.setIsDefault(ADDRESS_DEFAULT_TRUE);
        if (memberAddressDao.updateById(update) <= 0) {
            log.warn("执行设置默认地址失败memberId={},addressId={}", memberId, addressId);
            throw new GlobalException(AddressExceptionEnum.MEMBER_ADDRESS_SYSERR);
        }
        return true;
    }
}
