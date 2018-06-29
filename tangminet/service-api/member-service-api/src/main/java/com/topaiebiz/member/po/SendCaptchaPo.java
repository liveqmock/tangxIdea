package com.topaiebiz.member.po;

import lombok.Data;

/**
 * Created by ward on 2018-01-22.
 */
@Data
public class SendCaptchaPo {
    /***手机号***/
    private String telephone;

    /***短信验证码类型***/
    private String type;
}
