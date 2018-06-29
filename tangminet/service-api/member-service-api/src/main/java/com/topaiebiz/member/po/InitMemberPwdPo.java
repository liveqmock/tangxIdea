package com.topaiebiz.member.po;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * Created by ward on 2018-01-08.
 */
@Data
public class InitMemberPwdPo {


    /*** 密码。*/
    private String password;

    /*** 会员手机号。*/
    @Length(min = 11, max = 11)
    private String telephone;

    /***初始化密码***/
    private String setPwdCode;
}
