package com.topaiebiz.merchant.store.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.topaiebiz.goods.api.BackendCategoryApi;
import com.topaiebiz.goods.constants.GoodsConstants;
import com.topaiebiz.goods.dto.category.backend.BackendCategoryStatusDTO;
import com.topaiebiz.merchant.constants.MerchantConstants;
import com.topaiebiz.merchant.enter.dao.MerchantAccountDao;
import com.topaiebiz.merchant.enter.dao.MerchantQualificationDao;
import com.topaiebiz.merchant.enter.dao.StoreInfoDao;
import com.topaiebiz.merchant.info.dao.MerchantInfoDao;
import com.topaiebiz.merchant.info.entity.MerchantAccountEntity;
import com.topaiebiz.merchant.info.entity.MerchantInfoEntity;
import com.topaiebiz.merchant.info.entity.MerchantQualificationEntity;
import com.topaiebiz.merchant.info.entity.StoreInfoEntity;
import com.topaiebiz.merchant.store.dao.MerchantModifyInfoDao;
import com.topaiebiz.merchant.store.dao.MerchantModifyLogDao;
import com.topaiebiz.merchant.store.dto.MerchantModifyDetailDto;
import com.topaiebiz.merchant.store.dto.MerchantModifyInfosDto;
import com.topaiebiz.merchant.store.dto.MerchantModifyLogDto;
import com.topaiebiz.merchant.store.dto.ModifyMerchantDto;
import com.topaiebiz.merchant.store.entity.MerchantModifyInfoEntity;
import com.topaiebiz.merchant.store.entity.MerchantModifyLogEntity;
import com.topaiebiz.merchant.store.service.MerchantModifyInfoService;
import com.topaiebiz.merchant.util.FiledUpdateUtil;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Aurthor:zhaoxupeng
 * @Description:重新审核
 * @Date 2018/3/30 0030 上午 10:02
 */
@Service
public class MerchantModifyInfoServiceImpl implements MerchantModifyInfoService {

    @Autowired
    private MerchantModifyInfoDao merchantModifyInfoDao;

    @Autowired
    private MerchantInfoDao merchantInfoDao;

    @Autowired
    private BackendCategoryApi backendCategoryApi;

    @Autowired
    private MerchantQualificationDao merchantQualificationDao;

    @Autowired
    private MerchantAccountDao merchantAccountDao;

    @Autowired
    private MerchantModifyLogDao merchantModifyLogDao;

    @Autowired
    private StoreInfoDao storeInfoDao;

