package com.topaiebiz.giftcard.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 礼卡订单信息
 * </p>
 *
 * @author Jeff Chen123
 * @since 2018-01-25
 */
@TableName("t_giftcard_order")
@Data
public class GiftcardOrder implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 订单主键
	 */
	private Long id;

	/**
	 * UUID防止重复提交
	 */
	private String orderKey;
	/**
	 * 订单总金额
	 */
	private BigDecimal orderAmount;
	/**
	 * 支付总金额
	 */
	private BigDecimal payAmount;
	/**
	 * 支付通道：weixin-微信 alipay-支付宝
	 */
	private String payCode;
	/**
	 * 第三方交易号
	 */
	private String paySn;
	/**
	 * 支付时间
	 */
	private Date payTime;
	/**
	 * 订单状态（继承老系统）：0-已取消 10-未支付 20-已付款 30-已发货 40-已完成
	 */
	private Integer orderStatus;
	/**
	 * 购买者id
	 */
	private Long memberId;
	/**
	 * 购买者名称
	 */
	private String memberName;
	/**
	 * 用户手机号
	 */
	private String memberPhone;
	/**
	 * 创建时间
	 */
	private Date createdTime;

	/**
	 * 修改时间
	 */
	private Date modifiedTime;

	/**
	 * 0正常 1删除
	 */
	private Integer delFlag;

	/**
	 * 卡名称
	 */
	@TableField(exist = false)
	private String cardName;
	/**
	 * 面值
	 */
	@TableField(exist = false)
	private BigDecimal faceValue;

	/**
	 * 售价
	 */
	@TableField(exist = false)
	private BigDecimal salePrice;

	/**
	 * 卡数量
	 */
	@TableField(exist = false)
	private Integer cardNum;
	/**
	 * 封面
	 */
	@TableField(exist = false)
	private String cover;

	/**
	 * 卡号串
	 */
	@TableField(exist = false)
	private String cardNoList;
	/**
	 * 卡批次id
	 */
	@TableField(exist = false)
	private Long batchId;
}
