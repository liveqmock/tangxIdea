package com.topaiebiz.goods.spu.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Description 商品spu销售属性dto。
 * 
 * Author Hedda 
 *    
 * Date 2017年10月19日 上午11:28:55 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
@Data
public class GoodsSpuAttrSaleKeyAndValueDto implements Serializable{

	/** 销售属性的键。*/
	private String saleId;
	
	/** 是非显示sku图片。*/
	private Long imageField;
	
	/** 销售属性的值。*/
	private List<GoodsSpuAttrSaleValueDto> goodsSpuAttrSaleValueDtos;

}
	