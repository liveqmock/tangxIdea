package com.topaiebiz.member.po;

import lombok.Data;

/**
 * Created by ward on 2018-01-20.
 */
@Data
public class LoginOrRegiseterPo {

    private String memberFrom;

    private String telephone;

    private String ip;

    private String captcha;
}
