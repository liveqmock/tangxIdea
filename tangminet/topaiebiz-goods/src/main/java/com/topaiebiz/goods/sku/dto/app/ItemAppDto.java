package com.topaiebiz.goods.sku.dto.app;

import com.topaiebiz.goods.comment.dto.GoodsSkuCommentDto;
import com.topaiebiz.goods.sku.dto.GoodsSkuDto;
import com.topaiebiz.goods.sku.dto.GoodsSkuSaleDto;
import com.topaiebiz.goods.sku.dto.GoodsSkuSaleKeyAndValueDto;
import com.topaiebiz.goods.sku.dto.ItemPictureDto;
import com.topaiebiz.promotion.dto.PromotionDTO;
import lombok.Data;

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
public class ItemAppDto implements Serializable {

	/**
	 * 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。
	 */
	private Long id;

	/**
	 * 商品名称(标题显示的名称)。
	 */
	private String name;

	/**
	 * 默认价格（页面刚打开的价格）。
	 */
	private BigDecimal defaultPrice;

	/** 市场价。 */
	private BigDecimal marketPrice;

	/**
	 * 所属店铺。
	 */
	private Long belongStore;

	/**
	 * 店铺名称。
	 */
	private String storeName;

	/**
	 * 店铺图片。
	 */
	private String storeImage;

	/**
	 * 累计销量。
	 */
	private Long salesVolume;

	/** 库存数量。 */
	private long stockNumber;

	/** 后台类目。 */
	private Long belongCategory;

	/**
	 * 图片所属类目属性。
	 */
	private Long imageField;

	/**
	 * 商品描述。
	 */
	private String description;

	/**
	 * 快递费。
	 */
	private BigDecimal firstPrice;

	/**
	 * 选用物流模版。
	 */
	private Long logisticsId;

	/** 是否为秒杀。1为秒杀 */
	private Integer isKill;

	/**
	 * 秒杀销量百分比
	 */
	private BigDecimal percent;

	/**
	 * 结束时间。
	 */
	private Date endTime;

	/**
	 * 好评，差评，中评
	 */
	private Integer type;

	/**
	 * 评价条数。
	 */
	private Integer commentCount;

	/**
	 * 好评度。
	 */
	private BigDecimal praise;

	/**
	 * 商品状态（1 新录入 2 已上架 3 下架 4 违规下架）。
	 */
	private Integer status;

	/**
	 * 商品是否冻结（0为正常，1为冻结）
	 */
	private Integer frozenFlag;

	/**
	 * 店铺状态 2冻结 0正常
	 */
	private Integer changeState;
	/**
	 * 海淘标识，true为是，false为否。
	 */
	private Boolean haitao;

	/**
	 * 积分比例。小数形式。
	 */
	private Double integralRatio;

	/**
	 * 商品图片集合。
	 */
	private List<ItemPictureDto> itemPictureDtos;

	/**
	 * 商品属性集合。
	 */
	private List<GoodsSkuDto> goodsSkuDtos;

	/**
	 * 评价列表。
	 */
	private List<GoodsSkuCommentDto> goodsSkuCommentDtos;

	/**
	 * 销售属性集合。key中的id和值
	 */
	private List<GoodsSkuSaleDto> goodsSkuSaleKeyDtos;

	/**
	 * 销售属性集合。key与value
	 */
	private List<GoodsSkuSaleDto> goodsSkuSaleDtos;

	/**
	 * 属性值，加图片。
	 */
	private GoodsSkuSaleKeyAndValueDto goodsSkuSaleKeyAndValueDto;

	/**
	 * 单品折扣和一口价。
	 */
	private List<PromotionDTO> singlePromotions;

	/**
	 * 包邮。
	 */
	private PromotionDTO pinkagePromotions;

	/**
	 * 满减。
	 */
	private List<PromotionDTO> moneyOffPromotions;

	/**
	 * 店铺优惠券。
	 */
	private List<PromotionDTO> couponPromotions;
}
