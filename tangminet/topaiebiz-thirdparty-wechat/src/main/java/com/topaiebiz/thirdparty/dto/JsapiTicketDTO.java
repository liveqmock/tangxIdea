package com.topaiebiz.thirdparty.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Description 微信网页SDK 票根
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017/11/16 21:27
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class JsapiTicketDTO implements Serializable{

    private static final long serialVersionUID = -7886489315300996490L;

    private String jsapiTicket;

	private String errcode;

	private String errmsg;

	private String ticket;

	private String expires_in;

}
