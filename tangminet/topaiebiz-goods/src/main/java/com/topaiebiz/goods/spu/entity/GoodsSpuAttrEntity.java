
package com.topaiebiz.goods.spu.entity;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Description 商品SPU属性 。
 * 
 * Author Hedda 
 *    
 * Date 2017年8月23日 下午5:10:36 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_goo_goods_spu_attr")
@Data
public class GoodsSpuAttrEntity extends BaseBizEntity<Long>{

	/** 序列化版本号。 */
	@TableField(exist = false)
	private static final long serialVersionUID = -4057401632721956251L;
	
	/** SPU商品。*/
	private Long spuId;
	
	/** 属性集合以键值对形式存放。key：value，key1：value1。*/
	private String baseFieldValue;
	
	/** 销售属性集合以键值对形式存放key:value,key1:value1。*/
	private String saleFieldValue;
	
	/** SKU商品图片。*/
	private String saleImage;
	
	/** 市场价。*/
	private BigDecimal marketPrice;
	
	/** 销售价格  (最多两位小数)。*/
	private BigDecimal price;
	
	/** 商品条形码。*/
	private String barCode;
	
	/** 备注。用于备注其他信息。 */
	private String memo;

}
