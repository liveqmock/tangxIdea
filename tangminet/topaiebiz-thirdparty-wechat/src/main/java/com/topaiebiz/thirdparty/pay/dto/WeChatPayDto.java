package com.topaiebiz.thirdparty.pay.dto;

import lombok.Data;

/**
 * Description 微信支付业务参数实体类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017/11/13 14:02
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class WeChatPayDto {

	/**
	 * 用户的openId
	 */
	private String openId;

	/**
	 * 商品描述（128位，非空）
	 */
	private String body;

	/**
	 * 商品详情（6000位，可空）
	 */
	private String detail;

	/**
	 * 附加数据（127位，可空）
	 */
	private String attach;

	/**
	 * 商户订单号（32位，非空）
	 */
	private String out_trade_no;

	/**
	 * 标价金额（88位，分为单位，非空）
	 */
	private String total_fee;

	/**
	 * 终端IP（16位，非空）
	 */
	private String spbill_create_ip;
}
