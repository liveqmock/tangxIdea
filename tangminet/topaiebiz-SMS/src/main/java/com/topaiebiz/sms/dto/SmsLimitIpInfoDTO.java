package com.topaiebiz.sms.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Description ip 限制情况 by IP
 *
 * @Author hxpeng
 * <p>
 * Date 2018/6/11 19:32
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class SmsLimitIpInfoDTO implements Serializable {

    private static final long serialVersionUID = -4768767811981301987L;

    /**
     * 当前获取验证的IP / 最后一次发送时间(毫秒级别) / 最后一次验证码 / 当前IP历史获取次数 / 当日是否被禁止 / 当前IP 各个类型的验证码获取的次数
     */
    private String currentIp;
    private Long lastSendTime;
    private String lastSendCaptcha;
    private Integer sendCount;
    private Boolean prohibit;
    private Map<String, Integer> ipRequestGroupByType;

    /**
     * Description: 首次发送短信
     *
     * @Author: hxpeng
     * createTime: 2018/6/11
     * @param:
     **/
    public SmsLimitIpInfoDTO firstSend(String currentIp, String captcha) {
        this.currentIp = currentIp;
        this.sendCount = 1;
        this.lastSendCaptcha = captcha;
        this.lastSendTime = Instant.now().toEpochMilli();
        this.prohibit = false;
        this.ipRequestGroupByType = new HashMap<>();
        return this;
    }
}
