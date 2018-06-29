package com.topaiebiz.sms.service;

import com.topaiebiz.sms.dto.CaptchaDto;

public interface MessageService {

    /**
     * Description: 发送验证码
     *
     * @Author: hxpeng
     * createTime: 2018/6/11
     * @param:
     **/
    Boolean sendCaptcha(CaptchaDto captchaDto);

    /**
     * Description: 校验验证码
     *
     * @Author: hxpeng
     * createTime: 2018/6/11
     * @param:
     **/
    Boolean verifyCaptcha(CaptchaDto captchaDto);
}
