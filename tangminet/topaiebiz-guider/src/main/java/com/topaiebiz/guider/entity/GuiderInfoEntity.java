package com.topaiebiz.guider.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created by admin on 2018/5/30.
 * 导购信息表
 */
@Data
@TableName("t_guider_info")
public class GuiderInfoEntity extends BaseBizEntity<Long> {

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 身份证号码
     */
    private Long idCard;

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
     * 审核状态   1待审核；2审核不通过；0审核通过
     */
    private Long status;


}
