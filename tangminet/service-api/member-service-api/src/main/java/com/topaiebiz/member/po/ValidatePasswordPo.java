package com.topaiebiz.member.po;

import lombok.Data;

/**
 * Created by ward on 2018-01-22.
 */
@Data
public class ValidatePasswordPo {
    /***会员密码***/
    private String memberPwd;
    /**
     * 支付密码
     **/
    private String payPwd;

    /***类型***/
    private String type;

}
