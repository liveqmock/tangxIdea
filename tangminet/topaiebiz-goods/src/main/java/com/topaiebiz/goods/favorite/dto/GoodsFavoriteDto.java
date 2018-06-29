package com.topaiebiz.goods.favorite.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description： 收藏夹dto
 *
 * Author hecaifeng
 *    
 * Date 2017年9月26日 下午3:16:44 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class GoodsFavoriteDto implements Serializable{
	
	private Long id;
	
	/** 会员id*/
	@NotNull(message = "{validation.goodsFavorite.add.member}")
	private Long memberId;
	
	/** 商品id*/
	@NotNull(message = "{validation.goodsFavorite.add.goodsId}")
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

	
	@NotNull
	private Long[] ids;
}
