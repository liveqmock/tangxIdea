package com.topaiebiz.member.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

public enum MemberExceptionEnum implements ExceptionInfo {
    NOT_LOGIN("4000001", "请登录"),
    MEMBER_MEMBERCODE_NOT_REPETITION("4000005", "会员编号不能重复!"),
    MEMBER_USERNAME_NOT_REPETITION("4000006", "用户名不能重复!"),
    MEMBER_ID_NOT_NULL("4000007", "会员ID不能为空!"),
    MEMBER_ID_NOT_EXIST("4000008", "会员ID不存在!"),
    MEMBER_USERNAME_NOT_NULL("4000019", "账号不能为空！"),

    MEMBER_PASSWORD_NOT_NULL("4000020", "会员密码不能为空!"),
    MEMBER_PASSWORD_ERROR("4000022", "登录密码错误!"),
    MEMBER_USER_EXISTENCE("4000025", "用户已经存在！"),
    MEMBER_USER_NO_EXISTENCE("4000026", "用户已不存在!"),
    MEMBER_USERNAME_PASSWORD_NOT_NULL("4000027", "用户名或者密码不能为空！"),
    MEMBER_CAPTCHA_ERR("4000028", "验证码错误!"),
    MEMBER_CAPTCHA_NOT_NULL("4000029", "验证码不能为空!"),

    MEMBER_PHONENUMBER_EXIST("4000030", "该手机号已被注册使用!"),
    MEMBER_FROZEN("4000031", "该账号已冻结，请联系客服!"),
    MEMBER_PAYPASSWORD_NOT_NULL("4000034", "支付密码为空!"),

    MEMBER_REGISTER_FAIL("4000040", "注册失败!"),
    MEMBER_PASSWORD_FORMAT_ERR("4000041", "会员密码格式不符合要求！"),
    MEMBER_PAYPASSWORD_FORMAT_ERR("4000042", "支付密码格式不符合要求！"),
    MEMBER_INIT_PASSWORD_DISABLED("4000043", "已设置密码，无法初始化密码！"),
    MEMBER_INIT_PASSWORD_CODE_ERR("4000044", "请重新登录后操作！"),
    MEMBER_INIT_PASSWORD_FAIL("4000045", "设置密码操作失败！"),
    MEMBER_OPERATE_CODE_ERR("4000046", "操作码失效，请重新验证！"),
    MEMBER_BIND_TELEPHONE_FAIL("4000047", "绑定手机失败！"),
    MEMBER_PASSWORD_PARAM_ERR("4000048", "验证密码参数异常！"),
    MEMBER_BIND_PARM_ERR("4000049", "绑定手机参数异常！"),

    MEMBER_INIT_PAYPASSWORD_FAIL("4000050", "设置支付密码操作失败！"),
    MEMBER_PAYPASSWORD_EXIT("4000051", "支付密码已经存在，无法修改！"),
    MEMBER_PAYPASSWORD_ERR("4000052", "支付密码错误！"),
    MEMBER_CAPTCHA_ERR_LIMIT("4000053", "当日验证码错误次数超过限制，请明日再试!"),
    MEMBER_LOGIN_ERR_LIMIT("4000054", "当日密码错误次数超过限制，请明日再试!"),
    MEMBER_PAYPWD_ERR_LIMIT("4000055", "当日支付密码错误次数超过限制，请明日再试!"),
    MEMBER_MEMBERPWD_ERR_LIMIT("4000057", "当日登录密码错误次数超过限制，请明日再试!"),

    MB_NAME_PWD_ERROR("4000062", "账号或密码错误，请检查后重新登陆"),
    MB_LOGIN_ERROR_TIMES("4000063", "登陆密码错误次数超限，请24小时之后再尝试登陆"),
    MB_SIMPLE_PASSWORD("4000064", "系统升级，为了确保账户安全，请修改您的密码"),
    MB_MEMBER_DISABLED("4000065", "您的账户已被禁用，如需帮助请联系客服"),
    MB_MEMBER_NULL("4000066", "该用户不存在！"),
    MB_MOBILE_ERR("4000067", "手机格式错误或为空"),
    MB_USER_EXIT("4000068", "该账号已经存在！"),
    MEMBER_OPERATE_LATER("4000069", "操作过于频繁!"),

    MEMBER_WECHAT_LOGIN_ERROR("4000070", "绑定或解绑微信异常！"),
    MEMBER_REPEAT_CHECKIN("4000071", "已签到！"),
    MEMBER_CHECKIN_FAIL("4000072", "签到失败！"),
    MEMBER_WECHAT_AUTH_ERR("4000073", "微信授权信息异常！"),
    MEMBER_WECHAT_HAD_BIND("4000074", "该微信已经被绑定！"),
    MEMBER_HAD_BIND_WECHAT("4000075", "该用户已经绑定了微信！"),
    MEMBER_RESET_PASSWORD_FAIL("4000076", "重置密码操作失败！"),
    MEMBER_INFO_MULTI("4000077", "账号信息异常，请联系客服!"),
    MEMBER_NOT_BIND_WECHAT("4000078", "该用户未绑定任何微信账号！"),
    MEMBER_NOT_MATCH_WECHAT("4000079", "该用户和此微信账号不匹配！"),
    MEMBER_THIRDPARTY_MULTI("4000080", "第三方账号登录异常，请联系客服!");


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
     * Author: Scott.Yang
     *
     * @param code           异常的代码。
     * @param defaultMessage 异常的默认提示信息。
     */
    MemberExceptionEnum(String code, String defaultMessage) {
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
