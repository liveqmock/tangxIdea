package com.topaiebiz.trade.refund.facade;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.api.MemberApi;
import com.topaiebiz.member.dto.member.MemberDto;
import com.topaiebiz.trade.refund.exception.RefundOrderExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/30 10:25
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component(value = "refundMemberServiceFacede")
public class MemberServiceFacade {

    @Autowired
    private MemberApi memberApi;

    public MemberDto getMember(Long memberId) {
        MemberDto memberDto = memberApi.getMemberByMemberId(memberId);
        log.info("-----------memberApi.getMemberByMemberId-- params:{}--response:{}", memberId, JSON.toJSONString(memberDto));
        if (null == memberDto) {
            throw new GlobalException(RefundOrderExceptionEnum.USER_IS_NOT_FOUND);
        }
        return memberDto;
    }

}
