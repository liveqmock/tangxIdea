package com.topaiebiz.member.identity.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.topaiebiz.member.identity.constants.IdentityConstants;
import com.topaiebiz.member.identity.dao.MemberIdentityAuditDao;
import com.topaiebiz.member.identity.dao.MemberIdentityDao;
import com.topaiebiz.member.identity.dto.BatchMemberIdentityInfoDto;
import com.topaiebiz.member.identity.dto.MemberAuditStatus;
import com.topaiebiz.member.identity.dto.MemberIdentityAuditDto;
import com.topaiebiz.member.identity.dto.MemberIdentityDto;
import com.topaiebiz.member.identity.entity.MemberIdentityAuditEntity;
import com.topaiebiz.member.identity.entity.MemberIdentityEntity;
import com.topaiebiz.member.identity.service.MemberIdentityService;
import com.topaiebiz.member.member.entity.MemberEntity;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2018/5/31.
 */
@Service
@Transactional
public class MemberIdentityServiceImpl implements MemberIdentityService {

    @Autowired
    private MemberIdentityAuditDao memberIdentityAuditDao;

    @Autowired
    private MemberIdentityDao memberIdentityDao;


    @Override
    public Integer insertMemberIdentityInfo(Long memberId, MemberIdentityDto memberIdentityDto) {
        Integer i = 0;
        MemberIdentityEntity memberIdentityEntity = new MemberIdentityEntity();
        BeanCopyUtil.copy(memberIdentityDto, memberIdentityEntity);
        memberIdentityEntity.setCreatedTime(new Date());
        memberIdentityEntity.setCreatorId(memberId);
        memberIdentityEntity.setMemberId(memberId);
        //待审核
        memberIdentityEntity.setStatus(IdentityConstants.Status.CHECK);
        i = memberIdentityDao.insert(memberIdentityEntity);
        return i;
    }

    @Override
    public MemberIdentityAuditDto selectMemberIdentityAudit(Long memberId) {
        EntityWrapper<MemberIdentityAuditEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("memberId", memberId);
        cond.orderBy("id", false);
        RowBounds rowBounds = new RowBounds(0, 1);
        List<MemberIdentityAuditEntity> memberIdentityAuditEntities = memberIdentityAuditDao.selectPage(rowBounds, cond);
        MemberIdentityAuditDto memberIdentityAuditDto = new MemberIdentityAuditDto();
        if (CollectionUtils.isNotEmpty(memberIdentityAuditEntities)) {
            MemberIdentityAuditEntity memberIdentityAuditEntity = memberIdentityAuditEntities.get(0);
            BeanCopyUtil.copy(memberIdentityAuditEntity, memberIdentityAuditDto);
            MemberIdentityEntity memberIdentityEntity = new MemberIdentityEntity();
            memberIdentityEntity.cleanInit();
            memberIdentityEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            memberIdentityEntity.setMemberId(memberId);
            MemberIdentityEntity memberIdentityEntity1 = memberIdentityDao.selectOne(memberIdentityEntity);
            if (memberIdentityEntity1 != null) {
                memberIdentityAuditDto.setStatus(memberIdentityEntity1.getStatus());
            }
        }
        return memberIdentityAuditDto;
    }

    @Override
    public MemberIdentityDto selectMemberIdentityInfo(Long memberId) {
        MemberIdentityEntity memberIdentityEntity = new MemberIdentityEntity();
        memberIdentityEntity.cleanInit();
        memberIdentityEntity.setMemberId(memberId);
        memberIdentityEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        MemberIdentityEntity memberIdentityEntity1 = memberIdentityDao.selectOne(memberIdentityEntity);
        MemberIdentityDto memberIdentityDtos = new MemberIdentityDto();
        BeanCopyUtil.copy(memberIdentityEntity1, memberIdentityDtos);
        memberIdentityDtos.setIdentityId(memberIdentityEntity1.getId());
        return memberIdentityDtos;
    }

    @Override
    public Integer updateMemberIdentityInfo(MemberIdentityDto memberIdentityDto) {
        Integer i = 0;
        MemberIdentityEntity memberIdentityEntity = new MemberIdentityEntity();
        BeanCopyUtil.copy(memberIdentityDto, memberIdentityEntity);
        memberIdentityEntity.cleanInit();
        memberIdentityEntity.setId(memberIdentityDto.getIdentityId());
        memberIdentityEntity.setMemberId(memberIdentityDto.getMemberId());
        //待审核
        memberIdentityEntity.setStatus(IdentityConstants.Status.CHECK);
        memberIdentityEntity.setLastModifiedTime(new Date());
        memberIdentityEntity.setLastModifierId(memberIdentityDto.getMemberId());
        i = memberIdentityDao.updateById(memberIdentityEntity);
        return i;
    }

