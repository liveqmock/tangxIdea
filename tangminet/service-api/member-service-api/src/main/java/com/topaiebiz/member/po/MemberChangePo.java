package com.topaiebiz.member.po;


import lombok.Data;

/**
 * Description 会员信息表
 * <p>
 * <p>
 * Author scott
 * <p>
 * Date 2017年8月23日 下午7:49:54
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MemberChangePo {


    /*** 昵称。*/
    private String nickName;


    /*** 性别（1 男  0 女)。*/

   // @Length(min = 0, max = 1, message = "性别只能设置男或女")
    //@Null
    private Integer gender;

    /*** 生日。*/
    private String birthday;

    /*** 小会员头像。*/
    private String smallIcon;

    private String bigIcon;
}
