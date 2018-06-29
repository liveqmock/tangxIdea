package com.topaiebiz.member.po;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * Created by ward on 2018-01-08.
 */
@Data
public class ResetPayPwdPo {

    /*** 显示用户名。*/
    private String userName;

    /*** 会员手机号。*/
    @Length(min = 11, max = 11)
    private String telephone;

    /*** 支付密码*/
    private String payPassword;

    /***短信验证码***/
    private String captcha;

    private String operateCode;
}
