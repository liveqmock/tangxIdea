package com.topaiebiz.member.dto.grade;

import lombok.Data;

import java.util.Date;

/**
 * 
 * Description： 会员等级特权表,存储会员等级对应的特权。
 * 
 * 
 * Author Scott.Yang
 *    
 * Date 2017年9月25日 下午9:03:24 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MemberGradePrivilegeDto {
	
	/** 会员等级特权的全局唯一主键标识符*/
	private Long id;
	
	/*** 会员等级。*/
	private Long gradeId;
	
	/*** 特权类型 （1 打折，2 赠送 ）。*/
	private Integer privilegeType;
	
	/*** 是否参与结算(1 参与，0 不参与)。*/
	private Integer isSettle;
	
	/*** 打折，优惠，需要填写。*/
	private double privilegeValue;
	
	/** 所属店铺。*/
	private Long storeId;
	
	/*** 结算比例，以小数形式表示。*/
	private Double settleRatio;
	
	/*** 结算比例，以小数形式表示。*/
	private String memo;
	
	/*** 结算比例，以小数形式表示。*/
	private Date createdTime;
}
