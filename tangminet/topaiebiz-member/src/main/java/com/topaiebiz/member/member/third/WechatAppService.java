package com.topaiebiz.member.member.third;

import com.topaiebiz.member.member.bo.WechatAccessTokenBo;
import com.topaiebiz.member.member.bo.WechatUserBo;

public interface WechatAppService {

	WechatAccessTokenBo getAccessToken(String code);
	
	WechatUserBo getUserInfo(WechatAccessTokenBo accessToken);

	WechatUserBo getUserInfo(String accessToken, String openid);
}
