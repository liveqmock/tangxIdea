package com.topaiebiz.member.grade.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.dto.grade.MemberGradeDto;
import com.topaiebiz.member.exception.GradeExceptionEnum;
import com.topaiebiz.member.grade.dao.MemberGradeDao;
import com.topaiebiz.member.grade.entity.MemberGradeEntity;
import com.topaiebiz.member.grade.entity.MemberGradePrivilegeEntity;
import com.topaiebiz.member.grade.service.MemberGradeService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description： 会员等级实现类
 * <p>
 * <p>
 * Author Scott.Yang
 * <p>
 * Date 2017年9月26日 下午7:57:49
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
public class MemberGradeServiceIpml implements MemberGradeService {

    @Autowired
    private MemberGradeDao memberGradeDao;

    @Override
    public Integer saveMemberGrade(MemberGradeDto memberGrade) throws GlobalException {
        //Long storeId = SecurityContextUtils.getCurrentSystemUser().getStoreId();
        //Long systemUserId = SecurityContextUtils.getCurrentSystemUser().getId();
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        Long systemUserId = SecurityContextUtils.getCurrentUserDto().getId();


        memberGrade.setStoreId(storeId);
        /** 对会员等级名称进行重复验证 */
        MemberGradeEntity findMemberGradeByName = memberGradeDao.selectMemberGradeByName(memberGrade);
        if (null != findMemberGradeByName) {
            throw new GlobalException(GradeExceptionEnum.MEMBER_GRADE_NAME_NOT_REPETITION);
        }

        MemberGradeEntity entity = new MemberGradeEntity();
        BeanUtils.copyProperties(memberGrade, entity);
        entity.setCreatorId(systemUserId);
        entity.setCreatedTime(new Date());
        return memberGradeDao.insert(entity);
    }

    @Override
    public Integer modifyMemberGrade(MemberGradeDto memberGrade) throws GlobalException {
        //Long systemUserId = SecurityContextUtils.getCurrentSystemUser().getId();
        Long systemUserId = SecurityContextUtils.getCurrentUserDto().getId();
        /** 对会员等级名称进行重复验证 */
        MemberGradeEntity findMemberGradeByName = memberGradeDao.selectMemberGradeByNames(memberGrade);
        if (null != findMemberGradeByName) {
            throw new GlobalException(GradeExceptionEnum.MEMBER_GRADE_NAME_NOT_REPETITION);
        }
        MemberGradeEntity memberGradeEntity = memberGradeDao.selectById(memberGrade.getId());
        if (null == memberGrade.getSmallIcon() || memberGrade.getSmallIcon() == "") {
            memberGrade.setSmallIcon(" ");
        }
        BeanUtils.copyProperties(memberGrade, memberGradeEntity);
        memberGradeEntity.setLastModifierId(systemUserId);
        memberGradeEntity.setLastModifiedTime(new Date());
        return memberGradeDao.updateById(memberGradeEntity);
    }

    @Override
    public Page<MemberGradeDto> listMemberGrade(PagePO pagePo, MemberGradeDto memberGradeDto) {
        //Long storeId = SecurityContextUtils.getCurrentSystemUser().getStoreId();
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        //根据电话查到会员，获取所属店铺
        Page<MemberGradeDto> page = PageDataUtil.buildPageParam(pagePo);

        memberGradeDto.setStoreId(storeId);
        page.setRecords(memberGradeDao.selectPageMemberGrade(page, memberGradeDto));
        return page;
    }


    @Override
    public void removeMemberGrades(Long[] id) throws GlobalException {
        for (Long longs : id) {
            /** 通过ID查询对象是否存在 */
            MemberGradeEntity selectMemberGradeById = memberGradeDao.selectMemberGradeById(longs);
            if (null == selectMemberGradeById) {
                throw new GlobalException(GradeExceptionEnum.MEMBER_GRADE_ID_NOT_EXIST);
            }
        }
        memberGradeDao.deleteMemberGrades(id);
    }

    @Override
    public Integer saveMemberGradePrivilege(MemberGradePrivilegeEntity memberGradePrivilegeEntity)
            throws GlobalException {

        //Long systemUserId = SecurityContextUtils.getCurrentSystemUser().getId();
        Long systemUserId = SecurityContextUtils.getCurrentUserDto().getId();

        if (null == memberGradePrivilegeEntity.getId()) {
            throw new GlobalException(GradeExceptionEnum.MEMBER_GRADE_ID_NOT_EXIST);
        }
        memberGradePrivilegeEntity.setCreatorId(systemUserId);
        memberGradePrivilegeEntity.setCreatedTime(new Date());
        return memberGradeDao.insertMemberGradePrivilege(memberGradePrivilegeEntity);
    }

    @Override
    public MemberGradeEntity findMemberGradesById(Long id) throws GlobalException {
        return memberGradeDao.selectById(id);
    }

    @Override
    public List<MemberGradeEntity> getAllGrade() {
        MemberGradeEntity memberGradeEntity = new MemberGradeEntity();
        memberGradeEntity.cleanInit();
        memberGradeEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);

        Wrapper<MemberGradeEntity> param = new EntityWrapper<>();

        return memberGradeDao.selectList(param);

    }

    @Override
    public Map<Long, MemberGradeDto> getMemberGradeMap(List<Long> gradeIdList) {
        if (CollectionUtils.isEmpty(gradeIdList)) {
            return null;
        }
        EntityWrapper<MemberGradeEntity> condition = new EntityWrapper<>();
        condition.in("id", gradeIdList);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<MemberGradeEntity> memberGradeEntityList = memberGradeDao.selectList(condition);
        if (CollectionUtils.isEmpty(memberGradeEntityList)) {
            return null;
        }
        Map<Long, MemberGradeDto> memberGradeDtoMap = new HashMap<>();
        for (MemberGradeEntity memberGradeEntity : memberGradeEntityList) {
            MemberGradeDto memberGradeDto = new MemberGradeDto();
            BeanUtils.copyProperties(memberGradeEntity, memberGradeDto);
            memberGradeDtoMap.put(memberGradeEntity.getId(), memberGradeDto);
        }
        return memberGradeDtoMap;
    }

    @Override
    public MemberGradeDto getMemberGrade(Long gradeId) {
        MemberGradeEntity param = new MemberGradeEntity();
        param.cleanInit();
        param.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        param.setId(gradeId);
        MemberGradeEntity memberGradeEntity = memberGradeDao.selectOne(param);
        MemberGradeDto memberGradeDto = new MemberGradeDto();
        BeanUtils.copyProperties(memberGradeEntity, memberGradeDto);
        return memberGradeDto;
    }

}
