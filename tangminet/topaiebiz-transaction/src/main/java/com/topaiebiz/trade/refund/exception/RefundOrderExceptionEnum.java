package com.topaiebiz.trade.refund.exception;

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
public enum  RefundOrderExceptionEnum implements ExceptionInfo {

    AFTER_SALES_TYPE_ILLEGAL("6010001","Please select the correct after-sales code！"),

    MEMBER_IS_NOT_FOUND_IN_REFUND("6010002","member is not found in refund!"),

    STOREID_IS_NOT_FOUND_IN_REFUND("6010003","store id is not found by storeId "),

    REFUND_GOODS_CANT_BE_NULL("6010004","Apply for sale of goods can not be empty"),

    REFUND_ORDER_ID_CANT_BE_NULL("6010005","refund order id cant be null"),

    REFUND_ORDER_IS_NOT_FOUND("6010006","refund order is not found"),

    REFUND_ORDER_DETAILS_IS_NOT_FOUND("6010007","refund order details is not found"),

    OPERATION_CAN_NOT_BE_EXECUTED("6010008","Operation can not be executed"),

    CANT_FOUND_THE_EXPRESS_COMPANY_IN_DATADICT("6010009","cant found the express company in Data Dictionary"),

    ORDER_CANT_APPLY_REFUND_AGAIN("60100010", "order cant be apply of refund again!"),

    ILLEGAL_OPERATION("60100011", "Illegal operation"),

    REFUND_FAILED("60100012", "退款失败！"),

    REFUND_ORDER_OPERATE_DUPLICATE("60100013", "refund order operate duplicate"),

    REFUND_TYPE_IS_NOT_ALLOWABLE("60100014", "refund type is not allowable"),

    REFUND_REASON_CODE_IS_NOT_ALLOWABLE("60100015", "refund reason code is not allowable"),

    REFUND_PRICE_IS_NOT_ALLOWABLE("60100016", "refund price is not allowable"),

    USER_IS_NOT_FOUND("60100017", "user is not found"),

    INVALID_ENUMERATION_TYPE("60100018", "Invalid enumeration type"),

    REFUND_ORDER_CANT_BE_UPDATE("60100019", "refund order cant be update"),

    PLATFORMS_DO_NOT_INTERVENE_ORDER("60100020", "Platforms did not intervene in refund orders"),

    REFUND_NOT_ALLOW_TO_INTERVENED("60100021", "refund status is not allowed to be intervened"),

    MERCHANT_RETURN_ADDRESS_IS_NULL("60100022", "merchat's return address is null"),

    REFUND_APPLY_COUNT_IS_LIMITED("60100023", "售后的申请次数是受限制的！"),

    UN_DELIVERY_CANT_REJECT_REFUND("60100024", "未发货的售后申请无法拒绝."),

    AUDIT_REFUND_FAIL("60100025", "审核售后异常！"),

    CREATE_REFUND_FAIL("60100026", "创建/修改售后失败"),

    REFUND_LOG_EXISTED("60100027", "退款日志已存在！"),

    REFUND_LOG_DON_NOT_EXISTED("60100028", "退款日志不存在！"),

    SUBMIT_REFUND_PARAMS_ILLEGAL("60100029", "提交售后参数不正确")
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
    RefundOrderExceptionEnum(String code, String defaultMessage) {
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
