package com.topaiebiz.sms.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SMSRedisCacheKeys {

    //手机号每天重复发送限制
    public static final String SMS_LIMIT_PHONE = "sms_limit_phone_" + SMSRedisCacheKeys.nowDateToString() + "_";

    //IP 每天重复发送限制
    public static final String SMS_LIMIT_IP = "sms_limit_ip_" + SMSRedisCacheKeys.nowDateToString() + "_";

    //手机号发送时间前缀
    public static final String SMS_INTERVAL_TIME = "sms_interval_time_";

    //手机号验证码
    public static final String SMS_CAPTCHA = "sms_captcha_";

    /**
     * 输入验证错误过多，冷却中
     */
    public static final String SMS_VERIFY_FAIL_COOLING = "SMS_VERIFY_FAIL_COOLING_";

    /**
     * 验证码IP 获取限制次数 缓存key
     */
    public static final String CAPTCHA_IP_SENT_LIMIT = "CAPTCHA_IP_SENT_LIMIT_MAP";

    /**
     * 验证码 校验次数 缓存key
     */
    public static final String CAPTCHA_VERIFY_COUNT = "CAPTCHA_VERIFY_COUNT_";

    /**
     * 用户验证码 当日发送情况 缓存key
     */
    public static final String CAPTCHA_SEND_INFO_BY_USER = "CAPTCHA_SEND_INFO_BY_USER_";

    /**
     * 验证码操作锁 缓存key
     */
    public static final String CAPTCHA_OPERATION_LOCK_BY_TELEPHONE = "CAPTCHA_OPERATION_LOCK_BY_TELEPHONE_";

    public static String nowDateToString() {
        Date now = new Date();
        return new SimpleDateFormat("yyyy-MM-dd").format(now);
    }

}
