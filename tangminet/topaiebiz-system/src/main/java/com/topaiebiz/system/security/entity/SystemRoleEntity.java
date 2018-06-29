package com.topaiebiz.system.security.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * 描述：系统角色实体类，用于定义系统角色的成员属性。
 * 
 * @author Created by Amir Wang on 2017年10月7日。
 * 
 * @since 1.1.1
 */
@TableName(value = "t_sys_system_role")
@Data
public class SystemRoleEntity extends BaseBizEntity<Long> {

	/** 序列化版本号。 */
	private static final long serialVersionUID = 1932646107308528876L;

	/** 系统角色名称。 */
	private String name;

	/**角色类型 同人员类型*/
	private Integer roleType;

	/** 系统角色的父角色编号。 */
	private Long parentId;

	/** 系统角色的描述信息。 */
	private String description;

	/** 1内置角色， 2平台创建角色  3商家创建角色。 */
	private Byte inbuiltFlag;

	/**商家ID*/
	private Long merchantId;

}
