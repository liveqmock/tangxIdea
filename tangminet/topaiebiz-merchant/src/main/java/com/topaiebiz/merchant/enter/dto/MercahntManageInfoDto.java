package com.topaiebiz.merchant.enter.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Description: 经营信息(营业执照信息、银行账户信息)信息dto
 * 
 * Author : Anthony
 * 
 * Date :2017年10月18日 下午7:31:44
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MercahntManageInfoDto  implements Serializable {

	/** 商家入驻信息的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。 */
	private Long id;

	/** 营业执照号 */
	@NotNull(message = "{validation.qualification.licenseNo}")
	private String licenseNo;

	/** 营业执照电子版 */
	@NotNull(message = "{validation.qualification.licenseImage}")
	private String licenseImage;

	/** 营业执照号所在地 */
	@NotNull(message = "{validation.qualification.licenseLocation}")
	private String licenseLocation;
	
	/** 营业执照号所在地 */
	@NotNull(message = "{validation.qualification.licenseLocation}")
	private Long licenseRegionId;

	/** 营业执照有效期起始 */
	@NotNull(message = "{validation.qualification.licenseBegin}")
	private String licenseBegin;

	/** 营业执照有效期结束 */
	@NotNull(message = "{validation.qualification.licenseEnd}")
	private String licenseEnd;

	/** 所属商家 */
	private Long merchantId;

	/** 银行开户名 */
	@NotNull(message = "${validation.merchantaccount.accountName}")
	private String accountName;

	/** 公司银行帐号 */
	@NotNull(message = "${validation.merchantaccount.account}")
	private String account;

	/** 开户行所在区域 */
	//@NotNull(message = "${validation.merchantaccount.districtId}")
	private Long accountDistrictId;

	/** 开户银行支行名称 */
	@NotNull(message = "${validation.merchantaccount.bankName}")
	private String bankName;

	/** 支行银联号 */
	@NotNull(message = "${validation.merchantaccount.bankNum}")
	private String bankNum;

	/** 开户银行许可证电子版 */
	@NotNull(message = "${validation.merchantaccount.electronicImage}")
	private String electronicImage;

	/** 法定经营范围 */
	private String manageScope;
	/**
	 * 公司成立时间
	 */
	private String establishTime;
}
