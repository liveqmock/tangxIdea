package com.topaiebiz.member.dto.member;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 
 * Description：会员商家注册信息表  
 * 
 * 
 * Author Scott.Yang
 *    
 * Date 2017年10月13日 上午午10:11:24 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MemberRegisterDto {
	
	
	private String token; 
	
	/*** 会员信息的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。*/
	private Long id;
	
	/*** 显示用户名。*/
	private String userName;
	
	/*** 密码。*/
	private String password;
	
	/*** 会员手机号。*/
	@Length(min = 11,max = 11)
	private String telephone;
	
	/*** 支付密码*/
	private String payPassword;
}
