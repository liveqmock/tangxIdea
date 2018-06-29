package com.topaiebiz.sms.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Description 手机号当日 验证码发送情况 by 用户
 *
 * @Author hxpeng
 * <p>
 * Date 2018/6/11 16:33
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class SmsSendInfoDTO implements Serializable {
    private static final long serialVersionUID = -5383045531166526355L;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 当前验证类型 / 当前验证码 / 当日是否被禁止 / 发送次数 /
     */
    private String currentCaptchaType;
    private String currentCaptcha;
    private Boolean prohibit;
    private Integer sendCount;

    /**
     * 各类型短信最后发送时间(key:type枚举值, value:发送时间)
     * 各类型发送次数(key:type枚举值, value:发送次数)
     * 允许被发送的时间(key:类型， value:下次允许被发送的时间) 连续验证错误时 生效
     */
    private Map<String, Long> lastSendTimeByType;
    private Map<String, Integer> sendCountGroupByType;
    private Map<String, Long> allowToBeSendTime;

    /**
     * 当前验证码的 验证失败次数
     */
    private Integer currentVerifyFailCount;

    /**
     * Description: 首次发送短信
     *
     * @Author: hxpeng
     * createTime: 2018/6/11
     * @param:
     **/
    public SmsSendInfoDTO firstSend(String phone, String captchaType, String captcha) {
        this.phone = phone;
        this.currentCaptchaType = captchaType;
        this.currentCaptcha = captcha;
        this.prohibit = false;
        this.sendCount = 1;
        this.currentVerifyFailCount = 0;
        this.lastSendTimeByType = new HashMap<>();
        this.allowToBeSendTime = new HashMap<>();
        this.sendCountGroupByType = new HashMap<>();
        return this;
    }

}
