package com.topaiebiz.merchant.enter.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * Description: 店铺、商家人员Dto类
 * 
 * Author : Anthony
 * 
 * Date :2017年10月23日 下午2:11:02
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerchantStorePersionDto implements Serializable {

	/** 商家入驻信息的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。 */
	private Long id;

	/** 账号 */
	@NotNull(message = "{validation.merchantStorePersion.loginName}")
	private String loginName;

	/** 手机号 */
	@NotNull(message = "{validation.merchantStorePersion.telephone}")
	@Length(min = 11, max = 11)
	private String telephone;

	/** 验证码 */
	@NotNull(message = "{validation.merchantStorePersion.verificationCode}")
	private String captcha;

	/** 密码 */
	@NotNull(message = "{validation.merchantStorePersion.password}")
	private String password;
}
