package com.topaiebiz.merchant.enter.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.topaiebiz.basic.api.DistrictApi;
import com.topaiebiz.basic.dto.DistrictDto;
import com.topaiebiz.goods.api.BackendCategoryApi;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.constants.GoodsConstants;
import com.topaiebiz.goods.dto.category.backend.BackendCategoryDTO;
import com.topaiebiz.goods.dto.category.backend.BackendCategoryStatusDTO;
import com.topaiebiz.goods.dto.category.backend.BackendCategorysDTO;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.merchant.enter.dto.*;
import com.topaiebiz.merchant.freight.dto.AddFreightTempleteDto;
import com.topaiebiz.merchant.freight.dto.FreightTempleteDetailDto;
import com.topaiebiz.merchant.freight.dto.FreightTempleteDto;
import com.topaiebiz.merchant.freight.dto.MerFreightTempleteDto;
import com.topaiebiz.merchant.grade.dao.MerchantGradeDao;
import com.topaiebiz.merchant.grade.entity.MerchantGradeEntity;
import com.topaiebiz.merchant.store.exception.StoreInfoException;
import com.topaiebiz.message.api.TemplateApi;
import com.topaiebiz.system.security.api.SystemUserApi;
import com.topaiebiz.system.util.SecurityContextUtils;
import com.topaiebiz.system.util.SystemUserType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.freight.dao.FreightTempleteDao;
import com.topaiebiz.merchant.freight.dao.FreightTempleteDetailDao;
import com.topaiebiz.merchant.enter.dao.MerchantAccountDao;
import com.topaiebiz.merchant.enter.dao.MerchantAuditDetailDao;
import com.topaiebiz.merchant.enter.dao.MerchantInfoDaos;
import com.topaiebiz.merchant.enter.dao.MerchantQualificationDao;
import com.topaiebiz.merchant.enter.dao.MerchantauditLogDao;
import com.topaiebiz.merchant.enter.dao.StoreInfoDao;
import com.topaiebiz.merchant.freight.entity.FreightTempleteDetailEntity;
import com.topaiebiz.merchant.freight.entity.FreightTempleteEntity;
import com.topaiebiz.merchant.enter.entity.MerchantAuditDetailEntity;
import com.topaiebiz.merchant.enter.entity.MerchantauditLogEntity;
import com.topaiebiz.merchant.enter.exception.MerchantEnterException;
import com.topaiebiz.merchant.enter.service.MerchantEnterService;
import com.topaiebiz.merchant.info.entity.MerchantAccountEntity;
import com.topaiebiz.merchant.info.entity.MerchantInfoEntity;
import com.topaiebiz.merchant.info.entity.MerchantQualificationEntity;
import com.topaiebiz.merchant.info.entity.StoreInfoEntity;

