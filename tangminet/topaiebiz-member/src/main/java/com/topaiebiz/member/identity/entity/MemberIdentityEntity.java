package com.topaiebiz.member.identity.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created by admin on 2018/5/31.
 */
@Data
@TableName("t_mem_member_identity")
public class MemberIdentityEntity extends BaseBizEntity<Long> {

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 身份证号码
     */
    private Long idcard;

    /**
     * 身份证反面
     */
    private String identityPicBack;

    /**
     * 身份证正面
     */
    private String identityPicFront;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别
     */
    private Byte sex;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核状态   1待审核；2审核不通过；3审核通过
     */
    private Integer status;

}
