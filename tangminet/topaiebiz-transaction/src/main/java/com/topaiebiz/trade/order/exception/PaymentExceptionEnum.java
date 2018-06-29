package com.topaiebiz.trade.order.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

/**
 * Description 交易订单。
 * <p>
 * Author Aaron.Xue
 * <p>
 * Date 2017年10月14日 下午5:09:51
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public enum PaymentExceptionEnum implements ExceptionInfo {

    //支付
    ORDER_NOT_FOUND("13000210", "pay order dose not exists"),
    ORDER_ALREADY_PAYED("13000211", "alread pkg payed"),
    HAS_NO_USEFUL_CARDS("13000212", "has no card"),
    PAY_AMOUNT_PRECISION_ERROR("13000213", "amount precision is not valid"),
    INPUT_AMOUNT_IS_NEGATIVE("13000214", "input amount has negative"),
    ACCOUNT_LOCK_ERROR("13000215", "acount is lock"),
    PAY_AMOUNT_ERROR("13000216", "pay amount error"),
    PAY_PWD_EMPTY_ERROR("13000217", "pay password is empty"),
    PAY_PWD_WRONG_ERROR("13000218", "pay password is wrong"),
    SCORE_LACK_ERROR("13000219", "score is lack"),
    BALANCE_LACK_ERROR("13000220", "balance is lack"),
    PAY_FAIL_ERROR("13000221", "pay fail"),
    ORDER_CAN_NOT_CANCEL("13000222", "order can not cancel"),
    PAY_SUBMIT_DUPLICATE("13000223", "order can not pay duplicate"),
    PAY_PWD_HAS_NOT_SET("13000224", "pay password has not set"),
    SCORE_GOODS_FORBID("13000225", "score is forbidden"),
    SCORE_EXCEED_LIMIT("13000226", "score exceed goods limit"),
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
