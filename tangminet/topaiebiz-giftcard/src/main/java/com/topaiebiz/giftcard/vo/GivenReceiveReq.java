package com.topaiebiz.giftcard.vo;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @description: 转赠领取请求
 * @author: Jeff Chen
 * @date: created in 上午11:02 2018/1/20
 */
@Data
public class GivenReceiveReq {

    /**
     * 转赠链接id
     */
    @NotEmpty(message = "转赠链接id不能为空")
    private String linkId;
    /**
     * 电话
     */
    @NotEmpty(message = "手机号不能为空")
    private String phone;
    /**
     * 验证码
     */
    @NotEmpty(message = "验证码不能为空")
    private String validCode;
}
