package com.topaiebiz.member.member.third;

import com.topaiebiz.member.member.bo.WechatAccessTokenBo;
import com.topaiebiz.member.member.bo.WechatUserBo;

public interface WechatH5Service {
	
	String getAuthorizeUrl();

	String getAuthorizeUrl(String redirectType);

	WechatAccessTokenBo getAccessToken(String code);
	
	WechatUserBo getUserInfo(WechatAccessTokenBo accessToken);
}
