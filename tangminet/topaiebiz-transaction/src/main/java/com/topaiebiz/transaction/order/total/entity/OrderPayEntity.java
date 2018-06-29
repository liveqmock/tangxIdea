package com.topaiebiz.transaction.order.total.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * Description 支付订单
 *
 * @author zhushuyong
 * @date 2017年8月31日 上午11:31:46
 */
@Data
@TableName("t_tsa_order_pay")
public class OrderPayEntity extends BaseBizEntity<Long> {

	/** 序列化版本号 */
	@TableField(exist = false)
	private static final long serialVersionUID = -7553882818229374595L;

	/** 会员id */
	private Long memberId;

	/**
	 * 实际支付金额
	 */
	private BigDecimal payPrice;

	/** 支付状态(1失败 2成功) */
	private Integer payState;

	/** 1微信 2支付宝 3等 可以写到数据字典里 */
	private String payType;

	/** 支付时间 */
	private Date payTime;

	/** 外部交易号 */
	private String outerPaySn;

	/** 美礼卡支付金额 */
	private BigDecimal cardPrice;
	/** 积分支付金额 */
	private BigDecimal scorePrice;
	/** 使用积分数量 */
	private Long scoreNum;
	/**余额支付金额 */
	private BigDecimal balance;

	/**
	 * 订单备注
	 */
	private String memo;

	public BigDecimal getThirdAmount() {
		return payPrice.subtract(cardPrice).subtract(scorePrice).subtract(balance);
	}
}