package com.topaiebiz.member.exception;

import com.nebulapaas.web.exception.ExceptionInfo;

public enum PointExceptionEnum implements ExceptionInfo {

    MEMBER_POINT_ID_NOT_NULL("4000213", "The member integral ID cannot be empty!"),

    MEMBER_POINT_ID_NOT_EXIST("4000214", "The member integral ID does not exist!"),

    MEMBER_POINT_ORDERID_NOT_NULL("4000215", "The member order ID cannot be empty!"),

    MEMBER_POINT_USAGESCORE_NOT_NULL("4000216", "The member usageScore  cannot be empty!"),

    MEMBER_POINT_DEDUCTIBLEAMOUNT_NOT_NULL("4000217", "The member deductibleAmount cannot be empty!"),

    MEMBER_POINT_USAGESCORE_NOT_MORE("4000218", "Members are not able to integrate!"),

    MEMBER_POINT_ALREADY_RECEIVED("4000235", "You have received a new exclusive access integral integral, there are more!"),

    MEMBER_POINT_ALREADY_RECEIVED_DAY("4000236", "You have a try tomorrow today integral!"),

    MEMBER_INTEGRAL_RULE_IS_NOT_FOUND("4000241", "member integral rule is not found"),

    MEMBER_ASSET_NULL("4000241", "用户资产数据为空！"),
    MEMBER_ASSET_REPEAT_REQUESET("4000242", "用户资产变化重复请求"),
    MEMBER_ASSET_OVER("4000243", "用户资产变化异常！"),
    MEMBER_POINT_OVER("4000244", "用户积分变化总数异常！"),
    MEMBER_POINT_ZERO("4000245", "用户积分变化为0！"),
    MEMBER_UPDATE_ASSET_FAIL("4000246", "会员积分或余额更新失败！"),

    MEMBER_POINT_CONVERT_PARAM("4000250", "转换数量必须大于0!"),
    MEMBER_POINT_CONVERT_TELEPHONE_NULL("4000251", "没有和crm系统绑定手机号码"),
    MEMBER_POINT_CRM_ERR("4000252", "调用CRM接口获取用户信息异常!"),
    MEMBER_POINT_MULTIPLE_ERR("4000253", "输入兑换值应该是5的整数倍!"),
    MEMBER_POINT_CRM_LIMIT("4000254", "积分转换数量超过实际拥有数量!"),
    MEMBER_POINT_CRM_REDUCE_ERR("4000255", "扣减crm积分失败!"),
    MEMBER_POINT_CRM_POINT_CLOSE("4000256", "很遗憾，积分转换功能暂时未开放!"),
    MEMBER_POINT_CRM_LOG_FAIL("4000257", "写CRM积分转换日志表失败!"),
    MEMBER_POINT_CRM_RATE_ERR("4000258", "贝因美积分转换妈妈购积分比例异常!"),
    MEMBER_ASSET_REDRESS_CLOSE("4000259", "会员资产修正接口未启用!"),
    MEMBER_ASSET_REDRESS_MEMBER_NULL("4000260", "会员资产修正会员不存在!"),
    MEMBER_ASSET_REDRESS_MEBER_NO_MATCH("4000261", "会员资产修正会员信息不匹配!");


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
    PointExceptionEnum(String code, String defaultMessage) {
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
