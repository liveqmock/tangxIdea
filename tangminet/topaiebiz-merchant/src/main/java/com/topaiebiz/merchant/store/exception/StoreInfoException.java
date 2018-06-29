package com.topaiebiz.merchant.store.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

/**
 * Description: 商家入驻异常枚举类
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年10月14日 下午3:02:44
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public enum StoreInfoException implements ExceptionInfo {

    STOREINFO_ID_NOT_NULL("30000022", "StoreInfo StoreId  cannot be empty!"),
    STOREINFO_ID_NOT_EXIST("30000023", "StoreInfo StoreId cannot be exist!"),
    STOREINFO_HAS_ALREADY_EXISTED("30000030", "The storeInfo has already existed!"),
    MEMBER_HAS_ALREADY_EXISTED("300000032", "The member has already existed!"),
    STOREINFO_NAME_NOT_NULL("30000036", "StoreInfo name cannot be exist!"),
    STOREINFO_NOT_EXIST("30000037", "The storeInfo do not exist!");
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
     * Author: Anthony
     *
     * @param code           异常的代码。
     * @param defaultMessage 异常的默认提示信息。
     */
    StoreInfoException(String code, String defaultMessage) {
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
