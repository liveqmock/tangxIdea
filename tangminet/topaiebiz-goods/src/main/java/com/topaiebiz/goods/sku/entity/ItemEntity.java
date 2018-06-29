package com.topaiebiz.goods.sku.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;

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
@TableName("t_goo_item")
@Data
public class ItemEntity extends BaseBizEntity<Long> {

	/**
	 * 序列化版本号。
	 */
	@TableField(exist = false)
	private static final long serialVersionUID = 8956575417742055766L;

	/**
	 * 唯一编码 (本字段是从业务角度考虑的，相当于全局的唯一业务主键)。
	 */
	private String itemCode;

	/** 商品名称(标题显示的名称)。 */
	private String name;

	/**
	 * 佣金比例。小数形式。平台收取商家的佣金。
	 */
	private Double brokerageRatio;

	/**
	 * 积分比例。小数形式。
	 */
	private BigDecimal integralRatio;

	/**
	 * 销售数量。
	 */
	private Long salesVolume;

	/**
	 * 商品主图。
	 */
	private String pictureName;

	/**
	 * 引用SPU商品。
	 */
	private Long spuId;

	/**
	 * 市场价。
	 */
	private BigDecimal marketPrice;

	/** 默认价格（页面刚打开的价格）。 */
	private BigDecimal defaultPrice;

	/** sku最低价格 **/
	private BigDecimal minPrice;

	/**
	 * 所属店铺。
	 */
	private Long belongStore;

	/**
	 * 所属品牌。
	 */
	private Long belongBrand;

	/**
	 * 适用年龄段。
	 */
	private Long ageId;

	/**
	 * 所属类目。
	 */
	private Long belongCategory;

	/**
	 * 图片所属类目属性。
	 */
	private Long imageField;

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
	private Long logisticsId;

	/** 物流模版的体积、重量（体积默认为m3，重量默认为kg）。 */
	private Double weightBulk;

	/**
	 * 商品描述。
	 */
	private String description;

	/**
	 * 备注。用于备注其他信息。
	 */
	private String memo;

	/**
	 * 评价条数。
	 */
	private Integer commentCount;

	/**
	 * 税率。
	 */
	private BigDecimal taxRate;

	public void clearInit() {
		this.setVersion(null);
		this.setCreatedTime(null);
		this.setDeleteFlag(null);
  }
}
