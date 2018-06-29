package com.topaiebiz.system.security.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * 接受参数的用户DTO
 */
@Data
public class SystemUserDto extends PagePO{

	private Long id;
	
	/** 登录类型。*/
	@NotNull(message = "{nebula.system.security.systemUserDto.code.notNull}")
	private Integer type;

	/** 系统用户名称。可用作登录的账户名（即使用用户名登录）。 */
	@NotNull(message = "{nebula.system.security.systemUserDto.username.notNull}")
	private String username;

	/** 系统用户的当前登录密码。 */
	@NotNull(message = "{nebula.system.security.systemUserDto.password.notNull}")
	private String password;

	//手机号
	private String mobilePhone;

	//创建时间
	private Date createdTime;

	//角色Id
	private Long roleId;

	//内置标识
	private Byte inbuiltFlag;

}
