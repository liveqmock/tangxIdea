package com.topaiebiz.member.api;

import com.topaiebiz.member.dto.member.LoginOrRegiseterDto;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.member.dto.member.MemberTokenDto;

import java.util.List;
import java.util.Map;

/**
 * Created by ward on 2018-01-03.
 */
public interface MemberApi {

    MemberDto getMemberInfoByPhone(String phone);

    MemberTokenDto getMemberToken(String sessionId);

    boolean hasPayPwd(Long memberId);

    boolean validatePayPwd(Long memberId, String payPwd);

    MemberDto getMemberInfoByNameOrPhone(String var);

    MemberDto getMemberByMemberId(Long memberId);

    MemberDto getValidatedMember(String loginName, String memberPwd);

    List<MemberDto> getMemberList(List<Long> memberIds);

    Map<Long, MemberDto> getMemberMap(List<Long> memberIds);

    MemberTokenDto doLoginOrRegiseter(LoginOrRegiseterDto loginOrRegiseterDto);

}
