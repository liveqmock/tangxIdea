package com.topaiebiz.member.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

public enum AddressExceptionEnum implements ExceptionInfo {

    MEMBER_ADDRESS_ID_NULL("4000101", "地址ID不能为空!"),

    MEMBER_ADDRESS_NOT_EXIST("4000102", "该地址不存在!"),

    MEMBER_ADDRESS_COUNT_LIMIT("4000103", "地址数量超过限制!"),

    MEMBER_ADDRESS_SYSERR("4000104", "地址系统出错！");


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
     * Description: 根据异常的代码、默认提示信息构建一个异常信息对象。
     * <p>
     * Author: Scott.Yang
     *
     * @param code           异常的代码。
     * @param defaultMessage 异常的默认提示信息。
     */
    AddressExceptionEnum(String code, String defaultMessage) {
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
