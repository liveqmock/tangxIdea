package com.topaiebiz.member.vo;

import lombok.Data;

/**
 * Created by ward on 2018-01-08.
 */
@Data
public class LoginReturnVo {
    private String sessionId;

    private String userName;

    private Boolean isRegister;

    private Boolean hasSetPwd;

    private Boolean hasBindTel;

    private String setPwdCode = "";

    private String bindTelCode = "";

    private String telephone = "";
}
