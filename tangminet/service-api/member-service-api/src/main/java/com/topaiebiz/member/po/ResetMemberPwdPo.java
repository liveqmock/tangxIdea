package com.topaiebiz.member.po;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * Created by ward on 2018-01-08.
 */
@Data
public class ResetMemberPwdPo {

    /*** 显示用户名。*/
    // private String userName;

    /*** 密码。*/
    private String password;

    /*** 会员手机号。*/
    @Length(min = 11, max = 11)
    private String telephone;

    /***短信验证码***/
    // private String captcha;

    /****/
    private String operateCode;

    /***3忘记密码 5更新密码*/
    private String type = "3";
}
