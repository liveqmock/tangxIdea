package com.topaiebiz.giftcard.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 处理老系统退款
 */
@TableName("t_giftcard_refund_log")
@Data
public class GiftcardRefundLog implements Serializable{

	/**
	 * id
	 * 主键
	 */
	private Long id;
	/**
	 * 关联卡单元id
	 */
	private Long unitId;

	/**
	 * card_no
	 * 卡号
	 */
	private String cardNo;

	/**
	 * store_id
	 * 店铺id
	 */
	private Long storeId;

	/**
	 * store_name
	 * 店铺名称
	 */
	private String storeName;

	/**
	 * member_id
	 * 消费者id
	 */
	private Long memberId;

	/**
	 * member_name
	 * 消费者
	 */
	private String memberName;

	/**
	 * goods_id
	 * 对应支付的商品id
	 */
	private Long goodsId;

	/**
	 * goods_name
	 * 对应支付的商品名称
	 */
	private String goodsName;

	/**
	 * amount
	 * 变动的金额，可以正负
	 */
	private BigDecimal amount;

	/**
	 * balance
	 * 当前卡余额
	 */
	private BigDecimal balance;

	/**
	 * log_type
	 * 日志类型：1-消费，2-退款，3-绑定，4-冻结，5-解冻，6-续期
	 */
	private Integer logType;

	/**
	 * remark
	 * 操作备注
	 */
	private String remark;

	private String orderSn;

	private String paySn;

	/**
	 * created_time
	 * 创建时间
	 */
	private Date createdTime;

}
