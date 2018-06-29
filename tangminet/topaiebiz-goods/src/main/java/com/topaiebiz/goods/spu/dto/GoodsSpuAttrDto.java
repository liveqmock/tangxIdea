    
package com.topaiebiz.goods.spu.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Description 商品SPU属性dto 。
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
public class GoodsSpuAttrDto implements Serializable{

	/** 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。 */
	private Long id;
	
	/** SPU商品。*/
	private Long spuId;
	
	/** 属性集合以键值对形式存放。key：value，key1：value1。*/
	private String baseFieldValue;
	
	/** 销售属性集合以键值对形式存放key:value,key1:value1。*/
	private String saleFieldValue;
	
	/** SKU商品图片。*/
	private String saleImage;
	
	/** 销售价格  (最多两位小数)。*/
	private BigDecimal price;
	
	/** 商品条形码。*/
	private String barCode;
	
	private List<GoodsSpuAttrBaseDto> goodsSpuAttrBaseDtos = null;
	
	private List<GoodsSpuAttrSaleDto> goodsSpuAttrSaleDtos = null;
	
	private List<GoodsSpuAttrSaleValueDto> goodsSpuAttrSaleValueDto = null;
	
	private List<GoodsSpuAttrSaleKeyDto> goodsSpuAttrSaleKeyDto = null;
	
	private List<GoodsSpuAttrSaleKeyAndValueDto> goodsSpuAttrSaleKeyAndValueDtos = null;
	
}
