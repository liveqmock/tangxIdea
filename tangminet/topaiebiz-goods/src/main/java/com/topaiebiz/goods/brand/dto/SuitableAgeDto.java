package com.topaiebiz.goods.brand.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * Description 商品适用年龄段 dto。 
 * 
 * Author Hedda 
 *    
 * Date 2017年10月6日 下午1:37:42 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
@Data
public class SuitableAgeDto implements Serializable{

	/** 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。 */
	private Long id;
	
	/** 年龄段。*/
	private String ageGroup;
	
	/** 图片。*/
	private String ageImage;
	
	/** 备注。*/
	private String memo;
	
}
