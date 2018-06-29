package com.topaiebiz.member.member.utils;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.card.dto.CardBalanceDTO;
import com.topaiebiz.member.dto.grade.MemberGradeDto;
import com.topaiebiz.member.dto.member.MemberAccountDto;
import com.topaiebiz.member.dto.member.MemberMgmtDto;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.dto.point.MemberAssetDto;
import com.topaiebiz.member.member.bo.WechatUserBo;
import com.topaiebiz.member.member.constants.ThirdAccountType;
import com.topaiebiz.member.member.entity.MemberEntity;
import com.topaiebiz.member.reserve.entity.MemberBindAccountEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ward on 2018-01-19.
 */
public class MemberUtil {

    public static List<Long> extractMemberIdList(List<MemberMgmtDto> memberMgmtDtoList) {
        if (CollectionUtils.isEmpty(memberMgmtDtoList)) {
            return null;
        }
        List<Long> memberIdList = new ArrayList<>();
        for (MemberMgmtDto memberMgmtDto : memberMgmtDtoList) {
            memberIdList.add(memberMgmtDto.getId());
        }
        return memberIdList;
    }

    public static List<Long> extractGradeIdList(List<MemberMgmtDto> memberMgmtDtoList) {
        if (CollectionUtils.isEmpty(memberMgmtDtoList)) {
            return null;
        }
        List<Long> gradeIdList = new ArrayList<>();
        for (MemberMgmtDto memberMgmtDto : memberMgmtDtoList) {
            gradeIdList.add(memberMgmtDto.getGradeId());
        }
        return gradeIdList;
    }

    public static MemberTokenDto packageMemberToken(String sessionId, MemberEntity memberEntity) {
        MemberTokenDto memberTokenDto = new MemberTokenDto();
        memberTokenDto.setSessionId(sessionId);
        memberTokenDto.setMemberId(memberEntity.getId());
        memberTokenDto.setTelephone(memberEntity.getTelephone());
        memberTokenDto.setUserName(memberEntity.getUserName());
        return memberTokenDto;
    }

    public static String encryptTelephone(String telephone) {
        return StringUtils.isBlank(telephone) ? null : telephone.substring(0, 3) + "****" + telephone.substring(telephone.length() - 4);
    }

    public static MemberAccountDto packageMemberAccount(MemberEntity memberEntity,
                                                        MemberBindAccountEntity wxAccount, MemberBindAccountEntity qqAccount) {
        MemberAccountDto memberAccountDto = new MemberAccountDto();
        memberAccountDto.setMemberId(memberEntity.getId());
        memberAccountDto.setHasSetPayPwd(StringUtils.isNotBlank(memberEntity.getPayPassword()));
        memberAccountDto.setHasSetPwd(StringUtils.isNotBlank(memberEntity.getPassword()));
        memberAccountDto.setBindTelephone(StringUtils.isNotBlank(memberEntity.getTelephone()));
        memberAccountDto.setHiddenTelephone(encryptTelephone(memberEntity.getTelephone()));
        memberAccountDto.setTelephone(memberEntity.getTelephone());

        memberAccountDto.setBindWx(null != wxAccount);
        memberAccountDto.setBindQq(null != qqAccount);
        memberAccountDto.setWxNickname(extractWxNickname(wxAccount));
        return memberAccountDto;
    }

    public static String extractWxNickname(MemberBindAccountEntity wxAccount) {
        if (null == wxAccount || !ThirdAccountType.WX.equals(wxAccount.getAccountType())
                || StringUtils.isBlank(wxAccount.getThirdDesc())) {
            return "";
        }
        try {
            WechatUserBo wechatUser = JSON.parseObject(wxAccount.getThirdDesc(), WechatUserBo.class);
            return StringUtils.isBlank(wechatUser.getNickname()) ? "" : wechatUser.getNickname();
        } catch (Exception e) {
            return "";
        }

    }


    public static MemberMgmtDto packageMemberMgmt(MemberMgmtDto memberMgmtDto, MemberAssetDto memberAssetDto,
                                                  CardBalanceDTO cardBalanceDto, MemberGradeDto memberGradeDto) {
        if (null == memberAssetDto) {
            memberMgmtDto.setPoint(0);
            memberMgmtDto.setBalance(BigDecimal.ZERO);
        } else {
            memberMgmtDto.setPoint(memberAssetDto.getPoint());
            memberMgmtDto.setBalance(memberAssetDto.getBalance());
        }
        if (null == cardBalanceDto) {
            memberMgmtDto.setCardFrozenAccount(BigDecimal.ZERO);
            memberMgmtDto.setCardValidAccount(BigDecimal.ZERO);
        } else {
            memberMgmtDto.setCardFrozenAccount(cardBalanceDto.getFreezeBalance());
            memberMgmtDto.setCardValidAccount(cardBalanceDto.getBalance());
        }
        if (null != memberGradeDto) {
            memberMgmtDto.setGradeName(memberGradeDto.getGradeCode());
        }

        return memberMgmtDto;
    }

    public static Integer getResetSecond() {
        Long restSecond;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String curentDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
            Long nextDateTime = simpleDateFormat.parse(curentDate).getTime();
            restSecond = (nextDateTime - System.currentTimeMillis()) / 1000;
        } catch (Exception px) {
            return 24*60*60;
        }
        return restSecond.intValue();
    }
}
