package com.topaiebiz.member.identity.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

/**
 * Created by admin on 2018/5/31.
 */
public enum IdentityException implements ExceptionInfo {
    SEX_NOT_NOLL("4000039","The sex cannot be empty!"),
    REALNAME_NOT_NOLL("4000040","The Real names cannot be empty!"),
    IDCARD_NOT_NOLL("4000041","The idcard cannot be empty!");


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

    IdentityException(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getDefaultMessage() {
        return null;
    }

    @Override
    public String getOriginalMessage() {
        return null;
    }

    @Override
    public void setOriginalMessage(String s) {

    }

    @Override
    public String getRequestUrl() {
        return null;
    }

    @Override
    public void setRequestUrl(String s) {

    }

    @Override
    public String getDefaultRedirectUrl() {
        return null;
    }

    @Override
    public void setDefaultRedirectUrl(String s) {

    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void setData(Object o) {

    }
}
