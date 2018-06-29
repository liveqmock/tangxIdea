package com.topaiebiz.merchant.freight.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.basic.api.DistrictApi;
import com.topaiebiz.basic.dto.DistrictDto;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.merchant.enter.dto.DistrictInfoDto;
import com.topaiebiz.merchant.enter.exception.MerchantEnterException;
import com.topaiebiz.merchant.freight.dao.FreightTempleteDao;
import com.topaiebiz.merchant.freight.dao.FreightTempleteDetailDao;
import com.topaiebiz.merchant.freight.dto.AddFreightTempleteDto;
import com.topaiebiz.merchant.freight.dto.FreightTempleteDetailDto;
import com.topaiebiz.merchant.freight.dto.FreightTempleteDto;
import com.topaiebiz.merchant.freight.dto.MerFreightTempleteDto;
import com.topaiebiz.merchant.freight.entity.FreightTempleteDetailEntity;
import com.topaiebiz.merchant.freight.entity.FreightTempleteEntity;
import com.topaiebiz.merchant.freight.service.MerchantFreightService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class MerchantFreightServiceImpl implements MerchantFreightService {

    @Autowired
    private FreightTempleteDao freightTempleteDao;

    @Autowired
    private FreightTempleteDetailDao freightTempleteDetailDao;

    @Autowired
    private GoodsApi goodsApi;

    @Autowired
    private DistrictApi districtApi;


    @Override
    public PageInfo<MerFreightTempleteDto> getMerFreightTempleteList(MerFreightTempleteDto merFreightTempleteDto) {
        Page<MerFreightTempleteDto> page = PageDataUtil.buildPageParam(merFreightTempleteDto);
        //查询条件
        EntityWrapper<FreightTempleteEntity> condition = new EntityWrapper<>();
        condition.eq("storeId", SecurityContextUtils.getCurrentUserDto().getStoreId());
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.orderBy("createdTime", false);

        if (null != merFreightTempleteDto.getFreightName() && !"".equals(merFreightTempleteDto.getFreightName())) {
            condition.like("freightName", merFreightTempleteDto.getFreightName());
        }
        if (null != merFreightTempleteDto.getPricing() && 0 != merFreightTempleteDto.getPricing()) {
            condition.eq("pricing", merFreightTempleteDto.getPricing());
        }

        List<FreightTempleteEntity> templeteEntities = freightTempleteDao.selectPage(page, condition);
        if (CollectionUtils.isEmpty(templeteEntities)) {
            return PageDataUtil.copyPageInfo(page);
        }
        List<MerFreightTempleteDto> templateDtos = new ArrayList<>();
        for (FreightTempleteEntity freightTempleteEntity : templeteEntities) {
            MerFreightTempleteDto merFreightDto = new MerFreightTempleteDto();
            BeanCopyUtil.copy(freightTempleteEntity, merFreightDto);
            templateDtos.add(merFreightDto);
        }
        page.setRecords(templateDtos);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public void saveMerFreightTemplete(AddFreightTempleteDto addFreightTempleteDto) {
        FreightTempleteEntity freightTempleteEntity = new FreightTempleteEntity();
        // 根据当前用户查出商家id
        addFreightTempleteDto.setStoreId(SecurityContextUtils.getCurrentUserDto().getStoreId());
        BeanCopyUtil.copy(addFreightTempleteDto, freightTempleteEntity);
        freightTempleteEntity.setIsDefault(freightTempleteEntity.getIsDefault() == null ? 0 : freightTempleteEntity.getIsDefault());
        freightTempleteEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        freightTempleteEntity.setCreatedTime(new Date());
        freightTempleteDao.insert(freightTempleteEntity);
        List<FreightTempleteDetailDto> freightTempleteDetails = addFreightTempleteDto.getFreightTempleteDetails();
        for (FreightTempleteDetailDto freightTempleteDetailDto : freightTempleteDetails) {
            FreightTempleteDetailEntity entity = new FreightTempleteDetailEntity();
            BeanCopyUtil.copy(freightTempleteDetailDto, entity);
            entity.setIsDefault(entity.getIsDefault() == null ? 0 : entity.getIsDefault());
            entity.setDistrictIdList(freightTempleteDetailDto.getDistrictIdList());
            entity.setFreightId(freightTempleteEntity.getId());
            entity.setCreatedTime(new Date());
            entity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
            freightTempleteDetailDao.insert(entity);
        }
    }

    @Override
    public void removeMerFreightTempleteById(Long id) {
        List<ItemDTO> itemByLogisticsId = goodsApi.getItemByLogisticsId(id);
        if (CollectionUtils.isNotEmpty(itemByLogisticsId)) {
            throw new GlobalException(MerchantEnterException.THE_TEMPLATE_IS_IN_USE);
        }
        FreightTempleteEntity freightTempleteEntity = freightTempleteDao.selectById(id);
        if (null == freightTempleteEntity) {
            throw new GlobalException(MerchantEnterException.FREIGHTNAME_ID_NOT_EXIST);
        }
        freightTempleteEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        freightTempleteDao.updateById(freightTempleteEntity);

        //删除模版详情
        EntityWrapper<FreightTempleteDetailEntity> condition = new EntityWrapper<>();
        condition.eq("freightId", id);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        FreightTempleteDetailEntity detailEntity = new FreightTempleteDetailEntity();
        detailEntity.cleanInit();
        detailEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        freightTempleteDetailDao.update(detailEntity, condition);

    }

    @Override
    public AddFreightTempleteDto selectMerFreightTempleteById(Long id) {
        //返回DTO
        AddFreightTempleteDto addFreightTempleteDto = new AddFreightTempleteDto();
        //运费模版主表
        FreightTempleteEntity entity = freightTempleteDao.selectById(id);
        if (null == entity || entity.getDeletedFlag().equals(Constants.DeletedFlag.DELETED_YES)) {
            throw new GlobalException(MerchantEnterException.FREIGHTNAME_ID_NOT_EXIST);
        }
        EntityWrapper<FreightTempleteDetailEntity> condition = new EntityWrapper<>();
        condition.eq("freightId", id);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<FreightTempleteDetailEntity> detailEntities = freightTempleteDetailDao.selectList(condition);

        BeanCopyUtil.copy(entity, addFreightTempleteDto);

        // 返回详情dtos
        List<FreightTempleteDetailDto> dtos = new ArrayList<>();
        for (FreightTempleteDetailEntity freightTempleteDetailEntity : detailEntities) {
            FreightTempleteDetailDto detailDto = new FreightTempleteDetailDto();
            List<DistrictInfoDto> districtDtoList = null;
            String nameListStr = "";
            //区域集合
            String districtIdList = freightTempleteDetailEntity.getDistrictIdList();
            if (StringUtils.isNotBlank(districtIdList)) {
                Map<String, Object> resMap = this.convertDistrict(districtIdList);
                nameListStr = (String) resMap.get("nameList");
                districtDtoList = (List<DistrictInfoDto>) resMap.get("districtList");
            }
            if (!"".equals(nameListStr)) {
                nameListStr = nameListStr.substring(0, nameListStr.toString().length() - 1);
            }
            BeanCopyUtil.copy(freightTempleteDetailEntity, detailDto);
            detailDto.setNameListStr(nameListStr);
            detailDto.setDistrictDtoList(districtDtoList);
            dtos.add(detailDto);
        }
        addFreightTempleteDto.setFreightTempleteDetails(dtos);
        return addFreightTempleteDto;
    }

    @Override
    public void updateMerFreightTempleteById(AddFreightTempleteDto addFreightTempleteDto) {
        Long id = addFreightTempleteDto.getId();
        FreightTempleteEntity freightTemplete = freightTempleteDao.selectById(id);
        if (freightTemplete.getPricing().equals(addFreightTempleteDto.getPricing())==true){
            BeanCopyUtil.copy(addFreightTempleteDto, freightTemplete);
        }else{
            throw new GlobalException(MerchantEnterException.THE_PRICING_CANNOT_BE_MODIFIED);
        }
      //  BeanCopyUtil.copy(addFreightTempleteDto, freightTemplete);
        freightTemplete.setIsDefault(freightTemplete.getIsDefault() == null ? 0 : freightTemplete.getIsDefault());
        freightTemplete.setLastModifiedTime(new Date());
        freightTemplete.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        freightTempleteDao.updateById(freightTemplete);

        EntityWrapper<FreightTempleteDetailEntity> condition = new EntityWrapper<>();
        condition.eq("freightId", id);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        FreightTempleteDetailEntity detailEntity = new FreightTempleteDetailEntity();
        detailEntity.cleanInit();
        detailEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        freightTempleteDetailDao.update(detailEntity, condition);

        List<FreightTempleteDetailDto> freightTempleteDetails = addFreightTempleteDto.getFreightTempleteDetails();
        for (FreightTempleteDetailDto freightTempleteDetailDto : freightTempleteDetails) {
            FreightTempleteDetailEntity entity = new FreightTempleteDetailEntity();
            BeanCopyUtil.copy(freightTempleteDetailDto, entity);
            entity.setFreightId(id);
            entity.setIsDefault(entity.getIsDefault() == null ? 0 : entity.getIsDefault());
            entity.setDistrictIdList(freightTempleteDetailDto.getDistrictIdList());
            entity.setCreatedTime(new Date());
            entity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
            freightTempleteDetailDao.insert(entity);
        }

    }

    @Override
    public List<FreightTempleteDto> getList() {
        EntityWrapper<FreightTempleteEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("storeId", SecurityContextUtils.getCurrentUserDto().getStoreId());
        List<FreightTempleteEntity> freightTempleteEntities = freightTempleteDao.selectList(cond);

        if (CollectionUtils.isEmpty(freightTempleteEntities)) {
            return null;
        }
        List<FreightTempleteDto> templateDtos = new ArrayList<>();
        for (FreightTempleteEntity freightTempleteEntity : freightTempleteEntities) {
            FreightTempleteDto merFreightDto = new FreightTempleteDto();
            BeanCopyUtil.copy(freightTempleteEntity, merFreightDto);
            templateDtos.add(merFreightDto);
        }
        return templateDtos;
    }

    private Map<String, Object> convertDistrict(String listStr) {
        Map<String, Object> map = new HashMap<>();
        StringBuffer nameList = new StringBuffer();
        List<DistrictInfoDto> districtDtoList = new ArrayList<>();
        String[] split = listStr.split(",");
        List<String> idListStr = Arrays.asList(split);
        List<Long> idList= new ArrayList<>();
        for(String s : idListStr){
            idList.add(Long.valueOf(s));
        }
        Map<String, DistrictDto> districtMap = new HashMap<>();
        List<DistrictDto> districts = districtApi.getDistricts(idList);
        for(DistrictDto dto : districts){
            districtMap.put(String.valueOf(dto.getId()), dto);
        }

        for (String s : split) {
            DistrictDto oneLevelDistrict = districtMap.get(s);
            // 一级区域
            if (null != oneLevelDistrict && new Long(0).equals(oneLevelDistrict.getParentDistrictId())) {
                nameList.append((oneLevelDistrict.getFullName() == null || "".equals(oneLevelDistrict.getFullName())) ? "" : oneLevelDistrict.getFullName() + ",");
                DistrictInfoDto oneDto = new DistrictInfoDto();
                BeanCopyUtil.copy(oneLevelDistrict, oneDto);
                districtDtoList.add(oneDto);
                List<DistrictInfoDto> twoList = new ArrayList<DistrictInfoDto>();
                oneDto.setChildList(twoList);
                // 二级区域
                for (String s2 : split) {
                    DistrictDto twoDistrict = districtMap.get(s2);
                    if (null != twoDistrict && twoDistrict.getParentDistrictId().equals(oneLevelDistrict.getId())) {
                        nameList.append((twoDistrict.getFullName() == null || "".equals(twoDistrict.getFullName())) ? "" : twoDistrict.getFullName() + ",");
                        DistrictInfoDto twoDto = new DistrictInfoDto();
                        BeanCopyUtil.copy(twoDistrict, twoDto);
                        twoList.add(twoDto);
                        List<DistrictInfoDto> threeList = new ArrayList<DistrictInfoDto>();
                        twoDto.setChildList(threeList);
                        // 三级区域
                        for (String s3 : split) {
                            DistrictDto threeDistrict = districtMap.get(s3);
                            if (null != threeDistrict && threeDistrict.getParentDistrictId().equals(twoDistrict.getId())) {
                                nameList.append((threeDistrict.getFullName() == null || "".equals(threeDistrict.getFullName())) ? "" : threeDistrict.getFullName() + ",");
                                DistrictInfoDto dto3 = new DistrictInfoDto();
                                BeanCopyUtil.copy(threeDistrict, dto3);
                                threeList.add(dto3);
                            }
                        }
                    }
                }

            }
        }
        map.put("nameList", nameList.toString());
        map.put("districtList", districtDtoList);
        return map;
    }

}
