package com.topaiebiz.member.identity.dto;

import lombok.Data;

/**
 * Created by admin on 2018/6/8.
 */
@Data
public class MemberAuditStatus {

    /**
     * 判断是否上传
     */
    private Boolean isUploadIdentity=false;

    /**
     * 审核状态   1待审核；2审核不通过；3审核通过
     */
    private Integer status;

    /**
     * 会员id
     */
    private Long memberId;

}
