package com.topaiebiz.system.xiaoneng.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by ward on 2018-04-05.
 */
@Data
public class NtkfParamCommonDto {

    /**
     * siteid：企业ID，为固定值，必填
     */
    @JSONField(name = "siteid")
    private String siteId;

    /**
     * sellerid：商户ID，集成商户时填写(**当有商户时，集成使用该字段**)
     */
    @JSONField(name = "sellerid")
    private String sellerId;

    /**
     * settingid：接待组ID，为固定值，必填
     */
    @JSONField(name = "settingid")
    private String settingId;

    /**
     * uid：用户ID，未登录可以为空，但不能给null，uid赋予的值在显示到小能客户端上
     */
    @JSONField(name = "uid")
    private String uId;

    /**
     * uname：用户名称，未登录可以为空，但不能给null，uname赋予的值显示到小能客户端上
     */
    @JSONField(name = "uname")
    private String uName;

    /**
     * isvip：是否为vip用户，0代表非会员，1代表会员，取值显示到小能客户端上
     */
    @JSONField(name = "isvip")
    private Integer isVip;

    /**
     * userlevel：网站自定义会员级别，0-N，可根据选择判断，取值显示到小能客户端上
     */
    @JSONField(name = "userlevel")
    private Integer userLevel;


    @JSONField(name = "erpparam")
    private String erpParam;
}
