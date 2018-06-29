package com.topaiebiz.transaction.order.merchant.dto;

import lombok.Data;

/**
 * Description 店铺订单发票DTO
 * 
 * Author Aaron.Xue
 * 
 * Date 2017年10月11日 下午3:37:31
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class OrderInvoiceDto {

	/** 订单id */
	private Long orderId;

	/** 店铺id */
	private Long storeId;

	/** 发票类型。 1 普通 2电子 3增值税 */
	private Short invoiceType;

	/** 发票抬头。 */
	private String title;

	/** 发票内容。 */
	private String text;

	/** 纳税人识别号。 */
	private String taxpayerNo;

	/** 增值税发票专用。1 订单完成后开票 */
	private Short modeType;

	/** 增值税发票专用。 */
	private String name;

	/** 开票金额。 */
	private Double sum;

	/** 地址电话。 */
	private String addressTel;

	/** 开户行及账号。 */
	private String account;

	/** 状态。1 已开 2未开 */
	private Integer state;
}