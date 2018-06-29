package com.topaiebiz.member.reserve.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * Description 会员绑定信息表，第三方登录信息。
 * <p>
 * <p>
 * Author Scott
 * <p>
 * Date 2017年8月23日 下午7:50:52
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@TableName("t_mem_member_bind_account")
@Data
public class MemberBindAccountEntity extends BaseBizEntity<Long> {

    /**
     * 序列化版本号。
     */
    @TableField(exist = false)
    private static final long serialVersionUID = -768318710074028575L;

    /*** 会员编号。 */
    private Long memberId;

    /*** 登录类型（1 微信，2 QQ)。 */
    private Integer accountType;

    /*** 登录账户。 */
    private String platformAccount;

    /*** 登录名。 */
    private String platformName;

    /*** 授权登录密码。*/
    private String password;

    /*** 登录头像。*/
    private String platformIcon;

    /*** 登录验证手机号。*/
    private String telephone;

    /*** 绑定时间。*/
    private Date bindingTime;

    /*** 绑定IP */
    private String bindingIp;

    @TableField(exist = false)
    private Long lastModifierId;

    @TableField(exist = false)
    private Date lastModifiedTime;

    private String thirdUnionid;

    private String thirdDesc;

    private String userName;

    private String memo;


}
