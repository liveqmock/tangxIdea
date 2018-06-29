package com.topaiebiz.goods.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description 商品评价图片表，存储评价的图片。
 * 
 * Author Hedda
 * 
 * Date 2017年8月23日 下午5:25:18
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class GoodsSkuCommentPictureDto implements Serializable{

	/** 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。 */
	private Long id;

	/** 评价ID。 */
	@NotNull(message = "{validation.goodsSkuCommentPicture.commentId}")
	private Long commentId;

	/** 图片描述。 */
	private String description;

	/** 评价上传的图片。 */
	@NotNull(message = "{validation.goodsSkuCommentPicture.image}")
	private String image;

}
