package com.topaiebiz.member.reserve.dto;

import com.baomidou.mybatisplus.annotations.TableName;
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
public class MemberTypeDto  {


	/**类型主键Id**/
	private  Long id;

	/*** 会员类型表的名称。*/ 
	private String name;
	
	/*** 描述。*/ 
	private String description;



}
