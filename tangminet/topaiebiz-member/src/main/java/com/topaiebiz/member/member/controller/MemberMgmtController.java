package com.topaiebiz.member.member.controller;


import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.dto.member.MemberStatisticsDto;
import com.topaiebiz.member.exception.MemberExceptionEnum;
import com.topaiebiz.member.member.service.MemberService;
import com.topaiebiz.member.po.MemberFilterPo;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Description：会员管理控制层
 * <p>
 * Author Scott.Yang
 * <p>
 * Date 2017年9月26日 下午4:53:39
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping("/member/mgmt")
public class MemberMgmtController {

    @Autowired
    private MemberService memberService;

    /**
     * Description： 会员管理分页加列表加查询  （平台端？？）
     * <p>
     * Author Scott.Yang
     *
     * @param memberFilterPo 会员信息筛选Dto
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/getMemberList")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台会员列表")
    public ResponseInfo getMemberList(@RequestBody MemberFilterPo memberFilterPo) {
        PagePO pagePo = new PagePO();
        BeanCopyUtil.copy(memberFilterPo, pagePo);
        return new ResponseInfo(memberService.getMemberList(pagePo, memberFilterPo));
    }










    /**
     * Description： 根据id查询会员信息
     * <p>
     * Author Scott.Yang
     *
     * @param memberId 会员编号ID
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/detail/{memberId}")
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台端会员详情")
    public ResponseInfo getMemberMgmt(@PathVariable Long memberId) {
        Long systemUserId = SecurityContextUtils.getCurrentUserDto().getId();
        return new ResponseInfo(memberService.getMemberMgmtDto(memberId));
    }

    @RequestMapping(path = "/getStoreMember/{memberId}")
    @PermissionController(value = PermitType.MERCHANT, operationName = "会员端会员详情")
    public ResponseInfo getStoreMemberMgmt(@PathVariable Long memberId) {
        Long storeId = SecurityContextUtils.getCurrentUserDto().getStoreId();
        return new ResponseInfo(memberService.getMemberMgmtDto(storeId, memberId));
    }



    /**
     * Description： 根据id禁用会员状态
     * <p>
     * Author Ward.Wang
     *
     * @param memberId 会员编号ID
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/forbidden/{memberId}", method = RequestMethod.POST)
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台端批冻结会员")
    public ResponseInfo forbiddenMember(@PathVariable Long memberId) throws GlobalException {
        if (memberId <= 0) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_ID_NOT_NULL);
        }
        return new ResponseInfo(memberService.forbiddenMember(memberId));
    }

    /**
     * Description： 根据id解除冻结会员状态
     * <p>
     * Author Scott.Yang
     *
     * @param memberId 会员编号ID
     * @return
     * @throws GlobalException
     */
    @RequestMapping(path = "/relieve/{memberId}", method = RequestMethod.POST)
    @PermissionController(value = PermitType.PLATFORM, operationName = "平台端解冻会员")
    public ResponseInfo relieveMember(@PathVariable Long memberId) {
        if (memberId <= 0) {
            throw new GlobalException(MemberExceptionEnum.MEMBER_ID_NOT_NULL);
        }
        return new ResponseInfo(memberService.relieveMember(memberId));
    }


    /**
     * Description： 会员增值情况视图(按年展示)
     * <p>
     * Author Scott.Yang
     *
     * @param memberStatisticsDto
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/listMemberRecordByYear")
    public ResponseInfo listMemberRecordByYear(MemberStatisticsDto memberStatisticsDto)
            throws Exception {
        List<MemberStatisticsDto> list = memberService.listMemberRecordByYear(memberStatisticsDto);
        return new ResponseInfo(list);
    }

    /**
     * Description：  会员增值情况视图(按月展示)
     * <p>
     * Author Scott.Yang
     *
     * @param memberStatisticsDto
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/listMemberRecordByMonths")
    @ResponseBody
    public ResponseInfo listMemberRecordByMonths(MemberStatisticsDto memberStatisticsDto)
            throws Exception {
        List<MemberStatisticsDto> list = memberService.listMemberRecordByMonths(memberStatisticsDto);
        return new ResponseInfo(list);
    }

    /**
     * Description： 会员增值情况视图(按年展示商家端)
     * <p>
     * Author Scott.Yang
     *
     * @param memberStatisticsDto
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/listMemberRecordByYearOfBusiness")
    @ResponseBody
    public ResponseInfo listMemberRecordByYearOfBusiness(MemberStatisticsDto memberStatisticsDto)
            throws Exception {
        List<MemberStatisticsDto> list = memberService.listMemberRecordByYearOfBusiness(memberStatisticsDto);
        return new ResponseInfo(list);
    }

    /**
     * Description：  会员增值情况视图(按月展示商家端)
     * <p>
     * Author Scott.Yang
     *
     * @param memberStatisticsDto
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/listMemberRecordByMonthsOfBusiness")
    @ResponseBody
    public ResponseInfo listMemberRecordByMonthsOfBusiness(MemberStatisticsDto memberStatisticsDto)
            throws Exception {
        List<MemberStatisticsDto> list = memberService.listMemberRecordByMonthsOfBusiness(memberStatisticsDto);
        return new ResponseInfo(list);
    }
}
