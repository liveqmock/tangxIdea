package com.topaiebiz.member.member.third;


import com.topaiebiz.member.member.bo.WechatUserBo;

public interface WechatPublicService {

	String getAccessToken();
	
	WechatUserBo getUserInfo(String openId);
}
