package com.topaiebiz.system.security.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import com.nebulapaas.data.mybatis.common.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * Description: 系统用户信息实体。它实现了Spring Security的UserDetails接口。
 * 
 * Author: Amir Wang
 * 
 * Date: 2017年9月23日 上午3:59:18
 * 
 * Copyright: Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_sys_system_user")
@Data
public class SystemUserEntity extends BaseBizEntity<Long>{

	/** 序列化版本号。 */
	private static final long serialVersionUID = 8171775459096928825L;

	/** 系统用户名称。可用作登录的账户名（即使用用户名登录）。 */
	private String username;

	/** 系统用户的真实名称。一般作为冗余字段回写，以便显示使用。 */
	private String realname;

	/** 系统用户的当前登录密码。 */
	private String password;

	/** 系统用户的账户类型。 */
	private Integer type;

	/** 系统用户的所属商家。 */
	private Long merchantId;

	/** 系统用户的所属店铺。 */
	private Long storeId;

	/** 系统用户的移动电话。可用作登录的账户名（即使用手机号登录） 。 */
	private String mobilePhone;

	/**
	 * 锁定状态 0为可用，1为不可用 默认为0
	 */
	private Byte lockedFlag = 0;

	/** 系统用户的内置标识。1内置人员  2平台创建人员   3商家创建人员。 */
	private Byte inbuiltFlag;

}
