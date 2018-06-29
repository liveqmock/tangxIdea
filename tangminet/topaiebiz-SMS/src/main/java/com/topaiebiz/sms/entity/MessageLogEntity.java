package com.topaiebiz.sms.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_sms_message_log")
public class MessageLogEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = -5787452797918874099L;

    //手机号
    private String telephone;

    //验证码
    private String captcha;

    //IP
    private String sendIp;

    //发送类型
    private Integer sendType;

    //内容
    private String content;

    //发送时间
    private Date sendTime;

    /**
     * 操作者用户ID
     */
    private Long memberId;
}
