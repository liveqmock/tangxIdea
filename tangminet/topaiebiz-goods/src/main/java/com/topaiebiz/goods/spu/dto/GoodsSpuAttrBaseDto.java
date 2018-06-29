    
package com.topaiebiz.goods.spu.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * Description 商品SPU基本属性dto 。
 * 
 * Author Hedda 
 *    
 * Date 2017年8月23日 下午5:10:36 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class GoodsSpuAttrBaseDto implements Serializable{
	
	/** 商品基本属性key。*/
	private String id;
	
	/** 商品基本属性value。*/
	private String baseName;

}
