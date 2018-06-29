package com.topaiebiz.goods.sku.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Description 商品销售属性key。  
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
public class GoodsSkuSaleKeyDto implements Serializable{

	/** 商品销售属性的key。*/
	private String keyName;


}
