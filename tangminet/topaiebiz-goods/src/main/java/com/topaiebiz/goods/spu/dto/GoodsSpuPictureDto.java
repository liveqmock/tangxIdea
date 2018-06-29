
package com.topaiebiz.goods.spu.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Description 商品SPU图片。
 * 
 * Author Hedda
 * 
 * Date 2017年8月23日 下午5:16:07
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class GoodsSpuPictureDto implements Serializable{

	/** 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。 */
	private Long id;
	
	/** 商品SPU。 */
	private Long spuId;

	/** 图片名称。 */
	private String name;

	/** 图片类型。 */
	private Integer type;

	/** 是否为主图。 */
	private Integer isMain;

	/** 描述。 */
	private String description;

}
