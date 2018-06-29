package com.topaiebiz.system.security.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

/**
 * Description: 系统级异常信息枚举。
 * 
 * Author: Amir Wang
 * 
 * Date: 2017年9月15日 下午5:50:53
 * 
 * Copyright: Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public enum SystemExceptionEnum implements ExceptionInfo {

	/** 参数不完整时的异常代码、默认提示信息。 */
	USER_DONT_EXIST("-100", "The user don't exist！"),
	
	USER_TYPE_ERROR("-101", "The user code is error！"),
	
	USERNAME_OR_PASSWORD_ERROR("-102", "User name or password is error!"),
	
	USER_TYPE_CORRESPONDENCE("-103", "Please log on to the merchant platform!"),

	ROLE_NAME_IS_NULL("-104", "Role name is null!"),

	MOBILEPHONE_IS_NULL("-105", "Mobilephone is null!"),

	USER_IS_NULL("-106", "User is null!"),

	USER_ID_IS_NULL("-107", "User name is null!"),

	RESOURCE_IS_NULL("-108", "User name is null!"),

	ROLE_ID_IS_NULL("-109", "User name is null!"),

	INBUILT_ROLE_CANNOT_BE_DELETED("-110", "Inbuilt role cannot be deleted"),

	PASSWORD_IS_NULL("-111", "Password is null!"),

	USER_ALREADY_EXISTS("-112", "The user already exists!");

	/** 异常代码。 */
	private String code;

	/** 异常对应的默认提示信息。 */
	private String defaultMessage;

	/** 异常对应的原始提示信息。 */
	private String originalMessage;

	/** 当前请求的URL。 */
	private String requestUrl;

	/** 需转向（重定向）的URL，默认为空。 */
	private String defaultRedirectUrl = "";

	/** 异常对应的响应数据。 */
	private Object data;

	/**
	 * Description: 根据异常的代码、默认提示信息构建一个异常信息对象。
	 *
	 * Author: Amir Wang
	 * 
	 * @param code
	 *            异常的代码。
	 * 
	 * @param defaultMessage
	 *            异常的默认提示信息。
	 */
	SystemExceptionEnum(String code, String defaultMessage) {
		this.code = code;
		this.defaultMessage = defaultMessage;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getDefaultMessage() {
		return defaultMessage;
	}

	@Override
	public String getOriginalMessage() {
		return originalMessage;
	}

	@Override
	public void setOriginalMessage(String originalMessage) {
		this.originalMessage = originalMessage;
	}

	@Override
	public String getRequestUrl() {
		return requestUrl;
	}

	@Override
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	@Override
	public String getDefaultRedirectUrl() {
		return defaultRedirectUrl;
	}

	@Override
	public void setDefaultRedirectUrl(String defaultRedirectUrl) {
		this.defaultRedirectUrl = defaultRedirectUrl;
	}

	@Override
	public Object getData() {
		return data;
	}

	@Override
	public void setData(Object data) {
		this.data = data;
	}

}