/**
 * Description: 商家入驻流程业务层实现类
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年10月9日 上午11:05:48
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Transactional
@Service
public class MerchantEnterServiceImpl implements MerchantEnterService {

    @Autowired
    private MerchantQualificationDao merchantQualificationDao;

    @Autowired
    private MerchantAccountDao merchantAccountDao;

    @Autowired
    private StoreInfoDao storeInfoDao;

    @Autowired
    private MerchantInfoDaos merchantInfoDaos;

    @Autowired
    private MerchantAuditDetailDao merchantAuditDetailDao;

    @Autowired
    private MerchantauditLogDao merchantauditLogDao;

    @Autowired
    private FreightTempleteDao freightTempleteDao;

    @Autowired
    private FreightTempleteDetailDao freightTempleteDetailDao;


    @Autowired
    private BackendCategoryApi backendCategoryApi;

    @Autowired
    private DistrictApi districtApi;

    @Autowired
    private SystemUserApi systemUserApi;

    @Autowired
    private GoodsApi goodsApi;

    @Autowired
    private MerchantGradeDao merchantGradeDao;

    @Autowired
    private TemplateApi templateApi;

    @Override
    public void saveMerchantQualification(MerchantBasicInfoDto merchantBasicInfoDto, HttpSession session, String userLoginId) throws GlobalException {
        // 商家信息表中插入公司名称使商家信息表正成主键id
        MerchantInfoEntity merchantInfoEntity = new MerchantInfoEntity();
        merchantInfoEntity.setName(merchantBasicInfoDto.getName());
        merchantInfoEntity.setContactTele(merchantBasicInfoDto.getContactTele());
        merchantInfoEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantInfoEntity.setCreatedTime(new Date());
        merchantInfoEntity.setState(0);
        merchantInfoEntity.setChangeState(0);
        merchantInfoDaos.insert(merchantInfoEntity);
        // 添加商家公司及联系人信息
        MerchantQualificationEntity merchantQualificationEntity = new MerchantQualificationEntity();
        BeanUtils.copyProperties(merchantBasicInfoDto, merchantQualificationEntity);
        merchantQualificationEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantQualificationEntity.setCreatedTime(new Date());
        merchantQualificationEntity.setMerchantId(merchantInfoEntity.getId());
        merchantQualificationDao.insert(merchantQualificationEntity);
        //获取商家id
        MerchantInfoEntity merchantInfoId = merchantInfoDaos.selectById(merchantQualificationEntity.getMerchantId());
        //同步更新系统用户表中商家id
        systemUserApi.editUserMerchantId(merchantInfoId.getId(), userLoginId);
        SecurityContextUtils.getCurrentUserDto().setMerchantId(merchantInfoEntity.getId());
    }

    @Override
    public void saveMerchantAccount(MercahntManageInfoDto mercahntManageInfoDto) throws ParseException {
        // 时间格式转换格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // 获取的起始时间字符串转换成Date类型
        Date licenseBeginDate = format.parse(mercahntManageInfoDto.getLicenseBegin());
        // 获取的结束时间字符串转换成Date类型
        Date licenseEndDate = format.parse(mercahntManageInfoDto.getLicenseEnd());
        Date EstablishTime = format.parse(mercahntManageInfoDto.getEstablishTime());

        //todo:根据当前用户查出商家id
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        // 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象，添加营业执照信息
        //通过商家id查询商家入驻资质信息获取当前的资质信息
        MerchantQualificationEntity merchantQualificationEntity = merchantQualificationDao.selectMerchantInfoByMerchantId(merchantId);
        BeanCopyUtil.copy(mercahntManageInfoDto, merchantQualificationEntity);
        merchantQualificationEntity.setLicenseImage(mercahntManageInfoDto.getLicenseImage());
        merchantQualificationEntity.setEstablishTime(EstablishTime);
        merchantQualificationEntity.setId(merchantQualificationEntity.getId());
        merchantQualificationEntity.setLicenseBegin(licenseBeginDate);
        merchantQualificationEntity.setLicenseEnd(licenseEndDate);
        merchantQualificationDao.updateById(merchantQualificationEntity);
        // 添加银行账户信息
        MerchantAccountEntity merchantAccountEntity = new MerchantAccountEntity();
        BeanUtils.copyProperties(mercahntManageInfoDto, merchantAccountEntity);
        merchantAccountEntity.setMerchantId(merchantId);
        merchantAccountEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantAccountEntity.setCreatedTime(new Date());
        merchantAccountDao.insert(merchantAccountEntity);

    }

   /* @Override
    public Integer saveStoreInfo(StoreInfoDto storeInfoDto) {
        StoreInfoEntity storeInfoEntity = new StoreInfoEntity();
        BeanUtils.copyProperties(storeInfoDto, storeInfoEntity);
        // 根据电话查到会员，获取所属店铺
        // String telephone = SecurityContextUtils.getCurrentSystemUser().getMobilePhone();
        String mobilePhone = SecurityContextUtils.getCurrentUserDto().getMobilePhone();
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        //	MemberEntity member = memberMgmtDao.getMemberByuserTelephone(telephone);
        //	Long belongId = member.getMerchantId();
        MemberDto memberInfo = memberApi.getMemberInfoByPhone(mobilePhone);
        //Long merchantId = memberInfo.getMerchantId();
        //店铺表里新增
        storeInfoEntity.setMerchantId(merchantId);
        storeInfoEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        storeInfoEntity.setCreatedTime(new Date());
        storeInfoDao.insert(storeInfoEntity);
        //所属店铺换一下
        //member = memberMgmtDao.selectById(member.getId());
        MemberDto memberByMember = memberApi.getMemberByMemberId(memberInfo.getId());
        memberByMember.setStoreId(storeInfoEntity.getId());
        return memberApi.updateMemberMerchantId(memberByMember.getMerchantId(), memberByMember.getId());
        //return memberMgmtDao.updateById(member);
    }*/

    @Override
    public PageInfo<MerchantQualificationDto> getMerchantQualificationList(PagePO pagePO, MerchantQualificationDto merchantQualificationDto) {
        Page<MerchantQualificationDto> page = PageDataUtil.buildPageParam(pagePO);
        page.setRecords(merchantQualificationDao.selectMerchantQualificationList(page, merchantQualificationDto));
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    public MerchantQualificationDto getMerchantQualificationListById(Long id) {
        return merchantQualificationDao.selectMerchantQualificationListById(id);
    }

    @Override
    public Integer saveMerchantAuditDetail(MerchantAuditDetailDto merchantAuditDetailDto) {
        MerchantAuditDetailEntity merchantAuditDetailEntity = new MerchantAuditDetailEntity();
        BeanUtils.copyProperties(merchantAuditDetailDto, merchantAuditDetailEntity);
        merchantAuditDetailEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantAuditDetailEntity.setCreatedTime(new Date());
        return merchantAuditDetailDao.insert(merchantAuditDetailEntity);
    }

    @Override
    public Integer saveMerchantauditLog(MerchantauditLogDto merchantauditLogDto) {
        MerchantauditLogEntity merchantauditLogEntity = new MerchantauditLogEntity();
        BeanUtils.copyProperties(merchantauditLogDto, merchantauditLogEntity);
        merchantauditLogEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantauditLogEntity.setCreatedTime(new Date());
        return merchantauditLogDao.insert(merchantauditLogEntity);
    }

    @Override
    public StoreInfoDto getStoreInfoById(Long id) {
        return storeInfoDao.selectStoreInfoById(id);
    }

    @Override
    public Double getGoodsNumberPrice(Long storeId, Long goodsId, Long districtId, Integer goodsNumber) {
        return 0.0;

    }

    @Override
    public void insertMerchantInfo(MerchantInfoDto merchantInfoDto) {
        MerchantInfoEntity merchantInfoEntity = new MerchantInfoEntity();
        BeanUtils.copyProperties(merchantInfoDto, merchantInfoEntity);
        merchantInfoDaos.insert(merchantInfoEntity);

    }

    @Override
    public void updateManageInfoByQualificationId(MerchantQualificationDto merchantQualificationDto) {
        MerchantQualificationEntity merchantQualificationEntity = new MerchantQualificationEntity();
        BeanUtils.copyProperties(merchantQualificationDto, merchantQualificationEntity);
        merchantQualificationDao.updateById(merchantQualificationEntity);

    }

    @Override
    public Long getQualificationIdByMerchantId(Long qualificationId) {
        return merchantQualificationDao.selectQualificationIdByMerchantId(qualificationId);
    }

    @Override
    public void updateMerchantInfoByMerchantType(StoreInfoDtos storeInfoDto) {
        // 根据当前用户查出商家id
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        StoreInfoEntity StoreInfoEntity = new StoreInfoEntity();
        BeanUtils.copyProperties(storeInfoDto, StoreInfoEntity);
        StoreInfoEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        StoreInfoEntity.setCreatedTime(new Date());
        StoreInfoEntity.setMerchantId(merchantId);
        StoreInfoEntity.setName(storeInfoDto.getName());
        storeInfoDao.insert(StoreInfoEntity);
        // 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象，添加店铺经营信息
        MerchantInfoEntity merchantInfoEntity = new MerchantInfoEntity();
        BeanUtils.copyProperties(storeInfoDto, merchantInfoEntity);
        merchantInfoEntity.setId(merchantId);
        merchantInfoEntity.setMerchantType(storeInfoDto.getMerchantType());
        merchantInfoEntity.setState(1);
        merchantInfoDaos.updateById(merchantInfoEntity);
    }

    @Override
    public StateDto getMerchantInfoStateByLoginName(String username) throws GlobalException {
        // 根据当前登录的用户名查出商家id
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        if (merchantId == null) {
            throw new GlobalException(MerchantEnterException.MERCHANTENTER_NOT_ENTER);
        }
        MerchantInfoEntity merchantInfoEntity = merchantInfoDaos.selectById(merchantId);
        if (null == merchantInfoEntity) {
            throw new GlobalException(MerchantEnterException.MERCHANTENTER_NOT_ENTER);
        }
        StateDto stateDto = new StateDto();
        //  1.申请中审核   2.通过  3不通过   4.待缴费  5.缴费待审核（付款已提交）6.缴费未通过 7(9).缴费通过（完成）
        if (merchantInfoEntity.getState() == 0) {
            stateDto.setState(merchantInfoEntity.getState());
            return stateDto;
        }
        if (merchantInfoEntity.getState() == 1) {
            // 1申请
            stateDto.setState(merchantInfoEntity.getState());
            return stateDto;
        }
        if (merchantInfoEntity.getState() == 2) {
            // 2审核通过,进入缴纳费用阶段
            stateDto.setState(merchantInfoEntity.getState());
            return stateDto;
        }
        if (merchantInfoEntity.getState() == 3) {
            // 3 审核不通过
            // 商家信息关联商家信息表的ID
            List<MerchantauditLogDto> merchantauditLogDto = merchantauditLogDao.getMerchantauditLog(merchantId);
            if (CollectionUtils.isNotEmpty(merchantauditLogDto)) {
                // 审核不通过，查看不通过原因以及不通过字段（回显）
                List<MerchantAuditDetailEntity> merchantAuditDetailEntity = merchantAuditDetailDao.getMerchantAuditDetailByMerchantId(merchantauditLogDto.get(0).getId());
                merchantauditLogDto.get(0).setDetailList(merchantAuditDetailEntity);
                stateDto.setLogList(merchantauditLogDto);
            }
            List<Integer> statuses = null;
            List<BackendCategorysDTO> merchantCategory = backendCategoryApi.getMerchantCategory(merchantId, statuses);
            if (CollectionUtils.isNotEmpty(merchantCategory)) {
                stateDto.setBackendCategorysDTOList(merchantCategory);
            }
        }
        if (merchantInfoEntity.getState() == 4) {
            // 4待付款
            stateDto.setState(merchantInfoEntity.getState());
            return stateDto;
        }
        if (merchantInfoEntity.getState() == 5) {
            // 5 付款已提交
            stateDto.setState(merchantInfoEntity.getState());
            return stateDto;
        }
        if (merchantInfoEntity.getState() == 6) {
            // 6 已完成
            stateDto.setState(merchantInfoEntity.getState());
            return stateDto;
        }
        stateDto.setState(merchantInfoEntity.getState());
        return stateDto;
    }

    @Override
    public StateDto getMerchantAuditLogByMerchantId(Long id) {
        StateDto stateDto = new StateDto();
        // 商家信息关联商家信息表的ID
        List<MerchantauditLogDto> merchantauditLogDto = merchantauditLogDao.getMerchantauditLogs(id);
        // 审核不通过，查看不通过原因以及不通过字段（回显）
        List<MerchantAuditDetailEntity> merchantAuditDetailEntity = merchantAuditDetailDao
                .getMerchantAuditDetailByMerchantId(merchantauditLogDto.get(0).getId());
        merchantauditLogDto.get(0).setDetailList(merchantAuditDetailEntity);
        stateDto.setLogList(merchantauditLogDto);
        return stateDto;
    }

    @Override
    public MerchantBasicInfoDto getMerchantInfoByLoginName() {
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        return merchantQualificationDao.selectMerchantInfoByLoginName(merchantId);
    }

    @Override
    public MercahntManageInfoDto getMercahntManageInfoByLoginName(Long merchantId) {
        // 根据当前用户查出商家id
        //  Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        return merchantQualificationDao.selectMercahntManageInfoByLoginName(merchantId);
    }

    @Override
    public StoreInfoDto getMerchantTypeByLoginName(Long merchantId) {
        MerchantAccountEntity conn = new MerchantAccountEntity();
        conn.cleanInit();
        conn.setMerchantId(merchantId);
        conn.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        MerchantAccountEntity merchantAccountEntity = merchantAccountDao.selectOne(conn);
        if (merchantAccountEntity == null) {
            return null;
        }
        StoreInfoDto storeInfoDto = storeInfoDao.selectMerchantTypeByLoginName(merchantId);
        storeInfoDto.setSettleAccount(merchantAccountEntity.getSettleAccount());
        storeInfoDto.setSettleBankName(merchantAccountEntity.getSettleBankName());
        storeInfoDto.setSettleAccountName(merchantAccountEntity.getSettleAccountName());
        storeInfoDto.setSettleBankDistrictId(merchantAccountEntity.getSettleBankDistrictId());
        storeInfoDto.setSettleBankNum(merchantAccountEntity.getSettleBankNum());
        return storeInfoDto;
    }

    @Override
    public void savePayImage(CapitalDto capitalDto) {
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        // 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象，上传缴费凭证
        MerchantQualificationEntity merchantQualificationEntity = merchantQualificationDao.selectMerchantInfoByMerchantId(merchantId);
        if (null != merchantQualificationEntity) {
            MerchantQualificationEntity selectById = merchantQualificationDao.selectById(merchantQualificationEntity.getId());
            selectById.setPayImage(capitalDto.getPayImage());
            selectById.setPayTime(new Date());
            selectById.setLastModifiedTime(new Date());
            selectById.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            merchantQualificationDao.updateById(selectById);
        }
        MerchantInfoEntity merchantInfo = merchantInfoDaos.selectById(merchantId);
        // 5.缴费待审核（付款已提交）
        merchantInfo.setState(5);
        merchantInfo.setLastModifiedTime(new Date());
        merchantInfo.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantInfoDaos.updateById(merchantInfo);
    }

    @Override
    public void saveMerchantAuditLogAndDetail(MerchantauditLogDto merchantauditLogDto) {
        // 添加审核记录
        MerchantauditLogEntity merchantauditLogEntity = new MerchantauditLogEntity();
        merchantauditLogEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantauditLogEntity.setCreatedTime(new Date());
        BeanUtils.copyProperties(merchantauditLogDto, merchantauditLogEntity);
        //不通过
        merchantauditLogEntity.setAuditResult(3);
        merchantauditLogDao.insert(merchantauditLogEntity);
        // 添加审核记录详情
        List<MerchantAuditDetailEntity> detailList = merchantauditLogDto.getDetailList();
        for (MerchantAuditDetailEntity merchantAuditDetailEntity : detailList) {
            merchantAuditDetailEntity.setAuditLogId(merchantauditLogEntity.getId());
            merchantAuditDetailEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
            merchantAuditDetailEntity.setMerchantId(merchantauditLogEntity.getMerchantId());
            merchantAuditDetailDao.insert(merchantAuditDetailEntity);
        }
        // 添加审核记录的同时改变商家入驻的状态
        MerchantInfoEntity entity = merchantInfoDaos.selectById(merchantauditLogEntity.getMerchantId());
        entity.setState(3);
        //基本信息不通过为3
        entity.setLastModifiedTime(new Date());
        entity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantInfoDaos.updateById(entity);
    }

    @Override
    public MerchantbasicInfo selectMerchantBasicInfOById(Long id) {
        MerchantbasicInfo merchantbasicInfo = merchantQualificationDao.selectMerchantBasicInfOById(id);
        return merchantbasicInfo;
    }

    @Override
    public MerchantManageDto getMerchantManageInfoById(Long id) {
        MerchantManageDto merchantManageDto = merchantQualificationDao.selectMerchantManageInfoById(id);
        MerchantAccountEntity conn = new MerchantAccountEntity();
        conn.cleanInit();
        conn.setMerchantId(id);
        conn.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        MerchantAccountEntity merchantAccountEntity = merchantAccountDao.selectOne(conn);
        merchantManageDto.setSettleAccount(merchantAccountEntity.getSettleAccount());
        merchantManageDto.setSettleAccountName(merchantAccountEntity.getSettleAccountName());
        merchantManageDto.setSettleBankName(merchantAccountEntity.getSettleBankName());
        merchantManageDto.setSettleBankDistrictId(merchantAccountEntity.getSettleBankDistrictId());
        merchantManageDto.setSettleBankNum(merchantAccountEntity.getSettleBankNum());
        return merchantManageDto;
    }

    @Override
    public CostInfoDto getCostInfoById(Long id) {
        return merchantQualificationDao.selectCostInfoById(id);
    }

    @Override
    public MerchantAuditDto selectMerchantAuditByMerchantId(Long merchantId) {
        return merchantauditLogDao.selectMerchantAuditByMerchantId(merchantId);
    }

    @Override
    public void updateMerchantInfoStateById(String userLoginId, ExamineStateDto examineStateDto) {
        MerchantInfoEntity merchantInfoEntity = merchantInfoDaos.selectById(examineStateDto.getMerchantId());
        merchantInfoEntity.setState(examineStateDto.getState());
        merchantInfoEntity.setLastModifiedTime(new Date());
        merchantInfoEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        //更新修改后的状态
        merchantInfoDaos.updateById(merchantInfoEntity);
        //修改审核信息
        MerchantQualificationEntity merchantQualificationEntity = new MerchantQualificationEntity();
        merchantQualificationEntity.cleanInit();
        merchantQualificationEntity.setMerchantId(examineStateDto.getMerchantId());
        merchantQualificationEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        MerchantQualificationEntity merchantQualificationEntity1 = merchantQualificationDao.selectOne(merchantQualificationEntity);
        if (merchantQualificationEntity1 != null) {
            merchantQualificationEntity1.setExamineTime(new Date());
            merchantQualificationEntity1.setExamineAuditor(SecurityContextUtils.getCurrentUserDto().getUsername());
            merchantQualificationDao.updateById(merchantQualificationEntity1);
        }
        //修改不通过类目状态的状态
        List<Long> ids = new ArrayList<>();
        List<BackendCategoryStatusDTO> backendCategoryStatusDTOS = examineStateDto.getBackendCategoryStatusDTOS();
        if (CollectionUtils.isNotEmpty(backendCategoryStatusDTOS)) {
            for (BackendCategoryStatusDTO backendCategoryStatusDTO : backendCategoryStatusDTOS) {
                BackendCategoryStatusDTO backendCategoryStatusDTOs = new BackendCategoryStatusDTO();
                backendCategoryStatusDTOs.setMerchantId(examineStateDto.getMerchantId());
                backendCategoryStatusDTOs.setStatus(GoodsConstants.BackendMerchantCategoryStatus.AUDIT_FAILURE.getCode());
                backendCategoryStatusDTOs.setIds(backendCategoryStatusDTO.getIds());
                List<Long> arrayList = new ArrayList<Long>(Arrays.asList(backendCategoryStatusDTO.getIds()));
                ids.addAll(arrayList);
                backendCategoryApi.modifyBackendMerchanntStatus(backendCategoryStatusDTOs);
            }
        }
        //查询该商家所有待审核的类目
        List<Long> merchantcategoryIds = backendCategoryApi.getMerchantcategoryIds(examineStateDto.getMerchantId(), GoodsConstants.BackendMerchantCategoryStatus.TO_AUDIT.getCode());
        if (CollectionUtils.isNotEmpty(merchantcategoryIds)) {
            BackendCategoryStatusDTO backendCategoryStatusDTOs = new BackendCategoryStatusDTO();
            backendCategoryStatusDTOs.setMerchantId(examineStateDto.getMerchantId());
            backendCategoryStatusDTOs.setStatus(GoodsConstants.BackendMerchantCategoryStatus.AUDOT_APPROVAL.getCode());
            merchantcategoryIds.removeAll(ids);
            Long[] id = (Long[]) merchantcategoryIds.toArray(new Long[merchantcategoryIds.size()]);
            backendCategoryStatusDTOs.setIds(id);
            backendCategoryApi.modifyBackendMerchanntStatus(backendCategoryStatusDTOs);
        }
        //审核记录插入一条数据
        MerchantauditLogEntity merchantauditLogEntity = new MerchantauditLogEntity();
        merchantauditLogEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantauditLogEntity.setCreatedTime(new Date());
        merchantauditLogEntity.setAuditResult(examineStateDto.getState());
        merchantauditLogEntity.setMerchantId(examineStateDto.getMerchantId());
        merchantauditLogDao.insert(merchantauditLogEntity);
        List<MerchantAuditDetailEntity> detailList = examineStateDto.getDetailList();
        if (CollectionUtils.isNotEmpty(detailList)) {
            for (MerchantAuditDetailEntity merchantAuditDetailEntity : detailList) {
                merchantAuditDetailEntity.setAuditLogId(merchantauditLogEntity.getId());
                merchantAuditDetailEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
                merchantAuditDetailEntity.setMerchantId(merchantauditLogEntity.getMerchantId());
                merchantAuditDetailDao.insert(merchantAuditDetailEntity);
            }
        }
        //入驻成功之后创建店铺
        if (examineStateDto.getState() == 9) {
            List<StoreInfoEntity> storeInfoEntities = storeInfoDao.selectByMerchantId(merchantInfoEntity.getId());
            if (CollectionUtils.isNotEmpty(storeInfoEntities)) {
                throw new GlobalException(StoreInfoException.STOREINFO_HAS_ALREADY_EXISTED);
            } else {
                StoreInfoEntity storeInfoEntity = new StoreInfoEntity();
                //店铺信息与商家信息保持一致
                Long merchantIds = merchantInfoEntity.getId();
                MerchantBasicInfoDto merchantBasicInfoDto = merchantQualificationDao.selectMerchantInfoByMerchantIds(merchantIds);
                MerchantInfoEntity merchantInfoEntitys = merchantInfoDaos.selectById(merchantIds);
                //查询最新的商家等级
                List<MerchantGradeEntity> merchantGradeEntities = merchantGradeDao.selectGradeByCreateTime();
                MerchantGradeEntity merchantGradeEntity = merchantGradeEntities.get(1);
                if (merchantBasicInfoDto != null) {
                    storeInfoEntity.setMerchantId(merchantIds);
                    storeInfoEntity.setName(merchantInfoEntitys.getStoreName());
                    storeInfoEntity.setDistrictId(merchantBasicInfoDto.getDistrictId());
                    storeInfoEntity.setStoreAddress(merchantBasicInfoDto.getAddress());
                    storeInfoEntity.setPosition(merchantBasicInfoDto.getDistrictId());
                    storeInfoEntity.setContactName(merchantBasicInfoDto.getContactName());
                    storeInfoEntity.setContactTele(merchantBasicInfoDto.getContactTele());
                    storeInfoEntity.setStoreTele(merchantBasicInfoDto.getTelephone());
                    storeInfoEntity.setDescription(merchantBasicInfoDto.getMemo());
                    //取商家表中的门店Log
                    storeInfoEntity.setImages(merchantInfoEntitys.getImgages());
                    storeInfoEntity.setPosition(merchantBasicInfoDto.getDistrictId());
                    //初始店铺的等级为商家等级最新的一个（默认为）
                    storeInfoEntity.setMerchantGradeId(merchantGradeEntity.getId());
                    //海淘标识，1为是，0为否。
                    storeInfoEntity.setHaitao(0);
                    //店铺状态
                    storeInfoEntity.setChangeState(0);
                    storeInfoEntity.setCreatedTime(new Date());
                    storeInfoEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
                    //积分支付比例(新店铺默认100)
                    storeInfoEntity.setPtRate(BigDecimal.valueOf(100));
                    //店铺结算周期
                    storeInfoEntity.setSettleCycle(merchantBasicInfoDto.getSettleCycle());
                    storeInfoDao.insert(storeInfoEntity);
                    // 入驻成功修改角色
                    systemUserApi.editUserType(merchantInfoEntity.getId(), storeInfoEntity.getId());
                    //
                    templateApi.addMerchantTemplate(storeInfoEntity.getId(), storeInfoEntity.getName());
                }
            }
        }
    }

    @Override
    public void updateMerchantInfoStateByMerchantId(Long id) {
        MerchantInfoEntity selectById = merchantInfoDaos.selectById(id);
        selectById.setState(3);
        selectById.setLastModifiedTime(new Date());
        selectById.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantInfoDaos.updateById(selectById);
        //审核记录插入一条数据
        MerchantauditLogEntity merchantauditLogEntity = new MerchantauditLogEntity();
        merchantauditLogEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantauditLogEntity.setCreatedTime(new Date());
        merchantauditLogEntity.setAuditResult(3);
        merchantauditLogEntity.setMerchantId(id);
        merchantauditLogDao.insert(merchantauditLogEntity);
    }

    @Override
    public void updateStateByMerchantId(Long id) {
        MerchantInfoEntity selectById = merchantInfoDaos.selectById(id);
        selectById.setState(4);
        merchantInfoDaos.updateById(selectById);
    }

    @Override
    public void modifyMerchantQualificationById(MerchantBasicInfoDto merchantBasicInfoDto) {
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        MerchantQualificationEntity conn = new MerchantQualificationEntity();
        conn.cleanInit();
        conn.setMerchantId(merchantId);
        MerchantQualificationEntity merchantQualificationEntity = merchantQualificationDao.selectOne(conn);
        merchantQualificationEntity.setName(merchantBasicInfoDto.getName());
        merchantQualificationEntity.setDistrictId(merchantBasicInfoDto.getDistrictId());
        merchantQualificationEntity.setAddress(merchantBasicInfoDto.getAddress());
        merchantQualificationEntity.setStoreNumber(merchantBasicInfoDto.getStoreNumber());
        merchantQualificationEntity.setTelephone(merchantBasicInfoDto.getTelephone());
        merchantQualificationEntity.setStaffNo(merchantBasicInfoDto.getStaffNo());
        merchantQualificationEntity.setCapital(merchantBasicInfoDto.getCapital());
        merchantQualificationEntity.setContactTele(merchantBasicInfoDto.getContactTele());
        merchantQualificationEntity.setIdCard(merchantBasicInfoDto.getIdCard());
        merchantQualificationEntity.setEmail(merchantBasicInfoDto.getEmail());
        merchantQualificationEntity.setIdCardImage(merchantBasicInfoDto.getIdCardImage());
        merchantQualificationEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantQualificationEntity.setLastModifiedTime(new Date());
        merchantQualificationDao.updateById(merchantQualificationEntity);
    }

    @Override
    public void modifyMerchantInfoById(MercahntManageInfoDto mercahntManageInfoDto) throws ParseException {
        // 营业执照信息修改
        // 时间格式转换格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // 获取的起始时间字符串转换成Date类型
        Date licenseBeginDate = format.parse(mercahntManageInfoDto.getLicenseBegin());
        // 获取的结束时间字符串转换成Date类型
        Date licenseEndDate = format.parse(mercahntManageInfoDto.getLicenseEnd());
        Date establishTime = format.parse(mercahntManageInfoDto.getEstablishTime());
        // 根据当前用户查出商家id
        Long belongId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        MerchantQualificationEntity merchantQualificationEntity = merchantQualificationDao.selectMerchantInfoByMerchantId(belongId);
        Long id = merchantQualificationEntity.getId();
        BeanUtils.copyProperties(mercahntManageInfoDto, merchantQualificationEntity);
        merchantQualificationEntity.setId(id);
        merchantQualificationEntity.setLicenseBegin(licenseBeginDate);
        merchantQualificationEntity.setLicenseEnd(licenseEndDate);
        merchantQualificationEntity.setEstablishTime(establishTime);
        merchantQualificationEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantQualificationEntity.setLastModifiedTime(new Date());
        merchantQualificationDao.updateById(merchantQualificationEntity);
        // 银行账户信息修改
        MerchantAccountEntity conn = new MerchantAccountEntity();
        conn.cleanInit();
        conn.setMerchantId(belongId);
        conn.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        MerchantAccountEntity merchantAccountEntity = merchantAccountDao.selectOne(conn);
        if (merchantAccountEntity != null) {
            BeanCopyUtil.copy(mercahntManageInfoDto, merchantAccountEntity);
            merchantAccountEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
            merchantAccountEntity.setLastModifiedTime(new Date());
            merchantAccountDao.updateById(merchantAccountEntity);
        }
    }

    @Override
    public void savePaymentPrice(CapitalDto capitalDto) {
        Long belongId = capitalDto.getMerchantId();
        // 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象，添加营业执照信息
        MerchantQualificationEntity merchantQualificationEntity = merchantQualificationDao.selectMerchantInfoByMerchantId(belongId);
        merchantQualificationEntity.setMemo(capitalDto.getMemo());
        merchantQualificationEntity.setPaymentPrice(capitalDto.getPaymentPrice());
        merchantQualificationEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantQualificationEntity.setLastModifiedTime(new Date());
        merchantQualificationDao.updateById(merchantQualificationEntity);
        // 商家入驻状态为4：待审核，等待商家缴费上传缴费凭证
        merchantInfoDaos.updateMerchantStatus(4, belongId);
    }

    @Override
    public void addStoreBackendCategoryInfo(StoreInfoDtos storeInfoDto) {
        List<BackendCategoryStatusDTO> backendCategoryStatusDTOS = storeInfoDto.getBackendCategoryStatusDTOS();
        // 根据当前用户查出商家id
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        if (CollectionUtils.isNotEmpty(backendCategoryStatusDTOS)) {
            for (BackendCategoryStatusDTO backendCategoryStatusDTO : backendCategoryStatusDTOS) {
                BackendCategoryStatusDTO backendCategoryStatusDTOs = new BackendCategoryStatusDTO();
                backendCategoryStatusDTOs.setMerchantId(merchantId);
                backendCategoryStatusDTOs.setStatus(GoodsConstants.BackendMerchantCategoryStatus.TO_AUDIT.getCode());
                backendCategoryStatusDTOs.setIds(backendCategoryStatusDTO.getIds());
                backendCategoryApi.modifyBackendMerchanntStatus(backendCategoryStatusDTOs);
            }
        }
        MerchantInfoEntity entity = merchantInfoDaos.selectById(merchantId);
        //跟换商户类型
        entity.setMerchantType(storeInfoDto.getMerchantType());
        //log
        entity.setImgages(storeInfoDto.getImgages());
        //店铺名称
        entity.setStoreName(storeInfoDto.getStoreName());
        //添加结算周期
        entity.setSettleCycle(storeInfoDto.getSettleCycle());
        //修改商家入驻商家状态
        entity.setState(1);
        entity.setLastModifiedTime(new Date());
        entity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        //商家结算信息
        MerchantAccountEntity conmn = new MerchantAccountEntity();
        conmn.cleanInit();
        conmn.setMerchantId(merchantId);
        conmn.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        MerchantAccountEntity merchantAccountEntity = merchantAccountDao.selectOne(conmn);
        merchantAccountEntity.setSettleAccount(storeInfoDto.getSettleAccount());
        merchantAccountEntity.setSettleAccountName(storeInfoDto.getSettleAccountName());
        merchantAccountEntity.setSettleBankName(storeInfoDto.getSettleBankName());
        merchantAccountEntity.setSettleBankDistrictId(storeInfoDto.getSettleBankDistrictId());
        merchantAccountEntity.setSettleBankNum(storeInfoDto.getSettleBankNum());
        merchantAccountEntity.setLastModifiedTime(new Date());
        merchantAccountEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        merchantAccountDao.updateById(merchantAccountEntity);
        merchantInfoDaos.updateById(entity);
    }

    @Override
    public List<BackendCategorysDTO> getBackendCategoryDtoByBelongStore() {
        Long merchantId = SecurityContextUtils.getCurrentUserDto().getMerchantId();
        //TODO:根据商家id查询当前所选的商品类目
        return backendCategoryApi.getMerchantBacCategory(merchantId);
    }

    @Override
    public List<MerchantauditLogDto> selectMerchantauditLog(Long merchantId) {
        return merchantauditLogDao.getMerchantauditLogs(merchantId);
    }

    @Override
    public List<MerchantAuditDetailDto> getMerchantauditLogDetail(Long id) {
        List<MerchantAuditDetailDto> merchantAuditDetailDtos = merchantAuditDetailDao.selectAuditDetailByLogId(id);
        return merchantAuditDetailDtos;
    }

    @Override
    public List<MerchantauditLogDto> getPalmMerchantauditLogDetail(Long merchantId) {
        List<MerchantauditLogDto> merchantauditLog = merchantauditLogDao.getMerchantauditLog(merchantId);
        return merchantauditLog;
    }
}
