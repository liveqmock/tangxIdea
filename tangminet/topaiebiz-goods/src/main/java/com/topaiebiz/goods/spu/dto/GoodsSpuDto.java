
package com.topaiebiz.goods.spu.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryDto;
import lombok.Data;

/**
 * Description 商品SPU信息。
 * 
 * Author Hedda
 * 
 * Date 2017年8月23日 下午5:12:53
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class     GoodsSpuDto extends PagePO implements Serializable{
	
	/** 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。 */
	private Long id;

	/** 唯一编码 (本字段是从业务角度考虑的，相当于全局的唯一业务主键)。 */
	@NotNull(message = "{validation.goodsspu.spuCode}")
	private String spuCode;

	/** 商品名称(标题显示的名称)。 */
	@NotNull(message = "{validation.goodsspu.name}")
	private String name;

	/** 商品详情。 */
	@NotNull(message = "{validation.goodsspu.description}")
	private String description;

	/** 默认价格（页面刚打开的价格）。 */
	@NotNull(message = "{validation.goodsspu.defaultPrice}")
	private BigDecimal defaultPrice;

	/** 所属店铺。 */
	private Long belongStore;

	/** 所属品牌。 */
	private Long belongBrand;
	
	/** 品牌名称。*/
	private String brandName;
	
	/** 适用年龄段。*/
	private Long ageId;

	/** 所属类目。 */
	@NotNull(message = "{validation.goodsspu.belongCategory}")
	private Long belongCategory;
	
	/** 类目名称。*/
	private String categoryName;

	/** 创建时间。默认取值为系统的当前时间。 */
	private Date createdTime = new Date();

	private String createdTimes;
	
	/** 图片所属类目属性。*/
	private Long imageField;

	/** 自定义类目属性id。*/
	private List<Long> attrIds;

	/** 商品spu属性集合。*/
	private List<GoodsSpuAttrDto> goodsSpuAttrDtos;
	
	/** 商品spu图片集合。*/
	private List<GoodsSpuPictureDto> goodsSpuPictureDtos;
	
	/**商品后台类目。*/
	private List<BackendCategoryDto> backendCategoryDtos;

}
