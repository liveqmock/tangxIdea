package com.topaiebiz.trade.order.facade;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.member.api.MemberApi;
import com.topaiebiz.member.dto.member.MemberDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/***
 * @author yfeng
 * @date 2018-01-19 14:26
 */
@Slf4j
@Component("tradeMemberServiceFacade")
public class MemberServiceFacade {

    @Autowired
    private MemberApi memberApi;

    public MemberDto getMember(Long memberId) {
        MemberDto memberDto = memberApi.getMemberByMemberId(memberId);
        log.info("memberApi.getMemberByMemberId({}) return {}", memberId, JSON.toJSONString(memberDto));
        return memberDto;
    }

    public boolean validatePayPwd(Long memberId, String payPwd) {
        boolean result = memberApi.validatePayPwd(memberId, payPwd);
        log.info("memberApi.validatePayPwd({},{}) return {}", memberId, payPwd, result);
        return result;
    }
}