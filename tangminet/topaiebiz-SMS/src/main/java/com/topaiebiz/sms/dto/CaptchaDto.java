package com.topaiebiz.sms.dto;

import com.topaiebiz.message.util.CaptchaType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码参数Dto
 */
@Data
@NoArgsConstructor
public class CaptchaDto {

    /**
     * 手机号 / 验证码 / 验证类型 / IP / 调用者用户ID
     */
    private String telephone;
    private String captcha;
    private CaptchaType type;
    private String ip;
    private Long memberId;


    public CaptchaDto(String telephone, CaptchaType type, String ip) {
        this.telephone = telephone;
        this.type = type;
        this.ip = ip;
    }

    public CaptchaDto(String telephone, String captcha, CaptchaType type) {
        this.telephone = telephone;
        this.captcha = captcha;
        this.type = type;
    }

}
