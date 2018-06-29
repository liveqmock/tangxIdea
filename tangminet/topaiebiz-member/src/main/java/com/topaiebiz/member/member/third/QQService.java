package com.topaiebiz.member.member.third;

import com.topaiebiz.member.member.bo.QQUserBo;

public interface QQService {

	String getAuthorizeUrl();

	String getAccessToken(String code);
	
	String getOpenId(String token);

	QQUserBo getUserInfo(String token, String openId);
	

}
