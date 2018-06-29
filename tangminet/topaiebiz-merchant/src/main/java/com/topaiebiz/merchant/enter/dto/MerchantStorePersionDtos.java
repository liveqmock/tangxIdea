package com.topaiebiz.merchant.enter.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class MerchantStorePersionDtos  implements Serializable {

	/** 用户名 */
	@NotNull(message = "{validation.merchantStorePersion.loginName}")
	private String loginName;

	/** 密码 */
	@NotNull(message = "{validation.merchantStorePersion.password}")
	private String password;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
