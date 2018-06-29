package com.topaiebiz.member.identity.controller;

import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.identity.dto.MemberAuditStatus;
import com.topaiebiz.member.identity.dto.MemberIdentityAuditDto;
import com.topaiebiz.member.identity.dto.MemberIdentityDto;
import com.topaiebiz.member.identity.exception.IdentityException;
import com.topaiebiz.member.identity.service.MemberIdentityService;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by admin on 2018/5/31.
 */
@RestController
@RequestMapping(path = "/member/identity",method = RequestMethod.POST)
public class MemberIdentityController {

    @Autowired
    private MemberIdentityService memberIdentityService;

    /**
     * 平台端实名认证列表
     * @param memberIdentityDto
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/selectMemberIdentityList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "实名认证列表分页检索")
    public ResponseInfo getMemberIdentityList(@RequestBody MemberIdentityDto memberIdentityDto)
            throws GlobalException {
        int pageNo = memberIdentityDto.getPageNo();
        int pageSize = memberIdentityDto.getPageSize();
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(pageNo);
        pagePO.setPageSize(pageSize);
        return new ResponseInfo(memberIdentityService.getMemberIdentityInfoList(pagePO, memberIdentityDto));
    }

    /**
     * 平台端具体会员实名认证信息
     * @param id
     * @return
     */
    @RequestMapping(path = "/selectMemberIdentityDetails/{id}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "具体会员实名认证信息")
    public ResponseInfo selectMemberIdentityDetailsById(@PathVariable Long id) {
        return new ResponseInfo(memberIdentityService.selectMemberIdentityDetails(id));
    }

    /**
     * 平台端添加审核信息（审核）
     * @param memberIdentityAuditDto
     * @return
     */
    @RequestMapping(path = "/insertMemberIdentityAuditInfo")
    @PermissionController(value = PermitType.PLATFORM, operationName = "添加审核信息")
    public ResponseInfo insertMemberIdentityAuditInfo(@RequestBody MemberIdentityAuditDto memberIdentityAuditDto) {
        return new ResponseInfo(memberIdentityService.insertMemberIdentityAuditInfo(memberIdentityAuditDto));
    }

    @RequestMapping(path = "/batchInsertMemberIdentityAuditInfo")
    @PermissionController(value = PermitType.PLATFORM,operationName = "批量添加审核信息")
    public ResponseInfo batchInsertMemberIdentityAuditInfo(@RequestBody MemberIdentityDto memberIdentityDtos){
        return new ResponseInfo(memberIdentityService.batchInsertMemberIdentityAuditInfo(memberIdentityDtos));
    }

    /**
     * C端添加会员实名信息
     * @param memberIdentityDto
     * @return
     * @throws GlobalException
     */
   @RequestMapping(path = "/addMemberIdentityInfo")
   @MemberLogin
    public ResponseInfo addMemberIdentityInfo(@RequestBody MemberIdentityDto memberIdentityDto)
            throws GlobalException {
        if (memberIdentityDto.getIdcard() == null) {
            throw new GlobalException(IdentityException.IDCARD_NOT_NOLL);
        }
        if (memberIdentityDto.getRealName() == null) {
           throw new GlobalException(IdentityException.REALNAME_NOT_NOLL);
       }
       if (memberIdentityDto.getSex() == null) {
           throw new GlobalException(IdentityException.SEX_NOT_NOLL);
       }
       memberIdentityService.insertMemberIdentityInfo(MemberContext.getMemberId(), memberIdentityDto);
       return new ResponseInfo();
    }

    /**
     * c端回显会员实名信息
     * @return
     */
    @RequestMapping(path = "/selectMemberIdentityDetails")
    @MemberLogin
    public ResponseInfo selectMemberIdentityDetails() {
        return new ResponseInfo(memberIdentityService.selectMemberIdentityInfo(MemberContext.getMemberId()));
    }

    /**
     * C端修改会员实名信息
     * @param memberIdentityDto
     * @return
     */
    @RequestMapping(path = "/updateMemberIdentityInfoById")
    @MemberLogin
    public ResponseInfo updateMemberIdentityInfoById(@RequestBody MemberIdentityDto memberIdentityDto) {
        if (memberIdentityDto.getIdcard() == null) {
            throw new GlobalException(IdentityException.IDCARD_NOT_NOLL);
        }
        if (memberIdentityDto.getRealName() == null) {
            throw new GlobalException(IdentityException.REALNAME_NOT_NOLL);
        }
        if (memberIdentityDto.getSex() == null) {
            throw new GlobalException(IdentityException.SEX_NOT_NOLL);
        }
        memberIdentityService.updateMemberIdentityInfo(memberIdentityDto);
        return new ResponseInfo();
    }

    /**
     * c端回显审核信息
     * @return
     */
    @RequestMapping(path = "/selectMemberIdentityAudit")
    @MemberLogin
    public ResponseInfo selectMemberIdentityAudit() {
        return new ResponseInfo(memberIdentityService.selectMemberIdentityAudit(MemberContext.getMemberId()));
    }

    /**
     * 判断审核状态
     * @return
     */
    @RequestMapping(path = "returnMemberAuditStatus")
    @MemberLogin
    public ResponseInfo returnMemberAuditStatus(){
        return  new ResponseInfo(memberIdentityService.returnMemberAuditStatus(MemberContext.getMemberId()));
    }







}
