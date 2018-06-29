package com.topaiebiz.goods.category.backend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Description 商家可用后台类目
 * 
 * Author Hedda
 * 
 * Date 2017年11月8日 下午7:43:20
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
@Data
public class BackendMerchantCategoryDto implements Serializable{

	/** 后台类目ID */
	private Long id;

	/** 后台类目名称。*/
	private String name;

	/** 所属商家。 */
	private Long merchantId;

	/** 所属店铺。 */
	private Long storeId;

	/** 平台类目ID。 */
	private Long categoryId;

	/** 父类目。 */
	private Long parentId;

	/**
	 * 商家类目审核是否通过，1为审核通过。0为待审核。
	 */
	private Integer status;

	/** 备注。用于备注其他信息。 */
	private String memo;

}
