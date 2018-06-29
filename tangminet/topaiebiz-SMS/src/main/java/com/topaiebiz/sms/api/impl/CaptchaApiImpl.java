package com.topaiebiz.sms.api.impl;

import com.topaiebiz.message.api.CaptchaApi;
import com.topaiebiz.message.util.CaptchaType;
import com.topaiebiz.sms.dto.CaptchaDto;
import com.topaiebiz.sms.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaptchaApiImpl implements CaptchaApi {

    @Autowired
    private MessageService messageService;

    @Override
    public Boolean verifyCaptcha(String telephone, String captcha, CaptchaType type) {
        /**
         * 校验短信步骤
         * 1.校验参数是否正确
         * 2.
         *
         */
        CaptchaDto captchaDto = new CaptchaDto(telephone, captcha, type);
        return messageService.verifyCaptcha(captchaDto);
    }

    @Override
    public Boolean sendCaptcha(String telephone, CaptchaType type, String ip) {
        /**
         * 发送短信步骤
         * 1.校验参数 都不可为空
         * 2.校验是否是重复发送（IP.手机号）
         * 3.从数据库获取短信模版
         * 4.配置参数
         * 5.调用封装的阿里工具类发送信息
         * 6.放入redis
         * 7.存入日志
         */
        CaptchaDto captchaDto = new CaptchaDto(telephone, type, ip);
        return messageService.sendCaptcha(captchaDto);
    }

    @Override
    public Boolean sendCaptcha(String telephone, CaptchaType type, String ip, Long memberId) {
        CaptchaDto captchaDto = new CaptchaDto(telephone, type, ip);
        captchaDto.setMemberId(memberId);
        return messageService.sendCaptcha(captchaDto);
    }
}


