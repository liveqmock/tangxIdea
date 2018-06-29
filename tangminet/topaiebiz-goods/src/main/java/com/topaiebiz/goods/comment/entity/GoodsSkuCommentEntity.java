package com.topaiebiz.goods.comment.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * Description 商品评价表，存储商品的评价信息 。
 *
 * <p>Author Hedda
 *
 * <p>Date 2017年8月23日 下午5:24:55
 *
 * <p>Copyright Cognieon technology group co.LTD. All rights reserved.
 *
 * <p>Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_goo_goods_sku_comment")
@Data
public class GoodsSkuCommentEntity extends BaseBizEntity<Long> {

	/**
	 * 序列化版本号。
	 */
	@TableField(exist = false)
	private static final long serialVersionUID = -7561251338983651707L;

	/**
	 * 商品主键ID itemId。
	 */
	private Long itemId;

	/**
	 * 商品属性表中的主键ID。
	 */
	private Long skuId;

	/**
	 * 销售属性集合以键值对形式存放 (key:value,key1:value1)。
	 */
	private String saleFieldValue;

	/**
	 * 评价会员ID（如果此项为空 ，则为匿名评价）。
	 */
	private Long memberId;

	/**
	 * 会员名称。
	 */
	private String userName;

	/**
	 * 评价类型（1 好评 2 中评 3 差评）。
	 */
	private Integer type;

	/**
	 * 商品评价星级（1，2，3，4，5 颗心）。
	 */
	private Integer goodsLevel;

	/**
	 * 物流服务星级（1，2，3，4，5 颗心）。
	 */
	private Integer logisticsLevel;

	/** 服务态度星级（1，2，3，4，5 颗心）。 */
	private String serveLevel;

	/** 商品好评度（1，2，3，4，5 颗心）。 */
	private Integer goodsReputation;

	/** 是否包含图片(1 有图 0 无图）。 */
	private Integer isImage;

	/**
	 * 评价内容。
	 */
	private String description;

	/**
	 * 相关订单号。
	 */
	private Long orderId;

	/** 回评内容。 */
	private String replyText;

	/** 回评时间。 */
	private Date replyTime;

	/**
	 * 追评内容。
	 */
	private String appendText;

	/** 追评时间。 */
	private Date appendTime;

	/**
	 * 追评回评内容。
	 */
	private String replyAppendText;

	/** 追评回评时间。 */
	private Date replyAppendTime;

	/**
	 * 备注。用于备注其他信息。
	 */
	private String memo;
}
