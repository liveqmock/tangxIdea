package com.topaiebiz.goods.sku.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Description 商品属性表，一条数据对应一个SKU。  
 * 
 * Author Hedda 
 *    
 * Date 2017年8月23日 下午5:24:37 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class GoodsSkuSaleValueDto implements Serializable{

	/** 销售属性值。*/
	private String valueName;
	
	/** 销售属性图片。*/
	private String imageurl;
	
}
