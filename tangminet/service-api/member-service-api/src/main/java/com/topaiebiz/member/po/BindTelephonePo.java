package com.topaiebiz.member.po;

import lombok.Data;

/**
 * 手机绑定存在三种情况
 * 1.账号登录操作（未绑定手机号时不会返回sessionId）后绑定手机（未登录）
 * 2.登录第一次绑定手机号（已经登录）
 * 3.登录后更换新手机号（已经登录）
 * Created by ward on 2018-01-13.
 */
@Data
public class BindTelephonePo {

    /***新手机号***/
    private String newTelephone;

    /***短信验证码***/
    private String captcha;


    /**
     * 绑定操作的code，获得方式如下：
     * 1.（账号登录后如未绑定手机号 会返回）
     * 2.（登录后更换新手机号时，通过验证原先绑定手机号的验证码时返回）
     * 3.（登录后更换新手机号时，通过验证登录（支付）密码时返回）
     **/
    private String operateCode;

    /**
     * 登录名，仅账号登录后的绑定手机号的操作 要传
     **/
    private String loginName;
}
