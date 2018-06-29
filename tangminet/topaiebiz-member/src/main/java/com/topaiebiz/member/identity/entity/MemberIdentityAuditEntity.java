package com.topaiebiz.member.identity.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * Created by admin on 2018/5/31.
 */
@Data
@TableName("t_mem_member_identity_audit")
public class MemberIdentityAuditEntity  extends BaseBizEntity<Long> {

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 身份证id
     */
    private Long identityId;

    /**
     * 审核当时数据的备份
     */
    private String originData;

    /**
     * 审核原因
     */
    private String auditReason;
}
