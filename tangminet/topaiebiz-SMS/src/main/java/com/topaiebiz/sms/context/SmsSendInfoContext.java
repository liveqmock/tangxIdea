package com.topaiebiz.sms.context;

import com.topaiebiz.sms.dto.SmsSendInfoDTO;

/**
 * Description TODO
 *
 * @Author hxpeng
 * <p>
 * Date 2018/6/11 19:04
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public class SmsSendInfoContext {

    private static final ThreadLocal<SmsSendInfoDTO> SEND_INFO_DTO_THREAD_LOCAL = new ThreadLocal<>();

    public static void set(SmsSendInfoDTO smsSendInfoDTO) {
        SEND_INFO_DTO_THREAD_LOCAL.set(smsSendInfoDTO);
    }

    public static SmsSendInfoDTO get() {
        return SEND_INFO_DTO_THREAD_LOCAL.get();
    }

    public static void remove() {
        SEND_INFO_DTO_THREAD_LOCAL.remove();
    }


    private SmsSendInfoContext() {

    }

}
