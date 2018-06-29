package com.topaiebiz.system.security.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 描述：系统角色数据传输类，用于定义系统角色的数据传输成员属性。
 * 
 * @author Created by Amir Wang on 2017年10月7日。
 * 
 * @since 1.1.1
 */
@Data
public class SystemRoleDto extends PagePO{

	/** 系统角色编号。 */
	private Long id;

	/** 系统角色名称。 */
	@NotNull(message = "{nebula.system.security.systemRoleDto.name.notNull}")
	private String name;

	/**角色类型 同人员类型*/
	private String roleType;

	/** 系统角色的父角色编号。 */
	private Long parentId;

	/** 系统角色的父角色名称。 */
	private String parentName;

	/** 系统角色的描述信息。 */
	private String description;

	/** 系统角色的内置标识。仅且仅有0和1两个值，1表示内置角色，0表示非内置角色 注意，内置角色不能被删除。 */
	private Byte inbuiltFlag;

	private List<Long> resourceIds;

	private List<SecurityResourceDto> resources;

	private Date createdTime;

}
