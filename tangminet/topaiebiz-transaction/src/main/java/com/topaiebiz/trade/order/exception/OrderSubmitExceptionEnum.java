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
public enum OrderSubmitExceptionEnum implements ExceptionInfo {

  /*  PURCHASED_GOODS_DO_NOT_EXIST("11000001", "The purchased goods do not exist!"),
    INSUFFICIENT_STOCK_OF_GOODS("11000002", "Insufficient stock of goods!"),
    GOODS_EXCEED_THE_LIMIT("11000003", "Goods exceed the limit!"),
    ORDER_CANNOT_BE_CANCELLED("11000004", "The order has been paid and cannot be cancelled!"),*/
    ADDRESS_NOT_EXIST("11000005", "address dose not exist"),
    ADDRESS_IS_EMPTY("11000006", "address is empty"),
    ADDRESS_NOT_SUPPORT("11000007", "address is empty"),
    GOODS_STORE_NOT_VALID("11000008", "goods is not valid"),
    GOODS_LOAD_FAIL("11000009", "goods is not valid"),
    ORDER_SUBMIT_DUPLICATE("11000010", "duplicate submit order"),
    PROMOTION_NOT_VALID("11000011", "promotion is not valid"),
    PROMOTION_NOT_EXISTS("11000012", "promotion has not exists"),
    ORDER_FAIL_ERROR("11000013", "order submit fail"),
    ORDER_CANCEL_ERROR("11000014", "order cancel fail"),

    STORE_FROZE_ERROR("11000015", "Store is frozen,can not submit order"),
    ID_NUMBER_BLANK_ERROR("11000016", "ID number is blank"),
    ID_NUMBER_NOT_VALID_ERROR("11000017", "ID number is not valid"),
    BUYER_NAME_BLANK_ERROR("11000018", "buyer name is blank"),;

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
    OrderSubmitExceptionEnum(String code, String defaultMessage) {
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
