package com.topaiebiz.goods.sku.dto;

import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.goods.category.backend.dto.BackendCategoryDto;
import com.topaiebiz.goods.category.frontend.dto.FrontendCategoryDto;
import com.topaiebiz.goods.comment.dto.GoodsSkuCommentDto;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Description 商品基本信息表，存储商品的信息。
 *
 * <p>Author Hedda
 *
 * <p>Date 2017年8月23日 下午5:23:11
 *
 * <p>Copyright Cognieon technology group co.LTD. All rights reserved.
 *
 * <p>Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class ItemDto extends PagePO implements Comparable<ItemDto>, Serializable {

	/**
	 * 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。
	 */
	private Long id;

	private List<Long> ids;

	/**
	 * 活动ID
	 */
	private Long promotionId;

	/**
	 * 唯一编码 (本字段是从业务角度考虑的，相当于全局的唯一业务主键)。
	 */
	private String itemCode;

	/**
	 * 商品名称(标题显示的名称)。
	 */
	@NotNull(message = "{validation.item.name}")
	private String name;

	/** 佣金比例。小数形式。平台收取商家的佣金。 */
	private BigDecimal brokerageRatio;

	/** 积分比例。小数形式。 */
	private BigDecimal integralRatio;

	/** 引用SPU商品。 */
	private Long spuId;

	/**
	 * 市场价。
	 */
	@NotNull(message = "{validation.item.marketPrice}")
	private BigDecimal marketPrice;

	/**
	 * 默认价格（页面刚打开的价格）。
	 */
	@NotNull(message = "{validation.item.defaultPrice}")
	private BigDecimal defaultPrice;

	/**
	 * 具体一件商品sku价格。
	 */
	private BigDecimal skuPrice;

	/**
	 * 销售属性。
	 */
	private String saleFieldValue;

	/** 基本属性。 */
	private String baseFieldValue;

	/**
	 * 价格区间1。
	 */
	private String priceRangeLeft;

	/**
	 * 价格区间2。
	 */
	private String priceRangeRigth;

	/**
	 * 所属店铺。
	 */
	private Long belongStore;

	/**
	 * 所属店铺集合。
	 */
	private List<Long> storeIds;

	/** 店铺名称。 */
	private String storeName;

	/** 所属品牌。 */
	private Long belongBrand;

	/** 类目名 */
	private String brandName;

	/**
	 * 适用年龄段。
	 */
	private Long ageId;

	/**
	 * 年龄段。
	 */
	private String ageGroup;

	/** 所属后台类目。 */
	@NotNull(message = "{validation.item.belongCategory}")
	private Long belongCategory;

	/** 所属前台类目。 */
	private Long frontendCategory;

	/** 所属类目属性id。 */
	private Long categoryAttrId;

	/** 所属后台类目。 */
	private List<Long> belongCategoryIds;

	/**
	 * 图片所属类目属性。
	 */
	private Long imageField;

	/**
	 * 类目名
	 */
	private String categoryName;

	/**
	 * 库存数量。
	 */
	private Long stockNumber;

	/**
	 * 累计销量。
	 */
	private Long salesVolume;

	/**
	 * 购买商品数量。
	 */
	private Long buyNumber;

	/**
	 * 商品状态（1 新录入 2 已上架 3 下架 4 违规下架）。
	 */
	private Integer status;

	/**
	 * 商品是否冻结（0为正常，1为冻结）
	 */
	private Integer frozenFlag;

	/**
	 * 选用物流模版。
	 */
	@NotNull(message = "{validation.item.logisticsId}")
	private Long logisticsId;

	/** 物流模版的体积、重量（体积默认为m3，重量默认为kg）。 */
	private Double weightBulk;

	private String freightTemplateName;

	/**
	 * 商品详情。
	 */
	@NotNull(message = "{validation.item.description}")
	private String description;

	/**
	 * 创建时间。默认取值为系统的当前时间。
	 */
	private Date createdTime;

	/**
	 * 时间。
	 */
	private String createdTimes;

	/** 左时间。 */
	private String beganTime;

	/** 右时间。 */
	private String endTime;

	/** 图片名称。 */
	private String pictureName;

	/**
	 * 根据销售量进行排序。
	 */
	private Integer sales;

	/**
	 * 根据价格进行排序。
	 */
	private Integer price;

	/** 积分支付比例。 */
	private BigDecimal scoreRate;

	/** 会员id。 */
	private Long memberId;

	/**
	 * 具体skuId。
	 */
	private Long skuId;

	/**
	 * 商家id。
	 */
	private Long merchantId;

	/**
	 * 自定义类目属性id。
	 */
	private List<Long> attrIds;

	/**
	 * 商品图片集合。
	 */
	private List<ItemPictureDto> itemPictureDtos;

	/**
	 * 商品属性集合。
	 */
	private List<GoodsSkuDto> goodsSkuDtos;

	/**
	 * 商品后台类目集合。
	 */
	private List<BackendCategoryDto> backendCategoryDtos;

	/**
	 * 商品前台第三级类目。
	 */
	private List<FrontendCategoryDto> frontendCategoryDtos;

	/**
	 * 评价列表。
	 */
	private List<GoodsSkuCommentDto> goodsSkuCommentDtos;

	public int compareTo(ItemDto i) {
		if (i.salesVolume != null) {
			if (this.salesVolume > i.salesVolume) {
				return 1;
			} else if (this.salesVolume < i.salesVolume) {
				return -1;
			}
		}
		return 0;
	}
}
