package com.topaiebiz.system.security.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * 描述：系统资源实体类。
 * 
 * @author Created by Amir Wang on 2017年10月7日。
 * 
 * @since 1.1.1
 */
@TableName(value = "t_sys_system_resource")
@Data
public class SystemResourceEntity extends BaseBizEntity<Long> {

	/**
	 * 版本序列号
	 */
	private static final long serialVersionUID = -111273957111661185L;

	/** '系统资源类型（1为菜单 2 为 按钮）。 */
	private Integer resourceType;

	/** '系统资源名称（例如会员管理、添加）。' */
	private String name;

	/** '父资源ID（一级菜单则为0）' */
	private Long parentId;

	/** '系统资源排序号。' */
	private String sortNumber;

	/** '需要访问该资源的url。' */
	private String accessUrl;

	/** '点击该资源跳转的url。 */
	private String jumpUrl;

	/** '图标图片。' */
	private String iconImage;

	/** '系统资源描述。' */
	private String description;

	/** '备注' */
	private String memo;

}
