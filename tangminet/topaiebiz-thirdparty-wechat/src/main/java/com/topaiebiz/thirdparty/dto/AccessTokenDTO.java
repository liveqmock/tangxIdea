package com.topaiebiz.thirdparty.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Description 微信接口获取token类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017/11/16 21:36
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class AccessTokenDTO implements Serializable{

	private static final long serialVersionUID = -5844072675124167017L;

	private String access_token;

	private String expires_in;

	private String errcode;

	private String errmsg;
}
