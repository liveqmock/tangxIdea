package com.topaiebiz.payment.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/9 14:32
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public enum PaymentExceptionEnum implements ExceptionInfo {

    ORDER_ID_CANT_BE_NULL("80000001","order's id cant be null"),

    UNABLE_TO_GET_USER_OPENID("80000002","Unable to get user's openId"),

    FAILED_TO_BUILD_PREPPAY_SIGN("80000003","Failed to build pre-payment parameters"),

    ORDER_CANT_BE_FOUND("80000004", "order cant be null"),

    ORDER_CANT_PAID("80000005", "order status is not allowed to be paid"),

    PAY_NOTICE_AGAING("80000006", "order pay notice again!"),

    PAY_TYPE_IS_ILLEGAL("80000007", "调用第三方支付，支付类型非法！");
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
     *
     * Description： 根据异常的代码、默认提示信息构建一个异常信息对象。
     *
     * Author zhushuyong
     *
     * @param code
     * @param defaultMessage
     */
    PaymentExceptionEnum(String code, String defaultMessage) {
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
