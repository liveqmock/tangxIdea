package com.topaiebiz.merchant.dto.grade;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * Description: 商家等级管理dto
 * 
 * Author : Anthony
 * 
 * Date :2017年9月28日 下午7:42:58
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerchantGradeDTO implements Serializable {

	/** 商家等级的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。 */
	private Long id;

	/** 商家等级名称 */
	@NotNull(message = "{validation.merchantGrade.name}")
	private String name;

	/** 商家等级的小图标 */
	@NotNull(message = "{validation.merchantGrade.smallIcon}")
	private String smallIcon;

	/** 商家等级的大图标 */
	@NotNull(message = "{validation.merchantGrade.bigIcon}")
	private String bigIcon;

	/** 所需积分下限，达到该值就是该等级 */
	@NotNull(message = "{validation.merchantGrade.integralValue}")
	private Long integralValue;

	/** 会员等级说明 */
	private String description;

	/** 创建人编号。取值为创建人的全局唯一主键标识符。 */
	private Long creatorId;

	/** 备注 */
	private String memo;
}
