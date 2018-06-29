package com.topaiebiz.goods.sku.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Description 记录商品上下架记录的表，存储商品上下级的记录信息。
 * 
 * Author Hedda 
 *    
 * Date 2017年8月23日 下午5:24:17 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class ItemUpdownLogDto implements Serializable{

	/** 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。 */
	private Long id;
	
	/** 商品ID。*/
	private Long itemId;
	
	/** 改变后的状态（1 新录入 2 已上架 3 下架 4 违规下架）暂定。*/
	private Integer status;
	
	/** 说明。*/
	private String description;

}
