package com.topaiebiz.goods.category.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description 商品后台类目dto 。
 * 
 * Author Hedda
 * 
 * Date 2017年9月24日 下午3:10:58
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
@Data
public class BackendCategoryDto implements Serializable{

	/** 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。 */
	private Long id;

	/** 类目名称。 */
	@NotNull(message = "{validation.backendCategory.name}")
	private String name;

	/** 类目等级 (1 一级 2 二级 3 三级)。 */
	@NotNull(message = "{validation.backendCategory.level}")
	private Integer level;

	/** 父类目。 */
	private Long parentId;
	
	/** 类目id。*/
	private Long categoryId;

	/** 绑定前后台类目id。*/
	private Long frontBackId;

	/** 类目名称。*/
	private String categoryName;

	/** 类目个数 。*/
	private Integer count;

	/** 店铺id。*/
	private Long storeId;

	/** 商家id。*/
	private Long merchantId;

	/**
	 * 商家类目审核是否通过，1为审核通过。0为待审核,2 为审核不通过。
	 */
	private Integer status;

}
