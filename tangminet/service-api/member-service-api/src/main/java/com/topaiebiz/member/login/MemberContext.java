package com.topaiebiz.member.login;


import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.exception.MemberExceptionEnum;

/**
 * Created by ward on 2017-12-29.
 */
public class MemberContext {


    private static ThreadLocal<MemberTokenDto> tokenContext = new ThreadLocal<>();

    public static void saveMemberToken(MemberTokenDto tokenDTO) {
        tokenContext.set(tokenDTO);
    }

    public static MemberTokenDto getCurrentMemberToken() {
        MemberTokenDto memberTokenDto = tokenContext.get();
        if (null == memberTokenDto || null == memberTokenDto.getMemberId() || memberTokenDto.getMemberId() <= 0) {
            throw new GlobalException(MemberExceptionEnum.NOT_LOGIN);
        }
        return memberTokenDto;
    }

    public static Long getMemberId() {
        MemberTokenDto memberTokenDto = tokenContext.get();
        if (null == memberTokenDto || null == memberTokenDto.getMemberId() || memberTokenDto.getMemberId() <= 0) {
            throw new GlobalException(MemberExceptionEnum.NOT_LOGIN);
        }
        return memberTokenDto.getMemberId();
    }

    public static Long tryGetMemberId() {
        MemberTokenDto memberTokenDto = tokenContext.get();
        if (null == memberTokenDto || null == memberTokenDto.getMemberId() || memberTokenDto.getMemberId() <= 0) {
            return null;
        }
        return memberTokenDto.getMemberId();
    }

    public static MemberTokenDto tryGetCurrentMemberToken() {
        MemberTokenDto memberTokenDto = tokenContext.get();
        if (null == memberTokenDto || null == memberTokenDto.getMemberId() || memberTokenDto.getMemberId() <= 0) {
            return null;
        }
        return memberTokenDto;
    }

    public static String getSessionId() {
        MemberTokenDto memberTokenDto = tokenContext.get();
        if (null == memberTokenDto || null == memberTokenDto.getMemberId() || memberTokenDto.getMemberId() <= 0) {
            return null;
        }
        return memberTokenDto.getSessionId();
    }

    public static void clear() {
        if (null != tokenContext) {
            tokenContext.remove();
        }
    }
}
