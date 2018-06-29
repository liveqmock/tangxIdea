package com.topaiebiz.member.identity.dto;

import lombok.Data;

/**
 * Created by admin on 2018/5/31.
 */
@Data
public class MemberIdentityAuditDto {

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 审核当时数据的备份
     */
    private String originData;

    /**
     * 审核原因
     */
    private String auditReason;

    /**
     * 身份证id
     */
    private Long identityId;

    /**
     * 审核状态   1待审核；2审核不通过；3审核通过
     */
    private Integer status;
}
