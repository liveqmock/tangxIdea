package com.topaiebiz.sms.context;

import com.topaiebiz.sms.dto.SmsLimitIpInfoDTO;

/**
 * Description 当前获取验证码的IP 的一些信息
 *
 * @Author hxpeng
 * <p>
 * Date 2018/6/11 19:04
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public class LimitIpInfoContext {

    private static final ThreadLocal<SmsLimitIpInfoDTO> REQUEST_IP_INFO_DTO_THREAD_LOCAL = new ThreadLocal<>();

    public static void set(SmsLimitIpInfoDTO smsLimitIpInfoDTO) {
        REQUEST_IP_INFO_DTO_THREAD_LOCAL.set(smsLimitIpInfoDTO);
    }

    public static SmsLimitIpInfoDTO get() {
        return REQUEST_IP_INFO_DTO_THREAD_LOCAL.get();
    }

    public static void remove() {
        REQUEST_IP_INFO_DTO_THREAD_LOCAL.remove();
    }


    private LimitIpInfoContext() {

    }

}
