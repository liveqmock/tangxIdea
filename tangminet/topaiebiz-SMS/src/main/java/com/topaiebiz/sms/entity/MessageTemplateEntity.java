package com.topaiebiz.sms.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_sms_message_template")
public class MessageTemplateEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 399705066640118967L;

    //签名
    private String signName;

    //模版编码
    private String templateCode;

    //模版类型
    private Integer templateType;

    //模版内容
    private String content;

    //最后修改时间
    private Date lastModifiedTime;

}
