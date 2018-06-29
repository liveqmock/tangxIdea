package com.topaiebiz.openapi.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/6 21:12
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public enum OpenApiExceptionEnum implements ExceptionInfo {

    APP_ID_CANT_BE_NULL("18000001", "appId 不能为空！"),

    STORE_RESOURCE_IS_NOT_FOUND("18000002", "请输入正确的APP ID！"),

    VERIFY_SIGNATURE_FAILURE("18000003", "验证签名失败！"),

    PARAMETER_FORMAT_NOT_CORRECT("18000004", "业务参数格式不正确！"),

    METHOD_CANT_BE_NULL("18000005", "方法不能为空！"),

    METHOD_NAME_IS_ILLEGAL("18000006", "方法名非法！"),

    TIMESTAMP_CANT_BE_NULL("18000008", "时间戳不能为空！"),

    SIGN_CANT_BE_NULL("18000009", "签名不能为空！"),

    ORDER_ID_NO_UNIQUE("18000010", "订单ID不唯一！"),

    ORDER_PAY_NULL("18000010", "该推送的订单信息为空"),

    ORDER_PAY_HAD_PUSH_PAYMENT("18000011", "该订单已经推送过了"),

    ORDER_PAY_CANT_PUSH_PAYMENT("18000012", "该订单禁止推送支付信息"),

    NOT_SUPPORT_NOW("18000013", "该支付方式暂时不支持"),

    VERSION_CANT_BE_NULL("18000005", "版本号不能为空！"),


    ;

    /**
     * 异常代码。
     */
    private String code;

    /**
     * 异常对应的默认提示信息。
     */
    private String defaultMessage;

    /**
     * 异常对应的原始提示信息。
     */
    private String originalMessage;

    /**
     * 当前请求的URL。
     */
    private String requestUrl;

    /**
     * 默认的转向（重定向）的URL，默认为空。
     */
    private String defaultRedirectUrl = "";

    /**
     * 异常对应的响应数据。
     */
    private Object data;

    /**
     * Description 根据异常的代码、默认提示信息构建一个异常信息对象。
     * <p>
     * Author Aaron.Xue
     *
     * @param code           异常的代码。
     * @param defaultMessage 异常的默认提示信息。
     */
    OpenApiExceptionEnum(String code, String defaultMessage) {
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
