package com.topaiebiz.sms.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

public enum MessageExceptionEnum implements ExceptionInfo {

    CAPTCHA_IS_ERROR("16000001", "Captcha is error!"),

    CAPTCHA_STALE_DATED("16000002", "Captcha stale dated!"),

    SEND_MESSAGE_FAILURE("16000003", "Send message failure!"),

    IP_LIMIT_FAIL("16000004", "This Ip send message limit!"),

    IPHONE_LIMIT_FAIL("16000005", "This IPONE send message limit!"),

    TELEPHONE_IS_NULL("16000006", "Telephone is null!"),

    CAPTCHA_IS_NULL("16000007", "Captcha is null!"),

    TYPE_IS_NULL("16000008", "Type is null!"),

    IP_IS_NULL("16000009", "Ip is null!"),

    INTERVAL_TOO_SHORT("16000010", "The interval is too short!"),

    PLEASE_TRY_AGAIN_TOMORROW("16000011", "Please try again tomorrow!"),

    VERIFY_FAIL_NUM_IS_TO_MUCH_SEND_AGAIN("16000012", "为了您的账户安全，请10分钟之后再试！"),

    VERIFY_FAIL_NUM_IS_TO_MUCH("16000013", "验证次数过多啦，请明天再来试试吧！"),

    CAPTCHA_SEND_FAIL("16000014", "发送验证码失败了！"),

    SMS_VERIFY_FAIL_TO_MUCH_SEND_COOLING("16000015", "验证码输入错误次数太多啦， 请稍后再试试吧！"),

    CAPTCHA_OPERATION_LOCKING("16000016", "慢点, 你的手速太快啦！"),

    CAPTCHA_SEND_IS_PROHIBIT("16000017", "为了您的账户安全，请明天再试！"),

    CAPTCHA_SEND_IS_IN_THE_COOLING_ONE_MINUTE("16000018", "验证获取太频繁了, 请稍后再试！"),

    CAPTCHA_SEND_IS_IN_THE_COOLING_TEN_MINUTE("16000019", "操作过于频繁, 请距离上次获取验证码十分钟之后再获取！"),

    REQUEST_IP_IS_REACH_THE_LIMIT("16000020", "验证码发送失败！"),

    SENT_SMS_DON_NOT_FOUND_TEMPLATE("16000021", "未查询到短信模板！"),

    VERIFY_FAIL_SEND_CAPTCHA_INFO_IS_NULL("16000022", "验证失败, 为查询到相关的发送信息！"),

    VERIFY_FAIL_CAPTCHA_NOT_EQUALS("16000023", "验证码输入错误了！"),

    CURRENT_CAPTCHA_IS_NULL("16000024", "当前不存在需要被验证的验证码！"),

    CAPTCHA_CAN_NOT_SEND_AGAIN("16000025", "今日验证码获取次数已达上限");

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
    MessageExceptionEnum(String code, String defaultMessage) {
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
