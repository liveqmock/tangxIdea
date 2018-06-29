package com.topaiebiz.goods.brand.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description 商品品牌dto。
 * <p>
 * <p>Author Hedda
 * <p>
 * <p>Date 2017年9月23日 下午7:57:41
 * <p>
 * <p>Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * <p>Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class BrandDto implements Serializable {

	/**
	 * 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。
	 */
	private Long id;

	/**
	 * 品牌名称。
	 */
	@NotNull(message = "{validation.brand.name}")
	private String name;

	/**
	 * 英文名称。
	 */
	@NotNull(message = "{validation.brand.englishName}")
	private String englishName;

	/**
	 * 品牌首字母。
	 */
	@NotNull(message = "{validation.brand.brandInitials}")
	private String brandInitials;

	/**
	 * 品牌网址。
	 */
	private String brandWebsite;

	/**
	 * 品牌图片(Logo)。
	 */
	@NotNull(message = "{validation.brand.brandImage}")
	private String brandImage;

	/**
	 * 是否拥有类目(1有，0没有)
	 */
	private Integer isOwn = 0;

	/**
	 * 备注。用于备注其他信息。
	 */
	private String memo;
}
