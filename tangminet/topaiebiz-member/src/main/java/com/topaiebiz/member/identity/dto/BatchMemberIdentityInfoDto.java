package com.topaiebiz.member.identity.dto;

import lombok.Data;

/**
 * Created by admin on 2018/6/7.
 */
@Data
public class BatchMemberIdentityInfoDto {

    /**
     * 身份证id
     */
    private Long identityId;

    /**
     * 会员id
     */
    private Long memberId;
}
