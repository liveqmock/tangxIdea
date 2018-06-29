package com.topaiebiz.goods.comment.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
@Data
public class GoodsSkuCommentDto extends PagePO implements Serializable {

	/**
	 * 全局唯一主键标识符 （本字段是业务无关性的，仅用于关联）。
	 */
	private Long id;

	/**
	 * 商品属性表中的主键ID。
	 */
	@NotNull(message = "{validation.goodsSkuComment.skuId}")
	private Long skuId;

	private Long itemId;

	/**
	 * 店铺名称。
	 */
	private String storeName;

	/**
	 * 所属店铺。
	 */
	private Long belongStore;

	private List<Long> storeIds;

	/**
	 * 商品名称
	 */
	private String skuName;

	/**
	 * 商品图片。
	 */
	private String pictureName;

	/**
	 * 评价会员ID（如果此项为空 ，则为匿名评价）。
	 */
	private Long memberId;

	/**
	 * 判断是否为匿名评价。
	 */
	private Integer member;

	/**
	 * 会员名称。
	 */
	private String memberName;

	/**
	 * 会员头像。
	 */
	private String smallIcon;

	/**
	 * 评价类型（1 好评 2 中评 3 差评）。
	 */
	@NotNull(message = "{validation.goodsSkuComment.code}")
	private Integer type;

	/**
	 * 商品评价星级（1，2，3，4，5 颗心）。
	 */
	private Integer goodsLevel;

	/**
	 * 物流服务星级（1，2，3，4，5 颗心）。
	 */
	private Integer logisticsLevel;

	/**
	 * 服务态度星级（1，2，3，4，5 颗心）。
	 */
	private String serveLevel;

	/** 商品好评度（1，2，3，4，5 颗心）。 */
	private Integer goodsReputation;

	/**
	 * 是否包含图片(1 有图 0 无图）。
	 */
	@NotNull(message = "{validation.goodsSkuComment.isImage}")
	private Integer isImage;

	/**
	 * 评价内容。
	 */
	@NotNull(message = "{validation.goodsSkuComment.description}")
	private String description;

	/**
	 * 销售属性集合以键值对形式存放 (key:value,key1:value1)。
	 */
	private String saleFieldValue;

	/** 相关订单号。 */
	private Long orderId;

	/**
	 * 订单详情id。
	 */
	private Long orderDetailId;

	/** 回评内容。 */
	private String replyText;

	/** 是否回复评价。 */
	private Integer noReply;

	/**
	 * 回评时间。
	 */
	private Date replyTime;

	/** 追评内容。 */
	private String appendText;

	/** 追评时间。 */
	private Date appendTime;

	/**
	 * 追评回评内容。
	 */
	private String replyAppendText;

	/**
	 * 追评回评时间。
	 */
	private Date replyAppendTime;

	/**
	 * 时间。
	 */
	private String createdTimes;

	/** 时间。 */
	private Date createdTime;

	/**
	 * 商品sku评价图片集合。
	 */
	private List<GoodsSkuCommentPictureDto> goodsSkuCommentPictureDtos;
}
