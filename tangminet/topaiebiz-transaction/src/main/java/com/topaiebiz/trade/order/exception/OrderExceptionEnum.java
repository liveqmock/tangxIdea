package com.topaiebiz.trade.order.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

/**
 * Description 交易订单。
 * 
 * Author Aaron.Xue 
 *    
 * Date 2017年10月14日 下午5:09:51
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
public enum OrderExceptionEnum implements ExceptionInfo{

	ORDER_ADDRES_CANT_BE_FOUND("6020001", "order address cant be found!"),

    ORDER_INVOICE_CANT_BE_FOUND("6020002", "order invoice cant be found!"),

    ORDER_DETAILS_CANT_BE_FOUND("6020003", "order details cant be found!"),

    ORDER_CANT_BE_FOUND("6020004", "order cant be found!"),

    EXPRESS_COMPANY_CANT_BE_FOUND("6020005", "express company cant be found!"),

    ORDER_HAS_BEEN_LOCKED("6020006", "order has been locked"),

	ORDER_PAY_INFO_CANT_BE_FOUND("6020007", "order pay' info cant be found"),

    OPERATION_IS_NOT_ALLOWED("6020008", "订单当前状态不允许当前的操作！"),

	ORDER_CANT_UPDATE_FREIGHT("6020009", "order cant update freight"),

    ORDER_INFO_UPDATE_FAIL("6020010", "order' info update fail!"),

	ORDER_UPDATE_FREIGHT_FAIL("6020011", "order update freight fail"),

    EXPORT_ORDER_FAIL("6020012", "导出订单失败！！"),

	ORDER_SHIP_FAIL("6020013", "订单发货失败！"),

	ORDER_CONFIRM_RECEIPT_FAIL("6020014", "确认收货失败！"),

	UPDATE_ORDER_EXPRESS_FAIL("6020015", "修改物流信息失败！"),

	ORDER_QUERY_PARAMS_ILLEGAL("6020016", "订单参数查询不正确！"),

	EXPRESS_NO_CANT_BE_NULL("6020017", "物流单号不能为空"),

	EXPRESS_COMPANY_CODE_CANT_BE_NULL("6020018", "物流公司CODE不能为空"),

	ORDER_ID_CANT_BE_NUL("6020019", "订单ID不能为空"),

	ORDER_REPEAT_OPERATION("6020020", "订单不允许重复操作！"),

    ;

	/** 异常代码。 */
	private String code;

	/** 异常对应的默认提示信息。 */
	private String defaultMessage;

	/** 异常对应的原始提示信息。 */
	private String originalMessage;

	/** 当前请求的URL。 */
	private String requestUrl;

	/** 默认的转向（重定向）的URL，默认为空。 */
	private String defaultRedirectUrl = "";

	/** 异常对应的响应数据。 */
	private Object data;

	/**
	 * Description 根据异常的代码、默认提示信息构建一个异常信息对象。
	 *
	 * Author Aaron.Xue
	 *
	 * @param code
	 *            异常的代码。
	 *
	 * @param defaultMessage
	 *            异常的默认提示信息。
	 */
	OrderExceptionEnum(String code, String defaultMessage) {
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
