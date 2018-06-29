package com.topaiebiz.transaction.order.merchant.dto;

import lombok.Data;

/**
 * Description 订单收货地址DTO
 * 
 * Author Aaron.Xue
 * 
 * Date 2017年10月14日 上午9:41:23
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderAddressDto {

	/** 收货地址ID */
	private Long id;

	/** 订单编号 */
	private Long orderId;

	/** 收货人姓名 */
	private String name;

	/** 地址区域 */
	private Long districtId;

	/** 地区区域名称 */
	private String districtName;

	/** 收货人地址 */
	private String address;

	/** 收货人邮编 */
	private String zipCode;

	/** 收货人手机号 */
	private String telephone;

	/** 收货人座机号 */
	private String landline;

	/** 紧急联系人，备用电话 */
	private String otherTelephone;
}