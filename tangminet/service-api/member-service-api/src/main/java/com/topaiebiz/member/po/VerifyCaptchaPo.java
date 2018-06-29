package com.topaiebiz.member.po;

import lombok.Data;

/**
 * Created by ward on 2018-01-22.
 */
@Data
public class VerifyCaptchaPo {
    /***验证码***/
    private String captcha;

    /***短信验证码类型***/
    private String type;

    private String telephone;

    private Long memberId;
}
