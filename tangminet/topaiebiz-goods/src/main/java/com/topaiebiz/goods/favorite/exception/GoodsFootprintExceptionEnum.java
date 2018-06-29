package com.topaiebiz.goods.favorite.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

/**
 * Created by dell on 2018/1/5.
 */
public enum GoodsFootprintExceptionEnum implements ExceptionInfo {

    GOODSCART_GOODSATTR_NOT_NULL("2000044","Detecting the corresponding parameter commodity does not exist"),
    GOODSFAVOR_GOODSID_NOT_NULL("2000045","Please select goods!"),
    GOODSFAVOR_ID_NOT_NULL("2000045","Please select goods!"),
    GOODSFAVOR_ID_NOT_EXIST("2000046","This goods does not exist!"),
    FAVORITE_FULL("2000047","Your collection is full, please delete the collection of items in the collection！");

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
     *
     * Description： 根据异常的代码、默认提示信息构建一个异常信息对象。
     *
     * Author zhushuyong
     *
     * @param code
     * @param defaultMessage
     */
    GoodsFootprintExceptionEnum(String code, String defaultMessage) {
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
