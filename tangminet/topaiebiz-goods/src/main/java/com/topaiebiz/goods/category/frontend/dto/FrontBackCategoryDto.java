
package com.topaiebiz.goods.category.frontend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description 前后台类目对照dto，存储前后台类目的对照规则。
 * 
 * Author Hedda 
 *    
 * Date 2017年8月23日 下午5:02:12 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class FrontBackCategoryDto implements Serializable{

	/** 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。 */
	private Long id;
	
	/** 前台类目ID。*/
	@NotNull(message = "{validation.frontBackCategory.frontId}")
	private Long frontId;
	
	/** 后台类目ID。*/
	@NotNull(message = "{validation.frontBackCategory.backId}")
	private Long backId;

}
