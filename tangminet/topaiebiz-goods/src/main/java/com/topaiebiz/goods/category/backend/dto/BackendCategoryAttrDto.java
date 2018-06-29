package com.topaiebiz.goods.category.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description 商品后台类目属性dto
 * 
 * Author Hedda
 * 
 * Date 2017年9月25日 下午8:29:27
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
@Data
public class BackendCategoryAttrDto implements Serializable{

	/** 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。 */
	private Long id;
	
	/** 所属类目。*/
	@NotNull(message = "{validation.backendCategoryAttr.belongCategory}")
	private Long belongCategory;
	
	/** 是否为商家填的属性。（1为是，null为不是）*/
	private Long storeCustom;

	/** 类目属性名字。 */
	@NotNull(message = "{validation.backendCategoryAttr.name}")
	private String name;

	/** 属性类型。(1.文本2.日期3.数字)。 */
	private Integer type;

	/** 默认单位。 */
	private String defaultUnit;

	/** 是否为销售属性 (1是，0不是)。 */
	private Integer isSale;

	/** 是否为必填项 (1是，0不是)。 */
	private Integer isMust;

	/** 是否可以自定义 (1是，0不是)。 */
	private Integer isCustom;

	/** 属性值集合，用逗号隔开。 */
	private String valueList;
	
	/** 排序号。*/
	private Integer sortNo;

}
