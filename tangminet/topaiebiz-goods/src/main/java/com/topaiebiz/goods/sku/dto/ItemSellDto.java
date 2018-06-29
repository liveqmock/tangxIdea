package com.topaiebiz.goods.sku.dto;


import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description 商品销售情况  
 * 
 * Author Hedda 
 *    
 * Date 2017年11月1日 下午8:24:31 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
@Data
public class ItemSellDto extends PagePO implements Serializable{

	/** 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。 */
	private Long id;
	
	/** 商品名称(标题显示的名称)。 */
	private String name;
	
	/** 付款人数。*/
	private Integer  paymentPeople;
	
	/** 销售数量。*/
	private Integer saleNumber;
	
	/** 销售金额。*/
	private BigDecimal salesAmount;
	
	/** 时间。*/
	private String createdTimes;
	
	/** 时间查询。*/
	private Integer days;
	
	/** 店铺id。*/
	private Long storeId;
	
	/** 数量。*/
	private Integer count;
	
	/** 第一级类目id。*/
	private Long categoryId;
	
	/** 第一级类目的名称。*/
	private String categoryName;
	
	/** 第三级类目id。*/
	private Long categoryIdt;

}
