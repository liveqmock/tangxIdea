package com.topaiebiz.member.reserve.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * 
 * Description 会员类型表
 * 
 * 
 * Author Scott
 *    
 * Date 2017年8月23日 下午7:50:05 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@TableName("t_mem_member_type")
@Data
public class MemberTypeEntity extends BaseBizEntity<Long> {

	/** 序列化版本号。 */
	@TableField(exist = false)
	private static final long serialVersionUID = -7626599180778752978L;

	/**类型主键id**/
	private  Long id;

	/*** 会员类型表的名称。*/ 
	private String name;
	
	/*** 描述。*/ 
	private String description;


}
