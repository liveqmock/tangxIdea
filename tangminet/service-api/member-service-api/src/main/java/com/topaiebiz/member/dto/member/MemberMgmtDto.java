package com.topaiebiz.member.dto.member;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;


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
public class MemberMgmtDto {

    /*** 会员信息的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。*/
    private Long id;


    /*** 会员等级。*/
    @NotNull(message = "{validation.membermgmt.gradeId}")
    private Long gradeId;

    /*** 会员等级名称。*/
    private String gradeName;

    /*** 显示用户名。*/
    @NotNull(message = "{validation.membermgmt.userName}")
    @Length(min = 6, max = 20)
    private String userName;


    /*** 昵称。*/
    @Length(min = 2, max = 10)
    private String nickName;

    /*** 用户邮箱。*/
    @Email(message = "{validation.membermgmt.post}")
    private String post;

    /*** 会员真实姓名。*/
    @Length(min = 2, max = 8)
    private String realName;


    /*** 性别（1 南  0 女)。*/
    private Integer gender;

    /*** 生日。*/
    private String birthday;

    /*** 所在地区编码。*/
    private Long districtId;

    /*** 会员的详细地址。*/
    private String address;

    /*** 会员手机号。*/
    @Length(min = 11, max = 11)
    private String telephone;

    /*** 会员等级成长分值。。*/
    private Long upgradeScore;

    /*** 注册时间。*/
    private Date registerTime;

    /*** 注册IP。*/
    private String registerIp;

    /*** 最后登录时间。*/
    private Date lastLoginTime;

    /*** 最后登录IP。*/
    private String lastLoginIp;


    /*** 账户状态（1 锁定，0 正常）。*/
    private Integer accountState;


    /*** 小会员头像。*/
    private String smallIcon;

    /*** 大会员头像。*/
    private String bigIcon;


    /*** 创建时间。*/
    private Date createdTime;

    /***礼卡可用金额*/
    private BigDecimal cardValidAccount;

    /***礼卡冻结金额*/
    private BigDecimal cardFrozenAccount;

    /***积分***/
    private Integer point;

    /***余额***/
    private BigDecimal balance;

    /***可用优惠券张数**/
    private Integer couponNum;

}
