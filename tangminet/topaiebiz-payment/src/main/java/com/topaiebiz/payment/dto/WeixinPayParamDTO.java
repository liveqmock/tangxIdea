package com.topaiebiz.payment.dto;

import lombok.Data;

/***
 * @author yfeng
 * @date 2018-01-25 13:01
 */
@Data
public class WeixinPayParamDTO extends PayParamDTO {
    /**
     * 授权CODE
     */
    private String code;

    /**
     * 微信用户openId
     */
    private String openId;
}