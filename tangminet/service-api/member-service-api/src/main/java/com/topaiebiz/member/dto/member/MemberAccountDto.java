package com.topaiebiz.member.dto.member;

import lombok.Data;

/**
 * Created by ward on 2018-01-23.
 */
@Data
public class MemberAccountDto {

    private Boolean hasSetPwd;

    private Boolean hasSetPayPwd;

    private Boolean bindWx;

    private Boolean bindQq;

    private String telephone;

    private String hiddenTelephone;

    private Boolean bindTelephone;

    private Long memberId;

    private String wxNickname;
}
