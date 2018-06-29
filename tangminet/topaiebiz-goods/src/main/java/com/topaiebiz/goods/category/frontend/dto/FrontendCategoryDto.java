
package com.topaiebiz.goods.category.frontend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description 前台类目dto 。
 * 
 * Author Hedda 
 *    
 * Date 2017年8月23日 下午5:08:21 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class FrontendCategoryDto implements Serializable{

	/** 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。 */
	private Long id;
	
	/** 所属店铺。*/
	private Long belongStore;
	
	/** 类目名称。*/
	@NotNull(message = "{validation.frontendCategory.name}")
	private String name;
	
	/** 类目描述。*/
	private String description;
	
	/** 类目等级。*/
	@NotNull(message = "{validation.frontendCategory.level}")
	private Integer level;
	
	/** 父类目。*/
	private Long parentId;
	
	/** 类目图片。*/
	private String image;

}
