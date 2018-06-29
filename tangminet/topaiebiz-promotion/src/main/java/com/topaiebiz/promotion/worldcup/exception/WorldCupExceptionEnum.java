package com.topaiebiz.promotion.worldcup.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

/**
 * Description 营销活动枚举
 * <p>
 * <p>
 * Author Joe
 * <p>
 * Date 2017年9月22日 下午10:33:46
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public enum WorldCupExceptionEnum implements ExceptionInfo {


    /**
     * 投注积分余额不足或投注积分大于2000
     */
    INSUFFICIENT_BALANCE_BALANCE("71000001", "Insufficient balance balance!"),

    /**
     * 该比赛投注时间已过
     */
    THE_BETTING_TIME_OF_THE_GAME_HAS_PASSED("71000002", "The betting time of the game has passed！"),

    /**
     * 投注积分超限制
     */
    INJECTION_INTEGRAL_OVER_LIMIT("71000003", "The injection integral is more than 2000！"),

    /**
     * 投注积分超限制
     */
    INCONFORMITY_OF_INJECTION_INTEGRAL("71000004", "Inconformity of injection integral!");

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
     * Author: Joe
     *
     * @param code           异常的代码。
     * @param defaultMessage 异常的默认提示信息。
     */
    WorldCupExceptionEnum(String code, String defaultMessage) {
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