    @Override
    public PageInfo<MemberIdentityDto> getMemberIdentityInfoList(PagePO pagePO, MemberIdentityDto memberIdentityDto) {
        Page<MemberIdentityDto> page = PageDataUtil.buildPageParam(pagePO);
        List<MemberIdentityDto> memberIdentityDtos = memberIdentityDao.selectMemberIdentityList(page, memberIdentityDto);
        page.setRecords(memberIdentityDtos);
        return PageDataUtil.copyPageInfo(page);
    }


    @Override
    public MemberIdentityDto selectMemberIdentityDetails(Long identityId) {
        MemberIdentityEntity codd = new MemberIdentityEntity();
        codd.cleanInit();
        codd.setId(identityId);
        codd.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        MemberIdentityEntity memberIdentityEntity = memberIdentityDao.selectById(codd);
        MemberIdentityDto memberIdentityDto = new MemberIdentityDto();
        if (memberIdentityEntity != null) {
            BeanCopyUtil.copy(memberIdentityEntity, memberIdentityDto);
        }
        EntityWrapper<MemberIdentityAuditEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.eq("identityId", identityId);
        cond.orderBy("id", false);
        RowBounds rowBounds = new RowBounds(0, 1);
        List<MemberIdentityAuditEntity> memberIdentityAuditEntities = memberIdentityAuditDao.selectPage(rowBounds, cond);
        if (CollectionUtils.isNotEmpty(memberIdentityAuditEntities)) {
            MemberIdentityAuditEntity memberIdentityAuditEntity = memberIdentityAuditEntities.get(0);
            memberIdentityDto.setAuditReason(memberIdentityAuditEntity.getAuditReason());
        }
        return memberIdentityDto;
    }

    @Override
    public Integer insertMemberIdentityAuditInfo(MemberIdentityAuditDto memberIdentityAuditDto) {
        Integer i = 0;
        //查询数据
        MemberIdentityEntity codd = new MemberIdentityEntity();
        codd.cleanInit();
        codd.setId(memberIdentityAuditDto.getIdentityId());
        codd.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        MemberIdentityEntity memberIdentityEntity = memberIdentityDao.selectOne(codd);
        MemberIdentityAuditEntity coon = new MemberIdentityAuditEntity();
        BeanCopyUtil.copy(memberIdentityAuditDto, coon);
        if (memberIdentityEntity != null) {
            coon.setOriginData(JSON.toJSONString(memberIdentityEntity));
            memberIdentityEntity.setStatus(memberIdentityAuditDto.getStatus());
            memberIdentityEntity.setAuditTime(new Date());
            memberIdentityEntity.setId(memberIdentityAuditDto.getIdentityId());
            memberIdentityDao.updateById(memberIdentityEntity);
        }
        i = memberIdentityAuditDao.insert(coon);
        return i;
    }

    @Override
    public Integer batchInsertMemberIdentityAuditInfo(MemberIdentityDto memberIdentityDto) {
        Integer i = 0;
        List<BatchMemberIdentityInfoDto> batchMemberIdentityInfoDtos = memberIdentityDto.getBatchMemberIdentityInfoDtos();
        if (CollectionUtils.isNotEmpty(batchMemberIdentityInfoDtos)) {
            for (BatchMemberIdentityInfoDto batchMemberIdentityInfoDto : batchMemberIdentityInfoDtos) {
                MemberIdentityEntity codd = new MemberIdentityEntity();
                codd.cleanInit();
                codd.setId(batchMemberIdentityInfoDto.getIdentityId());
                codd.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
                MemberIdentityEntity memberIdentityEntity = memberIdentityDao.selectOne(codd);
                if (memberIdentityEntity != null) {
                    memberIdentityEntity.setStatus(memberIdentityDto.getStatus());
                    memberIdentityEntity.setAuditTime(new Date());
                    memberIdentityEntity.setId(batchMemberIdentityInfoDto.getIdentityId());
                    memberIdentityDao.updateById(memberIdentityEntity);
                    MemberIdentityAuditEntity coon = new MemberIdentityAuditEntity();
                    coon.setIdentityId(batchMemberIdentityInfoDto.getIdentityId());
                    coon.setMemberId(batchMemberIdentityInfoDto.getMemberId());
                    coon.setOriginData(JSON.toJSONString(memberIdentityEntity));
                    coon.setAuditReason(memberIdentityDto.getAuditReason());
                    coon.setCreatedTime(new Date());
                    coon.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
                    i = memberIdentityAuditDao.insert(coon);
                }
            }
        }
        return i;
    }


    @Override
    public MemberAuditStatus returnMemberAuditStatus(Long memberId) {
        MemberIdentityEntity where = new MemberIdentityEntity();
        where.cleanInit();
        where.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        where.setMemberId(memberId);
        MemberIdentityEntity memberIdentityEntity = memberIdentityDao.selectOne(where);
        MemberAuditStatus memberAuditStatus = new MemberAuditStatus();
        if (memberIdentityEntity ==null){
            memberAuditStatus.setIsUploadIdentity(false);
        }else{
            memberAuditStatus.setIsUploadIdentity(true);
                memberAuditStatus.setStatus(memberIdentityEntity.getStatus());
        }
        return memberAuditStatus;
    }


}
