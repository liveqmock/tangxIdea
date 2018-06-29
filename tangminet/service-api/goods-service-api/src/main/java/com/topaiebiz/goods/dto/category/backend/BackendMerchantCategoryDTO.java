package com.topaiebiz.goods.dto.category.backend;

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
public class BackendMerchantCategoryDTO implements Serializable{
	private static final long serialVersionUID = -2391498353161363119L;

	/** 后台类目ID */
	private Long id;

	/** 所属商家。 */
	private Long merchantId;

	/** 所属店铺。 */
	private Long storeId;

	/** 平台类目ID。 */
	private Long categoryId;

	/** 类目名称。 */
	private String name;

	/** 类目描述。 */
	private String description;

	/** 类目等级 (1 一级 2 二级 3 三级)。 */
	private Integer level;

	/** 父类目。 */
	private Long parentId;

	/** 备注。用于备注其他信息。 */
	private String memo;

}
