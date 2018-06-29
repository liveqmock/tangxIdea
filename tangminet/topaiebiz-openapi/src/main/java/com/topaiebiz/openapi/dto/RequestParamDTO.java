package com.topaiebiz.openapi.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Description open api 请求参数DTO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/2 13:32
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class RequestParamDTO implements Serializable {

    private static final long serialVersionUID = -2806672681559758931L;

    /**
     * appid
     */
    private String appId;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 业务参数JSON字符串
     */
    private String params;

    /**
     * 签名
     */
    private String sign;

    /**
     * 方法
     */
    private String method;

    /**
     * 版本号
     */
    private String version;

    /**
     * 店铺ID(后台查询获取, 不接收传入值)
     */
    private Long storeId;

}
