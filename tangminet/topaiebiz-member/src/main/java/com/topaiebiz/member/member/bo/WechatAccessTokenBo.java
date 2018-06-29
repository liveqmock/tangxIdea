package com.topaiebiz.member.member.bo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 微信返回信息
 * 
 * @author 陈相宇
 *
 */
public class WechatAccessTokenBo {

	private String errcode;
	private String errmsg;
	@JSONField(name = "access_token")
	private String accessToken;// 接口调用凭证
	@JSONField(name = "expires_in")
	private Integer expiresIn;// access_token接口调用凭证超时时间，单位（秒）
	@JSONField(name = "refresh_token")
	private String refreshToken;// 用户刷新access_token
	private String openid;// 授权用户唯一标识
	private String scope;// 用户授权的作用域，使用逗号（,）分隔
	private String unionid;

	public String getErrcode() {
		return errcode;
	}

	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Integer getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Integer expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

}
