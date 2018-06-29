package com.topaiebiz.member.member.api.impl;

import com.topaiebiz.member.api.MemberApi;
import com.topaiebiz.member.dto.member.LoginOrRegiseterDto;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by ward on 2018-01-05.
 */
@Component
public class MemberApiImpl implements MemberApi {


    @Autowired
    private MemberService memberService;

    @Override
    public MemberDto getMemberInfoByPhone(String phone) {
        return memberService.getMemberDto(phone);
    }

    @Override
    public MemberTokenDto getMemberToken(String sessionId) {
        return memberService.getMemberToken(sessionId);
    }

    @Override
    public boolean hasPayPwd(Long memberId) {
        return memberService.hasPayPwd(memberId);
    }

    @Override
    public boolean validatePayPwd(Long memberId, String payPwd) {
        return memberService.validatePayPwd(memberId, payPwd);
    }

    @Override
    public MemberDto getMemberInfoByNameOrPhone(String var) {
        return memberService.getMemberByNameOrPhone(var);
    }

    @Override
    public MemberDto getMemberByMemberId(Long memberId) {
        return memberService.getMemberDto(memberId);
    }

    @Override
    public MemberDto getValidatedMember(String loginName, String memberPwd) {
        return memberService.getValidatedMember(loginName, memberPwd);
    }

    @Override
    public List<MemberDto> getMemberList(List<Long> memberIds) {
        return memberService.getMemberList(memberIds);
    }

    @Override
    public Map<Long, MemberDto> getMemberMap(List<Long> memberIds) {
        return memberService.getMemberMap(memberIds);
    }

    @Override
    public MemberTokenDto doLoginOrRegiseter(LoginOrRegiseterDto loginOrRegiseterDto) {
        return memberService.doLoginOrRegiseter(loginOrRegiseterDto, false);
    }
}
