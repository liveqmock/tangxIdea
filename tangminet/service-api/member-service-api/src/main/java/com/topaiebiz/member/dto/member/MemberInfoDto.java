package com.topaiebiz.member.dto.member;


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
public class MemberInfoDto {

    /*** 会员信息的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。*/
    private Long id;

    /*** 昵称。*/
    private String nickName;

    /***用户名**/
    private String userName;

    /*** 性别（1 南  0 女)。*/
    private Integer gender;

    /*** 生日。*/
    private String birthday;

    /*** 小会员头像。*/
    private String smallIcon;

    private String bigIcon;
}
