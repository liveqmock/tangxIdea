package com.topaiebiz.member.po;

import lombok.Data;

/**
 * Created by ward on 2018-01-08.
 */
@Data
public class TelephoneLoginPo {
    /***手机号***/
    private String telephone;


    /***短信验证码***/
    private String captcha;

    private String ip;
}
