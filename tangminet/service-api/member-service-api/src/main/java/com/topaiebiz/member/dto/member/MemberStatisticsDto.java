package com.topaiebiz.member.dto.member;

import lombok.Data;

import java.util.Date;

/**
 * 
 * Description： 统计管理(会员分析)Dto  
 * 
 * 
 * Author Scott.Yang
 *    
 * Date 2017年11月2日 下午3:18:05 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MemberStatisticsDto {

	/*** 注册时间。*/
	private String registerTimeStr;
	
	/*** 注册时间。*/
	private Date registerTime;
	
	/*** 年。*/
	private String years;
	
	/*** 月。*/
	private String months;
	
	/*** 日。*/
	private String days;
	
	/*** 注册量。*/
	private Long record;
	
	/***所属店铺。*/
	private Long storeId;
}
