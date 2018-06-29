package com.topaiebiz.member.identity.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.member.identity.dto.MemberAuditStatus;
import com.topaiebiz.member.identity.dto.MemberIdentityAuditDto;
import com.topaiebiz.member.identity.dto.MemberIdentityDto;

import java.util.List;

/**
 * Created by admin on 2018/5/31.
 */
public interface MemberIdentityService {



    /**
     *  c端添加信息 根据当前会员id添加信息
     * @param memberId
     * @param memberIdentityDto
     * @return
     */
    Integer insertMemberIdentityInfo(Long memberId,MemberIdentityDto memberIdentityDto);

    /**
     * c端显示审核信息
     * @param memberId
     * @return
     */
    MemberIdentityAuditDto selectMemberIdentityAudit(Long memberId);

    /**
        c端回显基本信息
     * @param memberId
     * @return
     */
    MemberIdentityDto selectMemberIdentityInfo(Long memberId);

    /**
     * c端修改基本信息
     * @param memberIdentityDto
     * @return
     */
    Integer updateMemberIdentityInfo(MemberIdentityDto memberIdentityDto);

    /**
     * 实名认证审核列表
     * @param pagePO
     * @param memberIdentityDto
     * @return
     */
    PageInfo<MemberIdentityDto> getMemberIdentityInfoList(PagePO pagePO, MemberIdentityDto memberIdentityDto);

    /**
     * 查看具体信息的详情
     * @param identityId
     * @return
     */
    MemberIdentityDto selectMemberIdentityDetails(Long identityId);

    /**
     *  添加审核原因
     * @param memberIdentityAuditDto
     * @return
     */
    Integer insertMemberIdentityAuditInfo(MemberIdentityAuditDto memberIdentityAuditDto);

    /**
     * 批量添加审核原因（批量审核）
     * @param memberIdentityDto
     * @return
     */
    Integer batchInsertMemberIdentityAuditInfo(MemberIdentityDto memberIdentityDto);

    /**
     * 判断返回状态
     * @param memberId
     * @return
     */
    MemberAuditStatus returnMemberAuditStatus(Long memberId);



}