    @Override
    public PageInfo<MerchantModifyInfosDto> getMerchantModifyInfoList(PagePO pagePO, MerchantModifyInfosDto merchantModifyInfosDto) {
        Page<MerchantModifyInfosDto> page = PageDataUtil.buildPageParam(pagePO);
        List<MerchantModifyInfosDto> merchantModifyInfoDtoList = merchantModifyInfoDao.selectMerchantModifyInfoList(page, merchantModifyInfosDto);
        page.setRecords(merchantModifyInfoDtoList);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public void saveMerchantModifyInfo(MerchantModifyLogDto merchantModifyLogDto) {
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        MerchantModifyLogEntity connd = new MerchantModifyLogEntity();
        connd.cleanInit();
        connd.setMerchantId(merchantId);
        connd.setAuditResult(MerchantConstants.ModifyStatus.EXAMINE_NOT_THROUGH);
        MerchantModifyLogEntity merchantModifyLogEntitys = merchantModifyLogDao.selectOne(connd);
        if (merchantModifyLogEntitys != null) {
            merchantModifyLogEntitys.setAuditResult(MerchantConstants.ModifyStatus.EXAMINE_WAIT);
            merchantModifyLogDao.updateById(merchantModifyLogEntitys);
            List<MerchantModifyDetailDto> modifyDetailDtoList = merchantModifyLogDto.getModifyDetailDtoList();
            if (CollectionUtils.isNotEmpty(modifyDetailDtoList)) {
                EntityWrapper<MerchantModifyInfoEntity> modifyInfoEntityEntityWrapper = new EntityWrapper<>();
                modifyInfoEntityEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
                //审核id
                modifyInfoEntityEntityWrapper.eq("modifyId", merchantModifyLogEntitys.getId());
                List<MerchantModifyInfoEntity> merchantModifyInfoEntities = merchantModifyInfoDao.selectList(modifyInfoEntityEntityWrapper);
                if (CollectionUtils.isNotEmpty(merchantModifyInfoEntities)) {
                    for (MerchantModifyInfoEntity merchantModifyInfoEntity : merchantModifyInfoEntities) {
                        merchantModifyInfoEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                        merchantModifyInfoDao.updateById(merchantModifyInfoEntity);
                    }
                }
                for (MerchantModifyDetailDto merchantModifyDetailDto : modifyDetailDtoList) {
                    MerchantModifyInfoEntity merchantModifyInfoEntitys = new MerchantModifyInfoEntity();
                    merchantModifyInfoEntitys.setStatus(MerchantConstants.ModifyStatus.EXAMINE_WAIT);
                    //再次审核id
                    merchantModifyInfoEntitys.setModifyId(merchantModifyLogEntitys.getId());
                    merchantModifyInfoEntitys.setFieldName(merchantModifyDetailDto.getFieldName());
                    merchantModifyInfoEntitys.setModifiedValue(merchantModifyDetailDto.getModifiedValue());
                    merchantModifyInfoEntitys.setCreatedTime(new Date());
                    merchantModifyInfoEntitys.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
                    merchantModifyInfoDao.insert(merchantModifyInfoEntitys);
                }
            }
            //再次审核类目 修改类目状态 为待审核
            List<BackendCategoryStatusDTO> backendCategoryStatusdtos = merchantModifyLogDto.getBackendCategoryStatusdtos();
            if (CollectionUtils.isNotEmpty(backendCategoryStatusdtos)) {
                for (BackendCategoryStatusDTO backendCategoryStatusDTO : backendCategoryStatusdtos) {
                    BackendCategoryStatusDTO backendCategoryStatusDTOs = new BackendCategoryStatusDTO();
                    backendCategoryStatusDTOs.setMerchantId(merchantId);
                    backendCategoryStatusDTOs.setStatus(GoodsConstants.BackendMerchantCategoryStatus.TO_AUDIT.getCode());
                    backendCategoryStatusDTOs.setIds(backendCategoryStatusDTO.getIds());
                    backendCategoryApi.modifyBackendMerchanntStatus(backendCategoryStatusDTOs);
                }
            }
            return;
        }
        EntityWrapper<MerchantModifyLogEntity> cond = new EntityWrapper();
        cond.eq("merchantId", merchantId);
        //表示审核通过
        cond.eq("auditResult", 0);
        List<MerchantModifyLogEntity> merchantModifyLogEntities = merchantModifyLogDao.selectList(cond);
        if (CollectionUtils.isNotEmpty(merchantModifyLogEntities)) {
            for (MerchantModifyLogEntity merchantModifyLogEntity : merchantModifyLogEntities) {
                merchantModifyLogEntity.setStatus(1);
                merchantModifyLogDao.updateById(merchantModifyLogEntity);
            }
        }
        //审核添加数据
        MerchantModifyLogEntity merchantModifyLogEntity = new MerchantModifyLogEntity();
        merchantModifyLogEntity.setMerchantId(merchantId);
        merchantModifyLogEntity.setStoreId(SecurityContextUtils.getCurrentUserDto().getStoreId());
        merchantModifyLogEntity.setCreatedTime(new Date());
        //审核结果为待审核
        merchantModifyLogEntity.setAuditResult(MerchantConstants.ModifyStatus.EXAMINE_WAIT);
        merchantModifyLogDao.insert(merchantModifyLogEntity);
        List<MerchantModifyDetailDto> modifyDetailDtoList = merchantModifyLogDto.getModifyDetailDtoList();
        if (CollectionUtils.isNotEmpty(modifyDetailDtoList)) {
            EntityWrapper<MerchantModifyInfoEntity> modifyInfoEntityEntityWrapper = new EntityWrapper<>();
            modifyInfoEntityEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            //审核id
            modifyInfoEntityEntityWrapper.eq("modifyId", merchantModifyLogEntity.getId());
            List<MerchantModifyInfoEntity> merchantModifyInfoEntities = merchantModifyInfoDao.selectList(modifyInfoEntityEntityWrapper);
            if (CollectionUtils.isNotEmpty(merchantModifyInfoEntities)) {
                for (MerchantModifyInfoEntity merchantModifyInfoEntity : merchantModifyInfoEntities) {
                    merchantModifyInfoEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
                    merchantModifyInfoDao.updateById(merchantModifyInfoEntity);
                }
            }
            for (MerchantModifyDetailDto merchantModifyDetailDto : modifyDetailDtoList) {
                MerchantModifyInfoEntity merchantModifyInfoEntitys = new MerchantModifyInfoEntity();
                merchantModifyInfoEntitys.setStatus(MerchantConstants.ModifyStatus.EXAMINE_WAIT);
                //再次审核id
                merchantModifyInfoEntitys.setModifyId(merchantModifyLogEntity.getId());
                merchantModifyInfoEntitys.setFieldName(merchantModifyDetailDto.getFieldName());
                merchantModifyInfoEntitys.setModifiedValue(merchantModifyDetailDto.getModifiedValue());
                merchantModifyInfoEntitys.setCreatedTime(new Date());
                merchantModifyInfoEntitys.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
                merchantModifyInfoDao.insert(merchantModifyInfoEntitys);
            }
        }
        //修改每一级类目的类目的状态 为待审核
        List<BackendCategoryStatusDTO> backendCategoryStatusdtos = merchantModifyLogDto.getBackendCategoryStatusdtos();
        if (CollectionUtils.isNotEmpty(backendCategoryStatusdtos)) {
            for (BackendCategoryStatusDTO backendCategoryStatusDTO : backendCategoryStatusdtos) {
                BackendCategoryStatusDTO backendCategoryStatusDTOs = new BackendCategoryStatusDTO();
                backendCategoryStatusDTOs.setMerchantId(merchantId);
                backendCategoryStatusDTOs.setStatus(GoodsConstants.BackendMerchantCategoryStatus.TO_AUDIT.getCode());
                backendCategoryStatusDTOs.setIds(backendCategoryStatusDTO.getIds());
                backendCategoryApi.modifyBackendMerchanntStatus(backendCategoryStatusDTOs);
            }
        }
    }

    @Override
    public void saveMerchantModifyInfoExmine(MerchantModifyLogDto merchantModifyLogDto) {
        List<MerchantModifyDetailDto> modifyDetailDtoList = merchantModifyLogDto.getModifyDetailDtoList();
        List<Long> idss = new ArrayList<>();
        List<BackendCategoryStatusDTO> backendCategoryStatusdtos = merchantModifyLogDto.getBackendCategoryStatusdtos();
        if (CollectionUtils.isNotEmpty(backendCategoryStatusdtos)) {
            //修改类目状态
            for (BackendCategoryStatusDTO backendCategoryStatusDTO : backendCategoryStatusdtos) {
                BackendCategoryStatusDTO backendCategoryStatusDTOs = new BackendCategoryStatusDTO();
                backendCategoryStatusDTOs.setIds(backendCategoryStatusDTO.getIds());
                backendCategoryStatusDTOs.setMerchantId(backendCategoryStatusDTO.getMerchantId());
                backendCategoryStatusDTOs.setStatus(backendCategoryStatusDTO.getStatus());
                List<Long> arrayList = new ArrayList<Long>(Arrays.asList(backendCategoryStatusDTO.getIds()));
                idss.addAll(arrayList);
                backendCategoryApi.modifyBackendMerchanntStatus(backendCategoryStatusDTOs);
                List<Long> merchantcategoryIds = backendCategoryApi.getMerchantcategoryIds(merchantModifyLogDto.getMerchantId(), GoodsConstants.BackendMerchantCategoryStatus.TO_AUDIT.getCode());
                if (CollectionUtils.isNotEmpty(merchantcategoryIds)) {
                    BackendCategoryStatusDTO backendCategoryStatusDTOss = new BackendCategoryStatusDTO();
                    backendCategoryStatusDTOss.setMerchantId(merchantModifyLogDto.getMerchantId());
                    //类目正常
                    backendCategoryStatusDTOss.setStatus(GoodsConstants.BackendMerchantCategoryStatus.NORMAL.getCode());
                    Long[] ids = (Long[]) merchantcategoryIds.toArray(new Long[merchantcategoryIds.size()]);
                    backendCategoryStatusDTOs.setIds(ids);
                    backendCategoryApi.modifyBackendMerchanntStatus(backendCategoryStatusDTOss);
                }
                //审核结果不通过
                EntityWrapper<MerchantModifyLogEntity> modifyLogWrapper = new EntityWrapper();
                modifyLogWrapper.eq("id", merchantModifyLogDto.getModifyId());
                List<MerchantModifyLogEntity> merchantModifyLogEntities = merchantModifyLogDao.selectList(modifyLogWrapper);
                if (CollectionUtils.isNotEmpty(merchantModifyLogEntities)) {
                    for (MerchantModifyLogEntity merchantModifyLogEntity : merchantModifyLogEntities) {
                        merchantModifyLogEntity.setAuditResult(MerchantConstants.ModifyStatus.EXAMINE_NOT_THROUGH);
                        merchantModifyLogDao.updateById(merchantModifyLogEntity);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(modifyDetailDtoList)) {
            for (MerchantModifyDetailDto merchantModifyDetailDto : modifyDetailDtoList) {
                MerchantModifyInfoEntity cond = new MerchantModifyInfoEntity();
                cond.cleanInit();
                cond.setModifyId(merchantModifyLogDto.getModifyId());
                cond.setFieldName(merchantModifyDetailDto.getFieldName());
                cond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
                MerchantModifyInfoEntity merchantModifyInfoEntity = merchantModifyInfoDao.selectOne(cond);
                if (merchantModifyInfoEntity != null) {
                    merchantModifyInfoEntity.setNoPassReason(merchantModifyDetailDto.getNoPassReason());
                    merchantModifyInfoEntity.setStatus(MerchantConstants.ModifyStatus.EXAMINE_NOT_THROUGH);
                    merchantModifyInfoEntity.setExamineAuditor(SecurityContextUtils.getCurrentUserDto().getUsername());
                    merchantModifyInfoEntity.setExamineTime(new Date());
                    merchantModifyInfoDao.updateById(merchantModifyInfoEntity);
                }
                //审核结果不通过
                EntityWrapper<MerchantModifyLogEntity> modifyLogWrapper = new EntityWrapper();
                modifyLogWrapper.eq("id", merchantModifyLogDto.getModifyId());
                List<MerchantModifyLogEntity> merchantModifyLogEntities = merchantModifyLogDao.selectList(modifyLogWrapper);
                if (CollectionUtils.isNotEmpty(merchantModifyLogEntities)) {
                    for (MerchantModifyLogEntity merchantModifyLogEntity : merchantModifyLogEntities) {
                        merchantModifyLogEntity.setAuditResult(MerchantConstants.ModifyStatus.EXAMINE_NOT_THROUGH);
                        merchantModifyLogEntity.setExamineAuditor(SecurityContextUtils.getCurrentUserDto().getUsername());
                        merchantModifyLogEntity.setExamineTime(new Date());
                        merchantModifyLogDao.updateById(merchantModifyLogEntity);
                    }
                }
            }
            EntityWrapper<MerchantModifyInfoEntity> modifyInfoEntityWrapper = new EntityWrapper();
            modifyInfoEntityWrapper.eq("modifyId", merchantModifyLogDto.getModifyId());
            modifyInfoEntityWrapper.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            modifyInfoEntityWrapper.isNull("noPassReason");
            List<MerchantModifyInfoEntity> merchantModifyInfoEntities = merchantModifyInfoDao.selectList(modifyInfoEntityWrapper);
            if (CollectionUtils.isEmpty(merchantModifyInfoEntities)) {
                return;
            }
            for (MerchantModifyInfoEntity merchantModifyInfoEntity : merchantModifyInfoEntities) {
                merchantModifyInfoEntity.setStatus(MerchantConstants.ModifyStatus.EXAMINE_WAIT);
                merchantModifyInfoEntity.setExamineAuditor(SecurityContextUtils.getCurrentUserDto().getUsername());
                merchantModifyInfoEntity.setExamineTime(new Date());
                merchantModifyInfoDao.updateById(merchantModifyInfoEntity);
            }
        }
    }

    @Override
    @Transactional
    public void saveExamineAdoptInfo(MerchantModifyLogDto merchantModifyLogDto) {
        List<MerchantModifyDetailDto> modifyDetailDtoList = merchantModifyLogDto.getModifyDetailDtoList();
        if (CollectionUtils.isNotEmpty(modifyDetailDtoList)) {
            ModifyMerchantDto updateInfos = FiledUpdateUtil.getUpdateInfos(modifyDetailDtoList);
            if (updateInfos != null) {
                MerchantAccountEntity merchantAccountEntity = updateInfos.getMerchantAccountEntity();
                merchantAccountEntity.setCreatedTime(null);
                EntityWrapper<MerchantAccountEntity> entityEntityWrapper = new EntityWrapper<>();
                entityEntityWrapper.eq("merchantId", merchantModifyLogDto.getMerchantId());
                merchantAccountDao.update(merchantAccountEntity, entityEntityWrapper);
                MerchantInfoEntity merchantInfoEntity = updateInfos.getMerchantInfoEntity();
                merchantInfoEntity.setCreatedTime(null);
                EntityWrapper<MerchantInfoEntity> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("id", merchantModifyLogDto.getMerchantId());
                merchantInfoEntity.setId(merchantModifyLogDto.getMerchantId());
                merchantInfoDao.update(merchantInfoEntity, entityWrapper);
                if (merchantInfoEntity != null) {
                    //同步店铺信息
                    StoreInfoEntity conn = new StoreInfoEntity();
                    conn.cleanInit();
                    conn.setName(merchantInfoEntity.getStoreName());
                    conn.setImages(merchantInfoEntity.getImgages());
                    EntityWrapper<StoreInfoEntity> e = new EntityWrapper<>();
                    e.eq("merchantId",merchantInfoEntity.getId());
                    conn.setMerchantId(merchantInfoEntity.getId());
                    storeInfoDao.update(conn,e);
                }
                MerchantQualificationEntity merchantQualificationEntity = updateInfos.getMerchantQualificationEntity();
                merchantQualificationEntity.setCreatedTime(null);
                EntityWrapper<MerchantQualificationEntity> entityEntityWrappes = new EntityWrapper<>();
                entityEntityWrappes.eq("merchantId", merchantModifyLogDto.getMerchantId());
                merchantQualificationEntity.setMerchantId(merchantModifyLogDto.getMerchantId());
                merchantQualificationDao.update(merchantQualificationEntity, entityEntityWrappes);
            }
        }
        List<Integer> list = new ArrayList<>();
        list.add(GoodsConstants.BackendMerchantCategoryStatus.NORMAL.getCode());
        list.add(GoodsConstants.BackendMerchantCategoryStatus.TO_AUDIT.getCode());
        List<Long> merchantcategoryIds = backendCategoryApi.getMerchantcategoryListIds(merchantModifyLogDto.getMerchantId(), list);
        if (CollectionUtils.isNotEmpty(merchantcategoryIds)) {
            BackendCategoryStatusDTO backendCategoryStatusDTOss = new BackendCategoryStatusDTO();
            backendCategoryStatusDTOss.setStatus(GoodsConstants.BackendMerchantCategoryStatus.AUDOT_APPROVAL.getCode());
            backendCategoryStatusDTOss.setMerchantId(merchantModifyLogDto.getMerchantId());
            Long[] ids = (Long[]) merchantcategoryIds.toArray(new Long[merchantcategoryIds.size()]);
            backendCategoryStatusDTOss.setIds(ids);
            backendCategoryApi.modifyBackendMerchanntStatus(backendCategoryStatusDTOss);
        }
        EntityWrapper<MerchantModifyInfoEntity> conn = new EntityWrapper();
        conn.eq("modifyId",merchantModifyLogDto.getModifyId());
        conn.eq("status",MerchantConstants.ModifyStatus.EXAMINE_WAIT);
        conn.eq("deletedFlag",Constants.DeletedFlag.DELETED_NO);
        List<MerchantModifyInfoEntity> merchantModifyInfoEntities = merchantModifyInfoDao.selectList(conn);
        if (CollectionUtils.isNotEmpty(merchantModifyInfoEntities)){
            for (MerchantModifyInfoEntity merchantModifyInfoEntity :merchantModifyInfoEntities){
                merchantModifyInfoEntity.setStatus(MerchantConstants.ModifyStatus.EXAMINE_ADOPT);
                merchantModifyInfoEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
                merchantModifyInfoEntity.setLastModifiedTime(new Date());
                merchantModifyInfoDao.updateById(merchantModifyInfoEntity);
            }
        }
        //审核结果通过
        EntityWrapper<MerchantModifyLogEntity> modifyLogWrapper = new EntityWrapper();
        modifyLogWrapper.eq("id", merchantModifyLogDto.getModifyId());
        List<MerchantModifyLogEntity> merchantModifyLogEntities = merchantModifyLogDao.selectList(modifyLogWrapper);
        if (CollectionUtils.isNotEmpty(merchantModifyLogEntities)) {
            for (MerchantModifyLogEntity merchantModifyLogEntity : merchantModifyLogEntities) {
                merchantModifyLogEntity.setAuditResult(MerchantConstants.ModifyStatus.EXAMINE_ADOPT);
                merchantModifyLogEntity.setExamineAuditor(SecurityContextUtils.getCurrentUserDto().getUsername());
                merchantModifyLogEntity.setExamineTime(new Date());
                merchantModifyLogDao.updateById(merchantModifyLogEntity);
            }
        }
    }

    @Override
    public Boolean judgeMerchantModifyStatus(MerchantModifyLogDto merchantModifyLogDto) {
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        EntityWrapper<MerchantModifyLogEntity> coon = new EntityWrapper<>();
        coon.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        coon.eq("merchantId", merchantId);
        List<MerchantModifyLogEntity> merchantModifyLogEntities = merchantModifyLogDao.selectList(coon);
        if (CollectionUtils.isNotEmpty(merchantModifyLogEntities)) {
            for (MerchantModifyLogEntity merchantModifyLogEntity : merchantModifyLogEntities) {
                if (merchantModifyLogEntity.getAuditResult().equals(MerchantConstants.ModifyStatus.EXAMINE_WAIT)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<MerchantModifyLogDto> getMerchantModifysInfoList(MerchantModifyLogDto merchantModifyLogDto) {
        EntityWrapper<MerchantModifyInfoEntity> conn = new EntityWrapper<>();
        conn.eq("modifyId", merchantModifyLogDto.getModifyId());
        conn.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<MerchantModifyInfoEntity> merchantModifyInfoEntities = merchantModifyInfoDao.selectList(conn);
        List<MerchantModifyLogDto> merchantModifyLogDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(merchantModifyInfoEntities)) {
            for (MerchantModifyInfoEntity merchantModifyInfoEntity : merchantModifyInfoEntities) {
                MerchantModifyLogDto merchantModifyLogDtos = new MerchantModifyLogDto();
                BeanCopyUtil.copy(merchantModifyInfoEntity, merchantModifyLogDtos);
                merchantModifyLogDtoList.add(merchantModifyLogDtos);
            }
            return merchantModifyLogDtoList;
        }
        return null;
    }

    @Override
    public List<MerchantModifyLogDto> getMerchantModifysInfoDeail(MerchantModifyLogDto merchantModifyLogDto) {
        List<MerchantModifyLogDto> merchantModifyLogDtoList = merchantModifyInfoDao.selectMerchantModifysInfoDeail(merchantModifyLogDto);
        return merchantModifyLogDtoList;
    }

}
