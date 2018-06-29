package com.topaiebiz.decorate.constant;


import com.nebulapaas.web.exception.ExceptionInfo;

public class ErrorInfo implements ExceptionInfo {

    private String code;
    private String defaultMessage;
    private Object data;

    public ErrorInfo() {
    }


    public ErrorInfo(String code, String defaultMessage, Object data) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.data = data;
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
