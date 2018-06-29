package com.topaiebiz.message.api;

import com.topaiebiz.message.util.CaptchaType;

/**
 * Along.Xue 验证码接口
 */
public interface CaptchaApi {

    /**
     * Description: 校验验证码是否正确
     *
     * @Author: hxpeng
     * createTime: 2018/6/12
     * @param:
     **/
    Boolean verifyCaptcha(String telephone, String captcha, CaptchaType type);

    /**
     * Description: 发送验证码
     *
     * @Author: hxpeng
     * createTime: 2018/6/12
     * @param:
     **/
    Boolean sendCaptcha(String telephone, CaptchaType type, String ip);

    /**
     * Description: 发送验证码， 带用户ID
     *
     * @Author: hxpeng
     * createTime: 2018/6/12
     * @param:
     **/
    Boolean sendCaptcha(String telephone, CaptchaType type, String ip, Long memberId);

}
