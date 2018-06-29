package com.topaiebiz.member.identity.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2018/5/31.
 */
@Data
public class MemberIdentityDto  extends PagePO implements Serializable {

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

    /**
     * 身份证id
     */
    private Long identityId;

    /**
     * 手机号
     */
    private String telephone;

    /**
     * 用户名
     */
    private String userName;


    /**
     * 审核原因
     */
    private String auditReason;

    /**
     * 提交时间
     */
    private Date createdTime;

    private List<BatchMemberIdentityInfoDto> batchMemberIdentityInfoDtos;

    /**
     * 判断是否上传
     */
    private Boolean isUploadIdentity=false;





}
