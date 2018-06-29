package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 
 * Description： 营销活动商品表
 * 
 * 
 * Author Joe
 * 
 * Date 2017年9月22日 上午11:06:29
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_pro_promotion_goods")
@Data
public class PromotionGoodsEntity extends BaseBizEntity<Long> {

	private static final long serialVersionUID = -6899335865303603472L;

	/**
	 * 营销活动
	 */
	private Long promotionId;

	/**
	 * 所属店铺
	 */
	private Long storeId;

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
	 * 锁定数量
	 */
	private Integer lockedNum;

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
	private Long giveawayGoods;

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
	 * 商品编码
	 */
	private String itemCode;

	/**
	 * 是否为发布进行中老数据 0-不是  1-是
	 */
	private Byte isReleaseData;


	public void clearInit() {
		this.setCreatedTime(null);
		this.setVersion((Long) null);
	}

}
