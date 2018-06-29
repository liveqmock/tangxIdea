package com.topaiebiz.promotion.mgmt.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * Description 营销活动商品 DTO
 * 
 * 
 * Author Joe
 * 
 * Date 2017年10月9日 下午4:08:20
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class PromotionGoodsDto extends PagePO {

	/**
	 * id
	 */
	private Long id;

	/**
	 * 营销活动
	 */
	private Long promotionId;

	/**
	 * 所属店铺
	 */
	private Long storeId;

	/**
	 * 店铺名称
	 */
	private String storeName;

	/**
	 * 所属商品
	 */
	private Long itemId;

	/**
	 * 商品SKU
	 */
	private Long goodsSkuId;

	/**
	 * 原有库存
	 */
	private Integer repertoryNum;

	/**
	 * 活动数量
	 */
	private Integer promotionNum;

	/**
	 * 活动价格
	 */
	private BigDecimal promotionPrice;

	/**
	 * ID限购
	 */
	private Integer confineNum;

	/**
	 * 优惠类型
	 */
	private Integer discountType;

	/**
	 * 优惠值
	 */
	private BigDecimal discountValue;

	/**
	 * 优惠赠品
	 */
	private Long giveProduct;

	/**
	 * 平台补贴
	 */
	private Double platformPrice;

	/**
	 * 活动销量
	 */
	private Integer quantitySales;

	/**
	 * 状态
	 */
	private Integer state;

	/**
	 * 备注
	 */
	private String memo;

	/**
	 * 优惠金额
	 */
	private BigDecimal preferentialAmount;

	/**
	 * 商品原价
	 */
	private BigDecimal goodsPrice;

	/**
	 * 营销活动状态
	 */
	private Integer marketState;

	/**
	 * 商品图片
	 */
	private String saleImage;

	/**
	 * item编码
	 */
	private String itemCode;

	/**
	 * item名称
	 */
	private String goodsName;

	/**
	 * 营销活动结束时间
	 */
	private Date promotionEndTime;

	/**
	 * 买货比例
	 */
	private Double sellGoodsQuantity;

	/**
	 * 每件商品的销量
	 */
	private Long salesVolome;

	/**
	 * 商品所属活动名称
	 */
	private String promotionName;

	/**
	 * 商品所属活动级别
	 */
	private Integer gradeId;

	/**
	 * 商品所属活动类型
	 */
	private Integer typeId;

	/**
	 * 销售属性集合
	 */
	private String saleFieldValue;
	
	/**
	 * 所属活动类型
	 */
	private Integer promotionTypeId;

	/**
	 * 删除标识
	 */
	private byte deletedFlag;

}
