package com.topaiebiz.decorate.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

public enum DecorateExcepionEnum implements ExceptionInfo {
    ID_NOT_NULL("13000000", "id is not null!"),
    PAGENAME_NOT_NULL("13000001", "page name is not null!"),
    SUFFIXURL_NOT_NULL("13000002", "page address is not null!"),
    PUBLISHTIME_NOT_NULL("13000003", "publishtime is not  null!"),
    TIME_COMPARE_ERROR("13000004", "The start time cannot be greater than or equal to the end time!"),
    COMPONENT_NOT_NULL("13000005", "component of this page is not null!"),
    CONTENT_NOT_NULL("13000006", "content of component is not null!"),
    PAGE_ID_NOT_NULL("13000007", "page id is not null!"),
    COMPONENT_TYPE_NOT_NULL("13000008", "component type is not null!"),
    COMPONENT_ID_NOT_NULL("13000009", "component id is not null!"),
    BOTTOM_TAB_NOT_NULL("13000010", "bottom tab is not null!"),
    ITEM_NOT_NULL("13000011", "item is not null!"),
    IMAGE_NOT_NULL("13000012", "image is not null!"),
    WORDS_TOO_LONG("13000013", "words'length too long!"),
    ICONCOUNT_NOT_NULL("13000014", "icon count is not null!"),
    ICONINFO_NOT_NULL("13000015", "icon info is not null!"),
    ICONCOUNT_ERROR("13000016", "icon count must be same as icon info's size!"),
    WORDS_INFO_NOT_NULL("13000017", "words info is not null!"),
    PAGE_NOT_EXIST("13000018", "page is not exist!"),
    EXCEL_FORMAT_ERROR("13000019", "excel format error!"),
    NOT_IN_ACTIVITY_TIME("13000021","not in activity time!"),
    ACTIVITY_ITEM_NOT_NULL("13000022","activity item data not null!"),
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
     * Author Hedda
     *
     * @param code           异常的代码。
     * @param defaultMessage 异常的默认提示信息。
     */
    DecorateExcepionEnum(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    DecorateExcepionEnum(String code, String defaultMessage, Object data) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.data = data;
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
