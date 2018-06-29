package com.topaiebiz.merchant.enter.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Description: 商家账户信息Dto
 * 
 * Author : Anthony
 * 
 * Date :2017年10月10日 上午9:20:17
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerchantAccountDto  implements Serializable {

	/** 商家入驻信息的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。 */
	private Long id;

	/** 所属商家 */
	private Long merchantId;

	/** 银行开户名 */
	@NotNull(message = "${validation.merchantaccount.accountName}")
	private String accountName;

	/** 公司银行帐号 */
	@NotNull(message = "${validation.merchantaccount.account}")
	private String account;

	/** 开户银行支行名称 */
	@NotNull(message = "${validation.merchantaccount.bankName}")
	private String bankName;

	/** 支行银联号 */
	@NotNull(message = "${validation.merchantaccount.bankNum}")
	private String bankNum;

	/** 开户行所在区域 */
	@NotNull(message = "${validation.merchantaccount.accountDistrictId}")
	private Long accountDistrictId;

	/** 是否为结算账号 */
	private Integer isSettle;

	/** 开户银行许可证电子版 */
	@NotNull(message = "${validation.merchantaccount.electronicImage}")
	private String electronicImage;

	/** 预留电话 */
	private String telephone;

}
