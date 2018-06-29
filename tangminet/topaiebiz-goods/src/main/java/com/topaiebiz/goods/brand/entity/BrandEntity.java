package com.topaiebiz.goods.brand.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * Description 商品品牌。
 *
 * <p>Author Hedda
 *
 * <p>Date 2017年8月23日 下午4:24:44
 *
 * <p>Copyright Cognieon technology group co.LTD. All rights reserved.
 *
 * <p>Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_goo_brand")
@Data
public class BrandEntity extends BaseBizEntity<Long> {

	/**
	 * 序列化版本号。
	 */
	@TableField(exist = false)
	private static final long serialVersionUID = 5304153063104117657L;

	/**
	 * 唯一编码 （本字段是从业务角度考虑的，相当于全局的唯一业务主键）。
	 */
	private String brandCode;

	/**
	 * 品牌名称。
	 */
	private String name;

	/**
	 * 品牌展示排序。
	 */
	private Integer sortNo;

	/**
	 * 英文名称。
	 */
	private String englishName;

	/**
	 * 品牌首字母。
	 */
	private String brandInitials;

	/**
	 * 品牌网址。
	 */
	private String brandWebsite;

	/**
	 * 品牌图片。
	 */
	private String brandImage;

	/**
	 * 品牌故事。
	 */
	private String brandStory;

	/**
	 * 所属店铺ID。
	 */
	private Long storeId;

	/**
	 * 审核状态(0待审核，1审核通过，2审核不通)。
	 */
	private Integer status;

	/**
	 * 备注。用于备注其他信息。
	 */
	private String memo;

	public void clearInt() {
		this.setVersion(null);
		this.setDeleteFlag(null);
		this.setCreatedTime(null);
  }
}
