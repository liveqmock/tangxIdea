package com.topaiebiz.goods.favorite.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description 我的足迹dto
 * 
 * Author Hedda 
 *    
 * Date 2017年11月16日 下午4:54:37 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
@Data
public class GoodsFootprintDto extends PagePO implements Serializable{
	
	/** 全局唯一主键标识符。支持泛型，具体类型由传入的类型指定。 */
	private Long id;

	/** 会员id*/
	private Long memberId;
	
	/** 商品id*/
	private Long goodsId;

	/** 商品名称。*/
	private String name;

	/** 商品图片。*/
	private  String pictureName;

	/** 市场价。*/
	private BigDecimal marketPrice;

	/** 默认价格（页面刚打开的价格）。 */
	private BigDecimal defaultPrice;

	/** 累计销量。 */
	private Long salesVolome;

	/** 时间。*/
	private String createdTimes;

	/** 时间。*/
	private java.util.Date createdTime;


	
}
