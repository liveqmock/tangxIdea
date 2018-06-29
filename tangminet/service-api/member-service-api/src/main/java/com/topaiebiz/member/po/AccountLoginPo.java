package com.topaiebiz.member.po;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by ward on 2018-01-08.
 */
@Data
public class AccountLoginPo {

    /***登录名：目前只支持手机号/用户名?登录**/
    @NotEmpty
    private String loginName;

    /**用户密码**/
    @NotEmpty
    private String password;

}
