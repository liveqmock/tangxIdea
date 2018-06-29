package com.topaiebiz.member.po;

import lombok.Data;

/**
 * Created by ward on 2018-01-08.
 */
@Data
public class InitPayPwdPo {


    /*** 支付密码。*/
    private String payPassword;

    /***初始化密码操作code***/
    private String operateCode;
}
